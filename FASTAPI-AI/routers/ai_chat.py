# routers/ai_chat.py
# =====================================================
# 설문 기반 + jobs 테이블 기반 AI 스택 추천 API (JSON 구조 반환)
# =====================================================

from fastapi import APIRouter, HTTPException, Depends
import httpx
import os
import json
from dotenv import load_dotenv
from typing import Optional, List, Tuple
from collections import Counter

from sqlalchemy.orm import Session

from schemas.ai import ChatReq
from db import get_db
from models.job import Job

load_dotenv()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")
MODEL_ID = os.getenv("OPENAI_MODEL_ID")

router = APIRouter(
    prefix="/ai",
    tags=["ai"],
)

# -----------------------------------------------------
# 사용자 텍스트에서 목표 직군 추론
# -----------------------------------------------------
def detect_target_job_category(user_text: str) -> Optional[str]:
    text = user_text.lower()

    if "백엔드" in text or "backend" in text or "서버" in text:
        return "백엔드"
    if "프론트" in text or "front" in text or "react" in text or "vue" in text:
        return "프론트엔드"
    if "모바일" in text or "android" in text or "ios" in text or "앱 개발" in text:
        return "모바일"
    if "데브옵스" in text or "devops" in text or "인프라" in text:
        return "데브옵스"
    if "ai" in text or "머신러닝" in text or "딥러닝" in text or "ml" in text:
        return "AI"
    if "게임" in text or "unity" in text or "언리얼" in text:
        return "게임"

    return None


# -----------------------------------------------------
# jobs 테이블에서 최근 채용공고 가져오기
# -----------------------------------------------------
def fetch_recent_jobs(db: Session, target_job_category: Optional[str], limit: int = 100) -> List[Job]:
    query = db.query(Job)

    if target_job_category:
        query = query.filter(Job.job_category == target_job_category)

    jobs = (
        query.order_by(Job.created_at.desc())
        .limit(limit)
        .all()
    )
    return jobs


# -----------------------------------------------------
# tech_stacks 통계 요약 만들기
# -----------------------------------------------------
def build_market_summary(jobs: List[Job]) -> Tuple[str, List[Tuple[str, int]]]:
    counter: Counter[str] = Counter()

    for job in jobs:
        if not job.tech_stacks:
            continue

        parts = [s.strip() for s in job.tech_stacks.split(",") if s.strip()]
        for tech in parts:
            counter[tech] += 1

    if not counter:
        return "최근 채용공고에서 추출할 수 있는 기술 스택 정보가 충분하지 않습니다.", []

    top_stacks = counter.most_common(10)

    lines = ["최근 채용공고에서 자주 등장한 기술 스택 TOP 10입니다:"]
    for tech, cnt in top_stacks:
        lines.append(f"- {tech}: {cnt}회 등장")

    summary_text = "\n".join(lines)
    return summary_text, top_stacks


# -----------------------------------------------------
# 샘플 공고 몇 개 텍스트 생성
# -----------------------------------------------------
def build_sample_job_snippets(jobs: List[Job], max_count: int = 5) -> str:
    snippets: List[str] = []

    for job in jobs[:max_count]:
        snippet = (
            f"제목: {job.title or '제목 없음'}\n"
            f"회사: {job.company or '회사명 미상'}\n"
            f"직군: {job.job_category or '직군 미상'} / 경력: {job.career_level or '경력 정보 없음'}\n"
            f"고용형태: {job.employment_type or '고용형태 정보 없음'}\n"
            f"요구 기술 스택: {job.tech_stacks or '명시된 스택 없음'}\n"
        )
        snippets.append(snippet)

    if not snippets:
        return "참고할 샘플 채용공고가 충분하지 않습니다."

    return "\n\n".join(snippets)


# -----------------------------------------------------
# 메인 엔드포인트
# -----------------------------------------------------
@router.post("/chat")
async def chat(req: ChatReq, db: Session = Depends(get_db)):

    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY missing")

    if not OPENAI_BASE_URL:
        raise HTTPException(status_code=500, detail="OPENAI_BASE_URL missing")

    if not MODEL_ID:
        raise HTTPException(status_code=500, detail="OPENAI_MODEL_ID missing")

    user_text = req.messages[0].content

    target_job_category = detect_target_job_category(user_text)

    jobs = fetch_recent_jobs(db, target_job_category=target_job_category, limit=100)

    if not jobs and target_job_category is not None:
        jobs = fetch_recent_jobs(db, target_job_category=None, limit=100)
        target_job_category = None

    if not jobs:
        market_summary_text = "참고할 채용공고 데이터가 없어 일반적인 개발자 스택 기준으로 추천합니다."
        sample_jobs_text = ""
        top_stacks = []
    else:
        market_summary_text, top_stacks = build_market_summary(jobs)
        sample_jobs_text = build_sample_job_snippets(jobs)

    if target_job_category:
        category_line = f"사용자 목표 직군: {target_job_category}"
    else:
        category_line = "사용자가 직군을 지정하지 않아 설문 + 시장 데이터를 기반으로 자동 추천합니다."

    # -----------------------------------------------------
    # JSON만 출력하도록 강제하는 프롬프트
    # -----------------------------------------------------
    final_prompt = (
        "너는 한국어로 답변하는 전문 IT 커리어 컨설턴트다.\n"
        "아래 정보를 기반으로 사용자에게 가장 적합한 직군, 기술 스택, 로드맵을 추천하라.\n\n"
        "[사용자 입력]\n"
        f"{user_text}\n\n"
        "[시장 데이터 요약]\n"
        f"{market_summary_text}\n\n"
        "[샘플 공고]\n"
        f"{sample_jobs_text}\n\n"
        f"{category_line}\n\n"
        "반드시 다음 JSON 형식으로만 출력해라. JSON 외의 텍스트를 절대 포함하지 마라.\n"
        "{\n"
        '  "role": "추천 직군",\n'
        '  "stacks": ["스택1", "스택2", "스택3"],\n'
        '  "roadmap": ["1단계 설명", "2단계 설명", "3단계 설명"],\n'
        '  "reasons": {\n'
        '       "스택1": "추천 이유",\n'
        '       "스택2": "추천 이유"\n'
        "  }\n"
        "}"
    )

    payload = {
        "model": MODEL_ID,
        "messages": [
            {"role": "system", "content": "너는 JSON 구조만 출력하는 전문 커리어 컨설턴트다."},
            {"role": "user", "content": final_prompt},
        ],
        "temperature": 0.3,
        "stream": False,
    }

    headers = {"Authorization": f"Bearer " + OPENAI_API_KEY}

    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(
            f"{OPENAI_BASE_URL}/v1/chat/completions",
            json=payload,
            headers=headers,
        )

        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=r.status_code, detail=r.text)

        data = r.json()
        answer_text = data["choices"][0]["message"]["content"]

        # JSON 파싱
        try:
            answer = json.loads(answer_text)
        except json.JSONDecodeError:
            answer = {
                "role": "분석 실패",
                "stacks": [],
                "roadmap": [],
                "reasons": {"error": "응답이 JSON 형식이 아닙니다.", "raw": answer_text}
            }

        return answer

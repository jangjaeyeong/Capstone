# routers/jobs.py
# =====================================================
# jobs 테이블 조회 + AI 기반 추천 API
#  - GET /jobs               : 최근 채용공고 리스트
#  - POST /jobs/recommend/ai : 설문 기반 AI 추천
# =====================================================

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import List, Optional
from sqlalchemy.orm import Session
import json

from db import get_db
from models.job import Job
from config import OPENAI_API_KEY, MODEL_ID
from openai import OpenAI

router = APIRouter(
    prefix="/jobs",
    tags=["jobs"],
)

# OpenAI 클라이언트 (base_url은 기본값 사용: https://api.openai.com/v1)
client = OpenAI(
    api_key=OPENAI_API_KEY,
    base_url="https://api.openai.com/v1",
    )


# -----------------------------
# 공고 조회 응답 스키마
# -----------------------------

class JobOut(BaseModel):
    id: int
    title: Optional[str]
    company: Optional[str]
    location: Optional[str]
    url: Optional[str]
    job_category: Optional[str]
    career_level: Optional[str]
    employment_type: Optional[str]
    tech_stacks: Optional[str]
    description: Optional[str]

    class Config:
        orm_mode = True


# -----------------------------
# AI 추천 요청/응답 스키마
# -----------------------------

class AIRecommendRequest(BaseModel):
    # 설문 전체를 문자열로 넣는 필드 (Q: A: 형태 그대로)
    survey_text: str
    limit: int = 5   # 추천 개수


class AIJobRecommendationOut(BaseModel):
    #id: int
    title: Optional[str]
    company: Optional[str]
    location: Optional[str]
    url: Optional[str]
    description: Optional[str]

    # AI가 생성한 필드
    reason: str
    recommended_stacks: List[str] = []


# -----------------------------
# 1) 기본 jobs 조회 API
# -----------------------------

@router.get("/", response_model=List[JobOut])
def list_jobs(limit: int = 20, db: Session = Depends(get_db)):
    """
    최근 채용공고 리스트 조회 (테스트/디버깅용)
    """
    jobs = (
        db.query(Job)
        .order_by(Job.created_at.desc())
        .limit(limit)
        .all()
    )
    return jobs


# -----------------------------
# 2) AI 추천 API
# -----------------------------

@router.post("/recommend/ai", response_model=List[AIJobRecommendationOut])
def recommend_jobs_with_ai(req: AIRecommendRequest, db: Session = Depends(get_db)):
    """
    설문(survey_text) + DB에 저장된 jobs 목록을 OpenAI에 넘겨서
    - 이 사용자에게 어울리는 공고 TOP N
    - 추천 이유(reason)
    - 관련 스택(recommended_stacks)
    를 함께 반환하는 API
    """

    # 1) 후보 공고 가져오기 (최근 50개 정도만 사용)
    candidate_jobs: List[Job] = (
        db.query(Job)
        .order_by(Job.created_at.desc())
        .limit(50)
        .all()
    )

    if not candidate_jobs:
        raise HTTPException(status_code=404, detail="추천할 채용공고가 없습니다.")

    # 2) OpenAI에 줄 후보 목록을 텍스트로 만든다.
    #    (id, title, description 일부만 사용)
    lines = []
    for job in candidate_jobs:
        lines.append(
            f"- id: {job.id}\n"
            f"  title: {job.title}\n"
            f"  description: {job.description or ''}\n"
        )
    jobs_block = "\n".join(lines)

    # 3) 프롬프트 구성
    system_msg = """
너는 개발자 채용 추천을 도와주는 어시스턴트야.

입력으로는
1) 사용자의 설문 내용
2) 여러 개의 채용공고 후보(id, title, description)

이 주어진다.

너의 역할은:
- 사용자의 목표(직무, 관심 기술, 경력 수준)를 이해하고
- 주어진 후보 중에서 이 사용자에게 특히 잘 맞는 공고를 최대 N개 골라서
- JSON 형식으로만 출력하는 것이다.

JSON 형식은 반드시 아래와 같아야 한다:

{
  "recommendations": [
    {
      "job_id": 47,
      "reason": "이 공고가 이 사용자에게 적합한 이유를 한국어로 2~3문장으로 설명",
      "recommended_stacks": ["Java", "Spring", "JPA"]
    },
    ...
  ]
}

규칙:
- job_id는 반드시 내가 제공한 id 값만 사용해야 한다.
- recommended_stacks는 문자열 리스트로, 이 공고에서 사용되거나 준비하면 좋을 스택을 적어라.
- JSON 이외의 텍스트는 절대 출력하지 마라.
"""

    user_msg = f"""
[사용자 설문 내용]
{req.survey_text}

[채용공고 후보 목록]
{jobs_block}

위 정보를 바탕으로 이 사용자에게 특히 잘 맞는 채용공고를 최대 {req.limit}개 골라서
앞에서 설명한 JSON 형식으로만 응답해줘.
"""

    # 4) OpenAI 호출 (chat.completions)
    response = client.chat.completions.create(
        model=MODEL_ID,
        messages=[
            {"role": "system", "content": system_msg},
            {"role": "user", "content": user_msg},
        ],
        temperature=0.4,
    )

    content = response.choices[0].message.content or ""
    try:
        parsed = json.loads(content)
    except json.JSONDecodeError:
        # 모델이 JSON 형식을 어겼을 때 대비용
        raise HTTPException(
            status_code=500,
            detail=f"AI 응답 JSON 파싱 실패: {content[:200]}",
        )

    recos = parsed.get("recommendations") or []
    if not isinstance(recos, list) or len(recos) == 0:
        raise HTTPException(
            status_code=500,
            detail="AI가 추천 결과를 생성하지 않았습니다.",
        )

    # 5) job_id 기준으로 실제 DB 레코드와 매핑
    job_by_id = {job.id: job for job in candidate_jobs}

    results: List[AIJobRecommendationOut] = []
    for item in recos[: req.limit]:
        job_id = item.get("job_id")
        if job_id not in job_by_id:
            # 제공하지 않은 id가 나오면 스킵
            continue

        job = job_by_id[job_id]
        reason = item.get("reason") or ""
        stacks = item.get("recommended_stacks") or []

        if not isinstance(stacks, list):
            stacks = [str(stacks)]

        results.append(
            AIJobRecommendationOut(
                #id=job.id,
                title=job.title,
                company=job.company,
                location=job.location,
                url=job.url,
                description=job.description,
                reason=reason,
                recommended_stacks=stacks,
            )
        )

    if not results:
        raise HTTPException(
            status_code=500,
            detail="AI가 유효한 job_id를 포함한 추천을 생성하지 않았습니다.",
        )

    return results

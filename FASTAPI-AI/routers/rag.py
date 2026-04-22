# routers/rag.py
# ===========================================================
# RAG 검색 API (pgvector + OpenAI 조합)
# - 문서(content)는 이미 /documents API에서 저장 + 임베딩 생성됨
# - 이 파일은 "질문 → pgvector 유사도 검색 → OpenAI에게 답변 생성" 만 담당
# - main.py 에서 app.include_router(rag.router) 로 연결해서 사용
# ===========================================================

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import text
from db import get_db
from dotenv import load_dotenv
from pydantic import BaseModel
import httpx
import os
import json

# .env 파일 읽어서 환경변수로 등록 (.env에 OPENAI_API_KEY, OPENAI_BASE_URL 등)
load_dotenv()

# 환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")           # OpenAI 비밀 키
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")         # openai_URL
OPENAI_MODEL_ID = os.getenv("OPENAI_MODEL_ID")         # 챗 모델 ID
EMBED_MODEL_ID = "text-embedding-3-small"              # 임베딩 모델 ID

# 이 파일에서 사용할 FastAPI 라우터 객체
router = APIRouter(
    prefix="/rag",
    tags=["rag"],
)

# -----------------------------
# 요청 바디 스키마 (JSON Body)
# -----------------------------
class RagRequest(BaseModel):
    question: str


# -----------------------------------------------------------
# 질문/텍스트를 임베딩 벡터로 변환하는 함수
# -----------------------------------------------------------
async def embed_text(text: str) -> list[float]:
    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY is missing")

    url = f"{OPENAI_BASE_URL}/v1/embeddings"

    payload = {
        "model": EMBED_MODEL_ID,
        "input": text,
    }

    headers = {
        "Authorization": f"Bearer {OPENAI_API_KEY}"
    }

    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(url, json=payload, headers=headers)

        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        data = r.json()
        embedding = data["data"][0]["embedding"]
        return embedding


# -----------------------------------------------------------
# RAG 메인 엔드포인트 (JSON Body 버전)
# -----------------------------------------------------------
@router.post("/chat")
async def rag_chat(body: RagRequest, db: Session = Depends(get_db)):
    """
    RAG 검색 API: POST /rag/chat
    요청 JSON 예:
    { "question": "쿠버네티스가 뭐야?" }
    """

    question = body.question

    # 1) 질문 → 임베딩 벡터
    q_emb = await embed_text(question)

    # 2) 파이썬 리스트를 pgvector가 읽을 수 있는 문자열로 변환
    qvec_str = "[" + ",".join(str(x) for x in q_emb) + "]"

    # 3) pgvector 코사인 유사도 + distance 검색 쿼리
    sql = text("""
        SELECT id, title, description AS content, company, url, 
           (embedding <-> CAST(:qvec AS vector)) AS distance
    FROM jobs
    ORDER BY embedding <-> CAST(:qvec AS vector)
    LIMIT 3;
    """)

    rows = db.execute(sql, {"qvec": qvec_str}).mappings().all()

    THRESHOLD = 0.40

    if rows:
        best_distance = rows[0]["distance"]
        if best_distance is None or best_distance > THRESHOLD:
            rows = []

    context_parts = []
    used_docs = []

    if rows:
        for row in rows:
            context_parts.append(row["content"])
            used_docs.append(
                {
                    "id": row["id"],
                    "title": row["title"],
                }
            )
        context = "\n\n".join(context_parts)

        system_msg = (
            "당신은 제공된 문서 내용을 참고하여 답변하는 RAG AI 어시스턴트입니다. "
            "하지만 문서 내용이 부족하거나 질문과 직접 관련이 없을 경우 "
            "문서에 없다는 말을 하지 말고, 일반적인 지식과 상식으로 보완하여 반드시 답변하세요."
        )

        user_msg = (
            "아래 문서 내용은 참고용입니다. 가능하면 문서를 활용해서 답변하되, "
            "문서에 정보가 부족하면 당신의 일반 지식을 활용해 보완해서 답변해주세요.\n\n"
            f"문서 내용:\n{context}\n\n"
            f"질문: {question}"
        )

    else:
        system_msg = (
            "당신은 전문적인 IT 컨설턴트이자 개발 멘토입니다. "
            "이 질문에 대해 문서 기반 RAG 검색은 사용하지 않습니다. "
            "대신 일반적인 지식과 경험을 기반으로 최선을 다해 한국어로 답변하세요."
        )

        user_msg = f"질문: {question}"

    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY is missing")

    chat_url = f"{OPENAI_BASE_URL}/v1/chat/completions"

    payload = {
        "model": OPENAI_MODEL_ID,
        "messages": [
            {"role": "system", "content": system_msg},
            {"role": "user", "content": user_msg},
        ],
        "temperature": 0.2,
    }

    headers = {
        "Authorization": f"Bearer {OPENAI_API_KEY}"
    }

    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(chat_url, json=payload, headers=headers)

        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        data = r.json()

        try:
            # 모델이 JSON 문자열로 잘 보내준 경우 → 파싱해서 그대로 반환
            answer = json.loads(data["choices"][0]["message"]["content"])
            #print(answer)
            return answer
        except json.JSONDecodeError:
            # JSON 형식이 아니면, 그냥 content 통째로 묶어서 반환
            raw_content = data["choices"][0]["message"]["content"]
            answer = {"content": raw_content}
            #print(answer)
            return answer

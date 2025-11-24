# routers/rag.py
# ===========================================================
# RAG 검색 API (pgvector + OpenAI 조합)
# - 문서(content)는 이미 /documents API에서 저장 + 임베딩 생성됨
# - 이 파일은 "질문 → pgvector 유사도 검색 → OpenAI에게 답변 생성" 만 담당
# - main.py 에서 app.include_router(rag.router) 로 연결해서 사용
# ===========================================================

from fastapi import APIRouter, Depends, HTTPException   # FastAPI 라우터 / 예외 / 의존성
from sqlalchemy.orm import Session                      # SQLAlchemy 세션 타입
from sqlalchemy import text                             # 직접 SQL 작성용
from db import get_db                                   # DB 세션 가져오는 함수
from dotenv import load_dotenv                          # .env 로드
import httpx                                            # HTTP 비동기 통신
import os                                               # 환경변수 읽기

# .env 파일 읽어서 환경변수로 등록 (.env에 OPENAI_API_KEY, OPENAI_BASE_URL 등)
load_dotenv()

# 환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")                      # OpenAI 비밀 키
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")                    # openai_URL
OPENAI_MODEL_ID = os.getenv("OPENAI_MODEL_ID")                    # 챗 모델 ID
EMBED_MODEL_ID = "text-embedding-3-small"                         # 임베딩 모델 ID

# 이 파일에서 사용할 FastAPI 라우터 객체
router = APIRouter(
    prefix="/rag",          # 최종 경로: /rag/...
    tags=["rag"],           # Swagger에서 'rag' 그룹으로 표시
)

# -----------------------------------------------------------
# 질문/텍스트를 임베딩 벡터로 변환하는 함수
# -----------------------------------------------------------
async def embed_text(text: str) -> list[float]:
    """
    주어진 문자열(text)을 OpenAI 임베딩 API로 벡터(리스트[float])로 변환한다.
    - text: 질문 또는 문서 내용
    - return: 1536차원 float 리스트
    """
    if not OPENAI_API_KEY:
        # 키가 없으면 서버 설정 문제 → 500 에러로 반환
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY is missing")

    # OpenAI embeddings 엔드포인트 URL
    url = f"{OPENAI_BASE_URL}/v1/embeddings"

    # 요청 페이로드(본문)
    payload = {
        "model": EMBED_MODEL_ID,  # 사용할 임베딩 모델
        "input": text,            # 벡터로 만들 텍스트
    }

    # 인증 헤더
    headers = {
        "Authorization": f"Bearer {OPENAI_API_KEY}"
    }

    # 비동기 HTTP 클라이언트로 요청 보내기
    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(url, json=payload, headers=headers)

        # 상태코드 검사 (4xx, 5xx 에러인 경우 예외 발생)
        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            # OpenAI 쪽 에러 내용을 그대로 반환해주면 디버깅이 편함
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        data = r.json()
        embedding = data["data"][0]["embedding"]  # 임베딩 벡터 추출
        return embedding


# -----------------------------------------------------------
# RAG 메인 엔드포인트
# -----------------------------------------------------------
@router.post("/query")
async def rag_query(question: str, db: Session = Depends(get_db)):
    """
    RAG 검색 API: POST /rag/query
    """

    # 1) 질문 → 임베딩 벡터 (list[float])
    q_emb = await embed_text(question)

    # 2) 파이썬 리스트를 pgvector가 읽을 수 있는 문자열로 변환
    qvec_str = "[" + ",".join(str(x) for x in q_emb) + "]"

    # 3) pgvector 코사인 유사도 + distance 검색 쿼리
    sql = text("""
        SELECT id, title, content,
               (embedding <-> CAST(:qvec AS vector)) AS distance
        FROM documents
        ORDER BY embedding <-> CAST(:qvec AS vector)
        LIMIT 3;
    """)

    # 4) DB에 벡터 바인딩해서 쿼리 실행 (dict 형태로 받기)
    rows = db.execute(sql, {"qvec": qvec_str}).mappings().all()

    # 5) distance 기반 관련 문서 여부 판단
    THRESHOLD = 0.40  # 값이 클수록 "관련 없음"으로 보기 쉬움

    if rows:
        best_distance = rows[0]["distance"]
        # 관련성이 낮으면 문서가 없는 것으로 처리
        if best_distance > THRESHOLD:
            rows = []

    # 6) 검색된 문서들을 하나의 문자열(context)로 합치기
    context_parts = []
    used_docs = []

    if rows:
        # ---- RAG 모드 ----
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
        # ---- 문서 없음: 일반 모델 fallback 모드 ----
        system_msg = (
            "당신은 전문적인 IT 컨설턴트이자 개발 멘토입니다. "
            "이 질문에 대해 문서 기반 RAG 검색은 사용하지 않습니다. "
            "대신 일반적인 지식과 경험을 기반으로 최선을 다해 한국어로 답변하세요."
        )

        user_msg = f"질문: {question}"

    # 7) OpenAI Chat API에 프롬프트로 요청
    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY is missing")

    chat_url = f"{OPENAI_BASE_URL}/v1/chat/completions"

    payload = {
        "model": OPENAI_MODEL_ID,   # 사용할 챗 모델
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
        choice = data["choices"][0]
        content = choice["message"]["content"]

        return {
            "answer": content
            # "used_docs": used_docs
        }

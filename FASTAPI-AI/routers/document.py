# routers/document.py
# -------------------------------------------------------
# /documents API
# - POST /documents : 문서 저장 + 임베딩 생성 → pgvector 컬럼에 저장
# - GET  /documents : 최근 문서 목록 조회
# -------------------------------------------------------

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db import get_db
from models.document import Document
from schemas.document import DocumentCreate

from dotenv import load_dotenv
import httpx
import os

# .env 로드
load_dotenv()

router = APIRouter(prefix="/documents", tags=["documents"])

# 환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")
EMBED_MODEL_ID = "text-embedding-3-small"   # 임베딩 모델

# -------------------------------------------------------
# content → 임베딩 벡터 생성 함수 (httpx 사용)
# -------------------------------------------------------
async def get_embedding(text: str) -> list[float]:
    """
    문서 내용을 OpenAI 임베딩 API에 보내서
    1536차원 벡터(list[float])로 변환한다.
    """
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

        # 4xx/5xx면 자세한 메시지 그대로 띄워주기
        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            # OpenAI에서 온 에러 내용을 그대로 반환해주면 디버깅하기 좋음
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        data = r.json()
        embedding = data["data"][0]["embedding"]
        return embedding


# -------------------------------------------------------
# POST /documents  : 문서 저장 + 임베딩 생성
# -------------------------------------------------------
@router.post("/")
async def create_document(doc: DocumentCreate, db: Session = Depends(get_db)):
    """
    문서 생성 API
    - 클라이언트: title, content 만 보냄
    - 서버: content로 임베딩 생성 → pgvector 컬럼에 저장
    """
    # 1) content → 임베딩 벡터
    emb = await get_embedding(doc.content)

    # 2) DB row 생성
    db_doc = Document(
        title=doc.title,
        content=doc.content,
        embedding=emb,   # pgvector Vector(1536)에 그대로 들어감
    )

    db.add(db_doc)
    db.commit()
    db.refresh(db_doc)

    return {"id": db_doc.id}


# -------------------------------------------------------
# GET /documents  : 최근 문서 목록 조회
# -------------------------------------------------------
@router.get("/")
async def list_documents(db: Session = Depends(get_db)):
    """
    DB에 저장된 문서들을 최대 20개까지 조회한다.
    embedding 값은 응답에 포함하지 않는다.
    """
    docs = db.query(Document).limit(20).all()

    return [
        {
            "id": d.id,
            "title": d.title,
            "content": d.content,
        }
        for d in docs
    ]

# 문서 하나 삭제
@router.delete("/{doc_id}")
async def delete_document(doc_id: int, db: Session = Depends(get_db)):
    doc = db.query(Document).filter(Document.id == doc_id).first()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    db.delete(doc)
    db.commit()
    return {"detail": f"document {doc_id} deleted"}


# 모든 문서 삭제 (조심!!)
@router.delete("/")
async def delete_all_documents(db: Session = Depends(get_db)):
    db.query(Document).delete()
    db.commit()
    return {"detail": "all documents deleted"}


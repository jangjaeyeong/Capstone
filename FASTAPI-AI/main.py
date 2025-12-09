from fastapi import FastAPI, HTTPException      # FastAPI 서버 생성용 모듈

from schemas.ai import ChatReq
from routers import document, health, ai_chat, rag, document_loader, debug_db, test_db, admin_jobs, jobs

from config import OPENAI_API_KEY, OPENAI_BASE_URL, MODEL_ID

from models.job import Job

# FastAPI 앱 생성
app = FastAPI(
    title="Capstone RAG API",
    description="LangChain + RAG 기반 개발자 스택 추천 API",
    version="1.0.0"
)
# 브라우저에서 http://localhost:8000/docs 확인 가능

app.include_router(health.router) #routers/health.py

# API 엔드포인트 (핵심 기능)
app.include_router(ai_chat.router)

# # 문서 + 임베딩 저장
app.include_router(document.router)

app.include_router(document_loader.router)

app.include_router(rag.router)

app.include_router(debug_db.router)

app.include_router(test_db.router)

app.include_router(admin_jobs.router)

app.include_router(jobs.router)
# -*- coding: cp949 -*-
import sys
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")

from fastapi import FastAPI, HTTPException   # FastAPI 서버 생성용 모듈
from pydantic import BaseModel               # 요청 데이터 구조 정의용
import os, httpx                             # os: 환경변수 / httpx: HTTP 비동기 통신
from dotenv import load_dotenv               # .env 파일을 로드하기 위한 모듈

#env 파일에 적힌 환경변수 불러오기
# (.env 파일 안에는 OPENAI_API_KEY, MODEL_ID 등이 저장되어 있음)
load_dotenv()

#환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")          # OpenAI 비밀 키 (꼭 있어야 함)
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL") # 기본 API URL
MODEL_ID = os.getenv("OPENAI_MODEL_ID")            # 사용할 모델 (파인튜닝 시 교체)

#FastAPI 앱 생성
app = FastAPI(title="FastAPI")  
#브라우저에서 http://localhost:8000/docs 확인 가능

#요청/응답 모델 정의

#사용자 메시지 1개 단위 구조
class Message(BaseModel):
    role: str | None = "user"   # role 없으면 자동으로 user
    content: str # 실제 메시지 내용 (텍스트)

#요청 바디 전체 구조
class ChatReq(BaseModel):
    messages: list[Message]    # 여러 개의 메시지를 리스트 형태로 전달
    stream: bool | None = False     # 스트리밍 여부 (지금은 False로만 사용)
    temperature: float | None = 0.2   # 창의성 조절값 (0.2 ~ 1.0 사이)

@app.get("/health")
async def health():
    return {"status": "ok"}

# API 엔드포인트 (핵심 부분)
@app.post("/ai/chat")
async def chat(req: ChatReq):
    #키가 없을 경우 (환경설정 오류)
    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY missing")

    #OpenAI API로 보낼 요청 데이터(payload)
    payload = {
        "model": MODEL_ID,          # 사용할 모델 ID
        "messages": [m.model_dump() for m in req.messages],  # Message 객체 → dict로 변환
        "temperature": req.temperature or 0.2,         # 창의성 정도 -> 일관성 있는 답변
        "stream": False         # 스트리밍 비활성
    }

    #요청 헤더 (인증키 포함)
    headers = {"Authorization": f"Bearer {OPENAI_API_KEY}"}

    #OpenAI API 호출 (비동기 방식)
    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(
            f"{OPENAI_BASE_URL}/v1/chat/completions",  # OpenAI Chat API 엔드포인트
            json=payload,
            headers=headers
        )

        #응답이 실패일 경우 예외 발생
        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        #정상 응답 처리
        data = r.json()                  # JSON 응답 전체 파싱
        choice = data["choices"][0]          # 첫 번째 응답(choice) 선택
        usage = data.get("usage", {})        # 사용된 토큰 정보 (없으면 기본값)

        #필요한 데이터만 추출해 반환
        return {
            "id": data.get("id"),                              # 요청 ID
            "model": data.get("model"),                      # 사용된 모델명
            "content": choice["message"]["content"],         # AI의 답변 텍스트
            "finish_reason": choice.get("finish_reason"),    # 응답 종료 이유
            "usage": {                                       # 토큰 사용량
                "input_tokens": usage.get("prompt_tokens", 0),
                "output_tokens": usage.get("completion_tokens", 0)
            }
        }
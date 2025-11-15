# FastAPI AI Server

OpenAI API와 연동된 FastAPI 기반 AI 서버입니다.  
클라이언트는 content(메시지)만 보내면 되고, 서버가 자동으로 role="user"로 처리합니다.

--------------------------------------------

## 프로젝트 구성

main.py  
requirements.txt  
.env.example  
.gitignore  
README.md

--------------------------------------------

## 환경 설정

### 1) .env 파일 생성

.env.example을 참고하여 실제 값을 넣어 .env 파일을 생성합니다.

OPENAI_API_KEY=YOUR_API_KEY  
OPENAI_BASE_URL=https://api.openai.com  
OPENAI_MODEL_ID=gpt-3.5-turbo-0125

CHROMA_DIR=./chroma
TOP_K=4

--------------------------------------------

## 패키지 설치

pip install -r requirements.txt

--------------------------------------------

## 서버 실행

uvicorn main:app --reload --port 8000

--------------------------------------------

## API 문서

Swagger UI:  
http://localhost:8000/docs

--------------------------------------------

## Health Check

GET /health

--------------------------------------------

## AI Chat 요청

POST /ai/chat  
Content-Type: application/json

요청 예시:
{
  "messages": [
    { "content": "AI 개발자 되려면 무엇을 공부해야 하나?" }
  ],
  "temperature": 0.2
}

응답 예시:
{
  "content": "AI 개발자가 되기 위해서는..."
}

--------------------------------------------

## 주의사항

- .env 파일은 GitHub에 업로드하지 않습니다.
- .env.example 파일만 공유합니다.
# PROVI

> **AI 기반 개발자 직무 및 기술 스택 추천 플랫폼**

PROVI는 사용자의 관심 분야와 설문 결과를 분석하고, 실제 개발자 채용공고 데이터를 활용하여 **개인에게 적합한 개발 직무와 기술 스택을 추천**하는 AI 기반 커리어 지원 서비스입니다.

기술 스택 추천뿐만 아니라 채용공고 추천, AI 기반 면접 피드백, 개발자 커뮤니티, 팀 프로젝트 모집 및 실시간 채팅 등의 기능을 제공합니다.

---

## 📌 프로젝트 소개

개발자 취업을 준비하면서 가장 어려운 문제 중 하나는 자신의 성향과 목표에 맞는 **개발 직무와 기술 스택을 선택하는 것**입니다.

PROVI는 이러한 문제를 해결하기 위해 사용자의 설문 결과와 실제 채용공고에서 수집한 기술 스택 데이터를 결합하고, OpenAI API를 활용하여 개인에게 적합한 직무·기술 스택·학습 로드맵을 추천합니다.

또한 추천 이후 실제 채용공고를 확인하고, 면접 질문에 답변해 AI 피드백을 받을 수 있도록 하나의 서비스 안에서 취업 준비 과정을 지원합니다.

---

## ✨ 주요 기능

### 🤖 AI 직무 및 기술 스택 추천

사용자가 작성한 설문 결과를 분석하여 적합한 개발 직군과 기술 스택을 추천합니다.

- 설문 기반 개발 직군 분석
- 직군별 기술 스택 추천
- 기술 스택별 추천 이유 제공
- 단계별 학습 로드맵 제공
- DB에 등록된 직군 및 기술 스택 데이터를 기준으로 추천 결과 검증

### 💼 채용공고 수집 및 AI 추천

채용공고를 수집하여 직군, 경력, 고용 형태, 기술 스택 등의 정보를 추출하고 DB에 저장합니다.

저장된 채용공고를 사용자의 설문 결과와 비교하여 적합한 채용공고를 AI가 추천합니다.

- 채용공고 크롤링
- 직군 자동 분류
- 경력 수준 분류
- 고용 형태 분류
- 기술 스택 키워드 추출
- 사용자 설문 기반 채용공고 추천
- 추천 이유 및 관련 기술 스택 제공

### 🔎 RAG 기반 AI 질의응답

채용공고 데이터의 임베딩을 PostgreSQL의 **pgvector**에 저장하고, 사용자의 질문과 유사한 데이터를 검색하여 AI 답변 생성에 활용합니다.

```text
사용자 질문
    ↓
OpenAI Embedding
    ↓
pgvector 유사도 검색
    ↓
관련 채용공고 검색
    ↓
검색 결과 + 질문
    ↓
OpenAI API
    ↓
AI 답변
```

### 🎤 AI 면접 피드백

사용자가 면접 질문에 대한 답변을 작성하면 AI가 답변을 분석하여 피드백을 제공합니다.

- 답변 총평
- 강점
- 개선할 점
- 답변 작성 팁

### 👥 팀 프로젝트 모집

개발자들이 함께 프로젝트를 진행할 수 있도록 팀 프로젝트 모집 및 참여 기능을 제공합니다.

- 프로젝트 생성 및 수정
- 프로젝트 삭제
- 프로젝트 목록 및 상세 조회
- 프로젝트 참여
- 프로젝트 탈퇴
- 프로젝트별 모집 인원 및 역할 관리
- 기술 스택 태그 관리

### 💬 실시간 팀 프로젝트 채팅

WebSocket과 STOMP를 이용하여 프로젝트 참여자 간 실시간 채팅을 구현했습니다.

- 실시간 메시지 전송
- 프로젝트별 채팅방
- 메시지 읽음 상태 관리
- 채팅방 요약 및 안 읽은 메시지 관리
- 멘션 기능
- 파일 공유

### 📝 개발자 커뮤니티

개발자 간 정보 공유를 위한 커뮤니티 기능을 제공합니다.

- 게시글 작성 / 조회 / 수정 / 삭제
- 댓글
- 게시글 좋아요
- 사용자 활동 조회
- 이미지 및 파일 첨부

### 🔐 회원 및 인증

Spring Security를 기반으로 사용자 인증 및 권한 관리를 구현했습니다.

- 회원가입
- 로그인
- JWT 기반 인증
- 이메일 인증
- 비밀번호 암호화
- OAuth2 로그인
- 사용자 권한 관리

OAuth2 로그인은 **Kakao / Naver** 연동 구조를 사용합니다.

---

## 🏗️ 시스템 아키텍처

```text
                         ┌─────────────────┐
                         │      사용자      │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │    Frontend     │
                         │ HTML/CSS/JS     │
                         └────────┬────────┘
                                  │
                         REST API / WebSocket
                                  │
                                  ▼
                    ┌────────────────────────┐
                    │     Spring Boot        │
                    │       Backend          │
                    │                        │
                    │ 회원 / 인증 / 커뮤니티 │
                    │ 팀 프로젝트 / 채팅     │
                    │ AI API 중계            │
                    └───────┬────────┬───────┘
                            │        │
                       REST API      │ JPA
                            │        │
                            ▼        ▼
                   ┌────────────┐ ┌──────────────┐
                   │  FastAPI   │ │  PostgreSQL  │
                   │ AI Server  │ │   Neon DB    │
                   └─────┬──────┘ │  + pgvector  │
                         │         └──────────────┘
                         ▼
                   ┌──────────────┐
                   │  OpenAI API  │
                   │              │
                   │ Chat /       │
                   │ Embedding    │
                   └──────────────┘

                   ┌──────────────┐
                   │    AWS S3    │
                   │ 이미지/파일   │
                   └──────────────┘
```

Spring Boot는 서비스의 주요 비즈니스 로직과 인증, 커뮤니티, 팀 프로젝트, 채팅 등을 담당하며, AI가 필요한 요청은 FastAPI AI 서버로 전달합니다.

---

## 🔄 AI 추천 처리 과정

```text
[사용자 설문]
      ↓
[Spring Boot]
      ↓
[FastAPI AI Server]
      ↓
[DB 채용공고 데이터 조회]
      ↓
[직군 및 기술 스택 분석]
      ↓
[OpenAI API]
      ↓
[직군 + 기술 스택 + 추천 이유 + 로드맵]
      ↓
[Frontend]
```

Spring Boot에서는 DB에 저장된 직군과 기술 스택 정보를 함께 전달하여 AI가 서비스에서 관리하는 데이터 범위 내에서 추천하도록 구성했습니다.

---

## 🗄️ 채용공고 데이터 처리

```text
채용공고
   ↓
Selenium / BeautifulSoup
   ↓
채용공고 데이터 추출
   ↓
FastAPI 적재 API
   ↓
직군 / 경력 / 고용형태 / 기술 스택 추출
   ↓
PostgreSQL
```

채용공고 본문을 기준으로 개발 직군과 기술 스택을 키워드 기반으로 분류하고, 저장된 데이터는 AI 추천 및 RAG 검색에 활용합니다.

---

## 🔎 RAG 처리 과정

```text
채용공고 데이터
      ↓
OpenAI Embedding
      ↓
1536차원 Vector
      ↓
PostgreSQL + pgvector
      ↓
      │
사용자 질문
      ↓
OpenAI Embedding
      ↓
Vector Similarity Search
      ↓
관련 데이터 TOP 3
      ↓
OpenAI Chat Completion
      ↓
AI 답변
```

임베딩 모델은 `text-embedding-3-small`을 사용하며, 질문과 채용공고 데이터의 벡터 유사도를 기준으로 관련 데이터를 검색합니다.

---

## ☁️ 파일 저장

커뮤니티 및 팀 프로젝트 채팅에서 사용하는 파일은 **AWS S3**에 저장합니다.

```text
Frontend
    ↓
Spring Boot
    ↓
Presigned URL 발급
    ↓
AWS S3
```

Presigned URL을 사용하여 파일 업로드를 처리하고, 서버에서 파일 자체를 직접 전달하는 부담을 줄였습니다.

---

## 🛠️ 기술 스택

### Frontend

| 기술 | 용도 |
|---|---|
| HTML5 | 웹 페이지 구조 |
| CSS3 | UI 및 스타일링 |
| JavaScript | 화면 동작 및 API 연동 |
| Fetch API | Backend REST API 통신 |
| WebSocket / STOMP | 실시간 채팅 |

### Backend

| 기술 | 용도 |
|---|---|
| Java 17 | Backend 개발 |
| Spring Boot 3.5.3 | 서버 애플리케이션 |
| Spring MVC | REST API |
| Spring Data JPA | ORM 및 DB 접근 |
| Spring Security | 인증 및 권한 관리 |
| JWT | 로그인 인증 |
| OAuth2 Client | Kakao / Naver 로그인 |
| Spring Mail | 이메일 인증 |
| WebSocket / STOMP | 실시간 채팅 |
| Thymeleaf | Spring 웹 지원 |
| Gradle | 프로젝트 빌드 |

### AI Server

| 기술 | 용도 |
|---|---|
| Python | AI 서버 개발 |
| FastAPI | AI API 서버 |
| SQLAlchemy | DB 접근 |
| OpenAI API | AI 추천 및 답변 생성 |
| text-embedding-3-small | 텍스트 임베딩 |
| pgvector | 벡터 유사도 검색 |
| Selenium | 채용공고 크롤링 |
| BeautifulSoup | HTML 파싱 |

### Database / Storage

| 기술 | 용도 |
|---|---|
| PostgreSQL | 데이터베이스 |
| Neon | PostgreSQL 호스팅 |
| pgvector | 벡터 저장 및 검색 |
| AWS S3 | 이미지 및 파일 저장 |

---

## 📂 프로젝트 구조

### Frontend

```text
Capstone-frontend/
├── HTML/
│   ├── mainpage.html
│   ├── signin.html
│   ├── quiz.html
│   ├── result.html
│   ├── interview.html
│   ├── community.html
│   ├── teamproject.html
│   ├── chat.html
│   └── mypage.html
├── CSS/
│   ├── mainpage.css
│   ├── quiz.css
│   ├── result.css
│   ├── interview.css
│   ├── community.css
│   ├── teamproject.css
│   └── chat.css
└── JAVASCRIPT/
    ├── api.js
    ├── quiz1.js
    ├── result.js
    ├── interview.js
    ├── community.js
    ├── teamproject.js
    ├── chat.js
    └── signin.js
```

### Spring Boot Backend

```text
CapstoneProject/
└── src/main/java/com/capstone/CapstoneProject/
    ├── AI/
    ├── Member/
    │   └── Login/
    │       ├── JWT/
    │       ├── OAuth2/
    │       └── Email/
    ├── Community/
    │   ├── Board/
    │   └── TeamProject/
    ├── WebsocketChat/
    ├── MainController.java
    ├── SecurityConfig.java
    └── WebConfig.java
```

### FastAPI AI Server

```text
FASTAPI-AI/
├── crawler/
│   └── jobkorea_scraper.py
├── docs/
├── models/
│   ├── embedding.py
│   ├── job.py
│   └── document.py
├── routers/
│   ├── ai_chat.py
│   ├── jobs.py
│   ├── rag.py
│   ├── document.py
│   ├── document_loader.py
│   ├── admin_jobs.py
│   └── health.py
├── schemas/
├── config.py
├── db.py
└── main.py
```

---

## 🔌 주요 API

### Spring Boot

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/email/send-code` | 이메일 인증번호 발송 |
| POST | `/api/auth/email/verify-code` | 이메일 인증번호 확인 |
| POST | `/api/result` | AI 직무/스택 추천 요청 |
| POST | `/api/chat` | AI 채팅 요청 |
| GET | `/api/interview/questions` | 면접 질문 조회 |
| POST | `/api/interview/feedback` | AI 면접 피드백 |
| GET/POST/PATCH/DELETE | `/api/community/posts` | 커뮤니티 게시글 관리 |
| POST | `/api/teamproject` | 팀 프로젝트 생성 |
| GET | `/api/projects/{id}` | 프로젝트 상세 조회 |
| POST | `/api/teamproject/join` | 프로젝트 참여 |
| DELETE | `/api/teamproject/leave` | 프로젝트 탈퇴 |
| GET | `/api/teamproject/{projectId}/chat/messages` | 채팅 메시지 조회 |
| PATCH | `/api/teamproject/{projectId}/chat/read` | 채팅 읽음 처리 |

### FastAPI

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/health` | 서버 상태 확인 |
| POST | `/ai/chat` | 설문 기반 AI 스택 추천 |
| POST | `/ai/feedback` | AI 면접 답변 피드백 |
| GET | `/jobs/` | 채용공고 조회 |
| POST | `/jobs/recommend/ai` | AI 채용공고 추천 |
| POST | `/rag/chat` | RAG 기반 AI 질의응답 |
| POST | `/documents/` | 문서 저장 및 임베딩 생성 |
| POST | `/admin/jobs/ingest` | 채용공고 DB 적재 |

---

## 🚀 실행 방법

### 1. Spring Boot Backend

Java 17 환경이 필요합니다.

```bash
cd CapstoneProject
./gradlew bootRun
```

Windows 환경에서는:

```bash
gradlew.bat bootRun
```

### 2. FastAPI AI Server

Python 환경에서 필요한 패키지를 설치합니다.

```bash
cd FASTAPI-AI
pip install -r requirements.txt
```

서버 실행:

```bash
uvicorn main:app --reload
```

FastAPI Swagger 문서:

```text
http://localhost:8000/docs
```

### 3. Frontend

`HTML/mainpage.html`을 시작 페이지로 사용하며, 정적 웹 서버 또는 Nginx 등을 통해 제공합니다.

---

## 🔐 환경 변수

API Key 및 DB 접속 정보와 같은 민감한 정보는 환경변수로 관리합니다.

```env
OPENAI_API_KEY=your_openai_api_key
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL_ID=your_model_id
EMBED_MODEL_ID=text-embedding-3-small
DATABASE_URL=your_database_url
```

> 실제 API Key, JWT Secret, DB 비밀번호 등의 민감한 정보는 GitHub에 업로드하지 않습니다.

---

## 🎯 주요 구현 기술

- Spring Boot 기반 REST API 설계 및 구현
- Spring Data JPA를 이용한 데이터 처리
- Spring Security + JWT 인증/인가
- OAuth2 기반 소셜 로그인
- 이메일 인증
- WebSocket / STOMP 기반 실시간 채팅
- AWS S3 Presigned URL 기반 파일 업로드
- FastAPI 기반 AI 서버 분리
- OpenAI API 연동
- OpenAI Embedding 기반 벡터화
- PostgreSQL + pgvector 기반 RAG 검색
- 채용공고 크롤링 및 데이터 정제
- 채용공고 기반 AI 직무/기술 스택 추천
- AI 기반 면접 답변 피드백

---

## 👥 팀 구성

| 이름 | 역할 | 주요 담당 |
|---|---|---|
| **장재영** | Backend / AI | Spring Boot 기반 Backend 개발, REST API 및 DB 연동, 인증/인가, 팀 프로젝트 및 실시간 채팅 기능 구현, 기존 AI 서버 인수 및 기능 보완·수정, Backend-AI 서버 연동 |
| **정겨운** | Frontend | HTML, CSS, JavaScript 기반 UI 구현, 페이지 구성, Backend API 연동, AI 추천/면접/커뮤니티/팀 프로젝트 화면 구현 |

---

## 📅 프로젝트 정보

- **프로젝트명:** PROVI
- **프로젝트 유형:** 졸업작품 / 캡스톤 프로젝트
- **팀 구성:** 2인
- **목표:** AI와 실제 채용시장 데이터를 활용한 개발자 커리어 및 기술 스택 추천 서비스

---

## 📜 License

This project was developed as a university graduation project.

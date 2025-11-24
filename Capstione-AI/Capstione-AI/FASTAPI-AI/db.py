# db.py
# ---------------------------------------
# 역할:
# - PostgreSQL(DB)와 연결해주는 설정 파일
# - SQLAlchemy 엔진, 세션(SessionLocal), Base(모델 부모 클래스)를 만든다.
# - FastAPI에서 DB를 쓸 때 Depends(get_db)로 세션을 받아서 사용한다.
# ---------------------------------------

import os                          # 환경변수(.env)에서 값 읽어오기 위함
from sqlalchemy import create_engine         # DB 연결 엔진 생성 함수
from sqlalchemy.orm import sessionmaker, declarative_base  # 세션, Base 클래스 생성용
from dotenv import load_dotenv               # .env 파일을 로드하기 위한 모듈

# 1) .env 파일 불러오기
#    - .env 안에 DATABASE_URL 이라는 환경변수를 정의해두고,
#      그 값을 여기서 읽어서 DB 접속 정보로 사용한다.
load_dotenv()

# 2) .env 에서 DATABASE_URL 읽어오기
#    - 예: postgresql+psycopg2://아이디:비번@localhost:5432/DB이름
#    - 실제 .env 예:
#        DATABASE_URL=postgresql+psycopg2://rag_user:rag_password@localhost:5432/rag_db
DATABASE_URL = os.getenv("DATABASE_URL")

# DATABASE_URL 이 비어있으면 바로 에러를 내서 문제를 빨리 발견하도록 함
if not DATABASE_URL:
    # RuntimeError 를 발생시키면 서버 시작할 때 바로 에러가 보여서 디버깅하기 좋음
    raise RuntimeError("DATABASE_URL이 설정되지 않았습니다. .env 파일을 확인하세요.")

# 3) SQLAlchemy 엔진 생성
#    - 실제로 이 엔진을 통해 Python 코드 ↔ PostgreSQL 간 통신이 이루어짐
#    - echo=True 로 하면 실행되는 SQL이 콘솔에 출력됨 (디버깅용)
engine = create_engine(
    DATABASE_URL,
    echo=False,
    pool_pre_ping=True,  # 끊어진 커넥션이면 자동으로 다시 연결
)


# 4) DB 세션 팩토리 생성
#    - SessionLocal() 을 호출하면 실제 DB 세션(연결)이 하나 만들어짐
#    - autocommit=False: 직접 commit() 해줘야 DB에 반영됨
#    - autoflush=False: commit 하기 전에는 flush 자동으로 안 함 (명시적 제어를 위해)
SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

# 5) Base 클래스 생성
#    - 나중에 document.py 같은 파일에서
#        class Document(Base):
#      이런 식으로 모델을 만들 때 상속받는 부모 클래스
Base = declarative_base()


def get_db():
    """
    FastAPI 의 Depends 에서 사용할 DB 세션 제공 함수

    사용 예:
        from fastapi import Depends

        @app.get("/items")
        def read_items(db: Session = Depends(get_db)):
            # 여기서 db 를 사용해서 쿼리 실행
            items = db.query(Item).all()
            return items

    동작 원리:
    - 요청이 들어오면 SessionLocal()로 DB 세션(연결)을 하나 생성하고
    - yield 로 그 세션을 밖으로 넘겨서 엔드포인트 함수가 사용하게 하고
    - 함수가 끝나면 finally 블록에서 세션을 항상 닫아준다.
    """
    db = SessionLocal()   # DB 세션 생성
    try:
        yield db          # 이 세션을 엔드포인트 함수에 넘겨줌
    finally:
        db.close()        # 요청 처리 끝나면 세션 닫기 (커넥션 누수 방지)

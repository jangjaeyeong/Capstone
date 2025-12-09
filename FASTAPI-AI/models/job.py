# models/job.py
from sqlalchemy import Column, Integer, Text, DateTime
from sqlalchemy.sql import func
from db import Base   # 너가 FastAPI에서 사용하는 Base (declarative_base)

class Job(Base):
    __tablename__ = "jobs"

    id = Column(Integer, primary_key=True, index=True)

    # ===== 채용공고 기본 정보 =====
    title = Column(Text, nullable=True)        # 공고 제목
    company = Column(Text, nullable=True)      # 회사명
    location = Column(Text, nullable=True)     # 근무지
    url = Column(Text, nullable=True)          # 공고 링크
    description = Column(Text, nullable=True)  # 공고 본문 (원문 저장할 경우만, 지금은 optional)

    # ===== 분석/추출된 메타데이터 =====
    job_category = Column(Text, nullable=True)     # 개발 직군 (백엔드/프론트/AI/DevOps...)
    career_level = Column(Text, nullable=True)     # 신입/경력/무관
    employment_type = Column(Text, nullable=True)  # 정규직/계약직/인턴
    tech_stacks = Column(Text, nullable=True)      # "Java, Spring, MySQL, AWS" 형태 문자열

    # ===== 생성 시각 =====
    created_at = Column(DateTime(timezone=True), server_default=func.now())

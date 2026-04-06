# routers/admin_jobs.py
# =====================================================
# 크롤러에서 온 채용공고 데이터를 DB jobs 테이블에 저장하는 API
#  - POST /admin/jobs/ingest
#  - description/title을 기반으로
#    job_category / career_level / employment_type / tech_stacks
#    를 "키워드 기반"으로 자동 추출하여 저장
# =====================================================

from fastapi import APIRouter, Depends
from pydantic import BaseModel
from typing import Optional, List
from sqlalchemy.orm import Session

from db import get_db
from models.job import Job   # jobs 테이블에 매핑된 SQLAlchemy 모델

router = APIRouter(
    prefix="/admin/jobs",
    tags=["admin-jobs"],
)

# -----------------------------------------------------
# 1) 크롤러가 보내는 요청 스키마
#    (jobkorea_scraper.py 의 payload와 1:1 매칭)
# -----------------------------------------------------
class JobIngestReq(BaseModel):
    url: str
    title: str
    company: Optional[str] = None
    location: Optional[str] = None
    description: str


# -----------------------------------------------------
# 2) tech_stacks 추출용 키워드 & 함수
# -----------------------------------------------------
TECH_KEYWORDS: List[str] = [
    # 언어
    "Java", "JavaScript", "TypeScript", "Python", "Go", "C", "C++", "C#",
    "Kotlin", "Swift", "Ruby", "PHP", "Rust",

    # 백엔드 / 서버
    "Spring", "Spring Boot", "Django", "Flask", "FastAPI",
    "Node.js", "Express", "NestJS", "Ruby on Rails", "Laravel", "ASP.NET", "Go", 

    # 프론트엔드
    "React", "React.js", "Next.js", "Vue", "Vue.js", "Nuxt", "Svelte", "Angular", "Tailwind CSS", "TypeScript",
    "TSS", "CSS", "HTML",

    # DB / 검색
    "MySQL", "PostgreSQL", "MariaDB", "Oracle", "MongoDB",
    "Redis", "Elasticsearch", "Solr","InfluxDB", "TimescaleDB",

    # 클라우드 / DevOps
    "AWS", "GCP", "Azure",
    "Docker", "Kubernetes", "K8s",
    "Jenkins", "GitLab CI", "GitHub Actions", "Git",
    "Terraform", "Ansible", "Git", "CI/CD", "ArgoCD","Prometheus", "Grafana","Datadog","New Relic","Splunk",

    # OS / 기타
    "Linux", "Unix", "REST API", "GraphQL", "gRPC", "WebSocket",
    "Machine Learning", "Deep Learning", "AI", "ML", "TensorFlow",
]


def extract_tech_stacks(text: str) -> Optional[str]:
    """본문에서 TECH_KEYWORDS 목록에 있는 기술명을 찾아 문자열로 반환"""
    if not text:
        return None

    lower = text.lower()
    found: List[str] = []

    for kw in TECH_KEYWORDS:
        if kw.lower() in lower:
            found.append(kw)

    unique = sorted(set(found))
    return ", ".join(unique) if unique else None


# -----------------------------------------------------
# 3) 직군(job_category) 추출용 규칙
# -----------------------------------------------------
JOB_CATEGORY_RULES = [
    ("백엔드", ["백엔드", "서버 개발", "server", "spring", "jsp"]),
    ("프론트엔드", ["프론트엔드", "front-end", "frontend", "react", "vue", "next.js"]),
    ("모바일", ["모바일", "android", "ios", "안드로이드", "아이폰"]),
    ("데브옵스", ["데브옵스", "devops", "ci/cd", "인프라", "kubernetes", "k8s", "docker"]),
    ("AI", ["머신러닝", "딥러닝", "ai", "ml", "데이터 사이언티스트", "computer vision", "nlp"]),
    ("보안", ["보안", "security", "침해사고", "모의해킹"]),
    ("게임", ["게임", "unity", "unreal", "언리얼"]),
    ("임베디드", ["임베디드", "펌웨어", "embedded"]),
]


def extract_job_category(text: str) -> Optional[str]:
    """제목/본문에서 직군을 추정 (단순 키워드 매칭)"""
    if not text:
        return None

    lower = text.lower()

    for category, keywords in JOB_CATEGORY_RULES:
        for kw in keywords:
            if kw.lower() in lower:
                return category

    # 아무것도 매칭 안 되면 None (나중에 '기타'로 처리해도 됨)
    return None


# -----------------------------------------------------
# 4) 경력(career_level) 추출용 규칙
# -----------------------------------------------------
def extract_career_level(text: str) -> Optional[str]:
    """
    본문에서 신입/경력/무관을 단순 규칙으로 추정.
    - '경력 무관', '신입/경력' 등이 있으면 '무관'
    - '신입'만 있으면 '신입'
    - '경력'만 있으면 '경력'
    """
    if not text:
        return None

    lower = text.lower()

    # 1) '무관' 패턴 먼저 체크
    if "경력 무관" in lower or "신입/경력" in lower or "신입 및 경력" in lower:
        return "무관"

    # 2) 신입만 등장하는 경우
    has_junior = ("신입" in lower) or ("new grad" in lower) or ("junior" in lower)
    has_career = ("경력" in lower) or ("경험자" in lower)

    if has_junior and not has_career:
        return "신입"

    # 3) 경력만 강조된 경우
    if has_career and not has_junior:
        return "경력"

    # 4) 둘 다 애매하면 None
    return None


# -----------------------------------------------------
# 5) 고용형태(employment_type) 추출용 규칙
# -----------------------------------------------------
def extract_employment_type(text: str) -> Optional[str]:
    """
    본문에서 정규직/계약직/인턴/파견/프리랜서 등을 간단히 추출.
    """
    if not text:
        return None

    lower = text.lower()

    if "정규직" in lower:
        return "정규직"
    if "계약직" in lower:
        return "계약직"
    if "인턴" in lower:
        return "인턴"
    if "파견" in lower:
        return "파견"
    if "프리랜서" in lower or "freelancer" in lower:
        return "프리랜서"

    # 특별히 안 적혀있으면 None
    return None


# -----------------------------------------------------
# 6) /admin/jobs/ingest 엔드포인트
# -----------------------------------------------------
@router.post("/ingest")
def ingest_job(req: JobIngestReq, db: Session = Depends(get_db)):
    """
    크롤러(jobkorea_scraper.py)에서 전송한
    url / title / company / location / description 을 jobs 테이블에 저장한다.

    이때 title + description을 기반으로
    - job_category
    - career_level
    - employment_type
    - tech_stacks
    를 "키워드 기반"으로 추출해서 함께 저장한다.
    """

    # 제목 + 본문 합쳐서 분석하면 직군 추출 정확도 조금 더 올라감
    merged_text = f"{req.title}\n{req.description}"

    job_category = extract_job_category(merged_text)
    career_level = extract_career_level(req.description)
    employment_type = extract_employment_type(req.description)
    tech_stacks = extract_tech_stacks(req.description)

    job = Job(
        title=req.title,
        company=req.company,
        location=req.location,
        url=req.url,
        description=req.description,

        job_category=job_category,
        career_level=career_level,
        employment_type=employment_type,
        tech_stacks=tech_stacks,
    )

    db.add(job)
    db.commit()
    db.refresh(job)

    return {
        "id": job.id,
        "title": job.title,
        "company": job.company,
        "job_category": job.job_category,
        "career_level": job.career_level,
        "employment_type": job.employment_type,
        "tech_stacks": job.tech_stacks,
    }

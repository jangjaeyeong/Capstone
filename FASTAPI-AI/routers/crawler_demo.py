# routers/crawler_demo.py

from fastapi import APIRouter
from crawler.test_crawler import fetch_quotes

router = APIRouter(
    prefix="/crawler",
    tags=["crawler"],
)

@router.get("/quotes")
def crawl_quotes():
    """
    test_crawler.py의 fetch_quotes()를 불러와서
    FastAPI에서 바로 JSON으로 반환하는 테스트 엔드포인트
    """
    data = fetch_quotes()
    return {
        "count": len(data),
        "results": data
    }

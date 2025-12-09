# crawler/jobkorea_scraper.py
# =====================================================
# JobKorea 직무별 목록/상세 URL을 자동으로 처리하는 스크립트
#  1) jobkorea_urls.txt 에서 URL 목록 읽기
#     - JobList?... (목록 페이지)
#     - Recruit/GI_Read/... (상세 페이지)
#  2) 목록 페이지면 → 상세 공고 URL들 추출
#  3) 각 상세 URL HTML 크롤링
#  4) 채용공고 제목/회사/근무지/본문 텍스트 추출
#  5) FastAPI 서버의 /admin/jobs/ingest 로 전송
# =====================================================

import requests
from bs4 import BeautifulSoup
from typing import List, Dict
from pathlib import Path
import time

# FastAPI ingest API 엔드포인트
INGEST_URL = "http://127.0.0.1:8000/admin/jobs/ingest"
BASE_DOMAIN = "https://www.jobkorea.co.kr"

# URL 목록 파일 (한 줄당 한 개 URL)
URLS_FILE = Path(__file__).resolve().parent / "jobkorea_urls.txt"


# -----------------------------------------------------
# 1) 공통: HTML 가져오기
# -----------------------------------------------------
def fetch_html(url: str) -> str:
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    }
    resp = requests.get(url, headers=headers, timeout=10)
    resp.raise_for_status()
    return resp.text


# -----------------------------------------------------
# 2) 상세 공고 페이지에서 메타데이터 + 본문 텍스트 추출
# -----------------------------------------------------
def extract_job_info(html: str) -> Dict[str, str]:
    soup = BeautifulSoup(html, "html.parser")

    # 제목 후보
    title_tag = soup.select_one(".tit") or soup.select_one(".recruit-title, h1")
    title = title_tag.get_text(strip=True) if title_tag else "제목 없음"

    # 회사명 후보 (JobKorea 구조에 따라 클래스명은 필요시 조정)
    company_tag = soup.select_one(".coName, .recruit-company, .company, .coName > a")
    company = company_tag.get_text(strip=True) if company_tag else None

    # 근무지(지역) 후보
    # 필요에 따라 CSS 선택자는 조정 가능
    location_tag = soup.select_one(".loc, .job-condition .list li")
    location = location_tag.get_text(strip=True) if location_tag else None

    # 상세 본문 영역 후보
    detail_area = soup.select_one("#article") or soup.select_one(".detailArea, .view_contents")
    if detail_area:
        description = detail_area.get_text(separator="\n", strip=True)
    else:
        description = title  # 최소한 제목이라도 저장

    return {
        "title": title,
        "company": company,
        "location": location,
        "description": description,
    }


# -----------------------------------------------------
# 3) FastAPI /admin/jobs/ingest 호출
# -----------------------------------------------------
def send_to_ingest(
    origin_url: str,
    title: str,
    company: str | None,
    location: str | None,
    description: str,
):
    payload = {
        "url": origin_url,
        "title": title,
        "company": company,
        "location": location,
        "description": description,
        # B안에서는 job_category / career_level / employment_type / tech_stacks
        # 는 서버 쪽에서 나중에 채우거나 NULL로 두고,
        # 추천 단계에서 AI가 사용하는 방식으로 가는 거야.
    }
    resp = requests.post(INGEST_URL, json=payload, timeout=15)
    resp.raise_for_status()
    return resp.json()


# -----------------------------------------------------
# 4) 상세 공고 URL 하나 처리
# -----------------------------------------------------
def process_single_detail_url(url: str):
    print(f"\n[상세 처리] {url}")

    try:
        html = fetch_html(url)
        info = extract_job_info(html)
        print(" - 텍스트/메타데이터 추출 완료, ingest 전송 중...")

        result = send_to_ingest(
            origin_url=url,
            title=info["title"],
            company=info["company"],
            location=info["location"],
            description=info["description"],
        )
        print(" - 성공:", result.get("title"), "-", result.get("company"))

        return {"url": url, "status": "success", "data": result}

    except Exception as e:
        print(" - 실패:", e)
        return {"url": url, "status": "error", "error": str(e)}


# -----------------------------------------------------
# 5) 목록 페이지에서 상세 공고 URL들 추출
# -----------------------------------------------------
def extract_detail_urls_from_list(html: str, max_count: int = 20) -> List[str]:
    """
    목록 페이지 HTML에서 /Recruit/GI_Read/... 형태의 상세 공고 URL들을 추출한다.
    max_count: 너무 많이 수집하지 않도록 상한을 둔다.
    """
    soup = BeautifulSoup(html, "html.parser")
    detail_urls: List[str] = []

    for a in soup.select("a"):
        href = a.get("href") or ""
        if "/Recruit/GI_Read/" in href:
            # 절대 URL/상대 URL 처리
            if href.startswith("http"):
                full_url = href
            else:
                href = href.split("?")[0]  # 쿼리 문자열 제거
                full_url = BASE_DOMAIN + href

            if full_url not in detail_urls:
                detail_urls.append(full_url)

        if len(detail_urls) >= max_count:
            break

    return detail_urls


# -----------------------------------------------------
# 6) URL 목록 파일에서 seed URL 읽기
# -----------------------------------------------------
def load_urls_from_file(file_path: Path) -> List[str]:
    if not file_path.exists():
        print(f"[경고] URL 목록 파일을 찾을 수 없습니다: {file_path}")
        return []

    urls: List[str] = []
    for line in file_path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line:
            continue
        if line.startswith("#"):
            continue
        urls.append(line)

    return urls


# -----------------------------------------------------
# 7) seed URL(목록 또는 상세)을 모두 처리
# -----------------------------------------------------
def process_all_from_file():
    seed_urls = load_urls_from_file(URLS_FILE)
    if not seed_urls:
        print("처리할 URL이 없습니다. jobkorea_urls.txt를 확인하세요.")
        return

    print(f"총 {len(seed_urls)}개의 시드 URL을 처리합니다.")

    results = []

    for seed in seed_urls:
        print(f"\n[시드 URL 처리] {seed}")

        try:
            html = fetch_html(seed)

            if "GI_Read" in seed:
                # 이미 상세 공고 URL인 경우
                detail_urls = [seed]
                print(" - 상세 공고 URL 1개로 판단")
            else:
                # 목록 페이지인 경우
                detail_urls = extract_detail_urls_from_list(html, max_count=20)
                print(f" - 목록에서 상세 공고 {len(detail_urls)}개 추출")

            for detail_url in detail_urls:
                res = process_single_detail_url(detail_url)
                results.append(res)
                time.sleep(1.5)  # 차단 방지 딜레이

        except Exception as e:
            print(" - 시드 URL 처리 실패:", e)
            results.append({"url": seed, "status": "error", "error": str(e)})

    print("\n=== 전체 처리 결과 요약 ===")
    for r in results:
        print(r["url"], "=>", r["status"])


# -----------------------------------------------------
# 8) 진입점
# -----------------------------------------------------
if __name__ == "__main__":
    process_all_from_file()

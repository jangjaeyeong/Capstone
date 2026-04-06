# # crawler/jobkorea_scraper.py
# # =====================================================
# # JobKorea 직무별 목록/상세 URL을 자동으로 처리하는 스크립트
# #  1) jobkorea_urls.txt 에서 URL 목록 읽기
# #     - JobList?... (목록 페이지)
# #     - Recruit/GI_Read/... (상세 페이지)
# #  2) 목록 페이지면 → 상세 공고 URL들 추출
# #  3) 각 상세 URL HTML 크롤링
# #  4) 채용공고 제목/회사/근무지/본문 텍스트 추출
# #  5) FastAPI 서버의 /admin/jobs/ingest 로 전송
# # =====================================================

# import requests
# from bs4 import BeautifulSoup
# from typing import List, Dict
# from pathlib import Path
# import time

# # FastAPI ingest API 엔드포인트
# INGEST_URL = "http://127.0.0.1:8000/admin/jobs/ingest"
# BASE_DOMAIN = "https://www.jobkorea.co.kr"

# # URL 목록 파일 (한 줄당 한 개 URL)
# URLS_FILE = Path(__file__).resolve().parent / "jobkorea_urls.txt"


# # -----------------------------------------------------
# # 1) 공통: HTML 가져오기
# # -----------------------------------------------------
# def fetch_html(url: str) -> str:
#     headers = {
#         "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
#     }
#     resp = requests.get(url, headers=headers, timeout=10)
#     resp.raise_for_status()
#     return resp.text


# # -----------------------------------------------------
# # 2) 상세 공고 페이지에서 메타데이터 + 본문 텍스트 추출
# # -----------------------------------------------------
# def extract_job_info(html: str) -> Dict[str, str]:
#     soup = BeautifulSoup(html, "html.parser")

#     # 제목 후보
#     title_tag = soup.select_one(".tit") or soup.select_one(".recruit-title, h1")
#     title = title_tag.get_text(strip=True) if title_tag else "제목 없음"

#     # 회사명 후보 (JobKorea 구조에 따라 클래스명은 필요시 조정)
#     company_tag = soup.select_one(".coName, .recruit-company, .company, .coName > a")
#     company = company_tag.get_text(strip=True) if company_tag else None

#     # 근무지(지역) 후보
#     # 필요에 따라 CSS 선택자는 조정 가능
#     location_tag = soup.select_one(".loc, .job-condition .list li")
#     location = location_tag.get_text(strip=True) if location_tag else None

#     # 상세 본문 영역 후보
#     detail_area = soup.select_one("#article") or soup.select_one(".detailArea, .view_contents")
#     if detail_area:
#         description = detail_area.get_text(separator="\n", strip=True)
#     else:
#         description = title  # 최소한 제목이라도 저장

#     return {
#         "title": title,
#         "company": company,
#         "location": location,
#         "description": description,
#     }


# # -----------------------------------------------------
# # 3) FastAPI /admin/jobs/ingest 호출
# # -----------------------------------------------------
# def send_to_ingest(
#     origin_url: str,
#     title: str,
#     company: str | None,
#     location: str | None,
#     description: str,
# ):
#     payload = {
#         "url": origin_url,
#         "title": title,
#         "company": company,
#         "location": location,
#         "description": description,
    
#     }
#     resp = requests.post(INGEST_URL, json=payload, timeout=15)
#     resp.raise_for_status()
#     return resp.json()


# # -----------------------------------------------------
# # 4) 상세 공고 URL 하나 처리
# # -----------------------------------------------------
# def process_single_detail_url(url: str):
#     print(f"\n[상세 처리] {url}")

#     try:
#         html = fetch_html(url)
#         info = extract_job_info(html)
#         print(" - 텍스트/메타데이터 추출 완료, ingest 전송 중...")

#         result = send_to_ingest(
#             origin_url=url,
#             title=info["title"],
#             company=info["company"],
#             location=info["location"],
#             description=info["description"],
#         )
#         print(" - 성공:", result.get("title"), "-", result.get("company"))

#         return {"url": url, "status": "success", "data": result}

#     except Exception as e:
#         print(" - 실패:", e)
#         return {"url": url, "status": "error", "error": str(e)}


# # -----------------------------------------------------
# # 5) 목록 페이지에서 상세 공고 URL들 추출
# # -----------------------------------------------------
# def extract_detail_urls_from_list(html: str, max_count: int = 20) -> List[str]:
#     """
#     목록 페이지 HTML에서 /Recruit/GI_Read/... 형태의 상세 공고 URL들을 추출한다.
#     max_count: 너무 많이 수집하지 않도록 상한을 둔다.
#     """
#     soup = BeautifulSoup(html, "html.parser")
#     detail_urls: List[str] = []

#     for a in soup.select("a"):
#         href = a.get("href") or ""
#         if "/Recruit/GI_Read/" in href:
#             # 절대 URL/상대 URL 처리
#             if href.startswith("http"):
#                 full_url = href
#             else:
#                 href = href.split("?")[0]  # 쿼리 문자열 제거
#                 full_url = BASE_DOMAIN + href

#             if full_url not in detail_urls:
#                 detail_urls.append(full_url)

#         if len(detail_urls) >= max_count:
#             break

#     return detail_urls


# # -----------------------------------------------------
# # 6) URL 목록 파일에서 seed URL 읽기
# # -----------------------------------------------------
# def load_urls_from_file(file_path: Path) -> List[str]:
#     if not file_path.exists():
#         print(f"[경고] URL 목록 파일을 찾을 수 없습니다: {file_path}")
#         return []

#     urls: List[str] = []
#     for line in file_path.read_text(encoding="utf-8").splitlines():
#         line = line.strip()
#         if not line:
#             continue
#         if line.startswith("#"):
#             continue
#         urls.append(line)

#     return urls


# # -----------------------------------------------------
# # 7) seed URL(목록 또는 상세)을 모두 처리
# # -----------------------------------------------------
# def process_all_from_file():
#     seed_urls = load_urls_from_file(URLS_FILE)
#     if not seed_urls:
#         print("처리할 URL이 없습니다. jobkorea_urls.txt를 확인하세요.")
#         return

#     print(f"총 {len(seed_urls)}개의 시드 URL을 처리합니다.")

#     results = []

#     for seed in seed_urls:
#         print(f"\n[시드 URL 처리] {seed}")

#         try:
#             html = fetch_html(seed)

#             if "GI_Read" in seed:
#                 # 이미 상세 공고 URL인 경우
#                 detail_urls = [seed]
#                 print(" - 상세 공고 URL 1개로 판단")
#             else:
#                 # 목록 페이지인 경우
#                 detail_urls = extract_detail_urls_from_list(html, max_count=20)
#                 print(f" - 목록에서 상세 공고 {len(detail_urls)}개 추출")

#             for detail_url in detail_urls:
#                 res = process_single_detail_url(detail_url)
#                 results.append(res)
#                 time.sleep(1.5)  # 차단 방지 딜레이

#         except Exception as e:
#             print(" - 시드 URL 처리 실패:", e)
#             results.append({"url": seed, "status": "error", "error": str(e)})

#     print("\n=== 전체 처리 결과 요약 ===")
#     for r in results:
#         print(r["url"], "=>", r["status"])


# # -----------------------------------------------------
# # 8) 진입점
# # -----------------------------------------------------
# if __name__ == "__main__":
#     process_all_from_file()

# import requests
# from bs4 import BeautifulSoup
# from typing import Dict, List
# from pathlib import Path
# import time

# # 설정
# INGEST_URL = "http://127.0.0.1:8000/admin/jobs/ingest"
# URLS_FILE = Path(__file__).resolve().parent / "jobkorea_urls.txt"

# # [핵심 1] 봇 차단 방패를 뚫기 위한 '글로벌 세션(쿠키 유지)' 및 '강력한 가짜 신분증(헤더)' 장착
# session = requests.Session()
# HEADERS = {
#     "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
#     "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
#     "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
#     "Referer": "https://www.jobkorea.co.kr/",
#     "Sec-Fetch-Dest": "document",
#     "Sec-Fetch-Mode": "navigate",
#     "Sec-Fetch-Site": "same-origin"
# }
# session.headers.update(HEADERS)

# # -----------------------------------------------------
# # 1. 목록 페이지에서 상세 URL 추출
# # -----------------------------------------------------
# def get_detail_urls_from_list(list_url: str, max_count: int = 5) -> List[str]:
#     resp = session.get(list_url, timeout=10)
#     resp.raise_for_status()
#     soup = BeautifulSoup(resp.text, "html.parser")
    
#     detail_urls = []
#     for a in soup.find_all("a", href=True):
#         href = a["href"]
#         if "/Recruit/GI_Read/" in href:
#             # [핵심 2] URL 뒤에 붙는 꼬리표(?...)를 자르지 않고 원본 그대로 살립니다!
#             full_url = href if href.startswith("http") else "https://www.jobkorea.co.kr" + href
#             if full_url not in detail_urls:
#                 detail_urls.append(full_url)
#         if len(detail_urls) >= max_count:
#             break
#     return detail_urls

# # -----------------------------------------------------
# # 2. 상세 페이지 파싱 (iframe 뚫고 본문 가져오기 + 메타데이터)
# # -----------------------------------------------------
# def extract_job_info(url: str) -> Dict[str, str]:
#     resp = session.get(url, timeout=10)
#     resp.raise_for_status()
#     soup = BeautifulSoup(resp.text, "html.parser")

#     # [디버깅용] 잡코리아가 봇으로 인식하고 차단했는지 콘솔창에서 바로 확인!
#     page_title = soup.title.get_text(strip=True) if soup.title else ""
#     if "Just a moment" in page_title or "Security" in page_title:
#         print(f"  🚨 [경고] 잡코리아 방패에 막혔습니다! 봇으로 인식됨 ({url})")

#     # 제목, 회사, 근무지 추출
#     title_tag = soup.select_one(".tit, .recruit-title, h3.hd_3")
#     title = title_tag.get_text(strip=True) if title_tag else "제목 없음"

#     company_tag = soup.select_one(".coName, .recruit-company, .company")
#     company = company_tag.get_text(strip=True) if company_tag else "회사명 미상"

#     location_tag = soup.select_one(".loc, .job-condition .list li")
#     location = location_tag.get_text(strip=True) if location_tag else "지역 미상"

#     # 핵심: iframe 본문 텍스트 추출
#     iframe = soup.select_one("#gib_frame")
#     description = ""
    
#     if iframe and iframe.get("src"):
#         iframe_url = iframe["src"]
#         if not iframe_url.startswith("http"):
#             iframe_url = "https://www.jobkorea.co.kr" + iframe_url
#         try:
#             iframe_resp = session.get(iframe_url, timeout=10)
#             description = BeautifulSoup(iframe_resp.text, "html.parser").get_text(separator="\n", strip=True)
#         except Exception:
#             pass
    
#     # iframe이 없으면 메인 페이지 텍스트라도 저장
#     if not description:
#         detail_area = soup.select_one("#article, .detailArea, .view_contents, .artReadJobBtm")
#         description = detail_area.get_text(separator="\n", strip=True) if detail_area else title

#     return {
#         "url": url,
#         "title": title,
#         "company": company,
#         "location": location,
#         "description": description
#     }

# # -----------------------------------------------------
# # 3. 메인 실행 로직
# # -----------------------------------------------------
# def process_urls():
#     if not URLS_FILE.exists():
#         print(f"[{URLS_FILE}] 파일이 없습니다.")
#         return

#     seed_urls = [line.strip() for line in URLS_FILE.read_text(encoding="utf-8").splitlines() if line.strip() and not line.startswith("#")]
#     print(f"총 {len(seed_urls)}개의 시드 URL을 분석합니다...\n")

#     for seed_url in seed_urls:
#         target_urls = []
        
#         if "JobList" in seed_url or "Search" in seed_url:
#             print(f"📂 [목록] 상세 공고 수집 중: {seed_url[:50]}...")
#             target_urls = get_detail_urls_from_list(seed_url, max_count=5)
#         else:
#             target_urls = [seed_url]

#         for detail_url in target_urls:
#             try:
#                 info = extract_job_info(detail_url)
#                 print(f"  ✅ 데이터 추출 완료: {info['title'][:20]}...")
                
#                 payload = {
#                     "url": info["url"],
#                     "title": info["title"],
#                     "company": info["company"],
#                     "location": info["location"],
#                     "description": info["description"]
#                 }
                
#                 # 백엔드로 전송은 requests.post 그대로 사용 (FastAPI로 쏘는 거니까 세션 불필요)
#                 response = requests.post(INGEST_URL, json=payload, timeout=15)
                
#                 if response.status_code == 200:
#                     result = response.json()
#                     print(f"  ↳ 💾 DB 저장 성공! (직무: {result.get('job_category')}, 기술: {result.get('tech_stacks')})")
#                 else:
#                     print(f"  ↳ ⚠️ 서버 에러: {response.status_code}")
                
#                 time.sleep(2.0) # 눈치채지 못하게 쉬는 시간을 2초로 넉넉하게 늘림
                
#             except Exception as e:
#                 print(f"  ❌ 실패 ({detail_url}): {e}")

# if __name__ == "__main__":
#     process_urls()

import requests
from bs4 import BeautifulSoup
from pathlib import Path
import time

# 셀레늄 도구들
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By

# 설정
INGEST_URL = "http://127.0.0.1:8000/admin/jobs/ingest"
URLS_FILE = Path(__file__).resolve().parent / "jobkorea_urls.txt"

def setup_driver():
    options = Options()
    options.add_argument("--start-maximized")
    options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_experimental_option('useAutomationExtension', False)
    
    driver = webdriver.Chrome(options=options)
    driver.implicitly_wait(10)
    return driver

def get_detail_urls_from_list(driver, list_url: str) -> list:
    print(f"📂 [목록] 접속 중...")
    driver.get(list_url)
    time.sleep(3) 
    
    soup = BeautifulSoup(driver.page_source, "html.parser")
    detail_urls = []
    
    for a in soup.find_all("a", href=True):
        href = a["href"]
        if "/Recruit/GI_Read/" in href:
            full_url = href if href.startswith("http") else "https://www.jobkorea.co.kr" + href
            if full_url not in detail_urls:
                detail_urls.append(full_url)
        if len(detail_urls) >= 5: 
            break
            
    return detail_urls

def extract_job_info(driver, url: str) -> dict:
    driver.get(url)
    time.sleep(3) # 최신 UI가 다 그려질 때까지 대기
    
    soup = BeautifulSoup(driver.page_source, "html.parser")

    # 1. 제목 추출 (구형, 신형 이름표 다 때려넣기 + 최후의 수단으로 웹페이지 타이틀 탭 이름 쓰기)
    title_tag = soup.select_one("h1, .tit, .recruit-title, .job-title, h3.hd_3")
    if title_tag:
        title = title_tag.get_text(strip=True)
    else:
        title = soup.title.get_text(strip=True).replace("잡코리아", "").strip() if soup.title else "제목 없음"

    # 2. 회사명 추출
    company_tag = soup.select_one(".coName, .recruit-company, .company, .company-name, [class*='Company']")
    company = company_tag.get_text(strip=True) if company_tag else "회사명 미상"

    # 3. 근무지 추출
    location_tag = soup.select_one(".loc, .job-condition .list li, [class*='Location']")
    location = location_tag.get_text(strip=True) if location_tag else "지역 미상"

    description = ""
    
    # 4. 본문 내용 추출 (구형 iframe 검사)
    try:
        iframe_element = driver.find_element(By.ID, "gib_frame")
        driver.switch_to.frame(iframe_element)
        time.sleep(2) 
        iframe_soup = BeautifulSoup(driver.page_source, "html.parser")
        description = iframe_soup.get_text(separator="\n", strip=True)
        driver.switch_to.default_content()
    except Exception:
        pass

    # 5. 핵심! iframe이 없다면 (최신 디자인이라면) 화면 전체 텍스트를 싹쓸이!
    if not description:
        body_element = soup.select_one("body")
        # 싹쓸이해오면 아까 스크린샷의 'AR, JAVA, Spring...' 텍스트가 모두 잡힙니다!
        description = body_element.get_text(separator="\n", strip=True) if body_element else title

    return {
        "url": url,
        "title": title[:100], # 너무 길면 백엔드 DB에서 에러 날까봐 100자로 커트
        "company": company[:50],
        "location": location[:50],
        "description": description
    }

def process_urls():
    if not URLS_FILE.exists():
        print(f"[{URLS_FILE}] 파일이 없습니다.")
        return

    seed_urls = [line.strip() for line in URLS_FILE.read_text(encoding="utf-8").splitlines() if line.strip() and not line.startswith("#")]
    print(f"총 {len(seed_urls)}개의 시드 URL을 분석합니다...\n")

    driver = setup_driver()

    try:
        for seed_url in seed_urls:
            if not seed_url.startswith("http"):
                seed_url = "https://" + seed_url

            target_urls = []
            
            try:
                if "JobList" in seed_url or "Search" in seed_url:
                    target_urls = get_detail_urls_from_list(driver, seed_url)
                    print(f"   -> {len(target_urls)}개의 상세 공고 발견!")
                else:
                    target_urls = [seed_url]
            except Exception as e:
                print(f"  ❌ 목록 페이지 접속 실패! 이유: {e}")
                continue 

            for detail_url in target_urls:
                try:
                    info = extract_job_info(driver, detail_url)
                    print(f"  ✅ 추출 완료: {info['title'][:15]}... | 회사: {info['company']}")
                    
                    payload = {
                        "url": info["url"],
                        "title": info["title"],
                        "company": info["company"],
                        "location": info["location"],
                        "description": info["description"]
                    }
                    
                    response = requests.post(INGEST_URL, json=payload, timeout=15)
                    
                    if response.status_code == 200:
                        result = response.json()
                        print(f"  ↳ 💾 서버 전송 성공! (직무: {result.get('job_category')}, 기술: {result.get('tech_stacks')})")
                    else:
                        print(f"  ↳ ⚠️ 서버 에러: {response.status_code}")
                    
                except Exception as e:
                    print(f"  ❌ 크롤링 실패 ({detail_url}): {e}")
                    
    finally:
        print("\n🎉 모든 작업이 끝났습니다. 3초 뒤 브라우저를 닫습니다.")
        time.sleep(3)
        try:
            driver.quit()
        except:
            pass

if __name__ == "__main__":
    process_urls()
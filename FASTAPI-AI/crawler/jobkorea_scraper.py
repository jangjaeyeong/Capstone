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
    print(f"[목록] 접속 중...")
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
                print(f"목록 페이지 접속 실패! 이유: {e}")
                continue 

            for detail_url in target_urls:
                try:
                    info = extract_job_info(driver, detail_url)
                    print(f"추출 완료: {info['title'][:15]}... | 회사: {info['company']}")
                    
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
                        print(f"서버 전송 성공! (직무: {result.get('job_category')}, 기술: {result.get('tech_stacks')})")
                    else:
                        print(f"서버 에러: {response.status_code}")
                    
                except Exception as e:
                    print(f"크롤링 실패 ({detail_url}): {e}")
                    
    finally:
        print("\n 모든 작업이 끝났습니다. 3초 뒤 브라우저를 닫습니다.")
        time.sleep(3)
        try:
            driver.quit()
        except:
            pass

if __name__ == "__main__":
    process_urls()
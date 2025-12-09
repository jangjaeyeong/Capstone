import requests
from bs4 import BeautifulSoup

URL = "https://quotes.toscrape.com/"

def fetch_quotes():
    resp = requests.get(URL, timeout=10)
    resp.raise_for_status()

    soup = BeautifulSoup(resp.text, "lxml")

    results = []
    for block in soup.select(".quote"):
        text = block.select_one(".text").get_text(strip=True)
        author = block.select_one(".author").get_text(strip=True)

        results.append({
            "text": text,
            "author": author
        })

    return results


if __name__ == "__main__":
    data = fetch_quotes()
    print(f"총 {len(data)}개 명언을 가져왔습니다.")
    for d in data:
        print(d)

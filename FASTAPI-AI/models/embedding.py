# models/embedding.py
# ======================================================
# OpenAI 임베딩 함수 모듈
# - text-embedding-3-small 모델을 호출해서
#   문자열 텍스트를 1536차원 벡터(list[float])로 변환해준다.
#
# 이 함수는:
#   - 문서를 DB에 적재할 때(Document Loader)
#   - RAG 검색 시 쿼리를 임베딩할 때
# 공통으로 재사용할 수 있다.
# ======================================================

import os
from typing import List
from openai import OpenAI

# .env 에서 환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL", "https://api.openai.com/v1")
EMBED_MODEL_ID = os.getenv("EMBED_MODEL_ID", "text-embedding-3-small")

# OpenAI 클라이언트 생성
client = OpenAI(
    api_key=OPENAI_API_KEY,
    base_url=OPENAI_BASE_URL,
)


def embed_text(text: str) -> List[float]:
    """
    주어진 텍스트를 OpenAI 임베딩 벡터(1536차원)로 변환한다.
    - text-embedding-3-small 모델 사용
    - 반환값: float 리스트 (벡터)
    """
    if not OPENAI_API_KEY:
        raise RuntimeError("OPENAI_API_KEY가 설정되어 있지 않습니다.")

    resp = client.embeddings.create(
        model=EMBED_MODEL_ID,
        input=text,
    )
    return resp.data[0].embedding

import os
from dotenv import load_dotenv                  # .env 파일을 로드하기 위한 모듈

# env 파일에 적힌 환경변수 불러오기
# (.env 파일 안에는 OPENAI_API_KEY, MODEL_ID 등이 저장되어 있음)
load_dotenv()

# 환경변수 읽기
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")         # OpenAI 비밀 키 (반드시 있어야 함)
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")       # 기본 API URL
MODEL_ID = os.getenv("OPENAI_MODEL_ID")              # 사용할 모델 ID (파인튜닝 시 변경 가능)
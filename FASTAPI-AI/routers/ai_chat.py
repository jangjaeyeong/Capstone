# routers/ai_chat.py

from fastapi import APIRouter, HTTPException
import httpx
import os
from dotenv import load_dotenv

from schemas.ai import ChatReq

load_dotenv()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL")
MODEL_ID = os.getenv("OPENAI_MODEL_ID")

router = APIRouter(
    prefix="/ai",
    tags=["ai"],
)

@router.post("/chat")
async def chat(req: ChatReq):
    """
    설문 기반 AI 스택 추천 API
    """

    if not OPENAI_API_KEY:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY missing")

    # 사용자 입력을 서버에서 재가공
    user_text = req.messages[0].content

    final_prompt = (
        "넌 유명한 IT 컨설턴트야. 고객의 상황을 분석하고 가장 적합한 개발자 스택을 추천해."
        "\n\n고객이 입력한 정보:\n"
        f"{user_text}"
        "\n\n위 조건에 가장 잘 맞는 스택을 상세하게 추천해줘."
    )

    payload = {
        "model": MODEL_ID,
        "messages": [
            {"role": "system", "content": "너는 전문 IT 컨설턴트다."},
            {"role": "user", "content": final_prompt}
        ],
        "temperature": 0.3,
        "stream": False
    }

    headers = {"Authorization": f"Bearer {OPENAI_API_KEY}"}

    async with httpx.AsyncClient(timeout=60) as client:
        r = await client.post(
            f"{OPENAI_BASE_URL}/v1/chat/completions",
            json=payload,
            headers=headers
        )

        try:
            r.raise_for_status()
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=r.status_code, detail=r.text) from e

        data = r.json()
        answer = data["choices"][0]["message"]["content"]

        # content만 반환
        return {"content": answer}

# schemas/ai.py
from pydantic import BaseModel      # 요청 데이터 구조 정의용
from typing import List, Optional   # 리스트 타입, Optional 타입 사용

# 사용자 메시지 1개 단위 구조
class Message(BaseModel):
    role: Optional[str] = "user"   # role이 없으면 자동으로 user
    content: str                   # 실제 메시지 내용 (텍스트)

# /ai/chat 요청 바디 전체 구조
class ChatReq(BaseModel):
    messages: List[Message]        # 여러 개의 메시지를 리스트 형태로 전달
    stream: Optional[bool] = False # 스트리밍 여부 (지금은 False만 사용)
    temperature: Optional[float] = 0.2  # 답변 창의성 정도 (0.2 ~ 1.0)

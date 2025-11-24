# schemas/document.py
from pydantic import BaseModel

class DocumentCreate(BaseModel):
    title: str
    content: str    # 임베딩은 서버가 생성함

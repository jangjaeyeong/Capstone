#document.py
from sqlalchemy import Column, BigInteger, String, Text, DateTime, func
from pgvector.sqlalchemy import Vector
from db import Base

class Document(Base):
    __tablename__ = "documents"

    id = Column(BigInteger, primary_key=True, index=True)
    title = Column(String(255), nullable=False)
    content = Column(Text, nullable=False)
    hash = Column(String(64), nullable=True)

    # pgvector 1536차원
    embedding = Column(Vector(1536), nullable=False)

    created_at = Column(
        DateTime(timezone=True),
        server_default=func.now()
    )

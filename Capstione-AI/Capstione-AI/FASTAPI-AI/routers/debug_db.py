from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import text
from db import get_db

router = APIRouter(
    prefix="/debug",
    tags=["debug"],
)

@router.get("/columns")
def get_columns(db: Session = Depends(get_db)):
    rows = db.execute(text("""
        SELECT column_name
        FROM information_schema.columns
        WHERE table_name = 'documents';
    """)).fetchall()

    return [r[0] for r in rows]


@router.post("/add-hash-column")
def add_hash_column(db: Session = Depends(get_db)):
    """
    FastAPI가 실제로 연결된 DB에
    documents.hash 컬럼을 추가하는 일회용 API
    """
    try:
        db.execute(text("ALTER TABLE documents ADD COLUMN hash VARCHAR(64);"))
        db.commit()
        return {"status": "ok", "message": "hash 컬럼을 추가했습니다."}
    except Exception as e:
        # 이미 있는 경우 등 에러 메시지 그대로 반환
        return {"status": "error", "message": str(e)}
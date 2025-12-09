# routers/test_db.py
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from db import get_db
from sqlalchemy import text

router = APIRouter(prefix="/test-db")

@router.get("/")
def test_db(db: Session = Depends(get_db)):
    try:
        db.execute(text("SELECT 1"))
        return {"status": "ok", "message": "Neon DB connected!"}
    except Exception as e:
        return {"status": "error", "message": str(e)}
# routers/document_loader.py

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pathlib import Path
import hashlib

from db import get_db
from models.document import Document
from models.embedding import embed_text 

router = APIRouter(
    prefix="/documents",
    tags=["documents"],
)

# 프로젝트 루트 기준 ./docs 폴더
BASE_DIR = Path(__file__).resolve().parents[1]
DOCS_DIR = BASE_DIR / "docs"


def generate_hash(text: str) -> str:
    return hashlib.md5(text.encode("utf-8")).hexdigest()


@router.post("/load-from-folder")
def load_from_folder(db: Session = Depends(get_db)): 
    if not DOCS_DIR.exists():
        raise HTTPException(
            400,
            f"docs 폴더를 찾을 수 없습니다: {DOCS_DIR}",
        )

    created_files = []
    updated_files = []
    skipped_files = []

    for file_path in DOCS_DIR.glob("*.txt"):
        text = file_path.read_text(encoding="utf-8").strip()
        if not text:
            skipped_files.append(f"{file_path.name} (빈 파일)")
            continue

        title = file_path.stem
        file_hash = generate_hash(text)

        # 기존 문서 검사
        existing = db.query(Document).filter(Document.title == title).first()

        if not existing:
            # 임베딩 생성 → await 제거됨
            embedding = embed_text(text) 

            doc = Document(
                title=title,
                content=text,
                embedding=embedding,
                hash=file_hash,
            )
            db.add(doc)
            created_files.append(file_path.name)

        elif existing.hash != file_hash:
            existing.content = text
            existing.embedding = embed_text(text)
            existing.hash = file_hash
            updated_files.append(file_path.name)

        else:
            skipped_files.append(f"{file_path.name} (이미 존재)")

    db.commit()

    return {
        "created": created_files,
        "updated": updated_files,
        "skipped": skipped_files,
        "docs_dir": str(DOCS_DIR),
    }

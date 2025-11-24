# routers/document_loader.py

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pathlib import Path

import hashlib                          #문서 내용 수정시 - 확인
from db import get_db
from document import Document          # document.py의 ORM 모델
from routers.rag import embed_text     # 이미 있는 임베딩 함수 재사용

router = APIRouter(
    prefix="/documents",   # 기존 /documents 그룹
    tags=["documents"],
)

# main.py가 있는 프로젝트 루트 기준으로 /docs 폴더
DOCS_DIR = Path(__file__).resolve().parent.parent / "docs"

#문서 해시 내용 기반으로 생성
def generate_hash(text: str) -> str:
    return hashlib.md5(text.encode("utf-8")).hexdigest()


@router.post("/load-from-folder")
async def load_from_folder(db: Session = Depends(get_db)):
    """
    /docs 폴더에 있는 .txt 파일들을 읽어서
    새파일 DB INSERT
    내용 변경된 파일 DB UPDATE
    변경되지 않은 파일 SKIP
    documents 테이블에 저장
    """
    if not DOCS_DIR.exists():
        raise HTTPException(
            status_code=400,
            detail=f"docs 폴더를 찾을 수 없습니다: {DOCS_DIR}",
        )

    created_files = []
    updated_files = []
    skipped_files = []

    # 1) docs 폴더 안의 .txt 파일 반복
    for file_path in DOCS_DIR.glob("*.txt"):
        text = file_path.read_text(encoding="utf-8").strip()
        if not text:
            skipped_files.append(f"{file_path.name} (빈 파일)")
            continue

        title = file_path.stem  # 예: web_basic.txt → web_basic
        file_hash = generate_hash(text)

        # 2) 이미 있는 문서 찾기
        existing = db.query(Document).filter(Document.title == title).first()

        #신규 문서 INSERT
        if not existing:
            # 3) 임베딩 생성
            embedding = await embed_text(text)

            # 4) Document 레코드 생성
            doc = Document(
                title=title,
                content=text,
                embedding=embedding,   # Vector(1536)에 리스트 그대로 넣으면 됨
                hash=file_hash,
            )
            db.add(doc)
            created_files.append(file_path.name)
            continue

        elif existing.hash != file_hash:
            existing.content = text
            existing.embedding = await embed_text(text)
            existing.hash = file_hash
            updated_files.append(file_path.name)

        else:
            skipped_files.append(f"{file_path.name} (이미 존재)")
            continue


    # 5) 실제 DB 반영
    db.commit()

    return {
        "created": created_files,
        "updated": updated_files,
        "skipped": skipped_files,
        "docs_dir": str(DOCS_DIR),
    }

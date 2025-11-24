# db_init.py
# ---------------------------------------
# 기존 테이블을 모두 삭제 후, 다시 생성하는 스크립트
# 스키마(document.py)를 수정했을 때 항상 이 파일을 실행해주면 됨.
# ---------------------------------------

from db import Base, engine
from document import Document  # Document 모델을 Base에 등록하기 위해 import

if __name__ == "__main__":
    print("기존 테이블 모두 삭제 중(drop_all)...")
    Base.metadata.drop_all(bind=engine)

    print("테이블 다시 생성 중(create_all)...")
    Base.metadata.create_all(bind=engine)

    print("완료! documents 테이블이 새 스키마로 생성되었습니다.")

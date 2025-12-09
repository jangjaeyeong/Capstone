# create_tables.py
from db import Base, engine
from models.job import Job  # 반드시 import 해서 Base에 등록

print("Creating tables based on models...")
Base.metadata.create_all(bind=engine)
print("Done!")

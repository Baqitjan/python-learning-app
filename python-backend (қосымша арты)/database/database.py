# database/database.py (ТОЛЫҚ НҰСҚА)
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from utils.config import settings 

# Settings-тен ДБ URL-ді алу
SQLALCHEMY_DATABASE_URL = settings.DATABASE_URL 

# ДБ қосылуын жасау
engine = create_engine(
    SQLALCHEMY_DATABASE_URL
    # connect_args={"check_same_thread": False} - SQLite үшін ғана қажет
)

# SessionLocal - әрбір сұраныс үшін ДБ сессиясын құру үшін қолданылады
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base - барлық модельдердің мұрагері болатын негізгі класс
Base = declarative_base()

# ----------------------------------------------------
# FastAPI Dependency Injection үшін қолданылатын функция
# ----------------------------------------------------

def get_db():
    """Әрбір сұраныс үшін жаңа ДБ сессиясын береді."""
    db = SessionLocal()
    try:
        yield db # Сессияны береміз
    finally:
        db.close() # Сұраныс аяқталған соң сессияны жабамыз

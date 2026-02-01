# utils/config.py
from pydantic_settings import BaseSettings, SettingsConfigDict # 👈 Осы жол міндетті!
from pathlib import Path

# Жобаның негізгі директориясын табу
BASE_DIR = Path(__file__).resolve().parent.parent

class Settings(BaseSettings):
    # SQLAlchemy базасының URL-і
    DATABASE_URL: str
    
    # JWT құпия кілті
    SECRET_KEY: str
    
    # Конфигурация көзі (.env файлын оқу)
    model_config = SettingsConfigDict(env_file=BASE_DIR / ".env", extra="ignore")

settings = Settings() # 👈 ОСЫ ОБЪЕКТІНІ БАСҚА ФАЙЛДАР ИМПОРТТАП ЖАТЫР

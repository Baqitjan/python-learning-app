# models/user.py
from sqlalchemy.orm import relationship
from sqlalchemy import Column, Integer, String

# database/database.py файлынан Base импорттау
from database.database import Base 

class User(Base):
    __tablename__ = "users"

    # Негізгі авторизация өрістері
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True)
    email = Column(String, unique=True, index=True)
    password = Column(String) # Хештелген пароль

    # Профиль өрістері
    experience = Column(Integer, default=0) # Бұрынғы опыт өрісі, қазір xp қолданылады
    full_name = Column(String, default="")
    score = Column(Integer, default=0)  # Лидерборд үшін
    profile_image_url = Column(String, nullable=True)  # Профиль суреті
    
    # Деңгейлеу жүйесі
    level = Column(Integer, default=1)
    xp = Column(Integer, default=0)

    # Қатынастар
    
    # 1. Жетістіктермен қатынас (models/achievement.py файлындағы Achievement моделімен)
    achievements = relationship("Achievement", back_populates="user")
    
    # 2. Сабақты орындаумен қатынас (models/lesson.py файлындағы LessonCompletion моделімен)
    lesson_completions = relationship(
        "LessonCompletion", 
        back_populates="user", 
        cascade="all, delete" # Қолданушы жойылса, оның орындалулары да жойылады
    )

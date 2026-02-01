from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime, Boolean # ✅ Boolean импорты қосылды
from sqlalchemy.orm import relationship
from database.database import Base
from datetime import datetime

# --- Lesson Моделі ---
class Lesson(Base):
    __tablename__ = "lessons"
    
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    content = Column(Text)
    description = Column(String)
    
    chapter_id = Column(Integer, ForeignKey("chapters.id"))
    chapter = relationship("Chapter", back_populates="lessons")

    # LessonCompletion-мен қатынас
    completions = relationship("LessonCompletion", back_populates="lesson", cascade="all, delete")

    # ✅ Quiz-бен байланыс
    quiz = relationship("Quiz", back_populates="lesson", uselist=False, cascade="all, delete-orphan")


# --- Chapter Моделі (Өзгеріссіз) ---
class Chapter(Base):
    __tablename__ = "chapters"
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    lessons = relationship(
        "Lesson", 
        back_populates="chapter", 
        cascade="all, delete"
    )

# --- LessonCompletion Моделі (ЖАҢА ӨРІС ҚОСЫЛДЫ) ---
class LessonCompletion(Base):
    __tablename__ = "lesson_completions"
    # Бұл екі баған бірге Composite Primary Key болып табылады
    user_id = Column(Integer, ForeignKey("users.id"), primary_key=True)
    lesson_id = Column(Integer, ForeignKey("lessons.id"), primary_key=True)
    completed_at = Column(DateTime, default=datetime.utcnow)
    
    # ✅ ЖАҢА: Квизді сәтті өткізгенін белгілеу
    quiz_passed = Column(Boolean, default=False) 

    user = relationship("User", back_populates="lesson_completions")
    lesson = relationship("Lesson", back_populates="completions")

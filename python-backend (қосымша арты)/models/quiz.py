#models/quiz.py
from sqlalchemy import Column, Integer, String, Text, ForeignKey, Boolean
from sqlalchemy.orm import relationship
from database.database import Base

# =======================================================
# 1. Quiz (Квиз) Моделі
# =======================================================
class Quiz(Base):
    """Әр сабаққа арналған тест жиынтығын сипаттайды"""
    __tablename__ = "quizzes"

    id = Column(Integer, primary_key=True, index=True)
    lesson_id = Column(Integer, ForeignKey("lessons.id"), unique=True) # Сабаққа 1:1 қатынас

    # Қатынастар
    lesson = relationship("Lesson", back_populates="quiz")
    questions = relationship(
        "Question", 
        back_populates="quiz", 
        cascade="all, delete, delete-orphan", # Квиз өшірілсе, сұрақтары да өшеді
    )

# =======================================================
# 2. Question (Сұрақ) Моделі
# =======================================================
class Question(Base):
    """Тест сұрағы"""
    __tablename__ = "questions"

    id = Column(Integer, primary_key=True, index=True)
    text = Column(Text, nullable=False) # Сұрақтың мәтіні
    
    quiz_id = Column(Integer, ForeignKey("quizzes.id"))

    # Қатынастар
    quiz = relationship("Quiz", back_populates="questions")
    answers = relationship(
        "Answer", 
        back_populates="question", 
        cascade="all, delete, delete-orphan", # Сұрақ өшірілсе, жауаптары да өшеді
    )

# =======================================================
# 3. Answer (Жауап) Моделі
# =======================================================
class Answer(Base):
    """Сұраққа арналған жауап нұсқасы"""
    __tablename__ = "answers"

    id = Column(Integer, primary_key=True, index=True)
    text = Column(String, nullable=False) # Жауап мәтіні
    is_correct = Column(Boolean, default=False) # Дұрыс жауап па?
    
    question_id = Column(Integer, ForeignKey("questions.id"))

    # Қатынас
    question = relationship("Question", back_populates="answers")

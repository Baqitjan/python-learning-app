# main.py
from fastapi import FastAPI
from sqlalchemy import text
import os

# 💡 ЖАҢА ИМПОРТ: Setup файлды оқу үшін
# import io # Қазір қажет емес
# 💡 ТҮЗЕТУ 1: Барлық қажетті роутерлерді импорттау
from routers import (
    auth, lessons, compiler, 
    profile, leaderboard, chapters,
    chatbot
)
from routers import quiz as quiz_router 
# 💡 ТҮЗЕТУ 2: Модельдерді импорттау (кестелерді жасау үшін)
# (Бұл импорттардың болуы 'Base.metadata.create_all' үшін өте маңызды)
from models import user, achievement, lesson, quiz 
from database.database import Base, engine
from scripts.init_lessons import initialize_lessons_data



Base.metadata.create_all(bind=engine)
print("INFO: Кестелер дайын.")


# Кестелер жасалғаннан кейін деректерді енгіземіз
initialize_lessons_data()



import os
def print_setup_instructions_simple():
    setup_file_path = "SETUP.md"
    if os.path.exists(setup_file_path):
        try:
            with open(setup_file_path, "r", encoding="utf-8") as f:
                content = f.read()
            print(content)
            
        except Exception as e:
            print(f"ҚАТЕ: '{setup_file_path}' файлын оқу кезінде қате шықты: {e}")
    else:
        print(f"WARNING: '{setup_file_path}' файлы табылмады.")
print_setup_instructions_simple()

app = FastAPI(title="Python Learning App Backend")

app.include_router(auth.router, prefix="/api/v1/auth", tags=["Аутентификация"])
app.include_router(profile.router, prefix="/api/v1/profile", tags=["Профиль"])
app.include_router(lessons.router, prefix="/api/v1/lessons", tags=["Сабақтар"])
app.include_router(chapters.router, prefix="/api/v1/chapters", tags=["Бөлімдер"])
app.include_router(compiler.router, prefix="/api/v1/compiler", tags=["Код Орындау"])
app.include_router(leaderboard.router, prefix="/api/v1/leaderboard", tags=["Лидерборд"])
app.include_router(quiz_router.router, prefix="/api/v1/quiz", tags=["Квиздер"])
app.include_router(chatbot.router, prefix="/api/v1/chatbot", tags=["Чатбот"])

@app.get("/")
def root():
    return {"message": "Backend работает! Қосымшаның API құжаттамасын көру үшін /docs немесе /redoc-қа өтіңіз."}

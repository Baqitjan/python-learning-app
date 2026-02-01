# routers/leaderboard.py (ЖАҢАРТУ)
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
# Жаңа импорттар
from database.dependencies import get_db # <--- Өзгерді
from models.user import User
from typing import List
from schemas import LeaderboardResponse # <--- Өзгерді

router = APIRouter( tags=["Leaderboard"])

# get_db функциясы жойылды

@router.get("/", response_model=List[LeaderboardResponse], summary="Топ-10 қолданушыларды шығару")
def get_leaderboard(db: Session = Depends(get_db)):
    users = db.query(User).order_by(User.score.desc()).limit(10).all()
    # Pydantic схемасы SQLAlchemy моделінен деректерді өзі алады
    return users

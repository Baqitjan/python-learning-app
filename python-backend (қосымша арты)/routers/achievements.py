# routers/achievements.py

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import Optional

# Тәуелділіктер мен модельдер
from database.dependencies import get_db
from models.user import User
from models.achievement import Achievement
from utils.leveling import add_experience
from utils.dependencies import get_current_active_user # Қауіпсіздік үшін

router = APIRouter( tags=["Achievements"])

# --- Ішкі Логика: Жетістіктерді Тексеру ---

def check_and_award_achievements(db: Session, user: User):
    """
    Қолданушының ағымдағы XP және деңгейін тексеріп,
    жаңа жетістіктерді беріп, оны деректер базасына сақтайды.
    """
    awarded_achievements = []

    # 1. 500 XP жетістігін тексеру
    if user.xp >= 500 and not db.query(Achievement).filter(
        Achievement.user_id == user.id, 
        Achievement.name == "500 XP"
    ).first():
        new_achievement = Achievement(
            name="500 XP",
            description="500 тәжірибе жинады!",
            icon_url="https://cdn-icons-png.flaticon.com/512/1041/1041893.png",
            user_id=user.id
        )
        db.add(new_achievement)
        awarded_achievements.append(new_achievement.name)

    # 2. 5-деңгей жетістігін тексеру
    if user.level >= 5 and not db.query(Achievement).filter(
        Achievement.user_id == user.id, 
        Achievement.name == "Level 5"
    ).first():
        new_achievement = Achievement(
            name="Level 5",
            description="5-деңгейге жетті!",
            icon_url="https://cdn-icons-png.flaticon.com/512/2583/2583344.png",
            user_id=user.id
        )
        db.add(new_achievement)
        awarded_achievements.append(new_achievement.name)
        
    return awarded_achievements

# --- Роутер Endpoint-і ---

@router.post("/{user_id}/add_xp/{amount}/")
def add_xp(
    user_id: int, 
    amount: int, 
    db: Session = Depends(get_db),
    # ⚠️ Қауіпсіздік: Бұл функцияны тек логин жасаған қолданушы ғана шақыра алады. 
    # Егер тек әкімшіге ғана рұқсат керек болса, қосымша тексеру қажет.
    current_user: User = Depends(get_current_active_user) 
):
    """
    Қолданушыға XP қосу және жаңа жетістіктерді тексеру.
    (Тексеру: Қауіпсіздік үшін бұл endpoint-ті әдетте тек ішкі қызметтер пайдаланады)
    """
    
    # Қауіпсіздік тексеруі (Мысалы, тек әкімші ғана басқа қолданушыға XP қоса алады)
    # Егер сіздің User моделіңізде is_admin өрісі болса:
    # if current_user.id != user_id and not current_user.is_admin:
    #     raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Рұқсат етілмеген")
        
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    # 1. Тәжірибе қосу
    add_experience(user, amount)
    user.score += amount // 10  # XP-нің 10% ұпайға айналады
    
    # 2. Жетістіктерді тексеру және беру
    newly_awarded = check_and_award_achievements(db, user)
    
    db.commit()
    db.refresh(user)

    return {
        "message": f"XP қосылды! ({amount} XP)", 
        "new_level": user.level, 
        "total_xp": user.xp,
        "new_achievements": newly_awarded
    }

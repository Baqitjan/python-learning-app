from fastapi import APIRouter, HTTPException, Depends, status
from sqlalchemy.orm import Session, joinedload 
from typing import List

# Жаңа импорттар
from database.dependencies import get_db
from models.user import User
from models.achievement import Achievement
# ProfileResponse және UpdateProfileRequest жаңа schema атауларына сай
from schemas import UpdateProfileRequest, AchievementResponse, ProfileResponse
from utils.dependencies import get_current_active_user

router = APIRouter( tags=["Profile & Achievements"])

# ----------------------------------------------------
# 1. Профильді алу (joinedload енгізілген)
# ----------------------------------------------------
# Response model енді User моделіне сәйкес келетін ProfileResponse болады.
# ORM mode (from_attributes=True) жұмыс істеуі үшін объектіні тікелей қайтарамыз.
@router.get("/{user_id}/", response_model=ProfileResponse)
def get_user_profile(user_id: int, db: Session = Depends(get_db)):
    """
    Қолданушының профиль деректерін (жетістіктерімен бірге) қайтарады.
    """
    # joinedload арқылы бір сұраумен User және оның Achievements-терін алу
    user = db.query(User).options(
        joinedload(User.achievements) # User моделіндегі қатынас атауы
    ).filter(User.id == user_id).first()
    
    if not user:
        raise HTTPException(status_code=404, detail="Қолданушы табылмады.")
        
    # Pydantic ORM режимі (from_attributes=True) автоматты түрде түрлендіреді.
    # User.achievements қатынас аты AchievementResponse схемасындағы achievements тізімін толтырады.
    return user

# ----------------------------------------------------
# 2. Жетістіктерді алу
# ----------------------------------------------------
@router.get("/{user_id}/achievements/", response_model=List[AchievementResponse])
def get_user_achievements(user_id: int, db: Session = Depends(get_db)):
    """
    Қолданушының барлық жетістіктерін қайтарады.
    """
    # Жетістіктерді алу
    achievements = db.query(Achievement).filter(Achievement.user_id == user_id).all()
    
    return achievements # Pydantic ORM режимі автоматты түрде AchievementResponse List-ке түрлендіреді

# ----------------------------------------------------
# 3. Профильді жаңарту
# ----------------------------------------------------
@router.put("/{user_id}/", response_model=ProfileResponse)
def update_user_profile(
    user_id: int, 
    req: UpdateProfileRequest, 
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
):
    """
    Қолданушының профиль деректерін жаңартады (Тек өз профилін).
    """
    # Тексеру: Қолданушы өзінің профилін ғана өзгертсін (Авторизация)
    if current_user.id != user_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, 
            detail="Басқа қолданушының профилін өзгертуге рұқсат жоқ."
        )

    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="Қолданушы табылмады.")

    # Деректерді жаңарту (тек берілген өрістерді ғана жаңартамыз)
    update_data = req.model_dump(exclude_unset=True) 

    # Деректерді User моделіне қолдану
    for key, value in update_data.items():
        # User моделіндегі атрибуттарды жаңартамыз
        setattr(user, key, value) 

    db.commit()
    db.refresh(user)

    # Жаңартылған User объектісін ProfileResponse-ке түрлендіріп қайтарамыз
    return user

from fastapi import Depends, APIRouter, HTTPException, status
from sqlalchemy.orm import Session, joinedload 
from typing import List, Optional

# ✅ Дұрыс импорт
from database.database import get_db 
from models.lesson import Lesson, Chapter, LessonCompletion
from models.user import User
from schemas import ( 
    LessonSimpleResponse, 
    LessonDetailResponse,
    LessonCompletionStatus 
)
# ✅ Дұрыс импорт
from utils.dependencies import get_current_active_user, get_optional_user 
from utils.leveling import add_experience 
from datetime import datetime
from sqlalchemy.exc import SQLAlchemyError 

router = APIRouter( tags=["Сабақтар"])

LESSON_XP_AMOUNT = 50 # Бір сабақ үшін берілетін XP

# --- GET /api/v1/lessons/ (Тізім) ---
@router.get("/", response_model=List[LessonSimpleResponse])
def get_lessons(
    db: Session = Depends(get_db),
    # Қолданушы міндетті емес
    current_user: Optional[User] = Depends(get_optional_user) 
):
    """
    Барлық сабақтардың тізімін қайтарады. Қолданушы тіркелген болса,
    орындалу статусы (is_completed) қосылады.
    """
    # Барлық сабақтарды аламыз
    lessons = db.query(Lesson).all() 
    
    # Қолданушының орындау статусын қосу
    if current_user:
        # Қолданушы орындаған сабақ ID-ларын бір сұраумен аламыз
        completed_lesson_ids = {
            c.lesson_id for c in db.query(LessonCompletion).filter(
                LessonCompletion.user_id == current_user.id
            ).all()
        }
        for lesson_item in lessons:
            # is_completed өрісін орнату
            lesson_item.is_completed = lesson_item.id in completed_lesson_ids
    else:
        # Қолданушы жоқ болса, барлық сабақтарды әдепкі (False) мәнімен жібереміз
        for lesson_item in lessons:
            lesson_item.is_completed = False 
    
    return lessons 

# --- GET /api/v1/lessons/{lesson_id} (Толық мазмұн) ---
@router.get("/{lesson_id}", response_model=LessonDetailResponse)
def get_lesson( # Функция аты get_lesson_detail-ден get_lesson-ға ауыстырылды
    lesson_id: int, 
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_user)
):
    """
    ID бойынша сабақтың толық мазмұнын қайтарады, орындалу статусымен бірге.
    """
    # N+1 мәселесін шешу үшін joinedload қолданылады
    lesson = db.query(Lesson).options(
        joinedload(Lesson.chapter) 
    ).filter(Lesson.id == lesson_id).first() 
    
    if not lesson:
        raise HTTPException(status_code=404, detail="Сабақ табылмады")
    
    # Орындалу статусын тексеру логикасы
    lesson.is_completed = False 
    lesson.completed_at = None
    
    if current_user:
        completion = db.query(LessonCompletion).filter(
            LessonCompletion.user_id == current_user.id,
            LessonCompletion.lesson_id == lesson_id
        ).first()
        
        if completion:
            # Динамикалық өрістерді орнату
            lesson.is_completed = True
            lesson.completed_at = completion.completed_at
            
    # Lesson ORM объектісін LessonDetailResponse схемасына қайтарамыз
    return lesson

# --- POST /api/v1/lessons/{lesson_id}/complete (Сабақты орындауды тіркеу) ---
@router.post("/{lesson_id}/complete", response_model=LessonCompletionStatus)
def complete_lesson(
    lesson_id: int, 
    db: Session = Depends(get_db),
    # ✅ МІНДЕТТІ: Бұл жерде токен талап етілуі керек.
    current_user: User = Depends(get_current_active_user) 
):
    """
    Сабақты орындалған деп белгілейді және XP қосады.
    """
    # Сабақтың бар-жоғын тексеру
    lesson = db.query(Lesson).filter(Lesson.id == lesson_id).first()
    if not lesson:
        raise HTTPException(status_code=404, detail="Сабақ табылмады")
        
    # 1. Орындалған-орындалмағанын тексеру
    existing_completion = db.query(LessonCompletion).filter(
        LessonCompletion.user_id == current_user.id,
        LessonCompletion.lesson_id == lesson_id
    ).first()
    
    if existing_completion:
        return LessonCompletionStatus(
            message="Сабақ бұрын орындалған. XP қайта берілмейді.",
            lesson_id=lesson_id,
            xp_gained=0
        )
        
    # 2. Орындалуды тіркеу және XP/ұпай қосу логикасы
    xp_gained = LESSON_XP_AMOUNT
    
    try:
        new_completion = LessonCompletion(
            user_id=current_user.id, 
            lesson_id=lesson_id,
            completed_at=datetime.utcnow()
        )
        db.add(new_completion)
        
        # XP қосу
        add_experience(current_user, LESSON_XP_AMOUNT) 
        
        # Ұпайды қосу (ұпай XP-нің 1/10 бөлігі деп есептейміз)
        current_user.score += LESSON_XP_AMOUNT // 10 
        
        db.commit()
        db.refresh(current_user) 
        
        return LessonCompletionStatus(
            message="Сабақ орындалды! Сізге XP берілді.",
            lesson_id=lesson_id,
            xp_gained=xp_gained
        )
    except SQLAlchemyError as e:
        db.rollback()
        # print(f"SQLAlchemy Error: {e}") 
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
            detail="Деректер базасында қате шықты. Әрекет орындалмады."
        ) 

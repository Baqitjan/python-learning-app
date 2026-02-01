# routers/chapters.py (ТОЛЫҚ ЖАҢАРТЫЛҒАН НҰСҚА)

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session, joinedload
from typing import List

# 💡 ТҮЗЕТУ: get_db функциясын дұрыс импорттау
# Біздің жобамызда get_db database/database.py-да орналасқан деп есептейміз
from database.database import get_db
from models.lesson import Chapter
# 💡 ChapterResponse енді сабақтарды да қамтиды (schemas.py-дағы өзгерістен кейін)
from schemas import ChapterResponse 

router = APIRouter(tags=["Тараулар"])


@router.get("/", response_model=List[ChapterResponse])
def get_chapters(db: Session = Depends(get_db)):
    """
    Барлық тараулардың тізімін сабақтарымен бірге қайтарады.
    (joinedload арқасында тиімді)
    """
    # .options(joinedload(Chapter.lessons)) - N+1 мәселесін шешеді
    chapters = db.query(Chapter).options(joinedload(Chapter.lessons)).all()
    return chapters


# --- 2. ЖАҢА ENDPOINT: Бір тарауды сабақтарымен бірге алу ---
@router.get("/{chapter_id}", response_model=ChapterResponse) 
def get_chapter_with_lessons(chapter_id: int, db: Session = Depends(get_db)):
    """
    ID бойынша белгілі бір тарауды оның ішіндегі барлық сабақтармен
    бірге қайтарады.
    """
    # joinedload арқылы бір сұраумен тарау мен сабақтарды аламыз
    chapter = db.query(Chapter).options(joinedload(Chapter.lessons)).filter(
        Chapter.id == chapter_id
    ).first()
    
    if not chapter:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail="Тарау табылмады"
        )
        
    return chapter

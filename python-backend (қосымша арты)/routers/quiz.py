#routers/quiz.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session, joinedload
from typing import List, Dict, Any

from database.database import get_db
from models.quiz import Quiz, Question, Answer
from models.lesson import LessonCompletion
from models.user import User
from schemas import QuizResponse, QuizAnswerSubmission, QuizResultResponse, StatusMessage
from utils.dependencies import get_current_active_user
from utils.leveling import add_experience
from datetime import datetime

# ✅ ӨЗГЕРІС: Роутерден префиксті алып тастаймыз. Оны main.py-да орнатамыз.
router = APIRouter(tags=["Квиздер"])

QUIZ_PASS_THRESHOLD = 0.7 # 70% дұрыс жауап
QUIZ_SUCCESS_XP = 150 # Квизді сәтті тапсырғаны үшін XP

# --- GET /lesson/{lesson_id} (Квизді алу) ---
@router.get("/lesson/{lesson_id}", response_model=QuizResponse)
def get_quiz_by_lesson_id(
    lesson_id: int, 
    db: Session = Depends(get_db)
):
    """
    Сабақ ID-і бойынша квизді, оның сұрақтарын және жауап нұсқаларын қайтарады.
    (Жауаптарда 'is_correct' өрісі болмайды - қауіпсіздік үшін)
    """
    quiz = db.query(Quiz).options(
        joinedload(Quiz.questions)
        .joinedload(Question.answers)
    ).filter(Quiz.lesson_id == lesson_id).first()
    
    if not quiz:
        raise HTTPException(status_code=404, detail=f"Lesson ID {lesson_id} үшін квиз табылмады.")
        
    return quiz

# --- POST /submit (Квиз жауаптарын жіберу және тексеру) ---
@router.post("/submit", response_model=QuizResultResponse)
def submit_quiz_answers(
    submission: QuizAnswerSubmission,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_active_user)
):
    """
    Қолданушының жауаптарын тексереді, егер 70% дұрыс болса XP береді.
    """
    # 1. Квизді сұрақтар мен дұрыс жауаптарымен бірге аламыз
    quiz = db.query(Quiz).options(
        joinedload(Quiz.questions)
        .joinedload(Question.answers)
    ).filter(Quiz.lesson_id == submission.lesson_id).first()

    if not quiz:
        raise HTTPException(status_code=404, detail="Бұл сабаққа арналған квиз табылмады.")

    # 2. Нәтижелерді есептеу
    total_questions = len(quiz.questions)
    correct_count = 0
    results_detail: Dict[int, bool] = {} # {question_id: is_correct}

    for question in quiz.questions:
        # Қолданушының жауабын аламыз
        submitted_answer_id = submission.submitted_answers.get(question.id)
        is_question_correct = False
        
        if submitted_answer_id:
            # Дұрыс жауапты табамыз (is_correct=True)
            correct_answer = next((a for a in question.answers if a.is_correct), None)
            
            if correct_answer and correct_answer.id == submitted_answer_id:
                is_question_correct = True
                correct_count += 1
                
        results_detail[question.id] = is_question_correct

    # 3. Өткізу логикасы (70% немесе одан жоғары)
    pass_rate = correct_count / total_questions if total_questions > 0 else 0
    is_passed = pass_rate >= QUIZ_PASS_THRESHOLD
    
    xp_gained = 0
    score_earned = 0
    message = f"Сіз {total_questions} сұрақтың {correct_count}-іне дұрыс жауап бердіңіз ({pass_rate:.0%}). "

    if is_passed:
        # Егер квизді бұрын өткізген болса, XP қайта берілмейді
        existing_completion = db.query(LessonCompletion).filter(
            LessonCompletion.user_id == current_user.id,
            LessonCompletion.lesson_id == submission.lesson_id
        ).first()

        # Біз lesson.py-ға қосуымыз керек жаңа өрісті (quiz_passed) тексеру
        if existing_completion and getattr(existing_completion, 'quiz_passed', False):
            message += "Бірақ сіз бұл квизді бұрын өткізгенсіз. Қосымша XP берілмейді."
            xp_gained = 0
            score_earned = 0
        else:
            # XP және ұпай беру
            xp_gained = QUIZ_SUCCESS_XP
            score_earned = QUIZ_SUCCESS_XP // 10
            
            add_experience(current_user, xp_gained)
            current_user.score += score_earned

            # LessonCompletion-да квиздің өткенін белгілеу
            if not existing_completion:
                 # Сабақтың өзі бұрын орындалмаған болса, оны "квиз арқылы" аяқтау деп белгілейміз
                 new_completion = LessonCompletion(
                    user_id=current_user.id, 
                    lesson_id=submission.lesson_id,
                    completed_at=datetime.utcnow(),
                    quiz_passed=True 
                 )
                 db.add(new_completion)
            else:
                setattr(existing_completion, 'quiz_passed', True) # Егер сабақ орындалған болса, квизді белгілейміз

            db.commit()
            message += f"ҚҰТТЫҚТАЙМЫЗ! Сіз квизді сәтті өттіңіз және {xp_gained} XP алдыңыз."
    else:
        message += "Өкінішке орай, өту шегіне (70%) жетпедіңіз. Қайталап көріңіз!"

    return QuizResultResponse(
        message=message,
        xp_gained=xp_gained,
        score_earned=score_earned,
        correct_count=correct_count,
        total_questions=total_questions,
        is_passed=is_passed,
        results_detail=results_detail
    )

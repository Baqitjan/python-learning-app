from pydantic import BaseModel, ConfigDict, Field
from typing import List, Optional, Dict
from datetime import datetime

# SQLAlchemy ORM-мен жұмыс істеу үшін қажет
# from_attributes=True орнатылуы қажет
orm_config = ConfigDict(from_attributes=True)

# =======================================================
# 1. АВТОРИЗАЦИЯ ЖӘНЕ ҚОЛДАНУШЫ (Auth & User)
# =======================================================

class UserRegistrationRequest(BaseModel):
    """Жаңа қолданушыны тіркеу сұранысы"""
    email: str
    password: str = Field(min_length=8, max_length=72)
    username: str = Field(min_length=3, max_length=50)

class AuthRequest(BaseModel):
    """Қолданушының кіру сұранысы (Login)"""
    username: str # Бұл email немесе username болуы мүмкін
    password: str
    
class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: int 

class UserRegisterResponse(BaseModel):
    """Тіркеу кезіндегі жауап"""
    model_config = orm_config
    id: int
    username: str
    email: str
    score: int = 0
    level: int = 1


class UserResponse(BaseModel):
    """Жалпы қолданушы деректерін қайтару үшін"""
    model_config = orm_config
    id: int
    username: str
    email: str
    score: int
    level: int
    xp: int

class ProfileResponse(UserResponse):
    """Профильге арналған толық ақпарат"""
    model_config = orm_config
    full_name: Optional[str] = None 
    profile_image_url: Optional[str] = None 
    achievements: List['AchievementResponse'] = [] 

class UpdateProfileRequest(BaseModel):
    """Қолданушы профилін өзгерту сұранысы"""
    username: Optional[str] = Field(None, min_length=3, max_length=50) 
    email: Optional[str] = None
    full_name: Optional[str] = None 
    profile_image_url: Optional[str] = None 
    
# =======================================================
# 2. КОМПИЛЯТОР (Compiler)
# =======================================================

class CodeExecutionRequest(BaseModel):
    """Код орындау сұранысы"""
    code: str

class CodeExecutionResponse(BaseModel):
    """Код орындау нәтижесі"""
    status: str
    output: str
    runtime: str

# =======================================================
# 3. САБАҚТАР ЖӘНЕ БӨЛІМДЕР (Lessons & Chapters)
# =======================================================

class ChapterCreate(BaseModel):
    title: str

class LessonSimpleResponse(BaseModel):
    model_config = orm_config
    id: int
    title: str
    description: str
    is_completed: bool = False 

class ChapterResponse(BaseModel):
    model_config = orm_config
    id: int
    title: str
    lessons: List[LessonSimpleResponse] = [] 
    
class LessonCreate(BaseModel):
    title: str
    content: str
    description: str
    chapter_id: int
    
class LessonDetailResponse(BaseModel):
    model_config = orm_config
    id: int
    title: str
    content: str
    chapter_id: int
    is_completed: bool = False
    completed_at: Optional[datetime] = None

class LessonCompletionStatus(BaseModel):
    message: str
    lesson_id: int
    xp_gained: int

# =======================================================
# 4. ЛИДЕРБОРД (Leaderboard)
# =======================================================
class LeaderboardResponse(BaseModel):
    model_config = orm_config
    username: str
    score: int
    level: int
    xp: int

# =======================================================
# 5. ЖЕТІСТІКТЕР (Achievements)
# =======================================================
class AchievementResponse(BaseModel):
    model_config = orm_config
    id: int
    name: str
    description: str
    icon_url: Optional[str] = None
    user_id: int 

# =======================================================
# 6. ҚОСЫМША (Misc)
# =======================================================
class StatusMessage(BaseModel):
    message: str

# =======================================================
# 7. КВИЗ ЖӘНЕ ТЕСТТЕР (Quiz & Tests)
# =======================================================

class AnswerResponse(BaseModel):
    model_config = orm_config
    id: int
    text: str

class QuestionResponse(BaseModel):
    model_config = orm_config
    id: int
    text: str
    answers: List[AnswerResponse]

class QuizResponse(BaseModel):
    model_config = orm_config
    id: int
    lesson_id: int
    questions: List[QuestionResponse]

class QuizAnswerSubmission(BaseModel):
    lesson_id: int
    submitted_answers: Dict[int, int]

class QuizResultResponse(BaseModel):
    message: str
    xp_gained: int
    score_earned: int
    correct_count: int
    total_questions: int
    is_passed: bool
    results_detail: Dict[int, bool]

# =======================================================
# 8. ЧАТБОТ (Chatbot) - ЖАҢА
# =======================================================

class ChatRequest(BaseModel):
    """Чатқа жіберілетін хабарлама"""
    message: str

class ChatMessageResponse(BaseModel):
    """Чат тарихының элементі"""
    model_config = orm_config
    id: int
    text: str
    sender: str # "user" немесе "bot"
    timestamp: datetime

class ChatResponse(BaseModel):
    """Чаттың жауабы (тек жаңа хабарлама)"""
    # Өріс атауын "response" орнына "response_text" деп өзгертеміз
    response_text: str

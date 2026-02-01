# routers/auth.py

from fastapi import APIRouter, HTTPException, Depends, status
from fastapi.security import OAuth2PasswordBearer 
from sqlalchemy.orm import Session

# ✅ Дұрыс импорт
from database.database import get_db

from models.user import User

from schemas import (
    UserRegistrationRequest,
    UserRegisterResponse,
    Token,
    AuthRequest,
    StatusMessage # Токенді JSON-да қайтару үшін
)
from utils.security import (
    get_password_hash, 
    verify_password, 
    create_access_token,
)

# Роутер префиксін main.py-де тіркейтіндей етіп жасау
router = APIRouter( tags=["Аутентификация"]) 

# Қауіпсіздік схемасын анықтау (Токенді алу үшін)
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login") 

# --- 1. Тіркелу (Register) ---
@router.post("/register", 
            response_model=UserRegisterResponse, 
            status_code=status.HTTP_201_CREATED,
            summary="Жаңа қолданушыны тіркеу")
def register_user(req: UserRegistrationRequest, db: Session = Depends(get_db)):
    """
    Жаңа қолданушыны тіркейді, email мен username бірегейлігін тексереді.
    """
    
    # 1. Қолданушының бар-жоғын тексеру (Email)
    existing_email = db.query(User).filter(User.email == req.email).first()
    if existing_email:
        raise HTTPException(status_code=400, detail="Бұл email тіркелген.")
        
    # Қосымша тексеру: Username-нің бірегейлігі
    existing_username = db.query(User).filter(User.username == req.username).first()
    if existing_username:
        raise HTTPException(status_code=400, detail="Бұл қолданушы аты бос емес.")
    
    # 2. Парольді хештеу
    hashed_password = get_password_hash(req.password)
    
    # 3. Жаңа қолданушыны құру
    new_user = User(
        username=req.username, 
        email=req.email, 
        password=hashed_password 
    )
    
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    
    return new_user



# --- 2. Кіру (Login) ---
@router.post("/login", response_model=Token, summary="Email/Username және пароль арқылы кіру")
def login_user(req: AuthRequest, db: Session = Depends(get_db)):
    """
    Қолданушыны аутентификациялайды және JWT токенін қайтарады.
    """
    
    # 1. Қолданушыны username (немесе email) арқылы табу
    user = db.query(User).filter(
        (User.email == req.username) | (User.username == req.username)
    ).first()
    
    # 2. Парольді тексеру
    if not user or not verify_password(req.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, 
            detail="Қате қолданушы аты/email немесе пароль",
            headers={"WWW-Authenticate": "Bearer"},
        )

    # 3. JWT токенін жасау
    # sub - subject (қолданушы ID-сы)
    access_token = create_access_token(
        data={"sub": str(user.id)} 
    )

    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user_id": user.id  # ⬅️ ОСЫ ЖОЛ ӨТЕ МАҢЫЗДЫ!
    }

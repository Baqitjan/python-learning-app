# utils/dependencies.py

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session
from typing import Optional

# ✅ Дұрыс импорт: get_db database/database.py-да орналасқан
from database.database import get_db 
# ✅ Дұрыс импорт: decode_access_token utils/security.py-да орналасқан деп есептейміз
from utils.security import decode_access_token 
from models.user import User

# Токенді алу үшін (Swagger UI-де көрінеді)
# МІНДЕТТІ: tokenUrl дұрыс API префиксін қамтуы керек
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login") 

def get_current_active_user(
    token: str = Depends(oauth2_scheme), 
    db: Session = Depends(get_db)
) -> User:
    """
    Authorization header-ден JWT токенді алып, оны тексереді.
    Жарамды болса, қолданушы объектісін қайтарады. 
    Жарамсыз болса, 401 қатесін береді.
    """
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Токен жарамсыз немесе мерзімі өтті",
        headers={"WWW-Authenticate": "Bearer"},
    )
    
    # 1. Токенді декодтау
    try:
        # headers={"WWW-Authenticate": "Bearer"} қатесін болдырмау үшін
        # токен "Bearer <token>" форматында болса, тек токенді аламыз.
        if token.lower().startswith("bearer "):
            token = token.split(" ")[1]
            
        payload = decode_access_token(token)
    except Exception:
        raise credentials_exception

    if payload is None:
        raise credentials_exception

    # 2. Payload-тан user_id алу
    user_id = payload.get("sub") # OAuth2 стандартына сәйкес "sub" (subject) қолданамыз
    if user_id is None:
        raise credentials_exception

    # 3. Базадан қолданушыны табу
    user = db.query(User).filter(User.id == int(user_id)).first()
    if user is None:
        raise credentials_exception
        
    return user

# ----------------------------------------------------
# ЖАҢА ФУНКЦИЯ: Орындалуы міндетті емес қолданушы
# ----------------------------------------------------

# 💡 ӨЗГЕРІС: header-де токен болмаса, None қайтару үшін Optional[str] = Depends(oauth2_scheme) 
# орнына Header(None) немесе Query(None) қолданған тиімдірек. 
# Алайда, сіздің алдыңғы кодыңызды сақтау үшін, төмендегідей өзгеріс енгіземіз:
def get_optional_user(
    token: Optional[str] = Depends(oauth2_scheme), # FastAPI егер header жоқ болса, None-ді беруі керек
    db: Session = Depends(get_db)
) -> Optional[User]:
    """
    Токенді тексереді, бірақ егер ол жарамсыз болса немесе жоқ болса, 
    401 қатесін шығармайды, жай ғана None қайтарады.
    """
    # Егер FastAPI/OAuth2 схемасы токенді ала алмаса (Header жоқ болса)
    if token is None or token.lower().startswith("bearer"):
        return None # Токен жоқ немесе дұрыс емес форматта
        
    # Токенді "Bearer " префиксінен тазалау
    if token.lower().startswith("bearer "):
        token = token.split(" ")[1]

    try:
        # Токенді декодтауға тырысамыз
        payload = decode_access_token(token)
    except Exception:
        return None # Декодтау қатесі, бірақ 401 шығармаймыз

    if payload is None:
        return None

    user_id_str = payload.get("sub") # "sub" өрісін қолданамыз
    if user_id_str is None:
        return None

    try:
        user_id = int(user_id_str)
    except ValueError:
        return None # ID дұрыс форматта емес

    # Базадан қолданушыны табу
    user = db.query(User).filter(User.id == user_id).first()
    return user # Қолданушы табылса, қайтарамыз, әйтпесе None

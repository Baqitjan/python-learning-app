from datetime import datetime, timedelta, timezone
from typing import Optional, Any
from passlib.context import CryptContext
from jose import JWTError, jwt
from utils.config import settings
from fastapi import HTTPException, status, Depends
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session 

# Айналмалы импортты болдырмау үшін:
# Егер User моделіне қатысты проблема туындаса, осы импортты auth.py файлындағы
# router.post функциясының ішіне (функцияның ең басына) жылжытуға болады.
from database.database import get_db
from models.user import User 

# 2. OAuth2 схемасын анықтау
# login endpoint-ке сілтеме
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/auth/login/")

# Пароль және JWT константалары
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 * 7 # 7 күн

# ----------------------------------------------------
# A. Пароль функциялары
# ----------------------------------------------------

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Хештелген парольдің дұрыстығын тексереді."""
    # ❌ Қолмен қысқартуды алып тастаңыз. Passlib-ке таза парольді беріңіз.
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password: str) -> str:
    """Парольді хештейді."""
    
    # ❌ Қолмен қысқартуды алып тастаңыз. Passlib-ке таза парольді беріңіз.
    return pwd_context.hash(password)

# ----------------------------------------------------
# B. JWT Токен функциялары
# ----------------------------------------------------

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    # ... (өзгеріссіз қалдырамыз)
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
        
    to_encode.update({"exp": expire, "sub": "access"})
    encoded_jwt = jwt.encode(to_encode, settings.SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def decode_access_token(token: str) -> dict[str, Any]:
    # ... (өзгеріссіз қалдырамыз)
    try:
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[ALGORITHM])
        user_id: int = payload.get("id")
        
        if user_id is None:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Токен форматы дұрыс емес (ID өрісі жоқ)")
        
        return payload
        
    except JWTError:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Токен жарамсыз немесе мерзімі өтіп кеткен")

# ----------------------------------------------------
# C. Қазіргі Қолданушыны алу (Dependency)
# ----------------------------------------------------

def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)) -> User:
    """Токеннен қолданушы деректерін алып, оны ДБ-дан тексереді."""
    payload = decode_access_token(token)
    user_id = payload.get("id")
    
    # ID сан болуы керек
    if user_id is None or not isinstance(user_id, int):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Токенде қолданушы ID жоқ немесе дұрыс емес.")
        
    user = db.query(User).filter(User.id == user_id).first()
    
    if user is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Қолданушы табылмады.")
        
    return user

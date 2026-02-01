from fastapi import APIRouter, HTTPException
# ChatMessageResponse жаңа схемасын импорттау
from schemas import ChatRequest, ChatResponse, ChatMessageResponse 
from typing import List
from datetime import datetime
import random

router = APIRouter()

# Бұл жерде сіз OpenAI немесе Gemini API-ін қоса аласыз.
# Қазірше қарапайым жауаптар қайтаратын логика жазылды.

# Уақытша чат тарихын сақтау үшін:
TEMP_CHAT_HISTORY: List[ChatMessageResponse] = [
    ChatMessageResponse(
        id=1,
        text="Сәлем! Мен Python үйренуге көмектесетін жасанды интеллект ботпын. Сізге қалай көмектесе аламын?",
        sender="bot",
        timestamp=datetime.now()
    )
]

@router.get("/history", response_model=List[ChatMessageResponse])
async def get_chat_history():
    """Чат тарихын қайтарады (Уақытша статикалық деректер)."""
    # Шынайы қолданбада мұнда деректер базасынан алынған тарих болуы керек.
    return TEMP_CHAT_HISTORY


@router.post("/send", response_model=ChatResponse)
async def ask_chatbot(request: ChatRequest):
    user_message = request.message.lower()
    
    # 1. Қолданушы хабарламасын тарихқа қосу
    user_msg_response = ChatMessageResponse(
        id=len(TEMP_CHAT_HISTORY) + 1,
        text=request.message,
        sender="user",
        timestamp=datetime.now()
    )
    TEMP_CHAT_HISTORY.append(user_msg_response)
    
    bot_response_text = ""

    # 2. Чатбот логикасы
    if "python" in user_message:
        bot_response_text = "Python — бұл веб-қосымшаларды, деректерді талдауды және жасанды интеллектті әзірлеу үшін қолданылатын қуатты бағдарламалау тілі. Ол қарапайым синтаксисімен танымал."
    elif "сәлем" in user_message or "hello" in user_message:
        bot_response_text = "Сәлем! Мен Python үйренуге көмектесетін ботпын. Сізге қалай көмектесе аламын? Мысалы, айнымалылар туралы сұрай аласыз."
    elif "айнымалы" in user_message or "variable" in user_message:
        bot_response_text = "Айнымалы (Variable) — бұл деректерді сақтауға арналған контейнер. Мысалы: `x = 5` немесе `name = 'Ali'`."
    elif "цикл" in user_message or "loop" in user_message:
        bot_response_text = "Циклдар (Loops) кодты бірнеше рет қайталау үшін қолданылады. Python-да 'for' және 'while' циклдары бар. 'For' циклы тізім элементтерін өту үшін жиі қолданылады."
    elif "функция" in user_message or "function" in user_message:
        bot_response_text = "Функция — белгілі бір тапсырманы орындайтын код блогы. Ол 'def' сөзімен басталады. Функцияны қайта-қайта қолдануға болады, бұл кодты ұйымдастыруға көмектеседі."
    else:
        # Егер сұрақ түсініксіз болса, кездейсоқ кеңес береміз
        tips = [
            "Маған Python туралы сұрақ қойыңыз!",
            "print('Hello World') деп жазып көрдіңіз бе?",
            "Сабақтар бөлімінен жаңа тақырыптарды оқи аласыз.",
            "Код жазудан қорықпаңыз, қателіктер — үйренудің бір бөлігі."
        ]
        bot_response_text = "Кешіріңіз, мен толық түсінбедім. Мына кеңесті қолданып көріңіз: " + random.choice(tips)

    # 3. Бот хабарламасын тарихқа қосу
    bot_msg_response = ChatMessageResponse(
        id=len(TEMP_CHAT_HISTORY) + 1,
        text=bot_response_text,
        sender="bot",
        timestamp=datetime.now()
    )
    TEMP_CHAT_HISTORY.append(bot_msg_response)
    
    # 4. Жауапты қайтару (тек боттың жауап мәтіні)
    return ChatResponse(response_text=bot_response_text)

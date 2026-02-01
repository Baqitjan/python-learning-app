# routers/compiler.py
from fastapi import APIRouter, HTTPException, status
import subprocess
import json
import os
import sys

# Схемаларды импорттау (schemas.py файлынан)
# ⚠️ Енді Pydantic схемаларын schemas.py-дан импорттаймыз, бұл дұрыс архитектура
try:
    from schemas import CodeExecutionRequest, CodeExecutionResponse
except ImportError:
    # Егер импорттау мүмкін болмаса, минималды анықтама береміз
    from pydantic import BaseModel
    class CodeExecutionRequest(BaseModel):
        code: str
    class CodeExecutionResponse(BaseModel):
        status: str
        output: str
        runtime: str


router = APIRouter( tags=["Code Execution"])

# 1-Қадам: Sandbox Docker образының атауы
# Сіз оны осылай құрдыңыз: docker build -f Dockerfile.Sandbox.py -t python-sandbox-compiler .
SANDBOX_IMAGE_NAME = "python-sandbox-compiler"

@router.post("/execute", response_model=CodeExecutionResponse)
def execute_python_code(req: CodeExecutionRequest):
    """
    Пайдаланушы жіберген Python кодын қауіпсіз Docker Sandbox контейнерінде орындайды.
    Нәтижесінде CodeExecutionResponse схемасы қайтарылады.
    """
    
    # ⚠️ ҚАУІПСІЗДІК: subprocess арқылы Docker контейнерін іске қосу
    try:
        # Docker командасы:
        docker_command = [
            "docker", "run",
            "--rm",               # Контейнерді орындалғаннан кейін жою
            "-i",                 # Кірісті (stdin) қосу
            "--pids-limit", "50", # Процесс санын шектеу
            "--memory", "100m",   # Жадты шектеу
            "--cpus", "0.5",      # CPU қолдануды шектеу
            "--network", "none",  # Ең маңыздысы: сыртқы желіге шығуға тыйым салу
            SANDBOX_IMAGE_NAME    # Біздің Sandbox образымыз
        ]
        
        # Кодты stdin арқылы беру
        process = subprocess.run(
            docker_command,
            input=req.code,
            capture_output=True,
            text=True,
            timeout=8 # Docker командасының жалпы уақыты (sandbox-та 3 секунд бар, 8 секунд - Docker-дің өзінің іске қосылуына + буфер)
        )
        
        output_data = process.stdout.strip()
        
        # 2. Docker қатесін тексеру (мысалы, образ табылмаса)
        if process.returncode != 0:
            error_output = process.stderr.strip()
            
            # Егер қателік Docker-дің өзінен болса, оны тікелей қайтарамыз
            if "No such image" in error_output:
                output = f"СӘТСІЗДІК: '{SANDBOX_IMAGE_NAME}' Docker образы табылмады. Қатені жою үшін 'docker build -f Dockerfile.Sandbox.py -t {SANDBOX_IMAGE_NAME} .' командасын орындаңыз."
            elif error_output:
                 output = f"Docker орындау қатесі: {error_output}"
            else:
                 output = "Код орындау сәтсіз аяқталды (Қосымша мәлімет жоқ). Образдың бар екеніне көз жеткізіңіз."

            return CodeExecutionResponse(
                status="docker_error",
                output=output,
                runtime="N/A"
            )

        # 3. JSON нәтижесін парсерлеу
        try:
            result = json.loads(output_data)
        except json.JSONDecodeError:
            # Егер sandbox ішіндегі код JSON форматынан басқаны шығарса 
             return CodeExecutionResponse(
                status="compiler_error",
                output=f"Компиляторда қате: Sandbox JSON форматын қайтармады. Шығыс: {output_data}",
                runtime="N/A"
            )
            
        return CodeExecutionResponse(**result)

    except subprocess.TimeoutExpired:
        # Егер Docker командасы 8 секунд ішінде жауап бермесе
        return CodeExecutionResponse(
            status="timeout",
            output="Error: Docker контейнері іске қосу уақытынан (8.0 секунд) асып кетті.",
            runtime="8.000 sec"
        )
    except FileNotFoundError:
        # ⚠️ ЕҢ ЫҚТИМАЛ ҚАТЕ: docker командасы жүйеде орнатылмаған немесе қол жетімді емес
        return CodeExecutionResponse(
            status="system_error",
            output="КАТЕ: 'docker' командасы серверде табылмады. Docker орнатылмаған немесе PATH-да жоқ. Бұл 500 қатесінің ең көп таралған себебі.",
            runtime="N/A"
        )
    except Exception as e:
        # Басқа күтпеген қателер
        return CodeExecutionResponse(
            status="internal_error",
            output=f"Күтпеген ішкі қате: {e}",
            runtime="N/A"
        )

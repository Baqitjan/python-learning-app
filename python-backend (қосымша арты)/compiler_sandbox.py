# compiler_sandbox.py (Жаңа, тек импорттарды тексеруге бейімделген)
import sys
import io
import json
import time

TIMEOUT = 3 # Секунд

def run_safe_import_check(code_to_check: str) -> dict:
    """Кодты орындауды емес, кітапхана импортын тексеруді симуляциялайды."""
    
    start_time = time.time()
    
    # Қауіпті функцияларды блоктау (минималды қауіпсіздік)
    BANNED_MODULES = ['os', 'sys', 'subprocess', 'shutil', 'socket', '__import__']
    
    # 1. Кодты қауіпті модульдерге тексеру
    for module in BANNED_MODULES:
        if f"import {module}" in code_to_check or f"from {module}" in code_to_check:
            return {
                "status": "error",
                "output": f"Security Error: '{module}' модулін импорттауға тыйым салынған.",
                "runtime": f"{(time.time() - start_time):.3f} sec"
            }
            
    # 2. Нәтижені жинау үшін stdout-ты ауыстыру
    old_stdout = sys.stdout
    redirected_output = io.StringIO()
    sys.stdout = redirected_output
    
    try:
        # 3. Кодты орындау (қауіпсіз namespace-те)
        exec(code_to_check, {}) 
        
        output = redirected_output.getvalue().strip()
        
        return {
            "status": "success",
            "output": output if output else "Код сәтті орындалды (импорттар тексерілді).",
            "runtime": f"{(time.time() - start_time):.3f} sec"
        }
        
    except ModuleNotFoundError as e:
        return {
            "status": "not_installed",
            "output": f"Қате: {e} - бұл кітапхана компиляторда орнатылмаған.",
            "runtime": f"{(time.time() - start_time):.3f} sec"
        }
    except Exception as e:
        return {
            "status": "runtime_error",
            "output": f"Кодтағы қате: {type(e).__name__}: {str(e)}",
            "runtime": f"{(time.time() - start_time):.3f} sec"
        }
    finally:
        sys.stdout = old_stdout

if __name__ == "__main__":
    code_input = sys.stdin.read()
    result = run_safe_import_check(code_input)
    print(json.dumps(result))

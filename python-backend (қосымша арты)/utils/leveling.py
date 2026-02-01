# utils/leveling.py

def calculate_level(xp: int) -> int:
    """XP бойынша қолданушы деңгейін есептеу."""
    if xp < 100:
        return 1
    elif xp < 250:
        return 2
    elif xp < 500:
        return 3
    elif xp < 1000:
        return 4
    elif xp < 2000:
        return 5
    elif xp < 3500:
        return 6
    elif xp < 5000:
        return 7
    else:
        return 8 + (xp // 1000)
    

def add_experience(user, amount: int):
    """Қолданушыға тәжірибе қосу және деңгей жаңарту."""
    user.xp += amount
    user.level = calculate_level(user.xp)
    return user

from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from database.database import Base

class Achievement(Base):
    __tablename__ = "achievements"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    description = Column(String)
    icon_url = Column(String)
    user_id = Column(Integer, ForeignKey("users.id"))

    # ✅ Бұл жер де маңызды
    user = relationship("User", back_populates="achievements")

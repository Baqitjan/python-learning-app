package com.example.mobileapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp.data.model.SavedScript;

import java.util.List;

@Dao
public interface SavedScriptDao {

    // 1. Скриптті қосу (Енді insert деп аталады)
    // Егер ID бірдей болса, барды ауыстырады.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedScript script);

    // 2. Скриптті жою (Енді delete деп аталады)
    // Room бұл әдісті ақылды түрде өңдеп, PrimaryKey бойынша жояды.
    @Delete
    void delete(SavedScript script);

    // 3. Барлық скрипттерді жүктеу
    @Query("SELECT * FROM saved_scripts ORDER BY name ASC")
    LiveData<List<SavedScript>> getAllScripts();
}
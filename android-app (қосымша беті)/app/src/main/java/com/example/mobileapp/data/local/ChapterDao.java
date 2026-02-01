package com.example.mobileapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mobileapp.data.model.Chapter;
import com.example.mobileapp.data.model.ChapterWithLessons;

import java.util.List;

@Dao
public interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Chapter... chapters);

    @Query("SELECT * FROM chapters")
    LiveData<List<Chapter>> getAll();

    @Transaction
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    LiveData<ChapterWithLessons> getChapterWithLessons(int chapterId);

    @Query("DELETE FROM chapters")
    void deleteAll();
}

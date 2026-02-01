package com.example.mobileapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp.data.model.Lesson;

import java.util.List;

@Dao
public interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lesson lesson);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Lesson... lessons); // Added this method

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    LiveData<Lesson> getLessonById(int lessonId);

    @Query("SELECT * FROM lessons WHERE chapterId = :chapterId")
    LiveData<List<Lesson>> getLessonsForChapter(int chapterId);

    @Query("DELETE FROM lessons")
    void deleteAll();
}

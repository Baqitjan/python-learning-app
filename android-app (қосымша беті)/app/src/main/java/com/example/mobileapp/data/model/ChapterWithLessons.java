package com.example.mobileapp.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * This class represents the relationship between a Chapter and its Lessons.
 */
public class ChapterWithLessons {

    @Embedded
    public Chapter chapter;

    @Relation(
            parentColumn = "id",
            entityColumn = "chapterId"
    )
    public List<Lesson> lessons;
}

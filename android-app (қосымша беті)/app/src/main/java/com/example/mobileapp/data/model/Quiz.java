package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Quiz {

    @SerializedName("id")
    private int id;

    @SerializedName("lesson_id")
    private int lessonId;

    @SerializedName("questions")
    private List<Question> questions;

    // Getters
    public int getId() {
        return id;
    }

    public int getLessonId() {
        return lessonId;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}

package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class LessonCompletionStatus {

    @SerializedName("message")
    private String message;

    @SerializedName("lesson_id")
    private int lessonId;

    @SerializedName("xp_gained")
    private int xpGained;

    public String getMessage() {
        return message;
    }

    public int getLessonId() {
        return lessonId;
    }

    public int getXpGained() {
        return xpGained;
    }
}

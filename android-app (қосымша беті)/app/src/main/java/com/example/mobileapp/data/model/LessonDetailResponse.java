package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the LessonDetailResponse schema in Python.
 */
public class LessonDetailResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("chapter_id")
    private int chapterId;

    @SerializedName("is_completed")
    private boolean isCompleted;

    @SerializedName("completed_at")
    private String completedAt; // Using String for datetime from server

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getChapterId() {
        return chapterId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getCompletedAt() {
        return completedAt;
    }
}

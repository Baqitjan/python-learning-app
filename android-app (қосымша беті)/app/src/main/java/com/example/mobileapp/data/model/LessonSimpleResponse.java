package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the LessonSimpleResponse schema in Python.
 */
public class LessonSimpleResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("is_completed")
    private boolean isCompleted;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}

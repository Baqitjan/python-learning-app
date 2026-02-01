package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Corresponds to the ChapterResponse schema from the server, including a list of simple lessons.
 */
public class ChapterResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("lessons")
    private List<LessonSimpleResponse> lessons;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<LessonSimpleResponse> getLessons() {
        return lessons;
    }
}

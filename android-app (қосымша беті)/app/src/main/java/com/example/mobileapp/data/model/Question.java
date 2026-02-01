package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question {

    @SerializedName("id")
    private int id;

    @SerializedName("text")
    private String text;

    @SerializedName("answers")
    private List<Answer> answers;

    // Getters
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}

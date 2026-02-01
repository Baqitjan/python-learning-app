package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the AnswerResponse schema in Python.
 * The is_correct field is intentionally omitted as it's not sent to the client when fetching questions.
 */
public class Answer {

    @SerializedName("id")
    private int id;

    @SerializedName("text")
    private String text;

    // Getters
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}

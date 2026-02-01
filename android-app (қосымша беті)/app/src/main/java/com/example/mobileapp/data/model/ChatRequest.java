package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Backend-ке жіберілетін қолданушының чат сұранысы.
 * (schemas.py-дағы ChatRequest-ке сәйкес)
 */
public class ChatRequest {
    @SerializedName("message")
    private String message;

    public ChatRequest(String message) {
        this.message = message;
    }

    // Геттер мен Сеттер Retrofit/Gson үшін қажет болмауы мүмкін,
    // бірақ сақтық үшін қалдырамыз.
    public String getMessage() {
        return message;
    }
}
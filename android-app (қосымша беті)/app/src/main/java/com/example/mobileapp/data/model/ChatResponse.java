package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Backend-тен келетін чатботтың жауабы.
 * (schemas.py-дағы ChatResponse-ке сәйкес)
 */
public class ChatResponse {
    @SerializedName("response")
    private String responseText;

    public String getResponseText() {
        return responseText;
    }

    // Жауапты тестілеу үшін конструктор (міндетті емес)
    public ChatResponse(String responseText) {
        this.responseText = responseText;
    }
}
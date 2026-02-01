package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("id")
    private int id;

    @SerializedName("text")
    private String text;

    @SerializedName("sender")
    private String sender;

    @SerializedName("timestamp")
    private String timestamp; // Changed from Date to String to handle non-standard formats

    // Constructor
    public ChatMessage(int id, String text, String sender, String timestamp) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

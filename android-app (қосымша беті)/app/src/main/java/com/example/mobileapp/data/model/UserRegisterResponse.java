package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the UserRegisterResponse schema in Python.
 */
public class UserRegisterResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("score")
    private int score;

    @SerializedName("level")
    private int level;

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
}

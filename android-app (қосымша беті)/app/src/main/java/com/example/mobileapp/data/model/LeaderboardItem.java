package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardItem {

    @SerializedName("username")
    private String username;

    @SerializedName("score")
    private int score;

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}

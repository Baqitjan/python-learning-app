package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Corresponds to the ProfileResponse schema in Python, including achievements.
 */
public class ProfileResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    @SerializedName("score")
    private int score;

    @SerializedName("level")
    private int level;

    @SerializedName("xp")
    private int xp;

    @SerializedName("achievements")
    private List<Achievement> achievements;

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public List<Achievement> getAchievements() { return achievements; }
}

package com.example.mobileapp.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "lessons")
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @ColumnInfo(defaultValue = "")
    @SerializedName("title")
    private String title;

    @ColumnInfo(defaultValue = "")
    @SerializedName("description")
    private String description;

    @ColumnInfo(defaultValue = "")
    @SerializedName("content")
    private String content;

    @SerializedName("chapter_id")
    private int chapterId;

    @ColumnInfo(defaultValue = "")
    @SerializedName("imageUrl")
    private String imageUrl;

    @ColumnInfo(defaultValue = "")
    @SerializedName("codeSnippet")
    private String codeSnippet;

    @ColumnInfo(defaultValue = "")
    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("is_completed")
    private boolean isCompleted;

    @ColumnInfo(defaultValue = "")
    @SerializedName("completed_at")
    private String completedAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}

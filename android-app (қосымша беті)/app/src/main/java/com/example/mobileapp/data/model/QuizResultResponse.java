package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Corresponds to the QuizResultResponse schema in Python.
 * Represents the result of the quiz after submission.
 */
public class QuizResultResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("xp_gained")
    private int xpGained;

    @SerializedName("score_earned")
    private int scoreEarned;

    @SerializedName("correct_count")
    private int correctCount;

    @SerializedName("total_questions")
    private int totalQuestions;

    @SerializedName("is_passed")
    private boolean isPassed;

    @SerializedName("results_detail")
    private Map<Integer, Boolean> resultsDetail;

    // Getters
    public String getMessage() { return message; }
    public int getXpGained() { return xpGained; }
    public int getScoreEarned() { return scoreEarned; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalQuestions() { return totalQuestions; }
    public boolean isPassed() { return isPassed; }
    public Map<Integer, Boolean> getResultsDetail() { return resultsDetail; }
}

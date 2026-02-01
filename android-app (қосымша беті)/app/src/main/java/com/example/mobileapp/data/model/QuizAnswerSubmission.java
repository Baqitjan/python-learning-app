package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Corresponds to the QuizAnswerSubmission schema in Python.
 * Used to send the user's answers to the server for verification.
 */
public class QuizAnswerSubmission {

    @SerializedName("lesson_id")
    private int lessonId;

    // Using Map to represent the dictionary {question_id: answer_id}
    @SerializedName("submitted_answers")
    private Map<Integer, Integer> submittedAnswers;

    public QuizAnswerSubmission(int lessonId, Map<Integer, Integer> submittedAnswers) {
        this.lessonId = lessonId;
        this.submittedAnswers = submittedAnswers;
    }
}

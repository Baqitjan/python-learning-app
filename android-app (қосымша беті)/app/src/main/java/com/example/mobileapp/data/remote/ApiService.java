package com.example.mobileapp.data.remote;

import com.example.mobileapp.data.model.Achievement;
import com.example.mobileapp.data.model.AuthResponse;
import com.example.mobileapp.data.model.ChapterResponse;
import com.example.mobileapp.data.model.ExecuteRequest;
import com.example.mobileapp.data.model.ExecuteResponse;
import com.example.mobileapp.data.model.LeaderboardItem;
import com.example.mobileapp.data.model.LessonCompletionStatus;
import com.example.mobileapp.data.model.LessonDetailResponse;
import com.example.mobileapp.data.model.LessonSimpleResponse;
import com.example.mobileapp.data.model.ProfileResponse;
import com.example.mobileapp.data.model.Quiz;
import com.example.mobileapp.data.model.QuizAnswerSubmission;
import com.example.mobileapp.data.model.QuizResultResponse;
import com.example.mobileapp.data.model.UpdateProfileRequest;
import com.example.mobileapp.data.model.UserLoginRequest;
import com.example.mobileapp.data.model.UserRegistrationRequest;
import com.example.mobileapp.data.model.UserRegisterResponse;

// Чатбот модельдерін импорттау
import com.example.mobileapp.data.model.ChatRequest;
import com.example.mobileapp.data.model.ChatResponse;
// 💡 ЖАҢА ИМПОРТ: Чат тарихына арналған модель
import com.example.mobileapp.data.model.ChatMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Backend API-мен өзара әрекеттесуге арналған Retrofit интерфейсі.
 */
public interface ApiService {

    // --- Auth ---
    @POST("api/v1/auth/register")
    Call<UserRegisterResponse> register(@Body UserRegistrationRequest request);

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body UserLoginRequest request);

    // --- Profile ---
    @GET("api/v1/profile/{userId}/")
    Call<ProfileResponse> getProfile(@Path("userId") int userId);

    @PUT("api/v1/profile/{userId}/")
    Call<ProfileResponse> updateProfile(@Path("userId") int userId, @Body UpdateProfileRequest request);

    @GET("api/v1/profile/{userId}/achievements")
    Call<List<Achievement>> getAchievements(@Path("userId") int userId);

    // --- Content ---
    @GET("api/v1/chapters/")
    Call<List<ChapterResponse>> getChapters();

    @GET("api/v1/chapters/{chapterId}")
    Call<ChapterResponse> getChapterWithLessons(@Path("chapterId") int chapterId);

    @GET("api/v1/lessons/{lessonId}")
    Call<LessonDetailResponse> getLessonById(@Path("lessonId") int lessonId);

    @POST("api/v1/lessons/{lessonId}/complete")
    Call<LessonCompletionStatus> completeLesson(@Path("lessonId") int lessonId);

    // --- Quiz ---
    @GET("api/v1/quiz/lesson/{lesson_id}")
    Call<Quiz> getQuizByLessonId(@Path("lesson_id") int lessonId);

    @POST("api/v1/quiz/submit")
    Call<QuizResultResponse> submitQuiz(@Body QuizAnswerSubmission submission);

    // --- Compiler ---
    @POST("api/v1/compiler/execute")
    Call<ExecuteResponse> executeCode(@Body ExecuteRequest request);

    // --- Leaderboard ---
    @GET("api/v1/leaderboard/")
    Call<List<LeaderboardItem>> getLeaderboard();

    // --- Chatbot (ЖАҢА) ---
    // 💡 ТҮЗЕТУ 1: Чат тарихын алу endpoint-і
    @GET("api/v1/chatbot/history")
    Call<List<ChatMessage>> getChatHistory();

    // 💡 ТҮЗЕТУ 2: Хабарлама жіберу endpoint-і. Атауды "sendChatMessage" деп сақтаймыз.
    @POST("api/v1/chatbot/send")
    Call<ChatResponse> sendChatMessage(@Body ChatRequest request);
}
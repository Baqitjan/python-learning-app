package com.example.mobileapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp.MainApplication;
import com.example.mobileapp.data.local.AppDatabase;
import com.example.mobileapp.data.local.SavedScriptDao;
import com.example.mobileapp.data.model.ChapterResponse;
import com.example.mobileapp.data.model.ExecuteRequest;
import com.example.mobileapp.data.model.ExecuteResponse;
import com.example.mobileapp.data.model.LeaderboardItem;
import com.example.mobileapp.data.model.LessonCompletionStatus;
import com.example.mobileapp.data.model.LessonDetailResponse;
import com.example.mobileapp.data.model.Quiz;
import com.example.mobileapp.data.model.QuizAnswerSubmission;
import com.example.mobileapp.data.model.QuizResultResponse;
import com.example.mobileapp.data.model.SavedScript;
import com.example.mobileapp.data.remote.ApiClient;
import com.example.mobileapp.data.remote.ApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    private static final String TAG = "AppRepository";

    private final SavedScriptDao savedScriptDao;
    private final ApiService apiService;
    private final ExecutorService executorService;

    public AppRepository(Application application) {
        // MainApplication.getDatabase() және MainApplication.getExecutorService() сілтемелері дұрыс деп есептейміз
        AppDatabase db = MainApplication.getDatabase();
        this.savedScriptDao = db.savedScriptDao();
        this.apiService = ApiClient.getApiService();
        this.executorService = MainApplication.getExecutorService();
    }

    // --- Compiler and Saved Scripts ---
    public interface ExecuteCallback {
        void onResponse(ExecuteResponse response);
        void onFailure(Throwable t);
    }

    public void executeCode(ExecuteRequest request, ExecuteCallback callback) {
        apiService.executeCode(request).enqueue(new Callback<ExecuteResponse>() {
            @Override
            public void onResponse(Call<ExecuteResponse> call, Response<ExecuteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(new Exception("API call failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ExecuteResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public LiveData<List<SavedScript>> getAllScripts() {
        return savedScriptDao.getAllScripts();
    }

    // Бұл әдіс енді DAO-дағы insert() әдісін шақырады
    public void saveScript(SavedScript script) {
        executorService.execute(() -> savedScriptDao.insert(script));
    }

    // Бұл әдіс енді DAO-дағы delete() әдісін шақырады
    public void deleteScript(SavedScript script) {
        executorService.execute(() -> savedScriptDao.delete(script));
    }

    // --- Leaderboard ---
    public LiveData<List<LeaderboardItem>> getLeaderboard() {
        final MutableLiveData<List<LeaderboardItem>> data = new MutableLiveData<>();
        apiService.getLeaderboard().enqueue(new Callback<List<LeaderboardItem>>() {
            @Override
            public void onResponse(Call<List<LeaderboardItem>> call, Response<List<LeaderboardItem>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardItem>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch leaderboard", t);
            }
        });
        return data;
    }

    // --- Content (Chapters, Lessons, Quiz) ---
    public LiveData<List<ChapterResponse>> getAllChapters() {
        final MutableLiveData<List<ChapterResponse>> data = new MutableLiveData<>();
        apiService.getChapters().enqueue(new Callback<List<ChapterResponse>>() {
            @Override
            public void onResponse(Call<List<ChapterResponse>> call, Response<List<ChapterResponse>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChapterResponse>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch chapters", t);
            }
        });
        return data;
    }

    public LiveData<ChapterResponse> getChapterWithLessons(int chapterId) {
        final MutableLiveData<ChapterResponse> data = new MutableLiveData<>();
        apiService.getChapterWithLessons(chapterId).enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(Call<ChapterResponse> call, Response<ChapterResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChapterResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch chapter with lessons", t);
            }
        });
        return data;
    }

    public LiveData<LessonDetailResponse> getLessonById(int lessonId) {
        final MutableLiveData<LessonDetailResponse> data = new MutableLiveData<>();
        apiService.getLessonById(lessonId).enqueue(new Callback<LessonDetailResponse>() {
            @Override
            public void onResponse(Call<LessonDetailResponse> call, Response<LessonDetailResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<LessonDetailResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch lesson by id", t);
            }
        });
        return data;
    }

    public LiveData<LessonCompletionStatus> completeLesson(int lessonId) {
        final MutableLiveData<LessonCompletionStatus> data = new MutableLiveData<>();
        apiService.completeLesson(lessonId).enqueue(new Callback<LessonCompletionStatus>() {
            @Override
            public void onResponse(Call<LessonCompletionStatus> call, Response<LessonCompletionStatus> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<LessonCompletionStatus> call, Throwable t) {
                Log.e(TAG, "Failed to complete lesson: " + lessonId, t);
            }
        });
        return data;
    }

    public LiveData<Quiz> getQuizByLessonId(int lessonId) {
        MutableLiveData<Quiz> quizData = new MutableLiveData<>();
        apiService.getQuizByLessonId(lessonId).enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                if (response.isSuccessful()) {
                    quizData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Quiz> call, Throwable t) {
                Log.e(TAG, "Failed to get quiz for lesson: " + lessonId, t);
            }
        });
        return quizData;
    }

    public LiveData<QuizResultResponse> submitQuiz(QuizAnswerSubmission submission) {
        MutableLiveData<QuizResultResponse> resultData = new MutableLiveData<>();
        apiService.submitQuiz(submission).enqueue(new Callback<QuizResultResponse>() {
            @Override
            public void onResponse(Call<QuizResultResponse> call, Response<QuizResultResponse> response) {
                if (response.isSuccessful()) {
                    resultData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<QuizResultResponse> call, Throwable t) {
                Log.e(TAG, "Failed to submit quiz", t);
            }
        });
        return resultData;
    }
}
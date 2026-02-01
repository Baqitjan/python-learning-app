package com.example.mobileapp.ui.lessondetail;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.data.model.LessonDetailResponse;
import com.example.mobileapp.data.model.LessonCompletionStatus;
import com.example.mobileapp.data.repository.AppRepository;

public class LessonDetailViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final LiveData<LessonDetailResponse> lesson;
    private final int lessonId;

    public LessonDetailViewModel(@NonNull Application application, int lessonId) {
        super(application);
        this.lessonId = lessonId;
        repository = new AppRepository(application);
        lesson = repository.getLessonById(lessonId);
    }

    public LiveData<LessonDetailResponse> getLesson() {
        return lesson;
    }

    public LiveData<LessonCompletionStatus> completeLesson() {
        return repository.completeLesson(lessonId);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final int lessonId;

        public Factory(@NonNull Application application, int lessonId) {
            this.application = application;
            this.lessonId = lessonId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LessonDetailViewModel(application, lessonId);
        }
    }
}

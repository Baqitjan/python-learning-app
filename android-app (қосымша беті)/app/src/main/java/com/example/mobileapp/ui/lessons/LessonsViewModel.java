package com.example.mobileapp.ui.lessons;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.data.model.ChapterResponse;
import com.example.mobileapp.data.repository.AppRepository;

public class LessonsViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final LiveData<ChapterResponse> chapter;

    public LessonsViewModel(@NonNull Application application, int chapterId) {
        super(application);
        repository = new AppRepository(application);
        chapter = repository.getChapterWithLessons(chapterId);
    }

    public LiveData<ChapterResponse> getChapter() {
        return chapter;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final int chapterId;

        public Factory(@NonNull Application application, int chapterId) {
            this.application = application;
            this.chapterId = chapterId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LessonsViewModel(application, chapterId);
        }
    }
}

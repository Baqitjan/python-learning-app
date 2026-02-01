package com.example.mobileapp.ui.chapters;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.mobileapp.data.model.ChapterResponse;
import com.example.mobileapp.data.repository.AppRepository;

import java.util.List;

public class ChaptersViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final LiveData<List<ChapterResponse>> allChapters;

    public ChaptersViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        allChapters = repository.getAllChapters();
    }

    public LiveData<List<ChapterResponse>> getAllChapters() {
        return allChapters;
    }
}

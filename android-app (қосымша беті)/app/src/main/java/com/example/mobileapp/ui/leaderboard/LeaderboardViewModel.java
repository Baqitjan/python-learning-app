package com.example.mobileapp.ui.leaderboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileapp.data.model.LeaderboardItem;
import com.example.mobileapp.data.repository.AppRepository;

import java.util.List;

public class LeaderboardViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final LiveData<List<LeaderboardItem>> leaderboardData;

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        leaderboardData = repository.getLeaderboard();
    }

    public LiveData<List<LeaderboardItem>> getLeaderboardData() {
        return leaderboardData;
    }
}

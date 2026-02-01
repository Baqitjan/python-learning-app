package com.example.mobileapp.ui.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.mobileapp.data.model.ProfileResponse;
import com.example.mobileapp.data.model.UpdateProfileRequest;
import com.example.mobileapp.data.repository.AuthRepository;
import com.example.mobileapp.data.repository.ProfileRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository repository;
    private final SharedPreferences sharedPreferences;
    private final MediatorLiveData<ProfileResponse> userProfile = new MediatorLiveData<>();

    private LiveData<ProfileResponse> userProfileSource = null;

    public ProfileViewModel(Application application) {
        super(application);
        repository = new ProfileRepository(application);
        sharedPreferences = application.getSharedPreferences(AuthRepository.PREF_NAME, Context.MODE_PRIVATE);
        loadCurrentLoggedInUser();
    }

    private void loadCurrentLoggedInUser() {
        int userId = sharedPreferences.getInt(AuthRepository.KEY_USER_ID, -1);
        if (userId != -1) {
            loadData(userId);
        }
    }

    private void loadData(int userId) {
        if (userProfileSource != null) {
            userProfile.removeSource(userProfileSource);
        }
        userProfileSource = repository.getUserProfile(userId);
        userProfile.addSource(userProfileSource, userProfile::setValue);
    }

    public void saveProfile(String fullName, String username) {
        int userId = sharedPreferences.getInt(AuthRepository.KEY_USER_ID, -1);
        if (userId != -1) {
            UpdateProfileRequest request = new UpdateProfileRequest(fullName, username);
            LiveData<ProfileResponse> updatedProfileSource = repository.updateUserProfile(userId, request);
            userProfile.addSource(updatedProfileSource, updated -> {
                userProfile.setValue(updated);
                userProfile.removeSource(updatedProfileSource);
            });
        }
    }

    public LiveData<ProfileResponse> getUserProfile() {
        return userProfile;
    }
}

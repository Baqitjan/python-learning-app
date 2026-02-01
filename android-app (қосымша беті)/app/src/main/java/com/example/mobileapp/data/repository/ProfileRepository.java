package com.example.mobileapp.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp.data.model.ProfileResponse;
import com.example.mobileapp.data.model.UpdateProfileRequest;
import com.example.mobileapp.data.remote.ApiClient;
import com.example.mobileapp.data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private final ApiService apiService;

    public ProfileRepository(Application application) {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<ProfileResponse> getUserProfile(int userId) {
        MutableLiveData<ProfileResponse> data = new MutableLiveData<>();
        apiService.getProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // Handle failure
            }
        });
        return data;
    }

    public LiveData<ProfileResponse> updateUserProfile(int userId, UpdateProfileRequest request) {
        MutableLiveData<ProfileResponse> data = new MutableLiveData<>();
        apiService.updateProfile(userId, request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // Handle failure
            }
        });
        return data;
    }
}

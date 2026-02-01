package com.example.mobileapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp.data.model.AuthResponse;
import com.example.mobileapp.data.model.UserLoginRequest;
import com.example.mobileapp.data.model.UserRegistrationRequest;
import com.example.mobileapp.data.model.UserRegisterResponse;
import com.example.mobileapp.data.remote.ApiClient;
import com.example.mobileapp.data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private static final String TAG = "AuthRepository";
    public static final String PREF_NAME = "auth";
    public static final String KEY_TOKEN = "access_token";
    public static final String KEY_USER_ID = "user_id";

    private ApiService apiService;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();
    private final MutableLiveData<String> authError = new MutableLiveData<>();

    public AuthRepository(Application application) {
        // We get a fresh instance on creation
        this.apiService = ApiClient.getApiService();
        this.sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isAuthenticated.setValue(hasToken());
    }

    private boolean hasToken() {
        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    public LiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public void login(String username, String password) {
        apiService.login(new UserLoginRequest(username, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getAccessToken() != null) {
                    saveAuthData(response.body());
                    isAuthenticated.setValue(true);
                } else {
                    authError.setValue("Invalid credentials or server error.");
                    isAuthenticated.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Login failed", t);
                authError.setValue("Network error: " + t.getMessage());
                isAuthenticated.setValue(false);
            }
        });
    }

    public void register(String username, String email, String password) {
        apiService.register(new UserRegistrationRequest(username, email, password)).enqueue(new Callback<UserRegisterResponse>() {
            @Override
            public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                if (response.isSuccessful()) {
                    // Auto-login after successful registration
                    login(email, password);
                } else {
                    authError.setValue("Registration failed. User may already exist.");
                }
            }

            @Override
            public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                Log.e(TAG, "Registration failed", t);
                authError.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void logout() {
        sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_USER_ID).apply();
        isAuthenticated.setValue(false);
        ApiClient.resetApiService(); // Re-create client to clear old token
        apiService = ApiClient.getApiService(); // Get the new instance
    }

    private void saveAuthData(AuthResponse authResponse) {
        sharedPreferences.edit()
                .putString(KEY_TOKEN, authResponse.getAccessToken())
                .putInt(KEY_USER_ID, authResponse.getUserId())
                .apply();
        ApiClient.resetApiService(); // Re-create client with new token to apply interceptor
        apiService = ApiClient.getApiService(); // Get the new instance
    }
}

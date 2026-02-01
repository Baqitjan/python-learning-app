package com.example.mobileapp.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileapp.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final LiveData<Boolean> isAuthenticated; // Репозиторийден тікелей аламыз
    private final LiveData<String> authError;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
        isAuthenticated = repository.getIsAuthenticated(); // ОСЫ ЖЕРДЕ БАЙЛАНЫСТЫРАМЫЗ
        authError = repository.getAuthError();
    }

    // Енді бұл әдіс тікелей репозиторийдің LiveData-сын қайтарады
    public LiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public void login(String email, String password) {
        repository.login(email, password);
    }

    public void register(String username, String email, String password) {
        repository.register(username, email, password);
    }

    public void logout() {
        repository.logout();
    }
}

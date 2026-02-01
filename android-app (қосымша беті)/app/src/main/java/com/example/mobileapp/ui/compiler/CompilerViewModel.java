package com.example.mobileapp.ui.compiler;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp.data.local.AppDatabase;
import com.example.mobileapp.data.local.SavedScriptDao;
import com.example.mobileapp.data.model.ExecuteRequest; // ExecuteRequest моделі керек
import com.example.mobileapp.data.model.ExecuteResponse;
import com.example.mobileapp.data.model.SavedScript;
import com.example.mobileapp.data.remote.ApiClient; // ApiClient импорты (жолын тексеріңіз)

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Кодты орындауды басқаратын және скрипттерді локальді Room арқылы сақтайтын ViewModel.
 * ТҮЗЕТІЛГЕН НҰСҚА: Retrofit арқылы нақты API шақыру.
 */
public class CompilerViewModel extends AndroidViewModel {
    private static final String TAG = "CompilerViewModel";

    // Room компоненттері
    private final SavedScriptDao savedScriptDao;
    private final LiveData<List<SavedScript>> allScripts;

    private final MutableLiveData<String> currentCode = new MutableLiveData<>("");
    private final MutableLiveData<Event<ExecuteResponse>> executeResultEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public CompilerViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        savedScriptDao = db.savedScriptDao();
        allScripts = savedScriptDao.getAllScripts();
    }

    // --- EXECUTION & STATE ---

    public String getCurrentCode() {
        return currentCode.getValue();
    }

    public void setCurrentCode(String code) {
        currentCode.setValue(code);
    }

    public LiveData<Event<ExecuteResponse>> getExecuteResultEvent() {
        return executeResultEvent;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Backend API шақыруы (НАҚТЫ РЕАЛИЗАЦИЯ).
     * Енді имитация емес, Retrofit қолданылады.
     */
    public void executeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return;
        }

        isLoading.setValue(true);
        Log.d(TAG, "Executing code via API: " + code);

        // Сұраныс объектісін жасау (сізде ExecuteRequest моделі болуы керек)
        ExecuteRequest request = new ExecuteRequest(code);

        // ApiClient арқылы сұраныс жіберу
        // ТҮЗЕТУ: .getInstance() алып тасталды, себебі getApiService() статикалық әдіс.
        ApiClient.getApiService().executeCode(request).enqueue(new Callback<ExecuteResponse>() {
            @Override
            public void onResponse(Call<ExecuteResponse> call, Response<ExecuteResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API Success: " + response.body().getStatus());
                    executeResultEvent.setValue(new Event<>(response.body()));
                } else {
                    Log.e(TAG, "API Error: " + response.code());
                    ExecuteResponse errorResponse = new ExecuteResponse();
                    errorResponse.setStatus("API Error");
                    errorResponse.setOutput("Server error code: " + response.code());
                    executeResultEvent.setValue(new Event<>(errorResponse));
                }
            }

            @Override
            public void onFailure(Call<ExecuteResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Network Failure: " + t.getMessage());
                ExecuteResponse failResponse = new ExecuteResponse();
                failResponse.setStatus("Network Error");
                failResponse.setOutput("Failed to connect to server.\n" + t.getMessage());
                executeResultEvent.setValue(new Event<>(failResponse));
            }
        });
    }

    // --- ROOM DATABASE OPERATIONS ---

    public void saveScript(String name, String code) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            String newId = UUID.randomUUID().toString();
            SavedScript script = new SavedScript(newId, name, code);
            savedScriptDao.insert(script);
            Log.d(TAG, "Script saved/updated in Room: " + name);
        });
    }

    public LiveData<List<SavedScript>> getAllScripts() {
        return allScripts;
    }

    public void deleteScript(String scriptId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            SavedScript scriptToDelete = new SavedScript(scriptId, null, null);
            savedScriptDao.delete(scriptToDelete);
            Log.d(TAG, "Script deleted from Room with ID: " + scriptId);
        });
    }
}
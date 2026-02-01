package com.example.mobileapp.ui.chatbot;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp.data.model.ChatMessage;
import com.example.mobileapp.data.model.ChatRequest;
import com.example.mobileapp.data.model.ChatResponse;
import com.example.mobileapp.data.remote.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Чатботтың хабарлама жіберу және тарихын басқару логикасын ұстайтын ViewModel.
 */
public class ChatbotViewModel extends AndroidViewModel {
    private static final String TAG = "ChatbotViewModel";

    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorEvent = new MutableLiveData<>();

    // For formatting current time as String
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public ChatbotViewModel(@NonNull Application application) {
        super(application);
        loadChatHistory();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorEvent() {
        return errorEvent;
    }

    /**
     * Чат тарихын API арқылы жүктейді.
     */
    public void loadChatHistory() {
        isLoading.setValue(true);
        ApiClient.getApiService().getChatHistory().enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatMessage>> call, @NonNull Response<List<ChatMessage>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    messages.setValue(response.body());
                    Log.d(TAG, "Chat history loaded successfully.");
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
                    errorEvent.setValue("Чат тарихын жүктеу мүмкін болмады. Код: " + response.code() + ". " + errorBody);
                    Log.e(TAG, "Failed to load chat history: " + response.code() + " " + errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatMessage>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorEvent.setValue("Сервермен байланыс қатесі: " + t.getMessage());
                Log.e(TAG, "API call failed for chat history.", t);
            }
        });
    }

    /**
     * Қолданушының хабарламасын жібереді және чатботтың жауабын күтеді.
     */
    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String currentTime = isoDateFormat.format(new Date());

        // 1. Қолданушы хабарламасын қосу
        ChatMessage userMessage = new ChatMessage(0, text, "user", currentTime);
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(userMessage);
            messages.setValue(currentMessages);
        }

        // 2. Жүктеуді бастау
        isLoading.setValue(true);

        // 3. API сұранысын жіберу
        ChatRequest request = new ChatRequest(text);
        ApiClient.getApiService().sendChatMessage(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String botText = response.body().getResponseText();
                    String botResponseTime = isoDateFormat.format(new Date());

                    // 4. Чатботтың жауабын қосу
                    ChatMessage botMessage = new ChatMessage(0, botText, "bot", botResponseTime);

                    List<ChatMessage> updatedMessages = messages.getValue();
                    if (updatedMessages != null) {
                        updatedMessages.add(botMessage);
                        messages.setValue(updatedMessages);
                    }
                } else {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
                    errorEvent.setValue("Чатбот жауабын алу мүмкін болмады. Код: " + response.code() + ". " + errorBody);
                    Log.e(TAG, "Failed to get chatbot response: " + response.code() + " " + errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorEvent.setValue("Сервермен байланыс қатесі: " + t.getMessage());
                Log.e(TAG, "API call failed for send message.", t);
            }
        });
    }
}

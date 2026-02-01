package com.example.mobileapp.ui.chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.ChatMessage;

import java.util.ArrayList;

/**
 * Чатбот интерфейсін басқаратын Fragment.
 */
public class Chatbot extends Fragment {
    private static final String TAG = "ChatbotFragment";

    private ChatbotViewModel viewModel;
    private MessageAdapter adapter;
    private EditText inputEditText;
    private ImageButton sendButton;
    private RecyclerView recyclerView;

    // Сәлемдесу хабарламасын қосу үшін
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewModel-ді инициализациялау. Lifecycle-ды Fragment-ке байлаймыз.
        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI элементтерін инициализациялау (fragment_chatbot.xml-ден ID-лерді қолданамыз)
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        inputEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        // RecyclerView-ді орнату
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // ViewModel-дегі LiveData-дан бастапқы бос емес тізімді қолданамыз
        adapter = new MessageAdapter(requireContext(), new ArrayList<>(viewModel.getMessages().getValue()));
        recyclerView.setAdapter(adapter);

        // Әрқашан ең соңғы хабарламаға жылжыту
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);


        // --- LiveData-ны бақылау ---

        // 1. Хабарламалар тізімін бақылау
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            Log.d(TAG, "Messages updated: " + messages.size());
            adapter.updateMessages(messages);
            // Жаңа хабарлама келгенде ең соңына жылжыту
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        // 2. Жүктелу күйін бақылау
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Жүктелу күйінде енгізу өрісін өшіру
            inputEditText.setEnabled(!isLoading);
            sendButton.setEnabled(!isLoading);
            // Жүктелу индикаторын көрсету (Егер fragment_chatbot.xml-де болса)
            // Progressbar view.findViewById(R.id.loadingIndicator) арқылы қолданылуы мүмкін.
            if (isLoading) {
                Log.d(TAG, "Loading started...");
            } else {
                // Жіберуден кейін мәтін өрісін тазалау
                inputEditText.setText("");
                Log.d(TAG, "Loading finished.");
            }
        });

        // 3. Қате туралы хабарламаларды бақылау
        viewModel.getErrorEvent().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Қате: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // --- Батырманы басқару ---
        sendButton.setOnClickListener(v -> sendMessage());

        // EditText-те Enter батырмасын басқан кезде жіберу
        inputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    /**
     * Хабарламаны жіберу логикасы.
     */
    private void sendMessage() {
        String messageText = inputEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // ViewModel арқылы хабарламаны жіберу
            viewModel.sendMessage(messageText);

            // Мәтін өрісін тазалау (ViewModel-де тазаланған жақсы, бірақ UI-да да істеуге болады)
            // inputEditText.setText("");
        }
    }
}
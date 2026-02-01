package com.example.mobileapp.ui.compiler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.SavedScript;
import com.example.mobileapp.data.model.ExecuteResponse;
import com.example.mobileapp.ui.compiler.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.github.rosemoe.sora.widget.CodeEditor;

public class CompilerFragment extends Fragment implements SavedScriptAdapter.OnScriptInteractionListener {
    private static final String TAG = "CompilerFragment";

    private CompilerViewModel viewModel;
    private CodeEditor codeEditor;
    private Button runButton, saveButton, loadButton;
    private LiveData<List<SavedScript>> savedScriptsLiveData; // Room деректерін бақылау үшін

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CompilerViewModel.class);
        savedScriptsLiveData = viewModel.getAllScripts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compiler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        codeEditor = view.findViewById(R.id.codeEditor);
        runButton = view.findViewById(R.id.runButton);
        saveButton = view.findViewById(R.id.saveButton);
        loadButton = view.findViewById(R.id.loadButton);

        codeEditor.setText(viewModel.getCurrentCode());

        // 1. Код орындау жауабын бақылау (бұрынғыдай)
        viewModel.getExecuteResultEvent().observe(getViewLifecycleOwner(), (Event<ExecuteResponse> responseEvent) -> {
            ExecuteResponse response = responseEvent.getContentIfNotHandled();
            if (response == null) return;
            runButton.setEnabled(true);
            if (isResumed()) {
                try {
                    Navigation.findNavController(requireView()).navigate(R.id.action_compilerFragment_to_compilerOutputFragment);
                } catch (Exception e) {
                    Log.e(TAG, "Навигация қатесі: ", e);
                }
            }
        });

        // 2. RUN
        runButton.setOnClickListener(v -> {
            String code = codeEditor.getText().toString();
            if (!code.trim().isEmpty()) {
                runButton.setEnabled(false);
                Toast.makeText(getContext(), "Executing code...", Toast.LENGTH_SHORT).show();
                viewModel.executeCode(code);
            } else {
                Toast.makeText(getContext(), "Please enter some code.", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. SAVE
        saveButton.setOnClickListener(v -> showSaveDialog());

        // 4. LOAD (Енді LiveData-ны бақылау арқылы жұмыс істейді)
        loadButton.setOnClickListener(v -> showLoadDialog());
    }

    // ===================================
    // SAVE DIALOG (Room-ға сақтау)
    // ===================================

    private void showSaveDialog() {
        Context context = requireContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final TextInputEditText input = new TextInputEditText(context);
        input.setHint("Script Name");
        layout.addView(input);

        new MaterialAlertDialogBuilder(context)
                .setTitle("Save Script Locally (Room)")
                .setMessage("The script will be saved to your device's database.")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String scriptName = input.getText().toString().trim();
                    String codeContent = codeEditor.getText().toString();

                    if (scriptName.isEmpty() || codeContent.isEmpty()) {
                        Toast.makeText(context, "Name or code cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        // Room арқылы сақтау (ViewModel ішінде асинхронды орындалады)
                        viewModel.saveScript(scriptName, codeContent);
                        Toast.makeText(context, "Script '" + scriptName + "' saved to local DB!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Error saving script: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    // ===================================
    // LOAD DIALOG (Room-нан LiveData арқылы жүктеу)
    // ===================================
    private void showLoadDialog() {
        Context context = requireContext();

        // LiveData-ны бір рет бақылап, диалогты көрсету
        savedScriptsLiveData.observe(getViewLifecycleOwner(), new ListObserver(context, savedScriptsLiveData));
    }

    // LiveData-ны бақылауға арналған ішкі класс
    private class ListObserver implements androidx.lifecycle.Observer<List<SavedScript>>, AutoCloseable {
        private final Context context;
        private final LiveData<List<SavedScript>> liveData;
        private MaterialAlertDialogBuilder dialogBuilder;

        public ListObserver(Context context, LiveData<List<SavedScript>> liveData) {
            this.context = context;
            this.liveData = liveData;
        }

        @Override
        public void onChanged(List<SavedScript> scripts) {
            // Деректер өзгергенде немесе алғаш жүктелгенде шақырылады
            liveData.removeObserver(this); // Деректерді алғаннан кейін бақылауды тоқтату

            if (scripts == null || scripts.isEmpty()) {
                Toast.makeText(context, "No saved scripts found.", Toast.LENGTH_SHORT).show();
                return;
            }

            SavedScriptAdapter adapter = new SavedScriptAdapter(context, scripts, CompilerFragment.this);

            dialogBuilder = new MaterialAlertDialogBuilder(context)
                    .setTitle("Load Script from Local DB")
                    .setAdapter(adapter, null)
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

            dialogBuilder.show();
        }

        @Override
        public void close() throws Exception {
            liveData.removeObserver(this);
        }
    }


    // ===================================
    // OnScriptInteractionListener implementation
    // ===================================

    @Override
    public void onScriptClicked(SavedScript script) {
        codeEditor.setText(script.getCode());
        viewModel.setCurrentCode(script.getCode());
        Toast.makeText(requireContext(), "Script '" + script.getName() + "' loaded.", Toast.LENGTH_SHORT).show();

        // Диалогты автоматты түрде жабу үшін, сіз оны қолмен жабуыңыз керек еді (бұл қиын).
        // Қарапайымдық үшін, оны қолмен жабуды талап етіңіз.
    }

    @Override
    public void onDeleteClicked(SavedScript script) {
        Context context = requireContext();

        new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Script")
                .setMessage("Are you sure you want to delete '" + script.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Room арқылы жою (ViewModel ішінде асинхронды орындалады)
                    viewModel.deleteScript(script.getId());
                    Toast.makeText(context, "Script '" + script.getName() + "' deleted.", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    // Жоюдан кейін LiveData автоматты түрде жаңарып, диалогты қайта ашуға мүмкіндік береді,
                    // бірақ біз қарапайымдық үшін диалогты қайтадан қолмен ашамыз.
                    showLoadDialog();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }


    // ===================================
    // LIFECYCLE
    // ===================================

    @Override
    public void onPause() {
        super.onPause();
        if (codeEditor != null) {
            viewModel.setCurrentCode(codeEditor.getText().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView: Releasing CodeEditor with a delay.");
        if (codeEditor != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                if (codeEditor != null) {
                    try {
                        Log.d(TAG, "Delayed release: Releasing CodeEditor now.");
                        codeEditor.release();
                        codeEditor = null;
                    } catch (Exception e) {
                        Log.e(TAG, "Error during delayed release of CodeEditor", e);
                    }
                }
            }, 200);
        }
    }
}
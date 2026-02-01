package com.example.mobileapp.ui.compiler;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.ExecuteResponse;
// Event класын дұрыс импорттау үшін жоғарыдағы пакетті қолданамыз
import com.example.mobileapp.ui.compiler.Event;

public class CompilerOutputFragment extends Fragment {
    private CompilerViewModel viewModel;
    private TextView outputTextView;
    private ProgressBar loadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CompilerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compiler_output, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        outputTextView = view.findViewById(R.id.outputTextView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        // getIsLoading() енді CompilerViewModel-де бар
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getExecuteResultEvent().observe(getViewLifecycleOwner(), (Event<ExecuteResponse> responseEvent) -> {
            ExecuteResponse response = responseEvent.peekContent();

            if (response == null) {
                return;
            }

            loadingIndicator.setVisibility(View.GONE);

            // 1. Backend жауабын талдау (JSON-ға сәйкестендіру)
            String rawStatus = response.getStatus(); // Backend-тегі "status" өрісі
            String rawOutput = response.getOutput();
            String rawRuntime = response.getRuntime();

            boolean isError = false;

            // Егер статус null болса немесе "error" сөзін қамтыса, қате деп санаймыз
            if (rawStatus != null && rawStatus.toLowerCase().contains("error")) {
                isError = true;
            }

            // 2. Мәтінді дайындау
            String displayStatus = isError ? "Error" : "Success";
            String displayOutput = rawOutput;

            if (displayOutput == null || displayOutput.trim().isEmpty()) {
                // Егер output бос болса, бірақ бұл қате болса, қате туралы хабарламаны көрсетуге тырысамыз
                if (isError && response.getError() != null) {
                    displayOutput = response.getError();
                } else {
                    displayOutput = "[Output is empty]";
                }
            }

            String displayRuntime = (rawRuntime != null && !rawRuntime.isEmpty())
                    ? rawRuntime
                    : "[Runtime N/A]";

            // 3. SpannableStringBuilder арқылы барлық мәтінді БІРГЕ құрастыру
            SpannableStringBuilder builder = new SpannableStringBuilder();

            // A) Status бөлігін қосу
            builder.append("Status: ");

            int startSpan = builder.length();
            builder.append(displayStatus);
            int endSpan = builder.length();

            // Түсін қою (R.color.red_error және R.color.green_success ресурстары бар деп есептейміз)
            int colorResId = isError ? R.color.red_error : R.color.green_success;
            int color = ContextCompat.getColor(requireContext(), colorResId);
            builder.setSpan(new ForegroundColorSpan(color), startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append("\n\nOutput:\n");
            builder.append(displayOutput);
            builder.append("\n\nRuntime: ");
            builder.append(displayRuntime);

            outputTextView.setText(builder);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
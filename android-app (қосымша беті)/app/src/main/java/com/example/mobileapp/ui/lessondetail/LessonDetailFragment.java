package com.example.mobileapp.ui.lessondetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobileapp.R;
import com.google.android.material.textfield.TextInputLayout;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.glide.GlideImagesPlugin;

public class LessonDetailFragment extends Fragment {

    private LessonDetailViewModel viewModel;
    private int lessonId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lessonId = getArguments().getInt("lessonId");
        }

        LessonDetailViewModel.Factory factory = new LessonDetailViewModel.Factory(requireActivity().getApplication(), lessonId);
        viewModel = new ViewModelProvider(this, factory).get(LessonDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lesson_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView contentTextView = view.findViewById(R.id.lessonContentTextView);
        TextInputLayout codeSnippetLayout = view.findViewById(R.id.codeSnippetLayout);
        EditText codeSnippetEditText = view.findViewById(R.id.codeSnippetEditText);
        // Corrected the type from FloatingActionButton to Button
        Button runCodeButton = view.findViewById(R.id.runCodeButton);
        // Corrected the type from ExtendedFloatingActionButton to Button
        Button startQuizButton = view.findViewById(R.id.startQuizButton);

        final Markwon markwon = Markwon.builder(requireContext())
                .usePlugin(GlideImagesPlugin.create(requireContext()))
                .build();

        viewModel.getLesson().observe(getViewLifecycleOwner(), lesson -> {
            if (lesson != null) {
                markwon.setMarkdown(contentTextView, lesson.getContent());

                // Logic to show/hide buttons and code editor based on lesson content
                boolean isIntroChapter = lesson.getChapterId() == 1; // Assuming Chapter 1 is the introduction

                codeSnippetLayout.setVisibility(isIntroChapter ? View.GONE : View.VISIBLE);
                runCodeButton.setVisibility(isIntroChapter ? View.GONE : View.VISIBLE);

                if (isIntroChapter || lesson.isCompleted()) {
                    startQuizButton.setVisibility(View.GONE);
                } else {
                    startQuizButton.setVisibility(View.VISIBLE);
                }
            }
        });

        runCodeButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("code", codeSnippetEditText.getText().toString());
            NavHostFragment.findNavController(this).navigate(R.id.compilerOutputFragment, args);
        });

        startQuizButton.setOnClickListener(v -> {
            viewModel.completeLesson().observe(getViewLifecycleOwner(), status -> {
                if (status != null) {
                    Toast.makeText(getContext(), status.getMessage() + " (+ " + status.getXpGained() + " XP)", Toast.LENGTH_LONG).show();
                    Bundle args = new Bundle();
                    args.putInt("lessonId", lessonId);
                    NavHostFragment.findNavController(LessonDetailFragment.this)
                            .navigate(R.id.action_lessonDetailFragment_to_quizFragment, args);
                }
            });
        });
    }
}

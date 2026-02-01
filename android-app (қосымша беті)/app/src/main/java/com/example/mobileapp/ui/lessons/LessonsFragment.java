package com.example.mobileapp.ui.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

public class LessonsFragment extends Fragment {

    private LessonsViewModel lessonsViewModel;
    private LessonRefAdapter adapter;
    private int chapterId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chapterId = getArguments().getInt("chapterId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.lessonsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LessonRefAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(lesson -> {
            Bundle args = new Bundle();
            args.putInt("lessonId", lesson.getId());
            NavHostFragment.findNavController(LessonsFragment.this)
                    .navigate(R.id.action_lessonsFragment_to_lessonDetailFragment, args);
        });

        LessonsViewModel.Factory factory = new LessonsViewModel.Factory(requireActivity().getApplication(), chapterId);
        lessonsViewModel = new ViewModelProvider(this, factory).get(LessonsViewModel.class);

        lessonsViewModel.getChapter().observe(getViewLifecycleOwner(), chapterResponse -> {
            if (chapterResponse != null && chapterResponse.getLessons() != null) {
                adapter.setLessons(chapterResponse.getLessons());
            }
        });
    }
}

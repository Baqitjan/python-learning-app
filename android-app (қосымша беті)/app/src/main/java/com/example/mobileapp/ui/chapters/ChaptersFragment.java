package com.example.mobileapp.ui.chapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

public class ChaptersFragment extends Fragment {

    private ChaptersViewModel chaptersViewModel;
    private ChapterAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.chaptersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new ChapterAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(chapter -> {
            Bundle args = new Bundle();
            args.putInt("chapterId", chapter.getId());
            NavHostFragment.findNavController(ChaptersFragment.this)
                    .navigate(R.id.action_chaptersFragment_to_lessonsFragment, args);
        });

        chaptersViewModel = new ViewModelProvider(this).get(ChaptersViewModel.class);
        chaptersViewModel.getAllChapters().observe(getViewLifecycleOwner(), chapters -> {
            if (chapters != null) {
                adapter.setChapters(chapters);
            }
        });
    }
}
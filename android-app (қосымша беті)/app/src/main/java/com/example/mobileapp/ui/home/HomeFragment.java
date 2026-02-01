package com.example.mobileapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mobileapp.R;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView cardChapters = view.findViewById(R.id.card_chapters);

        cardChapters.setOnClickListener(v -> {
            // Find the NavController of the main graph from the Activity
            NavController mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            mainNavController.navigate(R.id.action_global_to_chaptersFragment);
        });
    }
}

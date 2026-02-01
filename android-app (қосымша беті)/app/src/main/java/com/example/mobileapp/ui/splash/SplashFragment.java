package com.example.mobileapp.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.example.mobileapp.R;
import com.example.mobileapp.ui.auth.AuthViewModel;

public class SplashFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel-ден аутентификация күйін бақылау
        authViewModel.getIsAuthenticated().observe(getViewLifecycleOwner(), isAuthenticated -> {
            // Бір сәт күту (мысалы, логотипті көрсету үшін)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (getView() == null) return; // Фрагмент жабылып қалса, ештеңе істемеу

                if (isAuthenticated) {
                    // Егер `true` болса, негізгі экранға өту
                    NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.action_splashFragment_to_homeFragment);
                } else {
                    // Егер `false` болса, логин экранына өту
                    NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.action_splashFragment_to_loginFragment);
                }
            }, 500); // 0.5 секунд күту (бұл міндетті емес, бірақ UX үшін жақсы)
        });
    }
}

package com.example.mobileapp.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp.R;
import com.example.mobileapp.ui.auth.AuthViewModel;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private AuthViewModel authViewModel;
    private CircleImageView profileImageView;
    private EditText fullNameEditText, usernameEditText;
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;
    private ImageButton settingsButton, editSaveButton, editProfileImageButton;
    private Uri tempImageUri;
    private boolean isEditing = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCameraInternal();
                } else {
                    Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onImageSelected
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            this::onPictureTaken
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.profileImageView);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        achievementsRecyclerView = view.findViewById(R.id.achievementsRecyclerView);
        settingsButton = view.findViewById(R.id.settingsButton);
        editSaveButton = view.findViewById(R.id.editSaveButton);
        editProfileImageButton = view.findViewById(R.id.editProfileImageButton);

        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        achievementAdapter = new AchievementAdapter();
        achievementsRecyclerView.setAdapter(achievementAdapter);

        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profileResponse -> {
            if (profileResponse != null) {
                fullNameEditText.setText(profileResponse.getFullName());
                usernameEditText.setText("@" + profileResponse.getUsername());
                Glide.with(this).load(profileResponse.getProfileImageUrl()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImageView);

                if (profileResponse.getAchievements() != null) {
                    achievementAdapter.setAchievements(profileResponse.getAchievements());
                }
            }
        });

        settingsButton.setOnClickListener(v -> showSettingsDialog());
        editProfileImageButton.setOnClickListener(v -> showImageSourceDialog());
        editSaveButton.setOnClickListener(v -> toggleEditSaveMode());
    }

    private void toggleEditSaveMode() {
        if (isEditing) {
            saveProfile();
        } else {
            setEditMode(true);
        }
    }

    private void setEditMode(boolean enable) {
        isEditing = enable;
        fullNameEditText.setEnabled(enable);
        usernameEditText.setEnabled(enable);
        editSaveButton.setImageResource(enable ? R.drawable.ic_save : R.drawable.ic_edit);
    }

    private void saveProfile() {
        String newFullName = fullNameEditText.getText().toString();
        String newUsername = usernameEditText.getText().toString().replace("@", "");

        viewModel.saveProfile(newFullName, newUsername);
        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();

        setEditMode(false);
    }

    private void showImageSourceDialog() {
        final String[] items = {"Галереядан таңдау", "Камерамен түсіру"};
        new AlertDialog.Builder(getContext()).setTitle("Суретті таңдаңыз").setItems(items, (dialog, which) -> {
            if (which == 0) {
                getContentLauncher.launch("image/*");
            } else if (which == 1) {
                checkCameraPermissionAndLaunch();
            }
        }).show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraInternal();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCameraInternal() {
        File imagePath = new File(requireContext().getCacheDir(), "images");
        if (!imagePath.exists()) imagePath.mkdirs();
        File newFile = new File(imagePath, "default_image.jpg");
        tempImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", newFile);
        takePictureLauncher.launch(tempImageUri);
    }

    private void onPictureTaken(boolean success) {
        if (success) {
            onImageSelected(tempImageUri);
        }
    }

    private void onImageSelected(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(profileImageView);
        }
    }

    private void showSettingsDialog() {
        final String[] items = {"Сменить тему", "Выйти из аккаунта"};
        new AlertDialog.Builder(getContext())
                .setTitle("Настройки")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        showThemeSelectionDialog();
                    } else if (which == 1) {
                        logout();
                    }
                })
                .show();
    }

    private void logout() {
        authViewModel.logout();
        NavController mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        mainNavController.navigate(R.id.action_global_authFragment);
    }

    private void showThemeSelectionDialog() {
        final String[] themes = {"По умолчанию", "Светлый", "Темный"};
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentTheme = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int checkedItem = (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) ? 1 : (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) ? 2 : 0;

        new AlertDialog.Builder(getContext())
                .setTitle("Выберите тему")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    int selectedMode = (which == 1) ? AppCompatDelegate.MODE_NIGHT_NO : (which == 2) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    AppCompatDelegate.setDefaultNightMode(selectedMode);
                    sharedPreferences.edit().putInt("night_mode", selectedMode).apply();
                    dialog.dismiss();
                })
                .show();
    }
}

package com.example.mobileapp;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.mobileapp.data.local.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication extends Application {

    private static Context appContext;
    private static AppDatabase database;
    private static ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newFixedThreadPool(4);

        // Apply theme based on saved preference
        int nightMode = getSharedPreferences("theme_prefs", MODE_PRIVATE)
                .getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public static Context getContext() {
        return appContext;
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}

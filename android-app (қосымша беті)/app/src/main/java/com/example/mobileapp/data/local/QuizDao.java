package com.example.mobileapp.data.local;

import androidx.room.Dao;

/**
 * This DAO is no longer used as Quizzes are fetched directly from the network.
 * It is kept for future local quiz features if needed.
 */
@Dao
public interface QuizDao {
    // No methods needed as we are not caching quizzes anymore.
}

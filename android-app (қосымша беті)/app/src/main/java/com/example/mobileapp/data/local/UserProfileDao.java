package com.example.mobileapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mobileapp.data.model.UserProfile;

@Dao
public interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Query("SELECT * FROM user_profile WHERE id = :userId")
    LiveData<UserProfile> getUserProfile(int userId);

    @Query("DELETE FROM user_profile")
    void deleteAll();
}

package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {

    // Server now expects a 'username' field which can be either username or email
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

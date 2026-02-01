package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the AuthRequest schema in Python.
 */
public class UserLoginRequest {

    @SerializedName("username") // Changed from "email" to "username" to match the server
    private String username;

    @SerializedName("password")
    private String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Corresponds to the UserRegistrationRequest schema in Python.
 */
public class UserRegistrationRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UserRegistrationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

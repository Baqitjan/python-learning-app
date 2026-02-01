package com.example.mobileapp.data.model;

public class UpdateProfileRequest {
    private String fullName;
    private String username;

    public UpdateProfileRequest(String fullName, String username) {
        this.fullName = fullName;
        this.username = username;
    }
}

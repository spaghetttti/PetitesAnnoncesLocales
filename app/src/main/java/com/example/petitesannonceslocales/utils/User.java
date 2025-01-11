package com.example.petitesannonceslocales.utils;

public class User {
    private String email;
    private String userType;

    // Default constructor required for Firestore
    public User() {}

    public User(String email, String userType) {
        this.email = email;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}


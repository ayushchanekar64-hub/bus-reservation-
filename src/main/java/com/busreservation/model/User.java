package com.busreservation.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model representing an authenticated system user.
 * Demonstrates Encapsulation and Data Security.
 */
public class User {
    private int userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String password;
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(int userId, String fullName, String email, String mobileNumber, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public User(String fullName, String email, String mobileNumber, String password) {
        this(0, fullName, email, mobileNumber, password);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return fullName + " (" + email + ")";
    }
}

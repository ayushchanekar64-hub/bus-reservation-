package com.busreservation.model;

import java.util.Objects;

/**
 * Model representing an individual passenger.
 * Demonstrates Encapsulation and Data Integrity.
 */
public class Passenger {
    private int passengerId;
    private String fullName;
    private int age;
    private String gender; // "Male", "Female", "Other"
    private String mobileNumber;
    private String email;

    public Passenger() {
    }

    public Passenger(int passengerId, String fullName, int age, String gender, String mobileNumber, String email) {
        this.passengerId = passengerId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    // Overloaded constructor for quick creation
    public Passenger(String fullName, int age, String gender, String mobileNumber) {
        this(0, fullName, age, gender, mobileNumber, null);
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger that)) return false;
        return Objects.equals(fullName, that.fullName) &&
               Objects.equals(mobileNumber, that.mobileNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, mobileNumber);
    }

    @Override
    public String toString() {
        return fullName + " (" + gender + ", " + age + "y) - " + mobileNumber;
    }
}

package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents a registered user (student) in the Kasnet Internship Portal.
 */
public class User {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       userId;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    passwordHash;
    private String    mobile;
    private String    countryCode;
    private String    role;
    private String    stream;
    private String    branch;
    private String    collegeName;
    private String    city;
    private String    referralCode;
    private boolean   isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public User() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public User(int userId, String firstName, String lastName, String email,
                String passwordHash, String mobile, String countryCode,
                String role, String stream, String collegeName, String city,
                String referralCode, boolean isActive,
                Timestamp createdAt, Timestamp updatedAt) {
        this.userId       = userId;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.mobile       = mobile;
        this.countryCode  = countryCode;
        this.role         = role;
        this.stream       = stream;
        this.collegeName  = collegeName;
        this.city         = city;
        this.referralCode = referralCode;
        this.isActive     = isActive;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Helper Method
    // -------------------------------------------------------------------------
    /**
     * Returns the full name by concatenating firstName and lastName.
     *
     * @return full name string
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", role='" + role + '\'' +
                ", stream='" + stream + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", city='" + city + '\'' +
                ", referralCode='" + referralCode + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

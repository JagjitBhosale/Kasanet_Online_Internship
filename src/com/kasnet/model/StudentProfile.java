package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents the extended profile of a student in the Kasnet Internship Portal.
 */
public class StudentProfile {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       profileId;
    private int       userId;
    private String    gender;
    private Date      dateOfBirth;
    private String    address;
    private String    state;
    private String    profilePhotoPath;
    private String    resumePath;
    private String    resumeFilename;
    private Timestamp resumeUploadedAt;
    private int       profileCompletion;
    private Timestamp lastLogin;
    private Timestamp previousLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public StudentProfile() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public StudentProfile(int profileId, int userId, String gender,
                          Date dateOfBirth, String address, String state,
                          String profilePhotoPath, String resumePath,
                          String resumeFilename, Timestamp resumeUploadedAt,
                          int profileCompletion, Timestamp lastLogin,
                          Timestamp previousLogin, Timestamp createdAt,
                          Timestamp updatedAt) {
        this.profileId         = profileId;
        this.userId            = userId;
        this.gender            = gender;
        this.dateOfBirth       = dateOfBirth;
        this.address           = address;
        this.state             = state;
        this.profilePhotoPath  = profilePhotoPath;
        this.resumePath        = resumePath;
        this.resumeFilename    = resumeFilename;
        this.resumeUploadedAt  = resumeUploadedAt;
        this.profileCompletion = profileCompletion;
        this.lastLogin         = lastLogin;
        this.previousLogin     = previousLogin;
        this.createdAt         = createdAt;
        this.updatedAt         = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public String getResumeFilename() { return resumeFilename; }
    public void setResumeFilename(String resumeFilename) { this.resumeFilename = resumeFilename; }

    public Timestamp getResumeUploadedAt() { return resumeUploadedAt; }
    public void setResumeUploadedAt(Timestamp resumeUploadedAt) { this.resumeUploadedAt = resumeUploadedAt; }

    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    public Timestamp getPreviousLogin() { return previousLogin; }
    public void setPreviousLogin(Timestamp previousLogin) { this.previousLogin = previousLogin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "StudentProfile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", state='" + state + '\'' +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", resumePath='" + resumePath + '\'' +
                ", resumeFilename='" + resumeFilename + '\'' +
                ", resumeUploadedAt=" + resumeUploadedAt +
                ", profileCompletion=" + profileCompletion +
                ", lastLogin=" + lastLogin +
                ", previousLogin=" + previousLogin +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

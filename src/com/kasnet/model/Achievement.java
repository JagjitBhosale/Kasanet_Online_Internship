package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents an achievement (award, publication, activity, etc.) of a student.
 */
public class Achievement {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       achievementId;
    private int       userId;
    private String    achievementType;
    private String    title;
    private String    description;
    private String    technology;
    private String    extraActivities;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Achievement() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Achievement(int achievementId, int userId, String achievementType,
                       String title, String description, String technology,
                       String extraActivities, Timestamp createdAt,
                       Timestamp updatedAt) {
        this.achievementId   = achievementId;
        this.userId          = userId;
        this.achievementType = achievementType;
        this.title           = title;
        this.description     = description;
        this.technology      = technology;
        this.extraActivities = extraActivities;
        this.createdAt       = createdAt;
        this.updatedAt       = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getAchievementId() { return achievementId; }
    public void setAchievementId(int achievementId) { this.achievementId = achievementId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAchievementType() { return achievementType; }
    public void setAchievementType(String achievementType) { this.achievementType = achievementType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getExtraActivities() { return extraActivities; }
    public void setExtraActivities(String extraActivities) { this.extraActivities = extraActivities; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Achievement{" +
                "achievementId=" + achievementId +
                ", userId=" + userId +
                ", achievementType='" + achievementType + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", technology='" + technology + '\'' +
                ", extraActivities='" + extraActivities + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

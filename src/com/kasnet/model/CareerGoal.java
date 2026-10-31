package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents a student's targeted career direction and preferences.
 * Stored against the student's user_id and serves as key contextual
 * criteria for future intelligent skill-gap analysis.
 */
public class CareerGoal {

    private int       goalId;
    private int       userId;
    private String    targetRole;
    private String    targetRoleOther;
    private String    preferredDomain;
    private String    preferredDomainOther;
    private String    careerObjective;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public CareerGoal() {}

    public CareerGoal(int goalId, int userId, String targetRole, String targetRoleOther,
                      String preferredDomain, String preferredDomainOther,
                      String careerObjective, Timestamp createdAt, Timestamp updatedAt) {
        this.goalId               = goalId;
        this.userId               = userId;
        this.targetRole           = targetRole;
        this.targetRoleOther      = targetRoleOther;
        this.preferredDomain      = preferredDomain;
        this.preferredDomainOther = preferredDomainOther;
        this.careerObjective      = careerObjective;
        this.createdAt            = createdAt;
        this.updatedAt            = updatedAt;
    }

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTargetRoleOther() { return targetRoleOther; }
    public void setTargetRoleOther(String targetRoleOther) { this.targetRoleOther = targetRoleOther; }

    public String getPreferredDomain() { return preferredDomain; }
    public void setPreferredDomain(String preferredDomain) { this.preferredDomain = preferredDomain; }

    public String getPreferredDomainOther() { return preferredDomainOther; }
    public void setPreferredDomainOther(String preferredDomainOther) { this.preferredDomainOther = preferredDomainOther; }

    public String getCareerObjective() { return careerObjective; }
    public void setCareerObjective(String careerObjective) { this.careerObjective = careerObjective; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Resolves the effective target role string, using targetRoleOther
     * if "Other" was selected.
     */
    public String getEffectiveTargetRole() {
        if ("Other".equalsIgnoreCase(targetRole) && targetRoleOther != null && !targetRoleOther.trim().isEmpty()) {
            return targetRoleOther.trim();
        }
        return targetRole != null ? targetRole.trim() : "";
    }

    /**
     * Resolves the effective preferred domain string, using preferredDomainOther
     * if "Other" was selected.
     */
    public String getEffectivePreferredDomain() {
        if ("Other".equalsIgnoreCase(preferredDomain) && preferredDomainOther != null && !preferredDomainOther.trim().isEmpty()) {
            return preferredDomainOther.trim();
        }
        return preferredDomain != null ? preferredDomain.trim() : "";
    }

    @Override
    public String toString() {
        return "CareerGoal{" +
                "goalId=" + goalId +
                ", userId=" + userId +
                ", targetRole='" + targetRole + '\'' +
                ", preferredDomain='" + preferredDomain + '\'' +
                '}';
    }
}

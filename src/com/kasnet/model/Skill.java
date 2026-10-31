package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents a technical or soft skill associated with a student.
 */
public class Skill {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       skillId;
    private int       userId;
    private String    skillName;
    private String    skillType;
    private String    proficiency;
    private Timestamp createdAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Skill() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Skill(int skillId, int userId, String skillName, String skillType,
                 String proficiency, Timestamp createdAt) {
        this.skillId     = skillId;
        this.userId      = userId;
        this.skillName   = skillName;
        this.skillType   = skillType;
        this.proficiency = proficiency;
        this.createdAt   = createdAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }

    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Skill{" +
                "skillId=" + skillId +
                ", userId=" + userId +
                ", skillName='" + skillName + '\'' +
                ", skillType='" + skillType + '\'' +
                ", proficiency='" + proficiency + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

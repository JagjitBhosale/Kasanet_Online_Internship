package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents professional work experience (internship, full-time, etc.)
 * of a student. Maintained as a separate entity from academic/personal projects.
 * Associated technologies represent empirical evidence of demonstrated skills.
 */
public class Experience {

    private int       experienceId;
    private int       userId;
    private String    companyName;
    private String    role;
    private String    employmentType; // Internship, Full-time, Part-time, Freelance, Contract
    private String    technologiesUsed;
    private String    description;
    private Date      startDate;
    private Date      endDate;
    private boolean   isCurrent;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Experience() {}

    public Experience(int experienceId, int userId, String companyName, String role,
                      String employmentType, String technologiesUsed, String description,
                      Date startDate, Date endDate, boolean isCurrent,
                      Timestamp createdAt, Timestamp updatedAt) {
        this.experienceId     = experienceId;
        this.userId           = userId;
        this.companyName      = companyName;
        this.role             = role;
        this.employmentType   = employmentType;
        this.technologiesUsed = technologiesUsed;
        this.description      = description;
        this.startDate        = startDate;
        this.endDate          = endDate;
        this.isCurrent        = isCurrent;
        this.createdAt        = createdAt;
        this.updatedAt        = updatedAt;
    }

    public int getExperienceId() { return experienceId; }
    public void setExperienceId(int experienceId) { this.experienceId = experienceId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getTechnologiesUsed() { return technologiesUsed; }
    public void setTechnologiesUsed(String technologiesUsed) { this.technologiesUsed = technologiesUsed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Experience{" +
                "experienceId=" + experienceId +
                ", userId=" + userId +
                ", companyName='" + companyName + '\'' +
                ", role='" + role + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", isCurrent=" + isCurrent +
                '}';
    }
}

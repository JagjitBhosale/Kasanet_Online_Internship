package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a project developed by a student during or before the internship.
 */
public class Project {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       projectId;
    private int       userId;
    private String    projectName;
    private String    description;
    private String    technologiesUsed;
    private String    role;
    private String    projectType;
    private int       teamSize;
    private Date      startDate;
    private Date      endDate;
    private String    projectUrl;
    private String    githubUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Project() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Project(int projectId, int userId, String projectName,
                   String description, String technologiesUsed, String role,
                   String projectType, int teamSize, Date startDate,
                   Date endDate, String projectUrl, String githubUrl,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.projectId        = projectId;
        this.userId           = userId;
        this.projectName      = projectName;
        this.description      = description;
        this.technologiesUsed = technologiesUsed;
        this.role             = role;
        this.projectType      = projectType;
        this.teamSize         = teamSize;
        this.startDate        = startDate;
        this.endDate          = endDate;
        this.projectUrl       = projectUrl;
        this.githubUrl        = githubUrl;
        this.createdAt        = createdAt;
        this.updatedAt        = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechnologiesUsed() { return technologiesUsed; }
    public void setTechnologiesUsed(String technologiesUsed) { this.technologiesUsed = technologiesUsed; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", userId=" + userId +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", technologiesUsed='" + technologiesUsed + '\'' +
                ", role='" + role + '\'' +
                ", projectType='" + projectType + '\'' +
                ", teamSize=" + teamSize +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", projectUrl='" + projectUrl + '\'' +
                ", githubUrl='" + githubUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

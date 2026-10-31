package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a competition participation record of a student.
 */
public class Competition {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       competitionId;
    private int       userId;
    private String    competitionName;
    private String    collegeOrganizer;
    private Date      eventDate;
    private String    projectName;
    private String    positionResult;
    private String    description;
    private String    teamName;
    private String    certificatePath;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Competition() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Competition(int competitionId, int userId, String competitionName,
                       String collegeOrganizer, Date eventDate, String projectName,
                       String positionResult, String description, String teamName,
                       String certificatePath, Timestamp createdAt,
                       Timestamp updatedAt) {
        this.competitionId    = competitionId;
        this.userId           = userId;
        this.competitionName  = competitionName;
        this.collegeOrganizer = collegeOrganizer;
        this.eventDate        = eventDate;
        this.projectName      = projectName;
        this.positionResult   = positionResult;
        this.description      = description;
        this.teamName         = teamName;
        this.certificatePath  = certificatePath;
        this.createdAt        = createdAt;
        this.updatedAt        = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getCompetitionId() { return competitionId; }
    public void setCompetitionId(int competitionId) { this.competitionId = competitionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }

    public String getCollegeOrganizer() { return collegeOrganizer; }
    public void setCollegeOrganizer(String collegeOrganizer) { this.collegeOrganizer = collegeOrganizer; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getPositionResult() { return positionResult; }
    public void setPositionResult(String positionResult) { this.positionResult = positionResult; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getCertificatePath() { return certificatePath; }
    public void setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Competition{" +
                "competitionId=" + competitionId +
                ", userId=" + userId +
                ", competitionName='" + competitionName + '\'' +
                ", collegeOrganizer='" + collegeOrganizer + '\'' +
                ", eventDate=" + eventDate +
                ", projectName='" + projectName + '\'' +
                ", positionResult='" + positionResult + '\'' +
                ", description='" + description + '\'' +
                ", teamName='" + teamName + '\'' +
                ", certificatePath='" + certificatePath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

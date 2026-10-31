package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents an educational qualification record of a student.
 */
public class Education {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       eduId;
    private int       userId;
    private String    level;
    private String    schoolCollegeName;
    private String    boardUniversity;
    private String    stream;
    private int       passingYear;
    private double    percentage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Education() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Education(int eduId, int userId, String level,
                     String schoolCollegeName, String boardUniversity,
                     String stream, int passingYear, double percentage,
                     Timestamp createdAt, Timestamp updatedAt) {
        this.eduId             = eduId;
        this.userId            = userId;
        this.level             = level;
        this.schoolCollegeName = schoolCollegeName;
        this.boardUniversity   = boardUniversity;
        this.stream            = stream;
        this.passingYear       = passingYear;
        this.percentage        = percentage;
        this.createdAt         = createdAt;
        this.updatedAt         = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getEduId() { return eduId; }
    public void setEduId(int eduId) { this.eduId = eduId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getSchoolCollegeName() { return schoolCollegeName; }
    public void setSchoolCollegeName(String schoolCollegeName) { this.schoolCollegeName = schoolCollegeName; }

    public String getBoardUniversity() { return boardUniversity; }
    public void setBoardUniversity(String boardUniversity) { this.boardUniversity = boardUniversity; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public int getPassingYear() { return passingYear; }
    public void setPassingYear(int passingYear) { this.passingYear = passingYear; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Education{" +
                "eduId=" + eduId +
                ", userId=" + userId +
                ", level='" + level + '\'' +
                ", schoolCollegeName='" + schoolCollegeName + '\'' +
                ", boardUniversity='" + boardUniversity + '\'' +
                ", stream='" + stream + '\'' +
                ", passingYear=" + passingYear +
                ", percentage=" + percentage +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

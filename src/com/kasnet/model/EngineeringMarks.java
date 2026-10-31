package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents year-wise academic performance for engineering students.
 * Supports both Percentage and CGPA for First Year, Second Year, Third Year, and Final Year.
 */
public class EngineeringMarks {

    private int markId;
    private int userId;
    private String yearLevel;   // "First Year", "Second Year", "Third Year", "Final Year"
    private String scoreType;   // "Percentage" or "CGPA"
    private double scoreValue;  // e.g. 85.50 or 8.75
    private int passingYear;
    private String collegeName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public EngineeringMarks() {}

    public EngineeringMarks(int markId, int userId, String yearLevel, String scoreType,
                            double scoreValue, int passingYear, String collegeName,
                            Timestamp createdAt, Timestamp updatedAt) {
        this.markId = markId;
        this.userId = userId;
        this.yearLevel = yearLevel;
        this.scoreType = scoreType;
        this.scoreValue = scoreValue;
        this.passingYear = passingYear;
        this.collegeName = collegeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }

    public String getScoreType() { return scoreType; }
    public void setScoreType(String scoreType) { this.scoreType = scoreType; }

    public double getScoreValue() { return scoreValue; }
    public void setScoreValue(double scoreValue) { this.scoreValue = scoreValue; }

    public int getPassingYear() { return passingYear; }
    public void setPassingYear(int passingYear) { this.passingYear = passingYear; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Formatted score string (e.g., "82.50 %" or "8.75 CGPA").
     */
    public String getFormattedScore() {
        if ("CGPA".equalsIgnoreCase(scoreType)) {
            return String.format("%.2f CGPA", scoreValue);
        }
        return String.format("%.2f %%", scoreValue);
    }

    @Override
    public String toString() {
        return "EngineeringMarks{" +
                "markId=" + markId +
                ", userId=" + userId +
                ", yearLevel='" + yearLevel + '\'' +
                ", scoreType='" + scoreType + '\'' +
                ", scoreValue=" + scoreValue +
                ", passingYear=" + passingYear +
                ", collegeName='" + collegeName + '\'' +
                '}';
    }
}

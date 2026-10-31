package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents a student enrollment in a course.
 */
public class Enrollment {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       enrollmentId;
    private int       userId;
    private int       courseId;
    private int       progressPercentage;
    private int       completedLessons;
    private Timestamp enrolledAt;
    private Timestamp completedAt;

    // Transient fields (populated by JOIN)
    private String    courseTitle;
    private String    userName;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Enrollment() {}

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }

    public Timestamp getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Timestamp enrolledAt) { this.enrolledAt = enrolledAt; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", progressPercentage=" + progressPercentage +
                ", completedLessons=" + completedLessons +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
}

package com.kasnet.model;

/**
 * Aggregated statistics object used to populate the student home.
 * This is a flat DTO assembled via multiple DAO queries; it has no
 * direct database table mapping of its own.
 */
public class HomeStats {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private String studentName;
    private int    profileCompletion;
    private int    totalAssignments;
    private int    pendingAssignments;
    private int    submittedAssignments;
    private int    evaluatedAssignments;
    private int    totalLectures;
    private int    completedLectures;
    private int    certificationsCount;
    private int    projectsCount;
    private int    hackathonsCount;
    private int    competitionsCount;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public HomeStats() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public HomeStats(String studentName, int profileCompletion,
                          int totalAssignments, int pendingAssignments,
                          int submittedAssignments, int evaluatedAssignments,
                          int totalLectures, int completedLectures,
                          int certificationsCount, int projectsCount,
                          int hackathonsCount, int competitionsCount) {
        this.studentName          = studentName;
        this.profileCompletion    = profileCompletion;
        this.totalAssignments     = totalAssignments;
        this.pendingAssignments   = pendingAssignments;
        this.submittedAssignments = submittedAssignments;
        this.evaluatedAssignments = evaluatedAssignments;
        this.totalLectures        = totalLectures;
        this.completedLectures    = completedLectures;
        this.certificationsCount  = certificationsCount;
        this.projectsCount        = projectsCount;
        this.hackathonsCount      = hackathonsCount;
        this.competitionsCount    = competitionsCount;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }

    public int getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(int totalAssignments) { this.totalAssignments = totalAssignments; }

    public int getPendingAssignments() { return pendingAssignments; }
    public void setPendingAssignments(int pendingAssignments) { this.pendingAssignments = pendingAssignments; }

    public int getSubmittedAssignments() { return submittedAssignments; }
    public void setSubmittedAssignments(int submittedAssignments) { this.submittedAssignments = submittedAssignments; }

    public int getEvaluatedAssignments() { return evaluatedAssignments; }
    public void setEvaluatedAssignments(int evaluatedAssignments) { this.evaluatedAssignments = evaluatedAssignments; }

    public int getTotalLectures() { return totalLectures; }
    public void setTotalLectures(int totalLectures) { this.totalLectures = totalLectures; }

    public int getCompletedLectures() { return completedLectures; }
    public void setCompletedLectures(int completedLectures) { this.completedLectures = completedLectures; }

    public int getCertificationsCount() { return certificationsCount; }
    public void setCertificationsCount(int certificationsCount) { this.certificationsCount = certificationsCount; }

    public int getProjectsCount() { return projectsCount; }
    public void setProjectsCount(int projectsCount) { this.projectsCount = projectsCount; }

    public int getHackathonsCount() { return hackathonsCount; }
    public void setHackathonsCount(int hackathonsCount) { this.hackathonsCount = hackathonsCount; }

    public int getCompetitionsCount() { return competitionsCount; }
    public void setCompetitionsCount(int competitionsCount) { this.competitionsCount = competitionsCount; }

    // -------------------------------------------------------------------------
    // Convenience helpers
    // -------------------------------------------------------------------------
    /**
     * Returns the percentage of lectures completed (0–100).
     * Returns 0 if totalLectures is zero to avoid division-by-zero.
     *
     * @return lecture completion percentage
     */
    public int getLectureCompletionPercent() {
        if (totalLectures == 0) return 0;
        return (int) Math.round((completedLectures * 100.0) / totalLectures);
    }

    /**
     * Returns the percentage of assignments submitted (0–100).
     * Returns 0 if totalAssignments is zero to avoid division-by-zero.
     *
     * @return assignment submission percentage
     */
    public int getAssignmentSubmissionPercent() {
        if (totalAssignments == 0) return 0;
        return (int) Math.round((submittedAssignments * 100.0) / totalAssignments);
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "homeStats{" +
                "studentName='" + studentName + '\'' +
                ", profileCompletion=" + profileCompletion +
                ", totalAssignments=" + totalAssignments +
                ", pendingAssignments=" + pendingAssignments +
                ", submittedAssignments=" + submittedAssignments +
                ", evaluatedAssignments=" + evaluatedAssignments +
                ", totalLectures=" + totalLectures +
                ", completedLectures=" + completedLectures +
                ", certificationsCount=" + certificationsCount +
                ", projectsCount=" + projectsCount +
                ", hackathonsCount=" + hackathonsCount +
                ", competitionsCount=" + competitionsCount +
                '}';
    }
}

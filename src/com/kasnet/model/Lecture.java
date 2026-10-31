package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a lecture/video lesson in the Kasnet Internship Portal.
 * Progress-tracking fields (isCompleted, watchProgressPercent, lastWatched)
 * are populated via JOIN with the student-progress table when needed.
 */
public class Lecture {

    // -------------------------------------------------------------------------
    // Lecture (core) Fields
    // -------------------------------------------------------------------------
    private int       lectureId;
    private String    title;
    private String    subject;
    private String    description;
    private String    instructor;
    private int       durationMinutes;
    private String    videoUrl;
    private int       lectureNumber;
    private Date      lectureDate;
    private boolean   isActive;
    private Timestamp createdAt;

    // -------------------------------------------------------------------------
    // Course Video System Fields (new)
    // -------------------------------------------------------------------------
    private int       courseId;               // FK to courses table
    private String    cloudinaryPublicId;     // Cloudinary public_id (NEVER a permanent URL)
    private int       durationSeconds;        // Video duration in seconds
    private boolean   isPreview;              // If true, accessible without enrollment
    private String    status;                 // DRAFT, PUBLISHED

    // -------------------------------------------------------------------------
    // Progress-Tracking Fields  (populated by JOIN queries; default false/0/null)
    // -------------------------------------------------------------------------
    private boolean   isCompleted;
    private int       watchProgressPercent;
    private int       progressSeconds;        // Watch position in seconds
    private Timestamp lastWatched;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Lecture() {}

    // -------------------------------------------------------------------------
    // All-args Constructor (original fields)
    // -------------------------------------------------------------------------
    public Lecture(int lectureId, String title, String subject,
                   String description, String instructor, int durationMinutes,
                   String videoUrl, int lectureNumber, Date lectureDate,
                   boolean isActive, Timestamp createdAt,
                   boolean isCompleted, int watchProgressPercent,
                   Timestamp lastWatched) {
        this.lectureId            = lectureId;
        this.title                = title;
        this.subject              = subject;
        this.description          = description;
        this.instructor           = instructor;
        this.durationMinutes      = durationMinutes;
        this.videoUrl             = videoUrl;
        this.lectureNumber        = lectureNumber;
        this.lectureDate          = lectureDate;
        this.isActive             = isActive;
        this.createdAt            = createdAt;
        this.isCompleted          = isCompleted;
        this.watchProgressPercent = watchProgressPercent;
        this.lastWatched          = lastWatched;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters  — Lecture (core)
    // -------------------------------------------------------------------------
    public int getLectureId() { return lectureId; }
    public void setLectureId(int lectureId) { this.lectureId = lectureId; }
    public int getId() { return lectureId; }
    public void setId(int id) { this.lectureId = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public int getDurationMinutes() {
        if (durationMinutes > 0) return durationMinutes;
        if (durationSeconds > 0) return (durationSeconds + 59) / 60;
        return 0;
    }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getLectureNumber() { return lectureNumber; }
    public void setLectureNumber(int lectureNumber) { this.lectureNumber = lectureNumber; }

    public Date getLectureDate() { return lectureDate; }
    public void setLectureDate(Date lectureDate) { this.lectureDate = lectureDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // -------------------------------------------------------------------------
    // Getters and Setters  — Course Video System (new)
    // -------------------------------------------------------------------------
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public boolean isPreview() { return isPreview; }
    public void setPreview(boolean isPreview) { this.isPreview = isPreview; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns true if this lecture has a video available (either Cloudinary or legacy URL).
     */
    public boolean hasVideo() {
        return (cloudinaryPublicId != null && !cloudinaryPublicId.isEmpty())
            || (videoUrl != null && !videoUrl.isEmpty());
    }

    /**
     * Returns the duration formatted as mm:ss.
     */
    public String getDurationFormatted() {
        int totalSeconds = durationSeconds > 0 ? durationSeconds : durationMinutes * 60;
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    // -------------------------------------------------------------------------
    // Getters and Setters  — Progress (joined)
    // -------------------------------------------------------------------------
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }

    public int getWatchProgressPercent() { return watchProgressPercent; }
    public void setWatchProgressPercent(int watchProgressPercent) { this.watchProgressPercent = watchProgressPercent; }

    public int getProgressSeconds() { return progressSeconds; }
    public void setProgressSeconds(int progressSeconds) { this.progressSeconds = progressSeconds; }

    public Timestamp getLastWatched() { return lastWatched; }
    public void setLastWatched(Timestamp lastWatched) { this.lastWatched = lastWatched; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Lecture{" +
                "lectureId=" + lectureId +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", courseId=" + courseId +
                ", lectureNumber=" + lectureNumber +
                ", isPreview=" + isPreview +
                ", status='" + status + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}

package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents a course in the Kasnet Learning Platform.
 */
public class Course {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       courseId;
    private String    title;
    private String    slug;
    private String    description;
    private String    thumbnailUrl;
    private String    instructorName;
    private String    category;
    private String    difficulty;      // Beginner, Intermediate, Advanced
    private int       durationWeeks;
    private int       totalLessons;
    private String    status;          // DRAFT, PUBLISHED, ARCHIVED
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // Transient / computed fields (populated by queries, not stored in DB)
    // -------------------------------------------------------------------------
    private int       lectureCount;    // actual count of published lectures
    private int       enrollmentCount; // number of enrolled students
    private boolean   enrolled;        // whether the current user is enrolled
    private int       userProgress;    // current user's progress percentage
    private int       userCompletedLessons; // current user's completed lesson count

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Course() {}

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getId() { return courseId; }
    public void setId(int id) { this.courseId = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(int durationWeeks) { this.durationWeeks = durationWeeks; }

    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // --- Transient getters/setters ---
    public int getLectureCount() { return lectureCount; }
    public void setLectureCount(int lectureCount) { this.lectureCount = lectureCount; }

    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }

    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }

    public int getUserProgress() { return userProgress; }
    public void setUserProgress(int userProgress) { this.userProgress = userProgress; }

    public int getUserCompletedLessons() { return userCompletedLessons; }
    public void setUserCompletedLessons(int userCompletedLessons) { this.userCompletedLessons = userCompletedLessons; }

    /**
     * Generates a slug from the title if one isn't set.
     */
    public String generateSlug() {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", instructorName='" + instructorName + '\'' +
                '}';
    }
}

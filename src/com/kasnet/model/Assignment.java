package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents an assignment posted in the Kasnet Internship Portal.
 * Also carries submission-side fields populated via JOIN queries,
 * so a single object can be used in both assignment-list and
 * student-submission views without requiring a separate DTO.
 */
public class Assignment {

    // -------------------------------------------------------------------------
    // Assignment (core) Fields
    // -------------------------------------------------------------------------
    private int       assignmentId;
    private String    title;
    private String    subject;
    private String    description;
    private String    instructionsText;
    private String    instructionPdfPath;
    private String    assignmentNumber;
    private Date      dueDate;
    private int       totalMarks;
    private boolean   isActive;
    private Timestamp createdAt;

    // -------------------------------------------------------------------------
    // Submission Fields  (populated by JOIN queries; null when not applicable)
    // -------------------------------------------------------------------------
    private int       submissionId;
    private Timestamp submissionDate;
    private String    filePath;
    private String    status;
    private int       marksObtained;
    private String    remarks;
    private boolean   resubmitted;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Assignment() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Assignment(int assignmentId, String title, String subject,
                      String description, String assignmentNumber, Date dueDate,
                      int totalMarks, boolean isActive, Timestamp createdAt,
                      int submissionId, Timestamp submissionDate, String filePath,
                      String status, int marksObtained, String remarks,
                      boolean resubmitted) {
        this.assignmentId     = assignmentId;
        this.title            = title;
        this.subject          = subject;
        this.description      = description;
        this.assignmentNumber = assignmentNumber;
        this.dueDate          = dueDate;
        this.totalMarks       = totalMarks;
        this.isActive         = isActive;
        this.createdAt        = createdAt;
        this.submissionId     = submissionId;
        this.submissionDate   = submissionDate;
        this.filePath         = filePath;
        this.status           = status;
        this.marksObtained    = marksObtained;
        this.remarks          = remarks;
        this.resubmitted      = resubmitted;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters  — Assignment (core)
    // -------------------------------------------------------------------------
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructionsText() { return instructionsText; }
    public void setInstructionsText(String instructionsText) { this.instructionsText = instructionsText; }

    public String getInstructionPdfPath() { return instructionPdfPath; }
    public void setInstructionPdfPath(String instructionPdfPath) { this.instructionPdfPath = instructionPdfPath; }

    public String getAssignmentNumber() { return assignmentNumber; }
    public void setAssignmentNumber(String assignmentNumber) { this.assignmentNumber = assignmentNumber; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // -------------------------------------------------------------------------
    // Getters and Setters  — Submission (joined)
    // -------------------------------------------------------------------------
    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public Timestamp getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Timestamp submissionDate) { this.submissionDate = submissionDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public boolean isResubmitted() { return resubmitted; }
    public void setResubmitted(boolean resubmitted) { this.resubmitted = resubmitted; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId=" + assignmentId +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", assignmentNumber='" + assignmentNumber + '\'' +
                ", dueDate=" + dueDate +
                ", totalMarks=" + totalMarks +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", submissionId=" + submissionId +
                ", submissionDate=" + submissionDate +
                ", filePath='" + filePath + '\'' +
                ", status='" + status + '\'' +
                ", marksObtained=" + marksObtained +
                ", remarks='" + remarks + '\'' +
                ", resubmitted=" + resubmitted +
                '}';
    }
}

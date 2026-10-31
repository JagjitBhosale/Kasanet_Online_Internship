package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a professional certification earned by a student.
 */
public class Certification {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       certId;
    private int       userId;
    private String    certName;
    private String    provider;
    private String    technology;
    private String    certIdNumber;
    private Date      issueDate;
    private Date      expiryDate;
    private String    credentialUrl;
    private String    studentName;
    private String    studentEmail;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Certification() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Certification(int certId, int userId, String certName,
                         String provider, String technology, String certIdNumber,
                         Date issueDate, Date expiryDate, String credentialUrl,
                         Timestamp createdAt, Timestamp updatedAt) {
        this.certId        = certId;
        this.userId        = userId;
        this.certName      = certName;
        this.provider      = provider;
        this.technology    = technology;
        this.certIdNumber  = certIdNumber;
        this.issueDate     = issueDate;
        this.expiryDate    = expiryDate;
        this.credentialUrl = credentialUrl;
        this.createdAt     = createdAt;
        this.updatedAt     = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getCertId() { return certId; }
    public void setCertId(int certId) { this.certId = certId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCertName() { return certName; }
    public void setCertName(String certName) { this.certName = certName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getCertIdNumber() { return certIdNumber; }
    public void setCertIdNumber(String certIdNumber) { this.certIdNumber = certIdNumber; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public String getStudentName() { return studentName != null ? studentName : ""; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail != null ? studentEmail : ""; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCredentialId() { return certIdNumber; }
    public void setCredentialId(String credentialId) { this.certIdNumber = credentialId; }

    public String getIssuingOrganization() { return provider; }
    public void setIssuingOrganization(String issuingOrganization) { this.provider = issuingOrganization; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Certification{" +
                "certId=" + certId +
                ", userId=" + userId +
                ", certName='" + certName + '\'' +
                ", provider='" + provider + '\'' +
                ", technology='" + technology + '\'' +
                ", certIdNumber='" + certIdNumber + '\'' +
                ", issueDate=" + issueDate +
                ", expiryDate=" + expiryDate +
                ", credentialUrl='" + credentialUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

package com.kasnet.model;

import java.math.BigDecimal;
import java.sql.Date;

public class PlatformCertificate {

    private int certificateId;
    private int userId;
    private String examName;
    private BigDecimal scoreObtained;
    private BigDecimal totalScore;
    private Date achievementDate;
    private String studentName;
    private String studentEmail;

    public int getCertificateId() { return certificateId; }
    public void setCertificateId(int certificateId) { this.certificateId = certificateId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public BigDecimal getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(BigDecimal scoreObtained) { this.scoreObtained = scoreObtained; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public Date getAchievementDate() { return achievementDate; }
    public void setAchievementDate(Date achievementDate) { this.achievementDate = achievementDate; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
}

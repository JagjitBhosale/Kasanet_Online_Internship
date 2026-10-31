package com.kasnet.model;

import java.math.BigDecimal;
import java.sql.Date;

public class PlatformProjectReport {

    private int reportId;
    private int userId;
    private String projectName;
    private String scopeDetails;
    private String projectDomain;
    private String projectTechnology;
    private String projectApplication;
    private String projectDuration;
    private String projectModel;
    private String requirementGatheringTime;
    private String projectAnalysisTime;
    private String projectDesigningTime;
    private String projectCodingTime;
    private String projectTestingTime;
    private BigDecimal projectCost;
    private String testingType;
    private String testingDuration;
    private int issuesFound;
    private String knowledgeRequirement;
    private String knowledgeAnalysis;
    private String knowledgeDesigning;
    private String knowledgeCoding;
    private String knowledgeTesting;
    private String knowledgePostImplementation;
    private Date createdAt;
    private String studentName;
    private String studentEmail;
    private String studentMobile;

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getScopeDetails() { return scopeDetails; }
    public void setScopeDetails(String scopeDetails) { this.scopeDetails = scopeDetails; }
    public String getProjectDomain() { return projectDomain; }
    public void setProjectDomain(String projectDomain) { this.projectDomain = projectDomain; }
    public String getProjectTechnology() { return projectTechnology; }
    public void setProjectTechnology(String projectTechnology) { this.projectTechnology = projectTechnology; }
    public String getProjectApplication() { return projectApplication; }
    public void setProjectApplication(String projectApplication) { this.projectApplication = projectApplication; }
    public String getProjectDuration() { return projectDuration; }
    public void setProjectDuration(String projectDuration) { this.projectDuration = projectDuration; }
    public String getProjectModel() { return projectModel; }
    public void setProjectModel(String projectModel) { this.projectModel = projectModel; }
    public String getRequirementGatheringTime() { return requirementGatheringTime; }
    public void setRequirementGatheringTime(String value) { this.requirementGatheringTime = value; }
    public String getProjectAnalysisTime() { return projectAnalysisTime; }
    public void setProjectAnalysisTime(String value) { this.projectAnalysisTime = value; }
    public String getProjectDesigningTime() { return projectDesigningTime; }
    public void setProjectDesigningTime(String value) { this.projectDesigningTime = value; }
    public String getProjectCodingTime() { return projectCodingTime; }
    public void setProjectCodingTime(String value) { this.projectCodingTime = value; }
    public String getProjectTestingTime() { return projectTestingTime; }
    public void setProjectTestingTime(String value) { this.projectTestingTime = value; }
    public BigDecimal getProjectCost() { return projectCost; }
    public void setProjectCost(BigDecimal projectCost) { this.projectCost = projectCost; }
    public String getTestingType() { return testingType; }
    public void setTestingType(String testingType) { this.testingType = testingType; }
    public String getTestingDuration() { return testingDuration; }
    public void setTestingDuration(String testingDuration) { this.testingDuration = testingDuration; }
    public int getIssuesFound() { return issuesFound; }
    public void setIssuesFound(int issuesFound) { this.issuesFound = issuesFound; }
    public String getKnowledgeRequirement() { return knowledgeRequirement; }
    public void setKnowledgeRequirement(String value) { this.knowledgeRequirement = value; }
    public String getKnowledgeAnalysis() { return knowledgeAnalysis; }
    public void setKnowledgeAnalysis(String value) { this.knowledgeAnalysis = value; }
    public String getKnowledgeDesigning() { return knowledgeDesigning; }
    public void setKnowledgeDesigning(String value) { this.knowledgeDesigning = value; }
    public String getKnowledgeCoding() { return knowledgeCoding; }
    public void setKnowledgeCoding(String value) { this.knowledgeCoding = value; }
    public String getKnowledgeTesting() { return knowledgeTesting; }
    public void setKnowledgeTesting(String value) { this.knowledgeTesting = value; }
    public String getKnowledgePostImplementation() { return knowledgePostImplementation; }
    public void setKnowledgePostImplementation(String value) { this.knowledgePostImplementation = value; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getStudentMobile() { return studentMobile; }
    public void setStudentMobile(String studentMobile) { this.studentMobile = studentMobile; }
}

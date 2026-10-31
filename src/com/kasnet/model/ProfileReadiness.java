package com.kasnet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the readiness assessment of a student's profile
 * for the future AI Analysis Engine (Phase 2).
 * Does NOT run AI analysis; acts as an informative gatekeeper and validator.
 */
public class ProfileReadiness {

    private boolean      readyForAnalysis;
    private int          readinessScore; // 0 to 100
    private boolean      hasCareerGoal;
    private int          skillCount;
    private int          projectCount;
    private int          experienceCount;
    private int          certificationCount;
    private int          educationCount;
    private List<String> completedSections;
    private List<String> missingRecommendations;

    public ProfileReadiness() {
        this.completedSections = new ArrayList<>();
        this.missingRecommendations = new ArrayList<>();
    }

    public boolean isReadyForAnalysis() { return readyForAnalysis; }
    public void setReadyForAnalysis(boolean readyForAnalysis) { this.readyForAnalysis = readyForAnalysis; }

    public int getReadinessScore() { return readinessScore; }
    public void setReadinessScore(int readinessScore) { this.readinessScore = readinessScore; }

    public boolean isHasCareerGoal() { return hasCareerGoal; }
    public void setHasCareerGoal(boolean hasCareerGoal) { this.hasCareerGoal = hasCareerGoal; }

    public int getSkillCount() { return skillCount; }
    public void setSkillCount(int skillCount) { this.skillCount = skillCount; }

    public int getProjectCount() { return projectCount; }
    public void setProjectCount(int projectCount) { this.projectCount = projectCount; }

    public int getExperienceCount() { return experienceCount; }
    public void setExperienceCount(int experienceCount) { this.experienceCount = experienceCount; }

    public int getCertificationCount() { return certificationCount; }
    public void setCertificationCount(int certificationCount) { this.certificationCount = certificationCount; }

    public int getEducationCount() { return educationCount; }
    public void setEducationCount(int educationCount) { this.educationCount = educationCount; }

    public List<String> getCompletedSections() { return completedSections; }
    public void setCompletedSections(List<String> completedSections) { this.completedSections = completedSections; }

    public List<String> getMissingRecommendations() { return missingRecommendations; }
    public void setMissingRecommendations(List<String> missingRecommendations) { this.missingRecommendations = missingRecommendations; }
}

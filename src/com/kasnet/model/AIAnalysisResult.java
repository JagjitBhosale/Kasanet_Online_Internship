package com.kasnet.model;

import com.kasnet.util.SimpleJson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the comprehensive AI-driven resume, skill gap, and
 * market-alignment intelligence analysis for a student's profile.
 */
public class AIAnalysisResult {

    private int analysisId;
    private int userId;
    private String targetRole;
    private int overallScore;      // 0 to 100
    private int marketFitScore;     // 0 to 100
    private String roleFitSummary;
    private List<String> topStrengths;
    private List<MatchingSkill> matchingSkills;
    private List<MissingSkill> missingRequiredSkills;
    private List<TrendingSkill> recommendedTrendingSkills;
    private List<ProjectSuggestion> projectSuggestions;
    private List<ResumeBulletTip> resumeBulletImprovements;
    private List<ActionMilestone> actionPlan;
    private String modelUsed;
    private String status;
    private Timestamp createdAt;
    private String rawJson;

    public AIAnalysisResult() {
        this.topStrengths = new ArrayList<>();
        this.matchingSkills = new ArrayList<>();
        this.missingRequiredSkills = new ArrayList<>();
        this.recommendedTrendingSkills = new ArrayList<>();
        this.projectSuggestions = new ArrayList<>();
        this.resumeBulletImprovements = new ArrayList<>();
        this.actionPlan = new ArrayList<>();
        this.status = "COMPLETED";
    }

    /**
     * Factory constructor: Builds an AIAnalysisResult by parsing a structured JSON string.
     */
    public static AIAnalysisResult fromJson(String json, int userId, String targetRole, String modelUsed) {
        AIAnalysisResult res = new AIAnalysisResult();
        res.setUserId(userId);
        res.setTargetRole(targetRole);
        res.setModelUsed(modelUsed);
        res.setRawJson(json);

        if (json == null || json.trim().isEmpty()) {
            return res;
        }

        try {
            Map<String, Object> root = SimpleJson.parseObject(json);

            res.setOverallScore(SimpleJson.getInt(root, "overallScore", 70));
            res.setMarketFitScore(SimpleJson.getInt(root, "marketFitScore", 65));
            res.setRoleFitSummary(SimpleJson.getString(root, "roleFitSummary", "Profile evaluated for " + targetRole + "."));

            // topStrengths
            List<Object> strengthsRaw = SimpleJson.getList(root, "topStrengths");
            for (Object s : strengthsRaw) {
                if (s != null) res.getTopStrengths().add(String.valueOf(s));
            }

            // matchingSkills
            List<Object> matchingRaw = SimpleJson.getList(root, "matchingSkills");
            for (Object item : matchingRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    MatchingSkill ms = new MatchingSkill(
                        SimpleJson.getString(m, "name", "Skill"),
                        SimpleJson.getString(m, "category", "Technical"),
                        SimpleJson.getString(m, "matchLevel", "Good Match"),
                        SimpleJson.getString(m, "comment", "")
                    );
                    res.getMatchingSkills().add(ms);
                }
            }

            // missingRequiredSkills
            List<Object> missingRaw = SimpleJson.getList(root, "missingRequiredSkills");
            for (Object item : missingRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    MissingSkill ms = new MissingSkill(
                        SimpleJson.getString(m, "name", "Skill"),
                        SimpleJson.getString(m, "importance", "High"),
                        SimpleJson.getString(m, "reason", "Required by industry employers for this role."),
                        SimpleJson.getString(m, "recommendedAction", "Learn fundamentals and build practical project proof.")
                    );
                    res.getMissingRequiredSkills().add(ms);
                }
            }

            // recommendedTrendingSkills
            List<Object> trendingRaw = SimpleJson.getList(root, "recommendedTrendingSkills");
            for (Object item : trendingRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    TrendingSkill ts = new TrendingSkill(
                        SimpleJson.getString(m, "name", "Skill"),
                        SimpleJson.getString(m, "trendingReason", "High market growth in modern engineering stacks."),
                        SimpleJson.getString(m, "priority", "Competitive Advantage")
                    );
                    res.getRecommendedTrendingSkills().add(ts);
                }
            }

            // projectSuggestions
            List<Object> projectsRaw = SimpleJson.getList(root, "projectSuggestions");
            for (Object item : projectsRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    ProjectSuggestion ps = new ProjectSuggestion(
                        SimpleJson.getString(m, "title", "Portfolio Project"),
                        SimpleJson.getString(m, "description", ""),
                        SimpleJson.getString(m, "techStack", ""),
                        SimpleJson.getString(m, "resumeImpact", "")
                    );
                    res.getProjectSuggestions().add(ps);
                }
            }

            // resumeBulletImprovements
            List<Object> bulletsRaw = SimpleJson.getList(root, "resumeBulletImprovements");
            for (Object item : bulletsRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    ResumeBulletTip bt = new ResumeBulletTip(
                        SimpleJson.getString(m, "section", "Experience / Projects"),
                        SimpleJson.getString(m, "issue", ""),
                        SimpleJson.getString(m, "recommendedImprovement", "")
                    );
                    res.getResumeBulletImprovements().add(bt);
                }
            }

            // actionPlan
            List<Object> planRaw = SimpleJson.getList(root, "actionPlan");
            for (Object item : planRaw) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) item;
                    List<String> tasks = new ArrayList<>();
                    List<Object> tasksRaw = SimpleJson.getList(m, "tasks");
                    for (Object t : tasksRaw) {
                        if (t != null) tasks.add(String.valueOf(t));
                    }
                    ActionMilestone am = new ActionMilestone(
                        SimpleJson.getString(m, "dayRange", "Phase"),
                        SimpleJson.getString(m, "focus", ""),
                        tasks
                    );
                    res.getActionPlan().add(am);
                }
            }

        } catch (Exception e) {
            res.setRoleFitSummary("Analysis parsed with partial standard format: " + e.getMessage());
        }

        return res;
    }

    // --- Sub-Entities ---

    public static class MatchingSkill {
        private String name;
        private String category;
        private String matchLevel;
        private String comment;

        public MatchingSkill() {}
        public MatchingSkill(String name, String category, String matchLevel, String comment) {
            this.name = name;
            this.category = category;
            this.matchLevel = matchLevel;
            this.comment = comment;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getMatchLevel() { return matchLevel; }
        public void setMatchLevel(String matchLevel) { this.matchLevel = matchLevel; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class MissingSkill {
        private String name;
        private String importance; // Critical, High, Medium
        private String reason;
        private String recommendedAction;

        public MissingSkill() {}
        public MissingSkill(String name, String importance, String reason, String recommendedAction) {
            this.name = name;
            this.importance = importance;
            this.reason = reason;
            this.recommendedAction = recommendedAction;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImportance() { return importance; }
        public void setImportance(String importance) { this.importance = importance; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getRecommendedAction() { return recommendedAction; }
        public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    }

    public static class TrendingSkill {
        private String name;
        private String trendingReason;
        private String priority;

        public TrendingSkill() {}
        public TrendingSkill(String name, String trendingReason, String priority) {
            this.name = name;
            this.trendingReason = trendingReason;
            this.priority = priority;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTrendingReason() { return trendingReason; }
        public void setTrendingReason(String trendingReason) { this.trendingReason = trendingReason; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class ProjectSuggestion {
        private String title;
        private String description;
        private String techStack;
        private String resumeImpact;

        public ProjectSuggestion() {}
        public ProjectSuggestion(String title, String description, String techStack, String resumeImpact) {
            this.title = title;
            this.description = description;
            this.techStack = techStack;
            this.resumeImpact = resumeImpact;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTechStack() { return techStack; }
        public void setTechStack(String techStack) { this.techStack = techStack; }
        public String getResumeImpact() { return resumeImpact; }
        public void setResumeImpact(String resumeImpact) { this.resumeImpact = resumeImpact; }
    }

    public static class ResumeBulletTip {
        private String section;
        private String issue;
        private String recommendedImprovement;

        public ResumeBulletTip() {}
        public ResumeBulletTip(String section, String issue, String recommendedImprovement) {
            this.section = section;
            this.issue = issue;
            this.recommendedImprovement = recommendedImprovement;
        }

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getIssue() { return issue; }
        public void setIssue(String issue) { this.issue = issue; }
        public String getRecommendedImprovement() { return recommendedImprovement; }
        public void setRecommendedImprovement(String recommendedImprovement) { this.recommendedImprovement = recommendedImprovement; }
    }

    public static class ActionMilestone {
        private String dayRange;
        private String focus;
        private List<String> tasks;

        public ActionMilestone() {}
        public ActionMilestone(String dayRange, String focus, List<String> tasks) {
            this.dayRange = dayRange;
            this.focus = focus;
            this.tasks = tasks != null ? tasks : Collections.emptyList();
        }

        public String getDayRange() { return dayRange; }
        public void setDayRange(String dayRange) { this.dayRange = dayRange; }
        public String getFocus() { return focus; }
        public void setFocus(String focus) { this.focus = focus; }
        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }
    }

    // --- Getters & Setters ---

    public int getAnalysisId() { return analysisId; }
    public void setAnalysisId(int analysisId) { this.analysisId = analysisId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getMarketFitScore() { return marketFitScore; }
    public void setMarketFitScore(int marketFitScore) { this.marketFitScore = marketFitScore; }

    public String getRoleFitSummary() { return roleFitSummary; }
    public void setRoleFitSummary(String roleFitSummary) { this.roleFitSummary = roleFitSummary; }

    public List<String> getTopStrengths() { return topStrengths; }
    public void setTopStrengths(List<String> topStrengths) { this.topStrengths = topStrengths; }

    public List<MatchingSkill> getMatchingSkills() { return matchingSkills; }
    public void setMatchingSkills(List<MatchingSkill> matchingSkills) { this.matchingSkills = matchingSkills; }

    public List<MissingSkill> getMissingRequiredSkills() { return missingRequiredSkills; }
    public void setMissingRequiredSkills(List<MissingSkill> missingRequiredSkills) { this.missingRequiredSkills = missingRequiredSkills; }

    public List<TrendingSkill> getRecommendedTrendingSkills() { return recommendedTrendingSkills; }
    public void setRecommendedTrendingSkills(List<TrendingSkill> recommendedTrendingSkills) { this.recommendedTrendingSkills = recommendedTrendingSkills; }

    public List<ProjectSuggestion> getProjectSuggestions() { return projectSuggestions; }
    public void setProjectSuggestions(List<ProjectSuggestion> projectSuggestions) { this.projectSuggestions = projectSuggestions; }

    public List<ResumeBulletTip> getResumeBulletImprovements() { return resumeBulletImprovements; }
    public void setResumeBulletImprovements(List<ResumeBulletTip> resumeBulletImprovements) { this.resumeBulletImprovements = resumeBulletImprovements; }

    public List<ActionMilestone> getActionPlan() { return actionPlan; }
    public void setActionPlan(List<ActionMilestone> actionPlan) { this.actionPlan = actionPlan; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
}

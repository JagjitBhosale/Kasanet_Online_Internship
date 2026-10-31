package com.kasnet.controller;

import com.kasnet.dao.UserDAO;
import com.kasnet.model.*;
import com.kasnet.service.ResumeAnalysisService;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ResumeAnalysisServlet - Controller for Resume Analysis & Student Profile Information.
 *
 * Implements MVC pattern, data ownership enforcement, server-side validation,
 * and handles all tab navigation and CRUD operations.
 */
public class ResumeAnalysisServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ResumeAnalysisService analysisService = new ResumeAnalysisService();
    private final UserDAO               userDAO         = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            SessionUtil.invalidateSession(req);
            resp.sendRedirect(req.getContextPath() + "/login?error=invalid-session");
            return;
        }

        // Active tab resolution
        String tab = req.getParameter("tab");
        if (tab == null || tab.trim().isEmpty()) {
            tab = "career";
        }
        tab = tab.trim().toLowerCase();

        // Load section data
        CareerGoal          careerGoal     = analysisService.getCareerGoal(userId);
        List<Skill>         skills         = analysisService.getSkills(userId);
        List<Project>       projects       = analysisService.getProjects(userId);
        List<Experience>    experiences    = analysisService.getExperiences(userId);
        List<Certification> certifications = analysisService.getCertifications(userId);
        List<Education>     educations     = analysisService.getEducations(userId);
        ProfileReadiness    readiness      = analysisService.evaluateReadiness(userId);

        // Edit item lookups if requested
        String editSkillIdStr = req.getParameter("editSkillId");
        if (editSkillIdStr != null && !editSkillIdStr.isEmpty()) {
            try {
                Skill editSkill = analysisService.getSkillById(Integer.parseInt(editSkillIdStr), userId);
                req.setAttribute("editSkill", editSkill);
            } catch (NumberFormatException ignored) {}
        }

        String editProjectIdStr = req.getParameter("editProjectId");
        if (editProjectIdStr != null && !editProjectIdStr.isEmpty()) {
            try {
                Project editProject = analysisService.getProjectById(Integer.parseInt(editProjectIdStr), userId);
                req.setAttribute("editProject", editProject);
            } catch (NumberFormatException ignored) {}
        }

        String editExpIdStr = req.getParameter("editExpId");
        if (editExpIdStr != null && !editExpIdStr.isEmpty()) {
            try {
                Experience editExp = analysisService.getExperienceById(Integer.parseInt(editExpIdStr), userId);
                req.setAttribute("editExperience", editExp);
            } catch (NumberFormatException ignored) {}
        }

        String editCertIdStr = req.getParameter("editCertId");
        if (editCertIdStr != null && !editCertIdStr.isEmpty()) {
            try {
                Certification editCert = analysisService.getCertificationById(Integer.parseInt(editCertIdStr), userId);
                req.setAttribute("editCertification", editCert);
            } catch (NumberFormatException ignored) {}
        }

        String editEduIdStr = req.getParameter("editEduId");
        if (editEduIdStr != null && !editEduIdStr.isEmpty()) {
            try {
                Education editEdu = analysisService.getEducationById(Integer.parseInt(editEduIdStr), userId);
                req.setAttribute("editEducation", editEdu);
            } catch (NumberFormatException ignored) {}
        }

        AIAnalysisResult    analysis       = analysisService.getLatestAIAnalysis(userId);
        boolean             isGroqReady    = analysisService.isGroqConfigured();
        String              maskedGroqKey  = analysisService.getMaskedGroqKey();

        // Pass models to view
        req.setAttribute("user", user);
        req.setAttribute("activeTab", tab);
        req.setAttribute("careerGoal", careerGoal);
        req.setAttribute("skills", skills);
        req.setAttribute("projects", projects);
        req.setAttribute("experiences", experiences);
        req.setAttribute("certifications", certifications);
        req.setAttribute("educations", educations);
        req.setAttribute("readiness", readiness);
        req.setAttribute("analysis", analysis);
        req.setAttribute("isGroqConfigured", isGroqReady);
        req.setAttribute("maskedGroqKey", maskedGroqKey);

        req.getRequestDispatcher("/views/resume-analysis.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        try {
            switch (action) {

                // -------------------------------------------------------------
                // CAREER GOAL
                // -------------------------------------------------------------
                case "save_career": {
                    String targetRole           = req.getParameter("targetRole");
                    String targetRoleOther      = req.getParameter("targetRoleOther");
                    String preferredDomain      = req.getParameter("preferredDomain");
                    String preferredDomainOther = req.getParameter("preferredDomainOther");
                    String careerObjective      = req.getParameter("careerObjective");

                    analysisService.saveCareerGoal(userId, targetRole, targetRoleOther,
                            preferredDomain, preferredDomainOther, careerObjective);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=career&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // SKILLS
                // -------------------------------------------------------------
                case "save_skill": {
                    int skillId = parseInt(req.getParameter("skillId"), 0);
                    String skillName   = req.getParameter("skillName");
                    String skillType   = req.getParameter("skillType");

                    analysisService.saveSkill(userId, skillId, skillName, skillType);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=skills&success=1");
                    return;
                }
                case "delete_skill": {
                    int skillId = parseInt(req.getParameter("skillId"), 0);
                    analysisService.deleteSkill(skillId, userId);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=skills&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // PROJECTS
                // -------------------------------------------------------------
                case "save_project": {
                    int projectId = parseInt(req.getParameter("projectId"), 0);
                    String projectName      = req.getParameter("projectName");
                    String description      = req.getParameter("description");
                    String technologiesUsed = req.getParameter("technologiesUsed");
                    String role             = req.getParameter("role");
                    String projectType      = req.getParameter("projectType");
                    int teamSize            = parseInt(req.getParameter("teamSize"), 1);
                    String startDate        = req.getParameter("startDate");
                    String endDate          = req.getParameter("endDate");
                    String projectUrl       = req.getParameter("projectUrl");
                    String githubUrl        = req.getParameter("githubUrl");

                    analysisService.saveProject(userId, projectId, projectName, description,
                            technologiesUsed, role, projectType, teamSize, startDate, endDate, projectUrl, githubUrl);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=projects&success=1");
                    return;
                }
                case "delete_project": {
                    int projectId = parseInt(req.getParameter("projectId"), 0);
                    analysisService.deleteProject(projectId, userId);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=projects&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // EXPERIENCE
                // -------------------------------------------------------------
                case "save_experience": {
                    int experienceId = parseInt(req.getParameter("experienceId"), 0);
                    String companyName      = req.getParameter("companyName");
                    String role             = req.getParameter("role");
                    String employmentType   = req.getParameter("employmentType");
                    String technologiesUsed = req.getParameter("technologiesUsed");
                    String description      = req.getParameter("description");
                    String startDate        = req.getParameter("startDate");
                    String endDate          = req.getParameter("endDate");
                    boolean isCurrent       = "true".equalsIgnoreCase(req.getParameter("isCurrent"))
                                              || "on".equalsIgnoreCase(req.getParameter("isCurrent"));

                    analysisService.saveExperience(userId, experienceId, companyName, role,
                            employmentType, technologiesUsed, description, startDate, endDate, isCurrent);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=experience&success=1");
                    return;
                }
                case "delete_experience": {
                    int experienceId = parseInt(req.getParameter("experienceId"), 0);
                    analysisService.deleteExperience(experienceId, userId);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=experience&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // CERTIFICATIONS
                // -------------------------------------------------------------
                case "save_certification": {
                    int certId = parseInt(req.getParameter("certId"), 0);
                    String certName     = req.getParameter("certName");
                    String provider     = req.getParameter("provider");
                    String technology   = req.getParameter("technology");
                    String certIdNumber = req.getParameter("certIdNumber");
                    String issueDate    = req.getParameter("issueDate");
                    String expiryDate   = req.getParameter("expiryDate");
                    String credentialUrl = req.getParameter("credentialUrl");

                    analysisService.saveCertification(userId, certId, certName, provider,
                            technology, certIdNumber, issueDate, expiryDate, credentialUrl);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=certifications&success=1");
                    return;
                }
                case "delete_certification": {
                    int certId = parseInt(req.getParameter("certId"), 0);
                    analysisService.deleteCertification(certId, userId);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=certifications&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // EDUCATION
                // -------------------------------------------------------------
                case "save_education": {
                    String level             = req.getParameter("level");
                    String schoolCollegeName = req.getParameter("schoolCollegeName");
                    String boardUniversity   = req.getParameter("boardUniversity");
                    String stream            = req.getParameter("stream");
                    String passingYear       = req.getParameter("passingYear");
                    String percentage        = req.getParameter("percentage");

                    analysisService.saveEducation(userId, level, schoolCollegeName,
                            boardUniversity, stream, passingYear, percentage);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=education&success=1");
                    return;
                }
                case "delete_education": {
                    int eduId = parseInt(req.getParameter("eduId"), 0);
                    analysisService.deleteEducation(eduId, userId);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=education&success=1");
                    return;
                }

                // -------------------------------------------------------------
                // AI RESUME & SKILL ANALYSIS (GROQ LLM)
                // -------------------------------------------------------------
                case "run_ai_analysis": {
                    String customApiKey = req.getParameter("customApiKey");
                    analysisService.runAIAnalysis(userId, customApiKey);
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=analyze&success=analyzed");
                    return;
                }

                case "save_groq_key": {
                    String apiKey = req.getParameter("groqApiKey");
                    if (apiKey != null && !apiKey.trim().isEmpty()) {
                        analysisService.saveGroqApiKey(apiKey.trim());
                    }
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=analyze&success=key_saved");
                    return;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/resume-analysis");
            }

        } catch (IllegalArgumentException e) {
            String tab = resolveTabFromAction(action);
            String encodedMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString());
            resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=" + tab + "&error=" + encodedMsg);
        } catch (Exception e) {
            String tab = resolveTabFromAction(action);
            String encodedMsg = URLEncoder.encode("Analysis process: " + e.getMessage(), StandardCharsets.UTF_8.toString());
            resp.sendRedirect(req.getContextPath() + "/resume-analysis?tab=" + tab + "&error=" + encodedMsg);
        }
    }

    private int parseInt(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private String resolveTabFromAction(String action) {
        if (action.contains("career")) return "career";
        if (action.contains("skill")) return "skills";
        if (action.contains("project")) return "projects";
        if (action.contains("experience")) return "experience";
        if (action.contains("certification")) return "certifications";
        if (action.contains("education")) return "education";
        if (action.contains("analysis") || action.contains("groq")) return "analyze";
        return "career";
    }
}

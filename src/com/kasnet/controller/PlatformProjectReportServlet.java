package com.kasnet.controller;

import com.kasnet.dao.PlatformProjectReportDAO;
import com.kasnet.model.PlatformProjectReport;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PlatformProjectReportServlet extends HttpServlet {

    private static final List<String> TEST_TYPES = Arrays.asList(
            "Unit Testing", "Integration Testing", "System Testing", "Acceptance Testing", "Other"
    );
    private static final List<String> KNOWLEDGE_LEVELS = Arrays.asList(
            "Excellent", "Good", "Fair", "Needs Improvement", "Not Applicable"
    );
    private final PlatformProjectReportDAO reportDAO = new PlatformProjectReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard?section=projects");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        Integer userId = SessionUtil.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String action = request.getParameter("action");
            if ("delete".equals(action)) {
                int reportId = Integer.parseInt(required(request, "reportId"));
                boolean deleted = reportDAO.delete(reportId, userId);
                redirect(request, response, deleted ? "project-deleted" : null,
                        deleted ? null : "project-not-found");
                return;
            }
            boolean saved = reportDAO.add(parseReport(request, userId));
            redirect(request, response, saved ? "project-saved" : null, saved ? null : "save-failed");
        } catch (IllegalArgumentException e) {
            redirect(request, response, null, "invalid-project");
        } catch (SQLException e) {
            throw new ServletException("Unable to save or delete project report.", e);
        }
    }

    private PlatformProjectReport parseReport(HttpServletRequest request, int userId) {
        PlatformProjectReport report = new PlatformProjectReport();
        report.setUserId(userId);
        report.setProjectName(required(request, "projectName", 200));
        report.setScopeDetails(required(request, "scopeDetails", 5000));
        report.setProjectDomain(optional(request, "projectDomain", 200));
        report.setProjectTechnology(optional(request, "projectTechnology", 500));
        report.setProjectApplication(optional(request, "projectApplication", 500));
        report.setProjectDuration(optional(request, "projectDuration", 100));

        String projectModel = required(request, "projectModel", 100);
        if ("Other".equals(projectModel)) projectModel = required(request, "otherProjectModel", 100);
        else if (!Arrays.asList("Waterfall", "V-Model", "Agile", "Spiral").contains(projectModel)) {
            throw new IllegalArgumentException("Unsupported project model.");
        }
        report.setProjectModel(projectModel);

        report.setRequirementGatheringTime(optional(request, "requirementGatheringTime", 100));
        report.setProjectAnalysisTime(optional(request, "projectAnalysisTime", 100));
        report.setProjectDesigningTime(optional(request, "projectDesigningTime", 100));
        report.setProjectCodingTime(optional(request, "projectCodingTime", 100));
        report.setProjectTestingTime(optional(request, "projectTestingTime", 100));

        String cost = optional(request, "projectCost", 20);
        if (cost != null) {
            BigDecimal amount = new BigDecimal(cost);
            if (amount.signum() < 0 || amount.scale() > 2 || amount.precision() - amount.scale() > 10) {
                throw new IllegalArgumentException("Project cost must be a non-negative amount with up to two decimals.");
            }
            report.setProjectCost(amount);
        }

        String testingType = optional(request, "testingType", 100);
        if ("Other".equals(testingType)) {
            testingType = required(request, "otherTestingType", 100);
        } else if (testingType != null && !TEST_TYPES.contains(testingType)) {
            throw new IllegalArgumentException("Unsupported testing type.");
        }
        report.setTestingType(testingType);
        report.setTestingDuration(optional(request, "testingDuration", 100));

        String issues = optional(request, "issuesFound", 10);
        if (issues != null) {
            int issueCount = Integer.parseInt(issues);
            if (issueCount < 0) throw new IllegalArgumentException("Issue count cannot be negative.");
            report.setIssuesFound(issueCount);
        }

        report.setKnowledgeRequirement(knowledgeLevel(request, "knowledgeRequirement"));
        report.setKnowledgeAnalysis(knowledgeLevel(request, "knowledgeAnalysis"));
        report.setKnowledgeDesigning(knowledgeLevel(request, "knowledgeDesigning"));
        report.setKnowledgeCoding(knowledgeLevel(request, "knowledgeCoding"));
        report.setKnowledgeTesting(knowledgeLevel(request, "knowledgeTesting"));
        report.setKnowledgePostImplementation(knowledgeLevel(request, "knowledgePostImplementation"));
        return report;
    }

    private String knowledgeLevel(HttpServletRequest request, String parameter) {
        String value = optional(request, parameter, 40);
        if (value != null && !KNOWLEDGE_LEVELS.contains(value)) {
            throw new IllegalArgumentException("Invalid knowledge gain selection.");
        }
        return value;
    }

    private String required(HttpServletRequest request, String parameter) {
        return required(request, parameter, Integer.MAX_VALUE);
    }

    private String required(HttpServletRequest request, String parameter, int maxLength) {
        String value = optional(request, parameter, maxLength);
        if (value == null) throw new IllegalArgumentException("Missing required field: " + parameter);
        return value;
    }

    private String optional(HttpServletRequest request, String parameter, int maxLength) {
        String value = request.getParameter(parameter);
        if (value == null || value.trim().isEmpty()) return null;
        value = value.trim();
        if (value.length() > maxLength) throw new IllegalArgumentException("Field is too long: " + parameter);
        return value;
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String success, String error)
            throws IOException {
        StringBuilder target = new StringBuilder(request.getContextPath()).append("/dashboard?section=projects");
        if (success != null) target.append("&success=").append(success);
        if (error != null) target.append("&error=").append(error);
        response.sendRedirect(target.toString());
    }
}

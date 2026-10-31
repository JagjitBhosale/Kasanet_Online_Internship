package com.kasnet.controller;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.ProjectDAO;
import com.kasnet.model.Project;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
public class ProjectServlet extends HttpServlet {

    private ProjectDAO projectDAO = new ProjectDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=projects");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "add";

        if ("delete".equalsIgnoreCase(action)) {
            String projIdStr = req.getParameter("projectId");
            if (projIdStr != null) {
                try {
                    int projId = Integer.parseInt(projIdStr);
                    projectDAO.deleteProject(projId, userId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            String projectName = req.getParameter("projectName");
            String description = req.getParameter("description");
            String technologiesUsed = req.getParameter("technologiesUsed");
            String role = req.getParameter("role");
            String projectType = req.getParameter("projectType");
            String teamSizeStr = req.getParameter("teamSize");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String projectUrl = req.getParameter("projectUrl");
            String githubUrl = req.getParameter("githubUrl");

            if (projectName != null && !projectName.trim().isEmpty()) {
                Project p = new Project();
                p.setUserId(userId);
                p.setProjectName(projectName.trim());
                p.setDescription(description != null ? description.trim() : null);
                p.setTechnologiesUsed(technologiesUsed != null ? technologiesUsed.trim() : null);
                p.setRole(role != null ? role.trim() : null);
                p.setProjectType(projectType != null ? projectType.trim() : "Academic");

                if (teamSizeStr != null && !teamSizeStr.trim().isEmpty()) {
                    try {
                        p.setTeamSize(Integer.parseInt(teamSizeStr.trim()));
                    } catch (NumberFormatException e) {
                        p.setTeamSize(1);
                    }
                } else {
                    p.setTeamSize(1);
                }

                if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                    try {
                        p.setStartDate(Date.valueOf(startDateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                    try {
                        p.setEndDate(Date.valueOf(endDateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }

                p.setProjectUrl(projectUrl != null ? projectUrl.trim() : null);
                p.setGithubUrl(githubUrl != null ? githubUrl.trim() : null);

                String projIdParam = req.getParameter("projectId");
                if ("update".equalsIgnoreCase(action) && projIdParam != null && !projIdParam.isEmpty()) {
                    try {
                        p.setProjectId(Integer.parseInt(projIdParam));
                        projectDAO.updateProject(p);
                    } catch (NumberFormatException ignored) {}
                } else {
                    projectDAO.addProject(p);
                }
            }
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=projects&success=1");
    }
}

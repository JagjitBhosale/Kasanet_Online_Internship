package com.kasnet.controller;

import com.kasnet.dao.CompetitionDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.Competition;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
public class CompetitionServlet extends HttpServlet {

    private CompetitionDAO compDAO = new CompetitionDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=activities");
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
            String idStr = req.getParameter("competitionId");
            if (idStr != null) {
                try {
                    int compId = Integer.parseInt(idStr);
                    compDAO.deleteCompetition(compId, userId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            String name = req.getParameter("competitionName");
            String organizer = req.getParameter("collegeOrganizer");
            String dateStr = req.getParameter("eventDate");
            String projectName = req.getParameter("projectName");
            String position = req.getParameter("positionResult");
            String description = req.getParameter("description");
            String teamName = req.getParameter("teamName");

            if (name != null && !name.trim().isEmpty()) {
                Competition c = new Competition();
                c.setUserId(userId);
                c.setCompetitionName(name.trim());
                c.setCollegeOrganizer(organizer != null ? organizer.trim() : null);
                c.setProjectName(projectName != null ? projectName.trim() : null);
                c.setPositionResult(position != null ? position.trim() : null);
                c.setDescription(description != null ? description.trim() : null);
                c.setTeamName(teamName != null ? teamName.trim() : null);

                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    try {
                        c.setEventDate(Date.valueOf(dateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }

                String idParam = req.getParameter("competitionId");
                if ("update".equalsIgnoreCase(action) && idParam != null && !idParam.isEmpty()) {
                    try {
                        c.setCompetitionId(Integer.parseInt(idParam));
                        compDAO.updateCompetition(c);
                    } catch (NumberFormatException ignored) {}
                } else {
                    compDAO.addCompetition(c);
                }
            }
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=activities&success=1");
    }
}

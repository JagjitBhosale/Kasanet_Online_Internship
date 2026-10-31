package com.kasnet.controller;

import com.kasnet.dao.HackathonDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.Hackathon;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
public class HackathonServlet extends HttpServlet {

    private HackathonDAO hackathonDAO = new HackathonDAO();
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
            String idStr = req.getParameter("hackathonId");
            if (idStr != null) {
                try {
                    int hackathonId = Integer.parseInt(idStr);
                    hackathonDAO.deleteHackathon(hackathonId, userId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            String name = req.getParameter("hackathonName");
            String organizer = req.getParameter("organizer");
            String dateStr = req.getParameter("eventDate");
            String position = req.getParameter("positionResult");
            String description = req.getParameter("description");
            String teamName = req.getParameter("teamName");

            if (name != null && !name.trim().isEmpty()) {
                Hackathon h = new Hackathon();
                h.setUserId(userId);
                h.setHackathonName(name.trim());
                h.setOrganizer(organizer != null ? organizer.trim() : null);
                h.setPositionResult(position != null ? position.trim() : null);
                h.setDescription(description != null ? description.trim() : null);
                h.setTeamName(teamName != null ? teamName.trim() : null);

                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    try {
                        h.setEventDate(Date.valueOf(dateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }

                String idParam = req.getParameter("hackathonId");
                if ("update".equalsIgnoreCase(action) && idParam != null && !idParam.isEmpty()) {
                    try {
                        h.setHackathonId(Integer.parseInt(idParam));
                        hackathonDAO.updateHackathon(h);
                    } catch (NumberFormatException ignored) {}
                } else {
                    hackathonDAO.addHackathon(h);
                }
            }
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=activities&success=1");
    }
}

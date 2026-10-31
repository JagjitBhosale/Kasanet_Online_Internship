package com.kasnet.controller;

import com.kasnet.dao.AchievementDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.Achievement;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class AchievementServlet extends HttpServlet {

    private AchievementDAO achievementDAO = new AchievementDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal");
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
        if (action == null) action = "save";

        if ("delete".equalsIgnoreCase(action)) {
            String achIdStr = req.getParameter("achievementId");
            if (achIdStr != null) {
                try {
                    int achievementId = Integer.parseInt(achIdStr);
                    achievementDAO.deleteAchievement(achievementId, userId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            // Save from the UI fields matching the screenshot
            // 1. Certification item
            String certName = req.getParameter("certName");
            String certTech = req.getParameter("certTech");
            if (certName != null && !certName.trim().isEmpty()) {
                Achievement a = new Achievement();
                a.setUserId(userId);
                a.setAchievementType("Certification");
                a.setTitle(certName.trim());
                a.setTechnology(certTech != null ? certTech.trim() : "");
                a.setDescription("Certification in " + (certTech != null ? certTech.trim() : ""));
                achievementDAO.addAchievement(a);
            }

            // 2. Project item
            String projName = req.getParameter("projectName");
            String projDetails = req.getParameter("projectDetails");
            if (projName != null && !projName.trim().isEmpty()) {
                Achievement a = new Achievement();
                a.setUserId(userId);
                a.setAchievementType("Project");
                a.setTitle(projName.trim());
                a.setDescription(projDetails != null ? projDetails.trim() : "");
                achievementDAO.addAchievement(a);
            }

            // 3. Extra Curriculum Activities
            String extraActivities = req.getParameter("extraActivities");
            if (extraActivities != null && !extraActivities.trim().isEmpty()) {
                Achievement a = new Achievement();
                a.setUserId(userId);
                a.setAchievementType("ExtraCurricular");
                a.setTitle("Extra Curricular Activities");
                a.setExtraActivities(extraActivities.trim());
                achievementDAO.addAchievement(a);
            }
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&success=1");
    }
}

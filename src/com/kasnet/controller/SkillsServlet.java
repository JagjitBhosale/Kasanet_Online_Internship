package com.kasnet.controller;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.SkillDAO;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SkillsServlet extends HttpServlet {

    private SkillDAO skillDAO = new SkillDAO();
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

        // 1. Technical Skills
        String techSkillsStr = req.getParameter("technicalSkills");
        if (techSkillsStr != null) {
            List<String> list = parseSkills(techSkillsStr);
            skillDAO.saveSkillsOfType(userId, "Technical", list);
        }

        // 2. Soft Skills
        String softSkillsStr = req.getParameter("softSkills");
        if (softSkillsStr != null) {
            List<String> list = parseSkills(softSkillsStr);
            skillDAO.saveSkillsOfType(userId, "Soft", list);
        }

        // 3. Domain Skills
        String domainSkillsStr = req.getParameter("domainSkills");
        if (domainSkillsStr != null) {
            List<String> list = parseSkills(domainSkillsStr);
            skillDAO.saveSkillsOfType(userId, "Domain", list);
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&success=1");
    }

    private List<String> parseSkills(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) {
            return result;
        }
        String[] parts = input.split("[,;\\n]+");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !result.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }
}

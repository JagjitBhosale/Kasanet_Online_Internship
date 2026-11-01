package com.onlineinternshipin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onlineinternshipin.dao.HomeDAO;
import com.onlineinternshipin.dao.ProfileDAO;
import com.onlineinternshipin.dao.UserDAO;
import com.onlineinternshipin.model.HomeStats;
import com.onlineinternshipin.model.StudentProfile;
import com.onlineinternshipin.model.User;
import com.onlineinternshipin.utility.SessionUtil;
public class HomeServlet extends HttpServlet {

    private final HomeDAO homeDAO = new HomeDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();
    private final UserDAO userDAO = new UserDAO();

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

        // Dynamic completion recalculation
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        HomeStats stats = homeDAO.gethomeStats(userId);
        StudentProfile profile = profileDAO.getProfileByUserId(userId);

        if (profile == null) {
            profileDAO.createProfile(userId);
            profile = profileDAO.getProfileByUserId(userId);
        }
        if (profile != null) {
            profile.setProfileCompletion(completion);
            if (stats != null) {
                stats.setProfileCompletion(completion);
            }
        }

        req.setAttribute("stats", stats != null ? stats : new HomeStats());
        req.setAttribute("user", user);
        req.setAttribute("profile", profile);

        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

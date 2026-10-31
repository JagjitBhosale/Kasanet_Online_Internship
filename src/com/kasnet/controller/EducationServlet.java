package com.kasnet.controller;

import com.kasnet.dao.EducationDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.Education;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class EducationServlet extends HttpServlet {

    private EducationDAO educationDAO = new EducationDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=education");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String[] levels = {"SSC", "HSC", "Graduation", "Master", "Diploma"};

        for (String level : levels) {
            String schoolName = req.getParameter("schoolName_" + level);
            String boardUniv = req.getParameter("board_" + level);
            String stream = req.getParameter("stream_" + level);
            String yearStr = req.getParameter("year_" + level);
            String pctStr = req.getParameter("percentage_" + level);

            // If at least school name is provided for this level, save it
            if (schoolName != null && !schoolName.trim().isEmpty()) {
                Education edu = new Education();
                edu.setUserId(userId);
                edu.setLevel(level);
                edu.setSchoolCollegeName(schoolName.trim());
                edu.setBoardUniversity(boardUniv != null ? boardUniv.trim() : null);
                edu.setStream(stream != null ? stream.trim() : null);

                if (yearStr != null && !yearStr.trim().isEmpty()) {
                    try {
                        edu.setPassingYear(Integer.parseInt(yearStr.trim()));
                    } catch (NumberFormatException ignored) {}
                }

                if (pctStr != null && !pctStr.trim().isEmpty()) {
                    try {
                        double pct = Double.parseDouble(pctStr.trim());
                        if (pct >= 0.0 && pct <= 100.0) {
                            edu.setPercentage(pct);
                        }
                    } catch (NumberFormatException ignored) {}
                }

                educationDAO.saveOrUpdateEducation(edu);
            }
        }

        // Recalculate profile completion
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=education&success=1");
    }
}

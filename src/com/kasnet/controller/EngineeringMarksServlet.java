package com.kasnet.controller;

import com.kasnet.dao.EngineeringMarksDAO;
import com.kasnet.model.EngineeringMarks;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller servlet for Engineering Year-wise Marks (First Year, Second Year, Third Year, Final Year).
 * Handles adding, editing, and deleting year-wise percentage/CGPA records.
 */
public class EngineeringMarksServlet extends HttpServlet {

    private EngineeringMarksDAO marksDAO = new EngineeringMarksDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String markIdStr = req.getParameter("id");

        if ("delete".equalsIgnoreCase(action) && markIdStr != null) {
            try {
                int markId = Integer.parseInt(markIdStr.trim());
                marksDAO.deleteMark(markId, userId);
            } catch (NumberFormatException ignored) {}
            resp.sendRedirect(req.getContextPath() + "/profile?tab=education&deleted=1");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/profile?tab=education");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String markIdStr = req.getParameter("markId");

        // Handle delete via POST
        if ("delete".equalsIgnoreCase(action)) {
            if (markIdStr != null && !markIdStr.trim().isEmpty()) {
                try {
                    int markId = Integer.parseInt(markIdStr.trim());
                    marksDAO.deleteMark(markId, userId);
                } catch (NumberFormatException ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/profile?tab=education&deleted=1");
            return;
        }

        String yearLevel = req.getParameter("yearLevel");
        String scoreType = req.getParameter("scoreType");
        String scoreValStr = req.getParameter("scoreValue");
        String passingYearStr = req.getParameter("passingYear");
        String collegeName = req.getParameter("collegeName");

        if (yearLevel == null || yearLevel.trim().isEmpty() || scoreValStr == null || scoreValStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/profile?tab=education&error=missingMarks");
            return;
        }

        double scoreVal;
        try {
            scoreVal = Double.parseDouble(scoreValStr.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/profile?tab=education&error=invalidScore");
            return;
        }

        if ("CGPA".equalsIgnoreCase(scoreType)) {
            if (scoreVal < 0.0 || scoreVal > 10.0) {
                resp.sendRedirect(req.getContextPath() + "/profile?tab=education&error=cgpaRange");
                return;
            }
        } else {
            if (scoreVal < 0.0 || scoreVal > 100.0) {
                resp.sendRedirect(req.getContextPath() + "/profile?tab=education&error=pctRange");
                return;
            }
        }

        int passingYear = 0;
        if (passingYearStr != null && !passingYearStr.trim().isEmpty()) {
            try {
                passingYear = Integer.parseInt(passingYearStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        EngineeringMarks mark = new EngineeringMarks();
        mark.setUserId(userId);
        mark.setYearLevel(yearLevel.trim());
        mark.setScoreType(scoreType != null && "CGPA".equalsIgnoreCase(scoreType.trim()) ? "CGPA" : "Percentage");
        mark.setScoreValue(scoreVal);
        mark.setPassingYear(passingYear);
        mark.setCollegeName(collegeName != null ? collegeName.trim() : null);

        int markId = 0;
        if (markIdStr != null && !markIdStr.trim().isEmpty()) {
            try {
                markId = Integer.parseInt(markIdStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        if (markId > 0) {
            mark.setMarkId(markId);
            marksDAO.updateMarkById(mark);
        } else {
            marksDAO.saveOrUpdateMark(mark);
        }

        resp.sendRedirect(req.getContextPath() + "/profile?tab=education&success=1");
    }
}

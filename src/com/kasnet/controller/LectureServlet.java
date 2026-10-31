package com.kasnet.controller;

import com.kasnet.dao.LectureDAO;
import com.kasnet.model.Lecture;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
public class LectureServlet extends HttpServlet {

    private LectureDAO lectureDAO = new LectureDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Lecture> lectures = lectureDAO.getLecturesWithProgress(userId);
        req.setAttribute("lectureList", lectures);
        req.getRequestDispatcher("/views/lectures.jsp").forward(req, resp);
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
        String lectureIdStr = req.getParameter("lectureId");

        if (lectureIdStr != null) {
            try {
                int lectureId = Integer.parseInt(lectureIdStr);
                if ("markComplete".equalsIgnoreCase(action)) {
                    lectureDAO.markLectureCompleted(lectureId, userId);
                } else if ("updateProgress".equalsIgnoreCase(action)) {
                    String progressStr = req.getParameter("progress");
                    int progress = progressStr != null ? Integer.parseInt(progressStr) : 0;
                    lectureDAO.updateWatchProgress(lectureId, userId, progress);
                }
            } catch (NumberFormatException ignored) {}
        }

        resp.sendRedirect(req.getContextPath() + "/lectures?success=1");
    }
}

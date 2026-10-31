package com.kasnet.controller;

import com.kasnet.dao.AssignmentDAO;
import com.kasnet.model.Assignment;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AssignmentServlet.class.getName());
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) {
            return;
        }
        if (!SessionUtil.isStudent(req)) {
            if (SessionUtil.isAdmin(req)) {
                resp.sendRedirect(req.getContextPath() + "/admin/assignments");
                return;
            }
            LOGGER.warning("Denied student assignments page request; role="
                    + getRoleCategory(req));
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("/assignment-details".equals(req.getServletPath())) {
            showAssignmentDetails(req, resp);
            return;
        }

        List<Assignment> assignments;
        try {
            assignments = assignmentDAO.getActiveAssignments();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load student assignments.", exception);
            assignments = Collections.emptyList();
            req.setAttribute("assignmentLoadError",
                    "Assignments could not be loaded. Please contact support if the problem continues.");
        }
        req.setAttribute("assignmentList", assignments);
        req.getRequestDispatcher("/views/assignments.jsp").forward(req, resp);
    }

    private void showAssignmentDetails(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int assignmentId;
        try {
            assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
            if (assignmentId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);
            if (assignment == null || !assignment.isActive()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("assignment", assignment);
            req.getRequestDispatcher("/views/assignment-detail.jsp").forward(req, resp);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load assignment details for assignmentId=" + assignmentId,
                    exception);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Allow", "GET");
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private String getRoleCategory(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object role = session == null ? null : session.getAttribute(SessionUtil.ATTR_USER_ROLE);
        if (!(role instanceof String) || ((String) role).trim().isEmpty()) {
            return "missing";
        }
        if ("STUDENT".equalsIgnoreCase(((String) role).trim())) {
            return "STUDENT";
        }
        if ("ADMIN".equalsIgnoreCase(((String) role).trim())) {
            return "ADMIN";
        }
        return "unrecognized";
    }
}

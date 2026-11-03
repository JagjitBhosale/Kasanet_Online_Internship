package com.onlineinternshipin.controller;

import com.onlineinternshipin.dao.AssignmentDAO;
import com.onlineinternshipin.model.Assignment;
import com.onlineinternshipin.utility.SessionUtil;

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
            Integer userId = SessionUtil.getUserId(req);
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            assignments = assignmentDAO.getAssignmentsForStudent(userId);
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
        int studentAssignmentId;
        try {
            String id = req.getParameter("studentAssignmentId");
            if (id == null) {
                id = req.getParameter("assignmentId");
            }
            studentAssignmentId = Integer.parseInt(id);
            if (studentAssignmentId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Integer userId = SessionUtil.getUserId(req);
            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            Assignment assignment = assignmentDAO.getAssignmentForStudent(studentAssignmentId, userId);
            if (assignment == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("assignment", assignment);
            req.setAttribute("csrfToken", SessionUtil.getOrCreateCsrfToken(req));
            req.getRequestDispatcher("/views/assignment-detail.jsp").forward(req, resp);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE,
                    "Unable to load student assignment details for studentAssignmentId=" + studentAssignmentId,
                    exception);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) {
            return;
        }
        if (!SessionUtil.isStudent(req)) {
            if (SessionUtil.isAdmin(req)) {
                resp.sendRedirect(req.getContextPath() + "/admin/assignments");
                return;
            }
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!SessionUtil.isValidCsrfToken(req, req.getParameter("csrfToken"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid security token.");
            return;
        }

        Integer studentId = SessionUtil.getUserId(req);
        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int studentAssignmentId;
        try {
            String id = req.getParameter("studentAssignmentId");
            if (id == null) {
                id = req.getParameter("assignmentId");
            }
            studentAssignmentId = Integer.parseInt(id);
            if (studentAssignmentId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String action = req.getParameter("action");
        String expectedStatus;
        String newStatus;
        if ("complete".equals(action)) {
            expectedStatus = "Assigned";
            newStatus = "Completed";
        } else if ("undo-completion".equals(action)) {
            expectedStatus = "Completed";
            newStatus = "Assigned";
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean updated = assignmentDAO.updateStudentAssignmentStatus(
                    studentAssignmentId, studentId, expectedStatus, newStatus);
            if (!updated) {
                Assignment current = assignmentDAO.getAssignmentForStudent(studentAssignmentId, studentId);
                if (current == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                if (!newStatus.equals(current.getStatus())) {
                    resp.sendError(HttpServletResponse.SC_CONFLICT,
                            "The assignment status has changed. Reload the assignment and try again.");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/assignment-details?studentAssignmentId="
                    + studentAssignmentId + "&updated="
                    + ("Completed".equals(newStatus) ? "completed" : "undone"));
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE,
                    "Unable to update assignment completion for studentAssignmentId="
                            + studentAssignmentId + ", studentId=" + studentId,
                    exception);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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

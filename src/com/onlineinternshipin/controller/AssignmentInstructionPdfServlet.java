package com.onlineinternshipin.controller;

import com.onlineinternshipin.dao.AssignmentDAO;
import com.onlineinternshipin.model.Assignment;
import com.onlineinternshipin.utility.AssignmentFileStorage;
import com.onlineinternshipin.utility.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignmentInstructionPdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AssignmentInstructionPdfServlet.class.getName());
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!SessionUtil.isAdmin(request) && !SessionUtil.isStudent(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean admin = SessionUtil.isAdmin(request);
        String idParameter = admin ? "assignmentId" : "studentAssignmentId";
        int assignmentId;
        try {
            String id = request.getParameter(idParameter);
            assignmentId = Integer.parseInt(id);
            if (assignmentId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path pdf;
        try {
            Assignment assignment;
            if (admin) {
                assignment = assignmentDAO.getAssignmentById(assignmentId);
            } else {
                Integer studentId = SessionUtil.getUserId(request);
                if (studentId == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                assignment = assignmentDAO.getAssignmentForStudent(assignmentId, studentId);
            }
            if (assignment == null || assignment.getInstructionPdfPath() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            pdf = AssignmentFileStorage.resolveStoredPdf(getServletContext(), assignment.getInstructionPdfPath());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load assignment instructions.", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Assignment instruction PDF is missing or invalid.", exception);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            response.setContentType("application/pdf");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Cache-Control", "private, no-store");
            response.setHeader("Content-Disposition",
                    "inline; filename=\"assignment-instructions-" + assignmentId + ".pdf\"");
            response.setContentLength((int) Files.size(pdf));
            Files.copy(pdf, response.getOutputStream());
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Unable to stream assignment instructions.", exception);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}

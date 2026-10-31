package com.kasnet.controller;

import com.kasnet.dao.AssignmentDAO;
import com.kasnet.model.Assignment;
import com.kasnet.util.AssignmentFileStorage;
import com.kasnet.util.MultipartRequestUtil;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminAssignmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminAssignmentServlet.class.getName());
    private static final String VIEW = "/views/admin-assignments.jsp";
    private static final String ASSIGNMENTS_URL = "/admin/assignments";
    private static final String CODE_PATTERN = "[A-Z0-9]+(?:-[A-Z0-9]+)*";
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdmin(request, response)) {
            return;
        }
        Assignment editing = null;
        String editId = request.getParameter("edit");
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                editing = assignmentDAO.getAssignmentById(Integer.parseInt(editId));
            } catch (NumberFormatException exception) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Unable to load assignment for editing.", exception);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            if (editing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        showPage(request, response, editing);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdmin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        HttpServletRequest formRequest;
        try {
            formRequest = MultipartRequestUtil.withParsedParameters(request);
        } catch (IllegalStateException exception) {
            LOGGER.log(Level.WARNING, "Assignment upload exceeded the configured request limit.", exception);
            showRequestError(request, response,
                    "The uploaded request is too large. Instruction PDFs must not exceed 5 MB.");
            return;
        } catch (IllegalArgumentException exception) {
            showRequestError(request, response, exception.getMessage());
            return;
        } catch (IOException | ServletException exception) {
            LOGGER.log(Level.WARNING, "Could not parse the assignment form upload.", exception);
            showRequestError(request, response, "The assignment form could not be processed. Please try again.");
            return;
        }
        if (!SessionUtil.isValidCsrfToken(formRequest, formRequest.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid security token.");
            return;
        }

        String action = formRequest.getParameter("action");
        if ("toggle".equals(action)) {
            toggleAssignment(formRequest, response);
        } else if ("save".equals(action)) {
            saveAssignment(formRequest, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void saveAssignment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Assignment assignment;
        Assignment previous = null;
        String previousPdf = null;
        String storedPdf = null;
        try {
            int assignmentId = parseOptionalId(request.getParameter("assignmentId"));
            if (assignmentId > 0) {
                previous = assignmentDAO.getAssignmentById(assignmentId);
                if (previous == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                previousPdf = previous.getInstructionPdfPath();
            }
            assignment = validateAssignment(request, assignmentId, previous);
            Part pdfPart = request.getPart("instructionPdf");
            boolean removePdf = "true".equals(request.getParameter("removePdf"));
            if (pdfPart != null && pdfPart.getSize() > 0) {
                storedPdf = AssignmentFileStorage.storePdf(getServletContext(), pdfPart);
                assignment.setInstructionPdfPath(storedPdf);
            } else if (removePdf) {
                assignment.setInstructionPdfPath(null);
            } else if (previous != null) {
                assignment.setInstructionPdfPath(previousPdf);
            }
            if (isBlank(assignment.getInstructionsText()) && isBlank(assignment.getInstructionPdfPath())) {
                throw new IllegalArgumentException("Provide text instructions, a PDF, or both.");
            }
            assignment.setActive("true".equals(request.getParameter("isActive")));
            boolean saved = assignmentId > 0
                    ? assignmentDAO.updateAssignment(assignment)
                    : assignmentDAO.createAssignment(assignment);
            if (!saved) {
                throw new SQLException("Assignment could not be saved.");
            }
            if (storedPdf != null && previousPdf != null) {
                AssignmentFileStorage.deleteStoredPdf(getServletContext(), previousPdf);
            } else if (removePdf && previousPdf != null) {
                AssignmentFileStorage.deleteStoredPdf(getServletContext(), previousPdf);
            }
            setFlash(request.getSession(), "Assignment saved successfully.", true);
            response.sendRedirect(request.getContextPath() + ASSIGNMENTS_URL);
        } catch (IllegalArgumentException exception) {
            Assignment form = assignmentFromRequest(request);
            form.setInstructionPdfPath(previousPdf);
            showError(request, response, exception.getMessage(), form);
        } catch (IllegalStateException exception) {
            Assignment form = assignmentFromRequest(request);
            form.setInstructionPdfPath(previousPdf);
            showError(request, response, "The uploaded request is too large. Instruction PDFs must not exceed 5 MB.", form);
        } catch (SQLException exception) {
            if (storedPdf != null) {
                AssignmentFileStorage.deleteStoredPdf(getServletContext(), storedPdf);
            }
            LOGGER.log(Level.SEVERE, "Unable to save assignment.", exception);
            String message = exception.getErrorCode() == 1062
                    ? "An assignment with this code already exists."
                    : "The assignment could not be saved. Verify the database connection and assignment-instruction migration.";
            Assignment form = assignmentFromRequest(request);
            form.setInstructionPdfPath(previousPdf);
            showError(request, response, message, form);
        } catch (IOException exception) {
            if (storedPdf != null) {
                AssignmentFileStorage.deleteStoredPdf(getServletContext(), storedPdf);
            }
            LOGGER.log(Level.WARNING, "Unable to store uploaded assignment instructions.", exception);
            Assignment form = assignmentFromRequest(request);
            form.setInstructionPdfPath(previousPdf);
            showError(request, response, "The instruction PDF could not be uploaded. Please try again.", form);
        } catch (ServletException exception) {
            if (storedPdf != null) {
                AssignmentFileStorage.deleteStoredPdf(getServletContext(), storedPdf);
            }
            LOGGER.log(Level.WARNING, "Could not read uploaded assignment instructions.", exception);
            Assignment form = assignmentFromRequest(request);
            form.setInstructionPdfPath(previousPdf);
            showError(request, response, "The instruction PDF upload could not be processed. Please try again.", form);
        }
    }

    private Assignment validateAssignment(HttpServletRequest request, int assignmentId, Assignment previous)
            throws SQLException {
        String title = required(request, "title", "Title", 200);
        String code = required(request, "assignmentNumber", "Assignment code", 50).toUpperCase(Locale.ROOT);
        if (!code.matches(CODE_PATTERN)) {
            throw new IllegalArgumentException("Use a code containing letters or numbers separated by hyphens (for example, ASN-003).");
        }
        String subject = required(request, "subject", "Subject", 200);
        String description = required(request, "description", "Description", 5000);
        String instructions = optional(request, "instructionsText", 16000);
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(required(request, "dueDate", "Due date", 10));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter the due date in YYYY-MM-DD format.");
        }
        if (dueDate.isBefore(LocalDate.now())
                && (previous == null || previous.getDueDate() == null
                || !dueDate.equals(previous.getDueDate().toLocalDate()))) {
            throw new IllegalArgumentException("The due date cannot be in the past.");
        }
        int marks;
        try {
            marks = Integer.parseInt(required(request, "totalMarks", "Maximum marks", 6));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Maximum marks must be a positive whole number.");
        }
        if (marks < 1 || marks > 100000) {
            throw new IllegalArgumentException("Maximum marks must be between 1 and 100000.");
        }
        if (assignmentDAO.assignmentCodeExists(code, assignmentId)) {
            throw new IllegalArgumentException("An assignment with this code already exists.");
        }

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setTitle(title);
        assignment.setAssignmentNumber(code);
        assignment.setSubject(subject);
        assignment.setDescription(description);
        assignment.setInstructionsText(instructions);
        assignment.setDueDate(Date.valueOf(dueDate));
        assignment.setTotalMarks(marks);
        return assignment;
    }

    private void toggleAssignment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            boolean active = "true".equals(request.getParameter("active"));
            if (!assignmentDAO.setAssignmentActive(assignmentId, active)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            setFlash(request.getSession(), active ? "Assignment republished." : "Assignment deactivated.", true);
            response.sendRedirect(request.getContextPath() + ASSIGNMENTS_URL);
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to change assignment publish status.", exception);
            setFlash(request.getSession(), "The assignment status could not be changed.", false);
            response.sendRedirect(request.getContextPath() + ASSIGNMENTS_URL);
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message,
                           Assignment formAssignment) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.setAttribute("formAssignment", formAssignment);
        request.setAttribute("formTotalMarks", request.getParameter("totalMarks"));
        showPage(request, response, null);
    }

    private void showRequestError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        Assignment form = new Assignment();
        form.setActive(true);
        form.setTotalMarks(100);
        request.setAttribute("errorMessage", message);
        request.setAttribute("formAssignment", form);
        request.setAttribute("formTotalMarks", 100);
        showPage(request, response, null);
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response, Assignment editing)
            throws ServletException, IOException {
        try {
            List<Assignment> assignments = assignmentDAO.getAllAssignmentsForAdmin();
            request.setAttribute("assignmentList", assignments);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load admin assignment list.", exception);
            request.setAttribute("loadError", "Assignments could not be loaded. Check the database migration and try again.");
        }
        request.setAttribute("editingAssignment", editing);
        if (request.getAttribute("formAssignment") == null) {
            Assignment form = editing;
            if (form == null) {
                form = new Assignment();
                form.setActive(true);
                form.setTotalMarks(100);
            }
            request.setAttribute("formAssignment", form);
        }
        request.setAttribute("csrfToken", SessionUtil.getOrCreateCsrfToken(request));
        HttpSession session = request.getSession(false);
        if (session != null) {
            boolean flashError = Boolean.TRUE.equals(session.getAttribute("assignmentFlashError"));
            request.setAttribute("successMessage", flashError ? null : session.getAttribute("assignmentFlashMessage"));
            request.setAttribute("flashMessage", flashError ? session.getAttribute("assignmentFlashMessage") : null);
            session.removeAttribute("assignmentFlashMessage");
            session.removeAttribute("assignmentFlashError");
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private Assignment assignmentFromRequest(HttpServletRequest request) {
        Assignment assignment = new Assignment();
        try {
            assignment.setAssignmentId(parseOptionalId(request.getParameter("assignmentId")));
        } catch (IllegalArgumentException ignored) {
            assignment.setAssignmentId(0);
        }
        assignment.setTitle(request.getParameter("title"));
        assignment.setAssignmentNumber(request.getParameter("assignmentNumber"));
        assignment.setSubject(request.getParameter("subject"));
        assignment.setDescription(request.getParameter("description"));
        assignment.setInstructionsText(request.getParameter("instructionsText"));
        assignment.setActive("true".equals(request.getParameter("isActive")));
        String dueDate = request.getParameter("dueDate");
        if (dueDate != null) {
            try {
                assignment.setDueDate(Date.valueOf(LocalDate.parse(dueDate)));
            } catch (DateTimeParseException ignored) {
                // Leave the invalid date in the browser's date field as submitted.
            }
        }
        String marks = request.getParameter("totalMarks");
        if (marks != null) {
            try {
                assignment.setTotalMarks(Integer.parseInt(marks));
            } catch (NumberFormatException ignored) {
                assignment.setTotalMarks(0);
            }
        }
        return assignment;
    }

    private String required(HttpServletRequest request, String name, String label, int maxLength) {
        String value = optional(request, name, maxLength);
        if (isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    private String optional(HttpServletRequest request, String name, int maxLength) {
        String value = request.getParameter(name);
        value = value == null ? "" : value.trim();
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(name + " must be no longer than " + maxLength + " characters.");
        }
        return value;
    }

    private int parseOptionalId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            int id = Integer.parseInt(value);
            if (id == 0) {
                return 0;
            }
            if (id < 0) {
                throw new IllegalArgumentException("Invalid assignment ID.");
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid assignment ID.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void setFlash(HttpSession session, String message, boolean success) {
        session.setAttribute("assignmentFlashMessage", message);
        session.setAttribute("assignmentFlashError", !success);
    }
}

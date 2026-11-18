package com.onlineinternshipin.controller;

import com.onlineinternshipin.dao.UserDAO;
import com.onlineinternshipin.model.User;
import com.onlineinternshipin.utility.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * AdminServlet - Simple monitoring interface for registered users and accounts.
 * Accessible only by users with role 'ADMIN'.
 */
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final com.onlineinternshipin.dao.CertificationDAO certDAO = new com.onlineinternshipin.dao.CertificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/views/unauthorized.jsp");
            return;
        }

        List<User> studentList = userDAO.getAllStudents();
        List<User> allUsers = userDAO.getAllUsers();
        int adminCount = 0;
        if (allUsers != null) {
            for (User u : allUsers) {
                if ("ADMIN".equalsIgnoreCase(u.getRole())) {
                    adminCount++;
                }
            }
        }
        int studentCount = (studentList != null) ? studentList.size() : 0;

        req.setAttribute("userList", studentList != null ? studentList : java.util.Collections.emptyList());
        req.setAttribute("totalUsers", allUsers != null ? allUsers.size() : 0);
        req.setAttribute("studentCount", studentCount);
        req.setAttribute("adminCount", adminCount);

        req.getRequestDispatcher("/views/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=admin&msg=admin_required");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/views/unauthorized.jsp");
            return;
        }

        String action = req.getParameter("action");
        if ("delete".equalsIgnoreCase(action) || "deleteStudent".equalsIgnoreCase(action)) {
            handleDelete(req, resp, session);
            return;
        } else if ("deleteCert".equalsIgnoreCase(action) || "deleteCertification".equalsIgnoreCase(action)) {
            handleDeleteCert(req, resp, session);
            return;
        }

        doGet(req, resp);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        Integer currentUserId = (Integer) session.getAttribute("userId");
        String userIdParam = req.getParameter("userId");
        String[] userIdsParam = req.getParameterValues("userIds");

        boolean success = false;
        String message = "";

        try {
            if (userIdParam != null && !userIdParam.trim().isEmpty()) {
                int targetId = Integer.parseInt(userIdParam.trim());
                if (currentUserId != null && currentUserId == targetId) {
                    success = false;
                    message = "Cannot delete the currently logged-in administrator account.";
                } else {
                    success = userDAO.deleteUser(targetId);
                    message = success ? "Student record deleted successfully." : "Failed to delete student record.";
                }
            } else if (userIdsParam != null && userIdsParam.length > 0) {
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                for (String idStr : userIdsParam) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        if (currentUserId == null || currentUserId != id) {
                            ids.add(id);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                if (!ids.isEmpty()) {
                    success = userDAO.deleteUsers(ids);
                    message = success ? ids.size() + " student record(s) deleted successfully." : "Failed to delete selected student records.";
                } else {
                    message = "No valid records selected to delete.";
                }
            } else {
                message = "No user ID specified.";
            }
        } catch (Exception e) {
            success = false;
            message = "An error occurred while deleting: " + e.getMessage();
        }

        if (isAjax) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":" + success + ",\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
        } else {
            session.setAttribute("adminFlashMsg", message);
            session.setAttribute("adminFlashType", success ? "success" : "danger");
            resp.sendRedirect(req.getContextPath() + "/admin");
        }
    }

    private void handleDeleteCert(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        String certIdParam = req.getParameter("certId");
        boolean success = false;
        String message = "";

        try {
            if (certIdParam != null && !certIdParam.trim().isEmpty()) {
                int certId = Integer.parseInt(certIdParam.trim());
                success = certDAO.deleteCertificationByAdmin(certId);
                message = success ? "Certificate record deleted successfully." : "Failed to delete certificate record.";
            } else {
                message = "Invalid certificate ID.";
            }
        } catch (Exception e) {
            success = false;
            message = "Error deleting certificate: " + e.getMessage();
        }

        if (isAjax) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":" + success + ",\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
        } else {
            session.setAttribute("adminFlashMsg", message);
            session.setAttribute("adminFlashType", success ? "success" : "danger");
            resp.sendRedirect(req.getContextPath() + "/admin?tab=certificates");
        }
    }
}

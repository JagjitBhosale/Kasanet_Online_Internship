package com.onlineinternshipin.controller;

import com.onlineinternshipin.dao.ProfileDAO;
import com.onlineinternshipin.dao.StudentDAO;
import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.utility.SessionUtil;
import com.onlineinternshipin.utility.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
 * PersonalDetailsServlet - Handles editing and updating student personal details.
 * Updates the production 'student' table directly.
 */
public class PersonalDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final StudentDAO studentDAO = new StudentDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();

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

        req.setCharacterEncoding("UTF-8");

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String gender = req.getParameter("gender");
        String dobStr = req.getParameter("dob");
        String mobile = req.getParameter("mobile");
        String address = req.getParameter("address");
        String city = req.getParameter("city");
        String state = req.getParameter("state");

        // Validate mandatory first and last name
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=nameRequired");
            return;
        }

        // Validate mobile if provided
        if (mobile != null && !mobile.trim().isEmpty() && !ValidationUtil.isValidMobile(mobile)) {
            resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=invalidMobile");
            return;
        }

        // Validate DOB (cannot be in the future)
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                LocalDate requestedDate = LocalDate.parse(dobStr.trim());
                if (requestedDate.isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata")))) {
                    resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=futureDob");
                    return;
                }
            } catch (DateTimeParseException ex) {
                resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=invalidDob");
                return;
            }
        }

        // Retrieve existing student to preserve any untouched fields
        Student existing = studentDAO.getStudentById(userId);
        if (existing == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=invalid-session");
            return;
        }

        String updatedFname = firstName.trim();
        String updatedLname = lastName.trim();
        String updatedGender = (gender != null && !gender.trim().isEmpty()) ? gender.trim() : existing.getGender();
        String updatedDob = (dobStr != null && !dobStr.trim().isEmpty()) ? dobStr.trim() : existing.getDob();
        String updatedPhone = (mobile != null && !mobile.trim().isEmpty()) ? mobile.trim() : existing.getPhone();
        String updatedAddress = (address != null) ? address.trim() : existing.getAddress();
        String updatedCity = (city != null) ? city.trim() : existing.getCity();
        String updatedState = (state != null) ? state.trim() : existing.getState();

        boolean updated = studentDAO.updatePersonalDetails(
                userId,
                updatedFname,
                updatedLname,
                updatedGender,
                updatedDob,
                updatedPhone,
                updatedAddress,
                updatedCity,
                updatedState
        );

        if (updated) {
            // Update session cache
            Student refreshed = studentDAO.getStudentById(userId);
            HttpSession session = req.getSession(false);
            if (session != null && refreshed != null) {
                session.setAttribute("userName", refreshed.getFullName());
                session.setAttribute("userFirstName", refreshed.getFirstName());
                session.setAttribute("user", refreshed);
            }
            SessionUtil.setUserName(req, refreshed != null ? refreshed.getFullName() : updatedFname + " " + updatedLname);
            resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&success=1");
        } else {
            resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=updateFailed");
        }
    }
}

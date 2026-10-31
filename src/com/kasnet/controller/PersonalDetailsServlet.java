package com.kasnet.controller;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
public class PersonalDetailsServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

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

        Date dateOfBirth = null;
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                LocalDate requestedDate = LocalDate.parse(dobStr.trim());
                if (requestedDate.isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata")))) {
                    resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=futureDob");
                    return;
                }
                dateOfBirth = Date.valueOf(requestedDate);
            } catch (DateTimeParseException ex) {
                resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&error=invalidDob");
                return;
            }
        }

        // 1. Update User entity
        User user = userDAO.getUserById(userId);
        if (user != null) {
            if (firstName != null && !firstName.trim().isEmpty()) user.setFirstName(firstName.trim());
            if (lastName != null && !lastName.trim().isEmpty()) user.setLastName(lastName.trim());
            if (mobile != null && !mobile.trim().isEmpty()) user.setMobile(mobile.trim());
            if (city != null && !city.trim().isEmpty()) user.setCity(city.trim());
            userDAO.updateUser(user);

            // Update session userName
            SessionUtil.setUserName(req, user.getFullName());
        }

        // 2. Update StudentProfile entity
        StudentProfile profile = profileDAO.getProfileByUserId(userId);
        if (profile == null) {
            profileDAO.createProfile(userId);
            profile = new StudentProfile();
            profile.setUserId(userId);
        }

        if (gender != null && !gender.trim().isEmpty()) profile.setGender(gender.trim());
        if (address != null) profile.setAddress(address.trim());
        if (state != null) profile.setState(state.trim());

        if (dateOfBirth != null) profile.setDateOfBirth(dateOfBirth);

        profileDAO.updatePersonalDetails(profile);

        // 3. Recalculate completion
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&success=1");
    }
}

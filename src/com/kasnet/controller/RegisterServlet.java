package com.kasnet.controller;

import com.kasnet.model.User;
import com.kasnet.service.AuthenticationService;
import com.kasnet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
public class RegisterServlet extends HttpServlet {

    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;

    private AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String captcha = generateCaptcha();
        session.setAttribute("regCaptcha", captcha);
        session.setAttribute("captcha", captcha);

        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String countryCode = req.getParameter("countryCode");
        String mobile = req.getParameter("mobile");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPass = req.getParameter("confirmPassword");
        String role = req.getParameter("role");
        String stream = req.getParameter("stream");
        String branchName = req.getParameter("branchName");
        String collegeName = req.getParameter("collegeName");
        String city = req.getParameter("city");
        String referralCode = req.getParameter("referralCode");

        String captchaInput = req.getParameter("captchaInput");
        if (captchaInput == null) {
            captchaInput = req.getParameter("captcha");
        }

        HttpSession session = req.getSession(false);

        // Required field validation
        if (isNullOrBlank(firstName) || isNullOrBlank(lastName) || isNullOrBlank(email) ||
            isNullOrBlank(mobile) || isNullOrBlank(password) || isNullOrBlank(confirmPass) ||
            isNullOrBlank(collegeName) || isNullOrBlank(stream) || isNullOrBlank(branchName) || isNullOrBlank(city)) {
            forwardWithError(req, resp, "All required fields must be filled.");
            return;
        }

        // Mobile validation (exactly 10 digits)
        if (!ValidationUtil.isValidMobile(mobile)) {
            forwardWithError(req, resp, "Phone number must be exactly 10 digits.");
            return;
        }

        // Email validation
        if (!ValidationUtil.isValidEmail(email.trim())) {
            forwardWithError(req, resp, "Please enter a valid email address.");
            return;
        }

        // Password length & matching
        if (password.length() < 8) {
            forwardWithError(req, resp, "Password must be at least 8 characters long.");
            return;
        }
        if (!password.equals(confirmPass)) {
            forwardWithError(req, resp, "Password and confirm password do not match.");
            return;
        }

        // Build User object (Strictly STUDENT for public registration)
        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setCountryCode(countryCode != null ? countryCode.trim() : "+91");
        user.setMobile(mobile.trim());
        user.setRole("STUDENT");
        user.setStream(stream != null ? stream.trim() : "Graduation");
        user.setBranch(branchName != null ? branchName.trim() : "");
        user.setCollegeName(collegeName != null ? collegeName.trim() : "");
        user.setCity(city != null ? city.trim() : "");
        user.setReferralCode(referralCode != null ? referralCode.trim() : "");

        try {
            authService.register(user, password);
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("registrationSuccess", true);
            resp.sendRedirect(req.getContextPath() + "/?registered=success");
        } catch (Exception e) {
            forwardWithError(req, resp, e.getMessage());
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String captcha = generateCaptcha();
        session.setAttribute("regCaptcha", captcha);
        session.setAttribute("captcha", captcha);

        req.setAttribute("error", error);
        req.setAttribute("errorMessage", error);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    private String generateCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

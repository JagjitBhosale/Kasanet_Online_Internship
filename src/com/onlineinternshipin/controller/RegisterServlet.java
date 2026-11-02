package com.onlineinternshipin.controller;

import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.utility.AuthenticationService;
import com.onlineinternshipin.utility.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * RegisterServlet - Student registration controller.
 * Handles GET (shows registration form with CAPTCHA) and POST (processes registration).
 * Stores records into the production 'student' table.
 */
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;

    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthenticationService();
    }

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
        String stream = req.getParameter("stream");
        String branchName = req.getParameter("branchName");
        String collegeName = req.getParameter("collegeName");
        String city = req.getParameter("city");
        String referralCode = req.getParameter("referralCode");

        String captchaInput = req.getParameter("captchaInput");
        if (captchaInput == null) {
            captchaInput = req.getParameter("captcha");
        }

        // Preserve input fields for form redisplay upon error
        req.setAttribute("enteredFirstName", sanitize(firstName));
        req.setAttribute("enteredLastName", sanitize(lastName));
        req.setAttribute("enteredCountryCode", sanitize(countryCode));
        req.setAttribute("enteredMobile", sanitize(mobile));
        req.setAttribute("enteredEmail", sanitize(email));
        req.setAttribute("enteredStream", sanitize(stream));
        req.setAttribute("enteredBranchName", sanitize(branchName));
        req.setAttribute("enteredCollegeName", sanitize(collegeName));
        req.setAttribute("enteredCity", sanitize(city));
        req.setAttribute("enteredReferralCode", sanitize(referralCode));

        HttpSession session = req.getSession(false);

        // Required field validation
        if (isNullOrBlank(firstName) || isNullOrBlank(lastName) || isNullOrBlank(email) ||
            isNullOrBlank(mobile) || isNullOrBlank(password) || isNullOrBlank(confirmPass) ||
            isNullOrBlank(collegeName) || isNullOrBlank(stream) || isNullOrBlank(branchName) || isNullOrBlank(city)) {
            forwardWithError(req, resp, "All required fields must be filled.");
            return;
        }

        // CAPTCHA validation
        if (session != null) {
            String expectedCaptcha = (String) session.getAttribute("captcha");
            if (expectedCaptcha == null) {
                expectedCaptcha = (String) session.getAttribute("regCaptcha");
            }
            if (expectedCaptcha != null && (captchaInput == null || !expectedCaptcha.equalsIgnoreCase(captchaInput.trim()))) {
                forwardWithError(req, resp, "Incorrect security code (CAPTCHA). Please try again.");
                return;
            }
        }

        // Mobile validation (exactly 10 numeric digits)
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

        // Construct Student model matching production 'student' table
        Student student = new Student();
        student.setFname(firstName.trim());
        student.setLname(lastName.trim());
        student.setEmail(email.trim().toLowerCase());
        student.setPhone(mobile.trim());
        student.setCity(city.trim());
        student.setGradCollegeName(collegeName.trim());

        String resolvedStream = (branchName != null && !branchName.trim().isEmpty())
                ? branchName.trim() : (stream != null ? stream.trim() : "Graduation");
        student.setGradStream(resolvedStream);

        if (referralCode != null && !referralCode.trim().isEmpty()) {
            student.setReferalCode(referralCode.trim());
            student.setReferalText(referralCode.trim());
        }

        int cCode = 91;
        if (countryCode != null) {
            String digits = countryCode.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                try { cCode = Integer.parseInt(digits); } catch (NumberFormatException ignored) {}
            }
        }
        student.setCountryCodeInt(cCode);
        student.setActive(1);

        try {
            authService.register(student, password);

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

    private String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>\"'&]", "").trim();
    }
}

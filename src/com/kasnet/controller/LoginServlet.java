package com.kasnet.controller;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kasnet.model.User;
import com.kasnet.service.AuthenticationService;
import com.kasnet.util.SessionUtil;

/**
 * Servlet handling user login (GET + POST) at /login.
 *
 * GET  – Redirects authenticated users to /home; otherwise generates
 *         a session-level CAPTCHA and forwards to login.jsp.
 * POST – Validates CAPTCHA, authenticates credentials via AuthenticationService,
 *         establishes session on success, or returns to login.jsp with an error.
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Characters used for CAPTCHA generation (no ambiguous 0/O/I/l). */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;

    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
    }

    // -------------------------------------------------------------------------
    // GET — Show login page
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If user is already logged in, redirect to home
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Generate CAPTCHA and store in session
        HttpSession session = request.getSession(true);
        String captcha = generateCaptcha();
        session.setAttribute("captcha", captcha);

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // -------------------------------------------------------------------------
    // POST — Process login form
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // --- Retrieve form fields ---
        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String role     = request.getParameter("role");
        String captchaInput = request.getParameter("captchaInput");
        if (captchaInput == null) {
            captchaInput = request.getParameter("captcha");
        }

        // --- Retrieve session (must already exist from GET) ---
        HttpSession session = request.getSession(false);

        // --- CAPTCHA validation ---
        if (session == null) {
            setErrorAndForwardToLogin(request, response,
                    "Session expired. Please refresh the page and try again.");
            return;
        }

        String expectedCaptcha = (String) session.getAttribute("captcha");
        if (expectedCaptcha == null || captchaInput == null
                || !expectedCaptcha.equalsIgnoreCase(captchaInput.trim())) {
            // Regenerate CAPTCHA so the user can try again
            session.setAttribute("captcha", generateCaptcha());
            // Preserve entered values so user doesn't retype everything
            request.setAttribute("enteredEmail", sanitize(email));
            request.setAttribute("enteredRole",  sanitize(role));
            setErrorAndForwardToLogin(request, response,
                    "Incorrect CAPTCHA. Please try again.");
            return;
        }

        // Invalidate used CAPTCHA immediately (prevent replay)
        session.removeAttribute("captcha");

        // --- Basic presence validation ---
        if (isNullOrBlank(email) || isNullOrBlank(password) || isNullOrBlank(role)) {
            regenerateCaptchaAndForwardError(request, response, session,
                    sanitize(email), sanitize(role),
                    "Email, password, and role are required fields.");
            return;
        }

        // --- Authenticate via service ---
        try {
            User user = authService.login(email.trim(), password, role.trim());

            // Invalidate old session and create a fresh one (session fixation prevention)
            session.invalidate();
            HttpSession newSession = request.getSession(true);

            // Populate session attributes
            newSession.setAttribute("userId",    user.getUserId());
            newSession.setAttribute("userName",  user.getFirstName() + " " + user.getLastName());
            newSession.setAttribute("userEmail", user.getEmail());
            newSession.setAttribute("userRole",  user.getRole());
            newSession.setAttribute("loginSuccessAlert", true);

            // Optional: set a reasonable session timeout (30 minutes)
            newSession.setMaxInactiveInterval(30 * 60);

            // Role-based redirect with login success flag
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin?loginSuccess=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?loginSuccess=1");
            }

        } catch (Exception e) {
            // Re-generate CAPTCHA for the retry attempt
            session = request.getSession(true);
            session.setAttribute("captcha", generateCaptcha());
            request.setAttribute("enteredEmail", sanitize(email));
            request.setAttribute("enteredRole",  sanitize(role));
            setErrorAndForwardToLogin(request, response, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Generates a random alphanumeric CAPTCHA string of length {@value #CAPTCHA_LENGTH}.
     */
    private String generateCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Sets an error attribute and forwards back to login.jsp.
     */
    private void setErrorAndForwardToLogin(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * Regenerates CAPTCHA, stores preserved form values, then forwards with error.
     */
    private void regenerateCaptchaAndForwardError(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   HttpSession session,
                                                   String email,
                                                   String role,
                                                   String errorMessage)
            throws ServletException, IOException {
        session.setAttribute("captcha", generateCaptcha());
        request.setAttribute("enteredEmail", email);
        request.setAttribute("enteredRole",  role);
        setErrorAndForwardToLogin(request, response, errorMessage);
    }

    /** Null-safe blank check. */
    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Returns empty string for null values to prevent NPE in setAttribute. */
    private String sanitize(String s) {
        return s == null ? "" : s.trim();
    }
}

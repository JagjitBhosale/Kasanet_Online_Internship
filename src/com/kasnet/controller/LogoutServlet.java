package com.kasnet.controller;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling user logout at /logout.
 *
 * Both GET and POST requests fully invalidate the current session,
 * clear the JSESSIONID cookie, and redirect the user to /login.
 */
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    // -------------------------------------------------------------------------
    // Shared logout logic
    // -------------------------------------------------------------------------

    /**
     * Invalidates the HTTP session, removes the JSESSIONID cookie, and
     * redirects the browser to the login page.
     *
     * @param request  the current HttpServletRequest
     * @param response the current HttpServletResponse
     */
    private void performLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Invalidate the session if one exists
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Expire the JSESSIONID cookie so the browser removes it
        Cookie jsessionCookie = new Cookie("JSESSIONID", "");
        jsessionCookie.setMaxAge(0);
        jsessionCookie.setPath(request.getContextPath().isEmpty()
                ? "/" : request.getContextPath());
        jsessionCookie.setHttpOnly(true);
        // Uncomment the line below if the app is served over HTTPS:
        // jsessionCookie.setSecure(true);
        response.addCookie(jsessionCookie);

        // Prevent caching of the post-logout response
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

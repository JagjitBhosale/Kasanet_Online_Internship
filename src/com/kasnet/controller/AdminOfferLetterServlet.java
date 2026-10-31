package com.kasnet.controller;

import com.kasnet.service.OfferLetterPdfService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AdminOfferLetterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OfferLetterPdfService pdfService = new OfferLetterPdfService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null
                || !"ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String name = required(request, "name", 100);
        String college = required(request, "college", 150);
        String branch = required(request, "branch", 100);
        String designation = required(request, "designation", 100);
        String duration = required(request, "duration", 20);
        String certificate = required(request, "certificate", 40);
        if (name == null || college == null || branch == null || designation == null || duration == null
                || !"Kasnet Offer Letter".equals(certificate)) {
            showError(request, response, session, "Complete all offer-letter fields and select Kasnet Offer Letter.");
            return;
        }

        int durationMonths = parseDuration(duration);
        LocalDate startDate = parseDate(request.getParameter("internshipStartDate"));
        LocalDate offerDate = parseDate(request.getParameter("offerLetterDate"));
        if (durationMonths < 1 || durationMonths > 24 || startDate == null || offerDate == null) {
            showError(request, response, session, "Enter a duration from 1 to 24 months and valid dates.");
            return;
        }

        byte[] pdf = pdfService.generate(getServletContext(), name, college, branch, designation,
                durationMonths, startDate, offerDate);
        String fileName = "offer_letter_" + name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "") + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    private String required(HttpServletRequest request, String name, int maxLength) {
        String value = request.getParameter(name);
        if (value == null) return null;
        value = value.trim();
        return value.isEmpty() || value.length() > maxLength ? null : value;
    }

    private int parseDuration(String value) {
        if (value == null || !value.matches("(?i)\\d{1,2}\\s*months?")) return -1;
        return Integer.parseInt(value.replaceAll("[^0-9]", ""));
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                           String message) throws IOException {
        session.setAttribute("adminFlashMsg", message);
        session.setAttribute("adminFlashType", "danger");
        response.sendRedirect(request.getContextPath() + "/admin?tab=offer-letter");
    }
}
package com.onlineinternshipin.controller;

import com.onlineinternshipin.model.InternshipEvaluationReport;
import com.onlineinternshipin.model.InternshipEvaluationReport.CriterionEvaluation;
import com.onlineinternshipin.utility.InternshipEvaluationPdfService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AdminEvaluationReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final List<String> INTERNSHIP_STATUSES =
            Arrays.asList("Successfully Completed", "Pending");
    private static final List<String> RECOMMENDATIONS = Arrays.asList(
            "Recommended",
            "Recommended with Improvement Areas",
            "Not Recommended");

    private final InternshipEvaluationPdfService pdfService = new InternshipEvaluationPdfService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.getRequestDispatcher("/views/admin-evaluation-report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String studentName = required(request, "studentName", 120);
        String collegeName = required(request, "collegeName", 180);
        String branchDepartment = required(request, "branchDepartment", 120);
        String city = required(request, "city", 100);
        String internshipDuration = required(request, "internshipDuration", 100);
        String certificationName = required(request, "certificationName", 180);
        String certificationScore = required(request, "certificationScore", 60);
        LocalDate certificationDate = parseDate(request.getParameter("certificationDate"));
        LocalDate evaluationDate = parseDate(request.getParameter("evaluationDate"));

        List<CriterionEvaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < InternshipEvaluationReport.CRITERIA.length; i++) {
            String status = required(request, "status" + i, 40);
            int rating = parseRating(request.getParameter("rating" + i));
            if (status == null || rating < 1 || rating > 5) {
                sendInvalid(response, "Enter a status and a rating from 1 to 5 for every evaluation criterion.");
                return;
            }
            evaluations.add(new CriterionEvaluation(status, rating));
        }

        int overallRating = parseRating(request.getParameter("overallRating"));
        String internshipStatus = required(request, "internshipStatus", 40);
        String recommendation = required(request, "companyRecommendation", 60);
        String keyStrengths = required(request, "keyStrengths", 500);
        String improvementAreas = required(request, "improvementAreas", 500);
        String evaluatorRemarks = required(request, "evaluatorRemarks", 1000);
        String evaluatorName = required(request, "evaluatorName", 120);
        String designation = required(request, "designation", 120);
        String signature = required(request, "signature", 120);

        if (studentName == null || collegeName == null || branchDepartment == null || city == null
                || internshipDuration == null || certificationName == null || certificationScore == null
                || certificationDate == null || evaluationDate == null
                || overallRating < 1 || overallRating > 5 || internshipStatus == null
                || !INTERNSHIP_STATUSES.contains(internshipStatus) || recommendation == null
                || !RECOMMENDATIONS.contains(recommendation) || keyStrengths == null
                || improvementAreas == null || evaluatorRemarks == null || evaluatorName == null
                || designation == null || signature == null) {
            sendInvalid(response, "Complete all report fields with valid dates and ratings.");
            return;
        }

        InternshipEvaluationReport report = new InternshipEvaluationReport(
                studentName, collegeName, branchDepartment, city, internshipDuration,
                certificationName, certificationScore, certificationDate, evaluations,
                overallRating, internshipStatus, recommendation, keyStrengths, improvementAreas,
                evaluatorRemarks, evaluatorName, designation, signature, evaluationDate);
        byte[] pdf = pdfService.generate(getServletContext(), report);
        String safeName = studentName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=\"internship_evaluation_" + (safeName.isEmpty() ? "report" : safeName) + ".pdf\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && session.getAttribute("userId") != null
                && "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")));
    }

    private String required(HttpServletRequest request, String name, int maxLength) {
        String value = request.getParameter(name);
        if (value == null) return null;
        value = value.trim();
        return value.isEmpty() || value.length() > maxLength ? null : value;
    }

    private int parseRating(String value) {
        if (value == null) return -1;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private void sendInvalid(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}

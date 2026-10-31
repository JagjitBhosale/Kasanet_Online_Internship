package com.kasnet.controller;

import com.kasnet.dao.PlatformCertificateDAO;
import com.kasnet.model.PlatformCertificate;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PlatformCertificateServlet extends HttpServlet {

    private static final List<String> SUPPORTED_EXAMS = Arrays.asList(
            "AZ-900 – Microsoft Azure Fundamentals",
            "AI-901 – Microsoft Azure AI Fundamentals",
            "PL-900 – Microsoft Power Platform Fundamentals",
            "DP-900 – Microsoft Azure Data Fundamentals",
            "Cisco Certified Cybersecurity Associate"
    );
    private static final BigDecimal MAX_SCORE = new BigDecimal("1000");
    private final PlatformCertificateDAO certificateDAO = new PlatformCertificateDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard?section=certificates");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!SessionUtil.requireLogin(request, response)) {
            return;
        }

        Integer userId = SessionUtil.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("delete".equals(action)) {
                int certificateId = Integer.parseInt(request.getParameter("certificateId"));
                boolean deleted = certificateDAO.delete(certificateId, userId);
                redirect(request, response, deleted ? "certificate-deleted" : "certificate-not-found", null);
                return;
            }

            PlatformCertificate certificate = parseCertificate(request, userId);
            if (certificateDAO.add(certificate)) {
                redirect(request, response, "certificate-added", null);
            } else {
                redirect(request, response, null, "save-failed");
            }
        } catch (IllegalArgumentException e) {
            redirect(request, response, null, "invalid-certificate");
        } catch (SQLException e) {
            throw new ServletException("Unable to save or delete platform certificate.", e);
        }
    }

    private PlatformCertificate parseCertificate(HttpServletRequest request, int userId) {
        String examName = required(request, "examName");
        if ("Other".equals(examName)) {
            examName = required(request, "otherExamName");
        } else if (!SUPPORTED_EXAMS.contains(examName)) {
            throw new IllegalArgumentException("Unsupported exam.");
        }
        if (examName.length() > 200) {
            throw new IllegalArgumentException("Exam name is too long.");
        }

        String scoreValue = required(request, "scoreObtained");
        if (!scoreValue.matches("\\d{1,4}")) {
            throw new IllegalArgumentException("Score must be a whole number.");
        }
        BigDecimal score = new BigDecimal(scoreValue);
        if (score.compareTo(MAX_SCORE) > 0) {
            throw new IllegalArgumentException("Score must be between 0 and 1000.");
        }

        Date achievementDate = Date.valueOf(required(request, "achievementDate"));
        if (achievementDate.toLocalDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Achievement date cannot be in the future.");
        }

        PlatformCertificate certificate = new PlatformCertificate();
        certificate.setUserId(userId);
        certificate.setExamName(examName);
        certificate.setScoreObtained(score);
        certificate.setTotalScore(MAX_SCORE);
        certificate.setAchievementDate(achievementDate);
        return certificate;
    }

    private String required(HttpServletRequest request, String parameter) {
        String value = request.getParameter(parameter);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field: " + parameter);
        }
        return value.trim();
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String success, String error)
            throws IOException {
        StringBuilder target = new StringBuilder(request.getContextPath())
                .append("/dashboard?section=certificates");
        if (success != null) {
            target.append("&success=").append(success);
        }
        if (error != null) {
            target.append("&error=").append(error);
        }
        response.sendRedirect(target.toString());
    }
}

package com.kasnet.util;

import com.kasnet.dao.EducationDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.Education;
import com.kasnet.model.User;
import com.kasnet.service.AuthenticationService;

import java.sql.Connection;
import java.util.List;

/**
 * SystemVerifier - Validates complete database connectivity,
 * authentication flows, validations, and profile completeness calculations.
 */
public class SystemVerifier {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" MY INTERNSHIP TECHNOLOGIES - SYSTEM VERIFICATION");
        System.out.println("==================================================");

        boolean allPassed = true;

        // 0. Environment & .env Loading
        System.out.println("[CONFIG] .env source: " + EnvUtil.getSource());
        System.out.println("[CONFIG] Database info: " + DBConnection.getMaskedConnectionInfo());
        System.out.print("[TEST 0] Verifying .env Configuration & Groq API Key... ");
        com.kasnet.service.GroqAIService groqService = new com.kasnet.service.GroqAIService();
        String groqKey = groqService.resolveApiKey(null);
        if (groqKey != null && groqKey.startsWith("gsk_")) {
            System.out.println("PASSED (Key masked: " + groqService.getMaskedApiKey() + ")");
        } else {
            System.out.println("FAILED (Could not resolve Groq API key from .env)");
            allPassed = false;
        }

        // 1. MySQL Connection
        System.out.print("[TEST 1] Verifying MySQL connection via EnvUtil credentials... ");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("PASSED (Connected to online_internship with user '" + DBConnection.getDbUser() + "')");
            } else {
                System.out.println("FAILED (Connection is null or closed)");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            allPassed = false;
        }

        // 2. ValidationUtil Checks
        System.out.print("[TEST 2] Verifying Input Validations... ");
        boolean valOk = ValidationUtil.isValidMobile("9876543210")
                && !ValidationUtil.isValidMobile("12345")
                && !ValidationUtil.isValidMobile("98765432101")
                && ValidationUtil.isValidEmail("student@myinternshiptech.com")
                && !ValidationUtil.isValidEmail("invalid-email")
                && ValidationUtil.isValidPercentage("85.5")
                && !ValidationUtil.isValidPercentage("105.0")
                && !ValidationUtil.isValidPercentage("-2")
                && ValidationUtil.isValidDate("2002-05-15")
                && !ValidationUtil.isValidDate("2099-01-01");

        if (valOk) {
            System.out.println("PASSED (Phone 10-digits, Percentage 0-100, Email, Date verified)");
        } else {
            System.out.println("FAILED (Validation logic issue)");
            allPassed = false;
        }

        // 3. AuthenticationService - Hardcoded Test ID
        System.out.print("[TEST 3] Verifying Hardcoded Student Test ID (test@myinternshiptech.com / test@kasnet.in)... ");
        AuthenticationService authService = new AuthenticationService();
        try {
            User testStudent = authService.login(AuthenticationService.TEST_STUDENT_EMAIL, AuthenticationService.TEST_STUDENT_PASSWORD, "STUDENT");
            if (testStudent != null && "STUDENT".equalsIgnoreCase(testStudent.getRole())) {
                System.out.println("PASSED (Authenticated as Student: " + testStudent.getFullName() + ")");
            } else {
                System.out.println("FAILED (Test ID failed authentication)");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            allPassed = false;
        }

        // 4. AuthenticationService - Admin Login
        System.out.print("[TEST 4] Verifying Admin Account... ");
        try {
            User admin = null;
            try {
                admin = authService.login("admin@myinternshiptech.com", "Admin@12345", "ADMIN");
            } catch (Exception ignored) {
                admin = authService.login("admin@kasnet.in", "Admin@12345", "ADMIN");
            }
            if (admin != null && "ADMIN".equalsIgnoreCase(admin.getRole())) {
                System.out.println("PASSED (Authenticated as Admin: " + admin.getFullName() + ")");
            } else {
                System.out.println("SKIPPED / OPTIONAL (No seeded admin with Admin@12345 in local db)");
            }
        } catch (Exception e) {
            System.out.println("SKIPPED / OPTIONAL (" + e.getMessage() + ")");
        }

        // 5. UserDAO - Admin User Listing
        System.out.print("[TEST 5] Verifying Admin User Monitoring Query... ");
        UserDAO userDAO = new UserDAO();
        try {
            List<User> allUsers = userDAO.getAllUsers();
            if (allUsers != null && !allUsers.isEmpty()) {
                System.out.println("PASSED (Retrieved " + allUsers.size() + " registered accounts)");
            } else {
                System.out.println("FAILED (User list empty)");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            allPassed = false;
        }

        // 6. ProfileDAO - Completeness Calculation
        System.out.print("[TEST 6] Verifying Profile Completeness Calculation... ");
        ProfileDAO profileDAO = new ProfileDAO();
        try {
            int score = profileDAO.calculateProfileCompletion(1);
            if (score >= 0 && score <= 100) {
                System.out.println("PASSED (Dynamic Score calculated: " + score + "%)");
            } else {
                System.out.println("FAILED (Invalid score: " + score + ")");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            allPassed = false;
        }

        // 7. EducationDAO - Validated Save
        System.out.print("[TEST 7] Verifying Education Percentage & Record Persistence... ");
        EducationDAO eduDAO = new EducationDAO();
        try {
            Education edu = new Education();
            edu.setUserId(1);
            edu.setLevel("Graduation");
            edu.setSchoolCollegeName("Kasnet Institute of Technology");
            edu.setBoardUniversity("Pune University");
            edu.setStream("B.Tech / B.E.");
            edu.setPassingYear(2024);
            edu.setPercentage(88.5);
            boolean saved = eduDAO.saveOrUpdateEducation(edu);
            if (saved) {
                System.out.println("PASSED (Education record saved with 88.5%)");
            } else {
                System.out.println("FAILED (Could not save education)");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            allPassed = false;
        }

        System.out.println("==================================================");
        if (allPassed) {
            System.out.println(" ALL VERIFICATION TESTS PASSED SUCCESSFULLY!");
        } else {
            System.out.println(" SOME TESTS ENCOUNTERED ISSUES. SEE LOG ABOVE.");
        }
        System.out.println("==================================================");
    }
}

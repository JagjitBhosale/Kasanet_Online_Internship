package com.onlineinternshipin.test;

import com.onlineinternshipin.dao.AdminDAO;
import com.onlineinternshipin.dao.EducationDAO;
import com.onlineinternshipin.dao.EngineeringMarksDAO;
import com.onlineinternshipin.dao.ProfileDAO;
import com.onlineinternshipin.dao.SkillDAO;
import com.onlineinternshipin.dao.StudentDAO;
import com.onlineinternshipin.model.Education;
import com.onlineinternshipin.model.EngineeringMarks;
import com.onlineinternshipin.model.Skill;
import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.model.StudentProfile;
import com.onlineinternshipin.model.User;
import com.onlineinternshipin.utility.AuthenticationService;
import com.onlineinternshipin.utility.DBConnection;
import com.onlineinternshipin.utility.PasswordUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * ProductionSchemaIntegrationTest
 *
 * Full integration test suite executing end-to-end against the production
 * 'onlineinternship' MySQL database schema.
 */
public class ProductionSchemaIntegrationTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   ONLINEINTERNSHIP.IN - PRODUCTION SCHEMA INTEGRATION TEST SUITE");
        System.out.println("=================================================================");
        System.out.println("Connecting to: " + DBConnection.getMaskedConnectionInfo());

        StudentDAO studentDAO = new StudentDAO();
        AdminDAO adminDAO = new AdminDAO();
        ProfileDAO profileDAO = new ProfileDAO();
        EducationDAO educationDAO = new EducationDAO();
        SkillDAO skillDAO = new SkillDAO();
        EngineeringMarksDAO marksDAO = new EngineeringMarksDAO();
        AuthenticationService authService = new AuthenticationService(studentDAO, adminDAO);

        try {
            // Clean test records before start
            cleanupTestRecords();

            // 1. REGISTRATION MODULE TESTS
            testStudentRegistration(authService, studentDAO);
            testRegistrationValidations(authService, studentDAO);

            // 2. LOGIN & LOGOUT MODULE TESTS
            testStudentLogin(authService, studentDAO);
            testAdminLogin(authService, adminDAO);
            testInactiveStudentAccount(authService, studentDAO);

            // 3. PROFILE VIEWING MODULE TESTS
            testProfileViewing(studentDAO, profileDAO, educationDAO, skillDAO);

            // 4. PROFILE EDITING MODULE TESTS
            testProfileEditingPersonalDetails(studentDAO, profileDAO);
            testProfileEditingEducation(studentDAO, educationDAO);
            testProfileEditingSkills(studentDAO, skillDAO);
            testProfileEditingProjectsAndCertifications(studentDAO);
            testProfileNullSafety(studentDAO);

            // 5. ENGINEERING YEAR-WISE PERFORMANCE TESTS
            testEngineeringMarksStorage(studentDAO, marksDAO);

            System.out.println("\n=================================================================");
            System.out.println("TEST RESULTS: " + testsPassed + " PASSED, " + testsFailed + " FAILED");
            System.out.println("=================================================================");

            if (testsFailed > 0) {
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            cleanupTestRecords();
        }
    }

    private static void assertTrue(String testName, boolean condition, String message) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName + " — " + message);
            testsFailed++;
        }
    }

    private static void cleanupTestRecords() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM `engineering_year_marks` WHERE user_id IN (SELECT id FROM `student` WHERE email LIKE '%@testsuite.com')");
            stmt.executeUpdate("DELETE FROM `student` WHERE email LIKE '%@testsuite.com'");
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------------------------
    // 1. Student Registration Tests
    // -------------------------------------------------------------------------
    private static void testStudentRegistration(AuthenticationService authService, StudentDAO studentDAO) throws Exception {
        Student s = new Student();
        s.setFname("Aarav");
        s.setLname("Patel");
        s.setEmail("aarav.patel@testsuite.com");
        s.setPhone("9876543299");
        s.setCity("Mumbai");
        s.setGradCollegeName("VJTI Mumbai");
        s.setGradStream("Computer Science");
        s.setReferalCode("REF1234");
        s.setCountryCodeInt(91);

        Student registered = authService.register(s, "SecretPassword123!");
        assertTrue("Registration - Returned Object Valid", registered != null && registered.getId() > 0, "Student ID must be > 0");

        // Verify direct DB persistence
        Student dbStudent = studentDAO.getStudentById(registered.getId());
        assertTrue("Registration - DB Record Exists", dbStudent != null, "Student record must exist in DB");
        assertTrue("Registration - First Name Aligned", "Aarav".equals(dbStudent.getFname()), "Fname matches");
        assertTrue("Registration - Last Name Aligned", "Patel".equals(dbStudent.getLname()), "Lname matches");
        assertTrue("Registration - Phone Aligned", "9876543299".equals(dbStudent.getPhone()), "Phone matches");
        assertTrue("Registration - College Aligned", "VJTI Mumbai".equals(dbStudent.getGradCollegeName()), "College matches");
        assertTrue("Registration - Stream Aligned", "Computer Science".equals(dbStudent.getGradStream()), "Stream matches");
        assertTrue("Registration - Active Status Default 1", dbStudent.getActive() != null && dbStudent.getActive() == 1, "Active status must be 1");
        assertTrue("Registration - Password BCrypt Hashed", dbStudent.getPassword().startsWith("$2a$"), "Password must be BCrypt hashed");
        assertTrue("Registration - Password Matches Plaintext", PasswordUtil.checkPassword("SecretPassword123!", dbStudent.getPassword()), "BCrypt check matches");
    }

    private static void testRegistrationValidations(AuthenticationService authService, StudentDAO studentDAO) {
        // Duplicate email
        Student sDupEmail = new Student();
        sDupEmail.setFname("Duplicate");
        sDupEmail.setLname("Email");
        sDupEmail.setEmail("aarav.patel@testsuite.com");
        sDupEmail.setPhone("9123456789");

        boolean emailDupBlocked = false;
        try {
            authService.register(sDupEmail, "AnotherPass123!");
        } catch (Exception ex) {
            emailDupBlocked = ex.getMessage().contains("already exists");
        }
        assertTrue("Validation - Duplicate Email Blocked", emailDupBlocked, "Duplicate email registration must fail");

        // Duplicate phone
        Student sDupPhone = new Student();
        sDupPhone.setFname("Duplicate");
        sDupPhone.setLname("Phone");
        sDupPhone.setEmail("different.email@testsuite.com");
        sDupPhone.setPhone("9876543299");

        boolean phoneDupBlocked = false;
        try {
            authService.register(sDupPhone, "AnotherPass123!");
        } catch (Exception ex) {
            phoneDupBlocked = ex.getMessage().contains("already registered");
        }
        assertTrue("Validation - Duplicate Phone Blocked", phoneDupBlocked, "Duplicate phone registration must fail");

        // Missing required fields
        boolean missingEmailBlocked = false;
        try {
            Student sMissing = new Student();
            sMissing.setFname("No");
            sMissing.setLname("Email");
            authService.register(sMissing, "Pass12345");
        } catch (Exception ex) {
            missingEmailBlocked = true;
        }
        assertTrue("Validation - Missing Required Email Blocked", missingEmailBlocked, "Missing email must be rejected");
    }

    // -------------------------------------------------------------------------
    // 2. Student Login Tests
    // -------------------------------------------------------------------------
    private static void testStudentLogin(AuthenticationService authService, StudentDAO studentDAO) throws Exception {
        // Valid login
        User user = authService.login("aarav.patel@testsuite.com", "SecretPassword123!", "STUDENT");
        assertTrue("Login - Valid Credentials Succeeded", user != null && user.getUserId() > 0, "User returned on valid login");
        assertTrue("Login - Role is STUDENT", "STUDENT".equalsIgnoreCase(user.getRole()), "Role must be STUDENT");
        assertTrue("Login - Full Name Available", "Aarav Patel".equals(user.getFullName()), "Full name concatenated correctly");

        // Login by mobile number
        User userByPhone = authService.login("9876543299", "SecretPassword123!", "STUDENT");
        assertTrue("Login - Mobile Identifier Succeeded", userByPhone != null && userByPhone.getUserId() == user.getUserId(), "Can login with 10-digit mobile");

        // Invalid password
        boolean wrongPassCaught = false;
        try {
            authService.login("aarav.patel@testsuite.com", "WrongPassword999!", "STUDENT");
        } catch (Exception ex) {
            wrongPassCaught = ex.getMessage().contains("Invalid email or password");
        }
        assertTrue("Login - Incorrect Password Rejected", wrongPassCaught, "Wrong password must fail");

        // Unknown account
        boolean unknownAccountCaught = false;
        try {
            authService.login("nonexistent.user@testsuite.com", "SomePassword123!", "STUDENT");
        } catch (Exception ex) {
            unknownAccountCaught = ex.getMessage().contains("Invalid email or password");
        }
        assertTrue("Login - Unknown Account Rejected", unknownAccountCaught, "Unknown account must fail");
    }

    private static void testAdminLogin(AuthenticationService authService, AdminDAO adminDAO) throws Exception {
        com.onlineinternshipin.dao.UserDAO userDAO = new com.onlineinternshipin.dao.UserDAO();

        // 1. Direct AdminDAO queries against production 'admin' table
        User adminByEmail = adminDAO.getAdminByEmail("admin@kasnet.in");
        assertTrue("AdminDAO - Lookup by Email",
                adminByEmail != null && "admin@kasnet.in".equalsIgnoreCase(adminByEmail.getEmail()),
                "Admin record found by email in production 'admin' table");

        assertTrue("AdminDAO - Role and Names Mapped",
                adminByEmail != null && "ADMIN".equalsIgnoreCase(adminByEmail.getRole())
                        && "Portal".equals(adminByEmail.getFirstName())
                        && "Admin".equals(adminByEmail.getLastName()),
                "Admin entity mapped properly with fname/lname/role");

        User adminById = adminDAO.getAdminById(adminByEmail.getUserId());
        assertTrue("AdminDAO - Lookup by ID",
                adminById != null && adminById.getUserId() == adminByEmail.getUserId(),
                "Admin record found by primary key id");

        List<User> allAdmins = adminDAO.getAllAdmins();
        assertTrue("AdminDAO - Get All Admins",
                allAdmins != null && !allAdmins.isEmpty(),
                "getAllAdmins returned records from production 'admin' table");

        // 2. AuthenticationService - Valid Admin Login
        User loggedInAdmin = authService.login("admin@kasnet.in", "Admin@12345", "ADMIN");
        assertTrue("Login - Valid Admin Database Credentials",
                loggedInAdmin != null && "ADMIN".equalsIgnoreCase(loggedInAdmin.getRole())
                        && "admin@kasnet.in".equalsIgnoreCase(loggedInAdmin.getEmail()),
                "Admin successfully authenticated against production DB record");

        // 3. AuthenticationService - Admin Login with Invalid Password
        boolean wrongPasswordBlocked = false;
        try {
            authService.login("admin@kasnet.in", "WrongPassword999!", "ADMIN");
        } catch (Exception ex) {
            wrongPasswordBlocked = true;
        }
        assertTrue("Login - Admin Wrong Password Rejected", wrongPasswordBlocked, "Invalid admin password threw exception");

        // 4. AuthenticationService - Admin Login with Non-existent Email
        boolean unknownAdminBlocked = false;
        try {
            authService.login("nonexistent.admin@kasnet.in", "Admin@12345", "ADMIN");
        } catch (Exception ex) {
            unknownAdminBlocked = true;
        }
        assertTrue("Login - Non-existent Admin Email Rejected", unknownAdminBlocked, "Unknown admin email threw exception");

        // 5. Cross-Role Validation: Student credential attempting ADMIN login
        boolean studentAsAdminBlocked = false;
        try {
            authService.login("aarav.patel@testsuite.com", "SecretPassword123!", "ADMIN");
        } catch (Exception ex) {
            studentAsAdminBlocked = true;
        }
        assertTrue("Login - Student Credentials Denied Admin Role", studentAsAdminBlocked, "Student cannot log in with ADMIN role");

        // 6. Cross-Role Validation: Admin credential attempting STUDENT login
        boolean adminAsStudentBlocked = false;
        try {
            authService.login("admin@kasnet.in", "Admin@12345", "STUDENT");
        } catch (Exception ex) {
            adminAsStudentBlocked = true;
        }
        assertTrue("Login - Admin Credentials Denied Student Role", adminAsStudentBlocked, "Admin cannot log in with STUDENT role");

        // 7. UserDAO Integration for Admin Role
        User userFromDAO = userDAO.getAdminById(adminByEmail.getUserId());
        assertTrue("UserDAO - Admin lookup by ID",
                userFromDAO != null && "ADMIN".equalsIgnoreCase(userFromDAO.getRole()),
                "UserDAO returned admin user by admin ID");

        User userByEmailDAO = userDAO.getUserByEmail("admin@kasnet.in");
        assertTrue("UserDAO - Fallback lookup for Admin by Email",
                userByEmailDAO != null && "ADMIN".equalsIgnoreCase(userByEmailDAO.getRole()),
                "UserDAO returned admin user by email");

        List<User> combinedUsers = userDAO.getAllUsers();
        boolean containsAdmin = false;
        for (User u : combinedUsers) {
            if ("ADMIN".equalsIgnoreCase(u.getRole())) {
                containsAdmin = true;
                break;
            }
        }
        assertTrue("UserDAO - Combined User List Includes Admins", containsAdmin, "getAllUsers includes admin accounts");
    }

    private static void testInactiveStudentAccount(AuthenticationService authService, StudentDAO studentDAO) throws Exception {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");
        s.setActive(0);
        studentDAO.updatePersonalDetails(s.getId(), s.getFname(), s.getLname(), s.getGender(), s.getDob(), s.getPhone(), s.getAddress(), s.getCity(), s.getState());

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE `student` SET active = 0 WHERE id = " + s.getId());
        }

        boolean inactiveBlocked = false;
        try {
            authService.login("aarav.patel@testsuite.com", "SecretPassword123!", "STUDENT");
        } catch (Exception ex) {
            inactiveBlocked = ex.getMessage().contains("inactive");
        }
        assertTrue("Login - Inactive Account Blocked", inactiveBlocked, "Inactive student must be blocked from logging in");

        // Restore active
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE `student` SET active = 1 WHERE id = " + s.getId());
        }
    }

    // -------------------------------------------------------------------------
    // 3. Profile Viewing Tests
    // -------------------------------------------------------------------------
    private static void testProfileViewing(StudentDAO studentDAO, ProfileDAO profileDAO, EducationDAO educationDAO, SkillDAO skillDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");
        assertTrue("Profile - Retrieve by ID", s != null, "Student retrieved by ID");

        StudentProfile profile = profileDAO.getProfileByUserId(s.getId());
        assertTrue("Profile - Profile Model Mapped", profile != null && profile.getUserId() == s.getId(), "StudentProfile mapped successfully");

        int completion = studentDAO.calculateProfileCompletion(s);
        assertTrue("Profile - Dynamic Completion Calculated", completion >= 20 && completion <= 100, "Completion score between 20% and 100%");

        List<Education> eduList = educationDAO.getEducationByUserId(s.getId());
        assertTrue("Profile - Academic Details List Accessible", eduList != null && eduList.size() == 5, "Education levels initialized (SSC, HSC, Diploma, Graduation, Master)");

        List<Skill> skills = skillDAO.getSkillsByUserId(s.getId());
        assertTrue("Profile - Skills Accessible Without SQL Exception", skills != null, "Skills query succeeds");
    }

    // -------------------------------------------------------------------------
    // 4. Profile Editing Tests
    // -------------------------------------------------------------------------
    private static void testProfileEditingPersonalDetails(StudentDAO studentDAO, ProfileDAO profileDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        boolean updated = studentDAO.updatePersonalDetails(
                s.getId(),
                "AaravUpdated",
                "PatelUpdated",
                "Male",
                "2002-05-15",
                "9876543299",
                "Flat 101, Marine Drive",
                "Mumbai",
                "Maharashtra"
        );
        assertTrue("Profile Edit - Personal Details Update", updated, "Personal details updated successfully");

        Student refreshed = studentDAO.getStudentById(s.getId());
        assertTrue("Profile Edit - Verify Updated Fname", "AaravUpdated".equals(refreshed.getFname()), "Fname updated in DB");
        assertTrue("Profile Edit - Verify Updated Gender", "Male".equals(refreshed.getGender()), "Gender updated in DB");
        assertTrue("Profile Edit - Verify Updated DOB", "2002-05-15".equals(refreshed.getDob()), "DOB updated in DB");
        assertTrue("Profile Edit - Verify Updated Address", "Flat 101, Marine Drive".equals(refreshed.getAddress()), "Address updated in DB");
        assertTrue("Profile Edit - Verify Updated State", "Maharashtra".equals(refreshed.getState()), "State updated in DB");
    }

    private static void testProfileEditingEducation(StudentDAO studentDAO, EducationDAO educationDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        // Update SSC
        Education ssc = new Education();
        ssc.setUserId(s.getId());
        ssc.setLevel("SSC");
        ssc.setSchoolCollegeName("St. Xavier's High School");
        ssc.setBoardUniversity("Maharashtra State Board");
        ssc.setPassingYear(2018);
        ssc.setPercentage(92.4);
        boolean sscSaved = educationDAO.saveOrUpdateEducation(ssc);
        assertTrue("Profile Edit - SSC Education Saved", sscSaved, "SSC education saved");

        // Update Graduation
        Education grad = new Education();
        grad.setUserId(s.getId());
        grad.setLevel("Graduation");
        grad.setSchoolCollegeName("VJTI Mumbai Engineering");
        grad.setBoardUniversity("Mumbai University");
        grad.setStream("Computer Engineering");
        grad.setPassingYear(2024);
        grad.setPercentage(88.5);
        boolean gradSaved = educationDAO.saveOrUpdateEducation(grad);
        assertTrue("Profile Edit - Graduation Education Saved", gradSaved, "Graduation education saved");

        // Verify from database
        Student refreshed = studentDAO.getStudentById(s.getId());
        assertTrue("Profile Edit - Verify SSC in student table", "St. Xavier's High School".equals(refreshed.getSchool10Name()), "10schoolname updated in student table");
        assertTrue("Profile Edit - Verify 10percentage in student table", "92.4".equals(refreshed.getPercentage10()), "10percentage updated in student table");
        assertTrue("Profile Edit - Verify Graduation College in student table", "VJTI Mumbai Engineering".equals(refreshed.getGradCollegeName()), "gradcollegename updated in student table");
        assertTrue("Profile Edit - Verify Graduation Stream in student table", "Computer Engineering".equals(refreshed.getGradStream()), "gradstream updated in student table");
    }

    private static void testProfileEditingSkills(StudentDAO studentDAO, SkillDAO skillDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        List<String> techSkills = Arrays.asList("Java", "MySQL", "JSP", "Servlets");
        boolean techSaved = skillDAO.saveSkillsOfType(s.getId(), "Technical", techSkills);
        assertTrue("Profile Edit - Technical Skills Saved", techSaved, "Technical skills saved");

        List<String> softSkills = Arrays.asList("Leadership", "Communication", "Teamwork");
        boolean softSaved = skillDAO.saveSkillsOfType(s.getId(), "Soft", softSkills);
        assertTrue("Profile Edit - Soft Skills Saved", softSaved, "Soft skills saved");

        Student refreshed = studentDAO.getStudentById(s.getId());
        assertTrue("Profile Edit - Verify technicalskill column", refreshed.getTechnicalSkill().contains("Java"), "technicalskill column updated");
        assertTrue("Profile Edit - Verify softskill column", refreshed.getSoftSkill().contains("Leadership"), "softskill column updated");
    }

    private static void testProfileEditingProjectsAndCertifications(StudentDAO studentDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        boolean projUpdated = studentDAO.updateProjects(s.getId(), "E-Commerce Microservices", "Spring Boot and React portal");
        assertTrue("Profile Edit - Project Details Saved", projUpdated, "Project updated");

        boolean certUpdated = studentDAO.updateCertifications(s.getId(), "Oracle Certified Java Associate", "Oracle Corporation", "Java SE 8");
        assertTrue("Profile Edit - Certification Details Saved", certUpdated, "Certification updated");

        Student refreshed = studentDAO.getStudentById(s.getId());
        assertTrue("Profile Edit - Verify Projectname column", "E-Commerce Microservices".equals(refreshed.getProjectName()), "Projectname updated");
        assertTrue("Profile Edit - Verify certification column", "Oracle Certified Java Associate".equals(refreshed.getCertification()), "certification updated");
    }

    private static void testProfileNullSafety(StudentDAO studentDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        // Pass nulls to optional fields
        boolean ok = studentDAO.updatePersonalDetails(
                s.getId(),
                "AaravUpdated",
                "PatelUpdated",
                null, // gender null
                null, // dob null
                "9876543299",
                null, // address null
                null, // city null
                null  // state null
        );
        assertTrue("Profile Edit - NULL Safety on Optional Fields", ok, "Null optional fields update smoothly without SQL failure");

        Student refreshed = studentDAO.getStudentById(s.getId());
        assertTrue("Profile Edit - Null Safe Verification", refreshed.getAddress() == null, "Address column correctly set to NULL in DB");
    }

    // -------------------------------------------------------------------------
    // 5. Engineering Year-wise Marks Storage Tests
    // -------------------------------------------------------------------------
    private static void testEngineeringMarksStorage(StudentDAO studentDAO, EngineeringMarksDAO marksDAO) {
        Student s = studentDAO.getStudentByEmail("aarav.patel@testsuite.com");

        // 1. Save First Year Percentage
        EngineeringMarks fe = new EngineeringMarks();
        fe.setUserId(s.getId());
        fe.setYearLevel("First Year");
        fe.setScoreType("Percentage");
        fe.setScoreValue(82.50);
        fe.setPassingYear(2021);
        fe.setCollegeName("VJTI Engineering");
        boolean feSaved = marksDAO.saveOrUpdateMark(fe);
        assertTrue("Engineering Marks - Save First Year", feSaved, "FE score saved to engineering_year_marks");

        // 2. Save Second Year CGPA
        EngineeringMarks se = new EngineeringMarks();
        se.setUserId(s.getId());
        se.setYearLevel("Second Year");
        se.setScoreType("CGPA");
        se.setScoreValue(8.95);
        se.setPassingYear(2022);
        se.setCollegeName("VJTI Engineering");
        boolean seSaved = marksDAO.saveOrUpdateMark(se);
        assertTrue("Engineering Marks - Save Second Year CGPA", seSaved, "SE CGPA score saved to engineering_year_marks");

        // 3. Query records
        List<EngineeringMarks> list = marksDAO.getMarksByUserId(s.getId());
        assertTrue("Engineering Marks - List Contains 2 Records", list != null && list.size() == 2, "List contains FE and SE records");

        EngineeringMarks feFromDb = list.get(0);
        assertTrue("Engineering Marks - Formatted Percentage", "82.50 %".equals(feFromDb.getFormattedScore()), "Formatted score is 82.50 %");

        EngineeringMarks seFromDb = list.get(1);
        assertTrue("Engineering Marks - Formatted CGPA", "8.95 CGPA".equals(seFromDb.getFormattedScore()), "Formatted score is 8.95 CGPA");

        // 4. Update FE Score
        feFromDb.setScoreValue(86.00);
        boolean feUpdated = marksDAO.updateMarkById(feFromDb);
        assertTrue("Engineering Marks - Update FE Score", feUpdated, "FE score updated via updateMarkById");

        EngineeringMarks feRefreshed = marksDAO.getMarksById(feFromDb.getMarkId(), s.getId());
        assertTrue("Engineering Marks - Verify Updated Value", feRefreshed != null && feRefreshed.getScoreValue() == 86.00, "Updated score value matches 86.00");

        // 5. Delete SE record
        boolean seDeleted = marksDAO.deleteMark(seFromDb.getMarkId(), s.getId());
        assertTrue("Engineering Marks - Delete Record", seDeleted, "SE record deleted");

        List<EngineeringMarks> remaining = marksDAO.getMarksByUserId(s.getId());
        assertTrue("Engineering Marks - Remaining Count 1", remaining != null && remaining.size() == 1, "Remaining count is 1 after deletion");
    }
}

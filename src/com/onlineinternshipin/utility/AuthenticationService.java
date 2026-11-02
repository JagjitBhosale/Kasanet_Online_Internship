package com.onlineinternshipin.utility;

import com.onlineinternshipin.dao.AdminDAO;
import com.onlineinternshipin.dao.StudentDAO;
import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.model.User;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AuthenticationService - Handles student registration, credential verification,
 * and session login workflows against the production 'student' and 'admin' tables.
 */
public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    public static final String TEST_STUDENT_EMAIL = "test@kasnet.in";
    public static final String TEST_STUDENT_EMAIL_ALT = "test@myinternshiptech.com";
    public static final String TEST_STUDENT_PASSWORD = "Test@12345";

    private final StudentDAO studentDAO;
    private final AdminDAO adminDAO;

    public AuthenticationService() {
        this.studentDAO = new StudentDAO();
        this.adminDAO = new AdminDAO();
    }

    public AuthenticationService(StudentDAO studentDAO, AdminDAO adminDAO) {
        this.studentDAO = studentDAO;
        this.adminDAO = adminDAO;
    }

    // -------------------------------------------------------------------------
    // Registration Workflow
    // -------------------------------------------------------------------------
    public Student register(Student student, String plainPassword) throws Exception {
        if (student == null) {
            throw new IllegalArgumentException("Student registration data cannot be null.");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (student.getFname() == null || student.getFname().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (student.getLname() == null || student.getLname().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        String normalizedEmail = student.getEmail().trim().toLowerCase();
        student.setEmail(normalizedEmail);

        // 1. Duplicate email check
        if (studentDAO.emailExists(normalizedEmail)) {
            throw new Exception("An account with this email already exists.");
        }

        // 2. Duplicate mobile check
        if (student.getPhone() != null && !student.getPhone().trim().isEmpty()) {
            if (studentDAO.phoneExists(student.getPhone().trim())) {
                throw new Exception("Phone number is already registered.");
            }
        }

        // 3. BCrypt Password Hashing
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        student.setPassword(hashedPassword);
        student.setActive(1);

        // 4. Persistence into production 'student' table
        Student registeredStudent = studentDAO.registerStudent(student);
        if (registeredStudent == null || registeredStudent.getId() <= 0) {
            throw new Exception("Registration failed due to a database error. Please try again.");
        }

        return registeredStudent;
    }

    public User register(User user, String plainPassword) throws Exception {
        if (user instanceof Student) {
            return register((Student) user, plainPassword);
        }
        Student s = new Student();
        s.setFname(user.getFirstName());
        s.setLname(user.getLastName());
        s.setEmail(user.getEmail());
        s.setPhone(user.getMobile());
        s.setCity(user.getCity());
        s.setGradCollegeName(user.getCollegeName());
        s.setGradStream(user.getStream() != null ? user.getStream() : user.getBranch());
        s.setReferalCode(user.getReferralCode());
        return register(s, plainPassword);
    }

    // -------------------------------------------------------------------------
    // Login Workflow
    // -------------------------------------------------------------------------
    public User login(String email, String password, String role) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String requestedRole = (role != null && !role.trim().isEmpty()) ? role.trim().toUpperCase() : "STUDENT";

        // =====================================================================
        // 1. Admin Authentication Path (Production 'admin' table)
        // =====================================================================
        if ("ADMIN".equalsIgnoreCase(requestedRole)) {
            User admin = adminDAO.getAdminByEmail(normalizedEmail);
            if (admin == null) {
                throw new Exception("Invalid email or password.");
            }
            if (!PasswordUtil.checkPassword(password, admin.getPasswordHash())) {
                throw new Exception("Invalid email or password.");
            }
            return admin;
        }

        // =====================================================================
        // 2. Student Demo Account Support
        // =====================================================================
        if ((TEST_STUDENT_EMAIL.equalsIgnoreCase(normalizedEmail) || TEST_STUDENT_EMAIL_ALT.equalsIgnoreCase(normalizedEmail))
                && TEST_STUDENT_PASSWORD.equals(password)) {
            Student demoStudent = studentDAO.getStudentByEmail(normalizedEmail);
            if (demoStudent == null && TEST_STUDENT_EMAIL_ALT.equalsIgnoreCase(normalizedEmail)) {
                demoStudent = studentDAO.getStudentByEmail(TEST_STUDENT_EMAIL);
            }
            if (demoStudent != null) {
                studentDAO.updateLastLogin(demoStudent.getId());
                return demoStudent;
            }

            // Create demo student if missing
            Student s = new Student();
            s.setFname("Test");
            s.setLname("Student");
            s.setEmail(normalizedEmail);
            s.setPassword(PasswordUtil.hashPassword(TEST_STUDENT_PASSWORD));
            s.setPhone("9876543210");
            s.setCity("Pune");
            s.setGradCollegeName("Institute of Technology");
            s.setGradStream("Computer Science and Engineering");
            s.setActive(1);

            Student persisted = studentDAO.registerStudent(s);
            if (persisted != null) {
                studentDAO.updateLastLogin(persisted.getId());
                return persisted;
            }
        }

        // =====================================================================
        // 3. Student Production Database Lookup
        // =====================================================================
        Student student = studentDAO.getStudentByEmail(normalizedEmail);
        if (student == null) {
            // Also check by phone if user entered phone in email field
            if (normalizedEmail.matches("^[0-9]{10}$")) {
                student = studentDAO.getStudentByPhone(normalizedEmail);
            }
        }

        if (student == null) {
            throw new Exception("Invalid email or password.");
        }

        // Verify password
        boolean passwordMatches = PasswordUtil.checkPassword(password, student.getPassword());
        if (!passwordMatches) {
            throw new Exception("Invalid email or password.");
        }

        // Auto-upgrade legacy plain-text password to BCrypt hash
        if (student.getPassword() != null && !student.getPassword().startsWith("$2")) {
            try {
                String upgradedHash = PasswordUtil.hashPassword(password);
                studentDAO.updatePassword(student.getId(), upgradedHash);
                student.setPassword(upgradedHash);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Failed to auto-upgrade password hash for student id=" + student.getId(), ex);
            }
        }

        // Account status check
        if (student.getActive() != null && student.getActive() == 0) {
            throw new Exception("Account is inactive. Please contact administrator.");
        }

        // Update login activity in student table
        studentDAO.updateLastLogin(student.getId());

        return student;
    }
}

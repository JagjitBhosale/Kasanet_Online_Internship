package com.kasnet.service;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.User;
import com.kasnet.util.PasswordUtil;

import java.sql.Timestamp;
import java.sql.SQLException;

public class AuthenticationService {

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.profileDAO = new ProfileDAO();
    }

    public AuthenticationService(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    public User register(User user, String plainPassword) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();
        user.setEmail(normalizedEmail);

        if (userDAO.emailExists(normalizedEmail)) {
            throw new Exception("An account with this email already exists.");
        }

        if (user.getMobile() != null && !user.getMobile().trim().isEmpty()) {
            if (userDAO.mobileExists(user.getMobile().trim())) {
                throw new Exception("Mobile number is already registered.");
            }
        }

        // Hash password with BCrypt
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        user.setPasswordHash(hashedPassword);
        user.setActive(true);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        User registeredUser = userDAO.registerUser(user);
        if (registeredUser == null || registeredUser.getUserId() <= 0) {
            throw new Exception("Registration failed. Please try again.");
        }

        // Initialize default profile
        profileDAO.createProfile(registeredUser.getUserId());

        return registeredUser;
    }

    public static final String TEST_STUDENT_EMAIL = "test@kasnet.in";
    public static final String TEST_STUDENT_EMAIL_ALT = "test@myinternshiptech.com";
    public static final String TEST_STUDENT_PASSWORD = "Test@12345";

    public User login(String email, String password, String role) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // 1. Hardcoded Test ID support for testing
        if ((TEST_STUDENT_EMAIL.equalsIgnoreCase(normalizedEmail) || TEST_STUDENT_EMAIL_ALT.equalsIgnoreCase(normalizedEmail))
                && TEST_STUDENT_PASSWORD.equals(password)) {
            if (role != null && !role.trim().isEmpty() && !"STUDENT".equalsIgnoreCase(role.trim())) {
                throw new Exception("Role does not match this account.");
            }
            User dbTestUser = findUserForLogin(normalizedEmail);
            if (dbTestUser == null && TEST_STUDENT_EMAIL_ALT.equalsIgnoreCase(normalizedEmail)) {
                dbTestUser = findUserForLogin(TEST_STUDENT_EMAIL);
            }
            if (dbTestUser != null) {
                userDAO.updateLastLogin(dbTestUser.getUserId());
                return dbTestUser;
            }

            User testUser = new User();
            testUser.setFirstName("Test");
            testUser.setLastName("Student");
            testUser.setEmail(normalizedEmail);
            testUser.setPasswordHash(PasswordUtil.hashPassword(TEST_STUDENT_PASSWORD));
            testUser.setRole("STUDENT");
            testUser.setStream("Graduation");
            testUser.setBranch("Computer Science and Engineering");
            testUser.setCollegeName("Institute of Technology");
            testUser.setCity("Pune");
            testUser.setCountryCode("+91");

            User persistedTestUser = userDAO.registerUser(testUser);
            if (persistedTestUser == null || persistedTestUser.getUserId() <= 0) {
                throw new Exception("Unable to create the demo student account. Check the database connection and users table.");
            }
            if (!profileDAO.createProfile(persistedTestUser.getUserId())) {
                throw new Exception("Unable to initialize the demo student profile.");
            }
            userDAO.updateLastLogin(persistedTestUser.getUserId());
            return persistedTestUser;
        }

        // 2. Admin login support (admin@kasnet.in / Admin@12345)
        if ("admin@kasnet.in".equalsIgnoreCase(normalizedEmail) && "Admin@12345".equals(password)) {
            if (role != null && !role.trim().isEmpty() && !"ADMIN".equalsIgnoreCase(role.trim())) {
                throw new Exception("Role does not match this account.");
            }
            User adminUser = findUserForLogin("admin@kasnet.in");
            if (adminUser != null) {
                userDAO.updateLastLogin(adminUser.getUserId());
                return adminUser;
            }
        }

        // 3. Database user lookup
        User user = findUserForLogin(normalizedEmail);

        if (user == null) {
            throw new Exception("Invalid email or password.");
        }

        // BCrypt check
        boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPasswordHash());
        if (!passwordMatches) {
            throw new Exception("Invalid email or password.");
        }

        // Role check
        if (role != null && !role.trim().isEmpty()) {
            if (!role.trim().equalsIgnoreCase(user.getRole())) {
                throw new Exception("Role does not match this account.");
            }
        }

        if (!user.isActive()) {
            throw new Exception("Account is inactive. Please contact administrator.");
        }

        // Update login activity
        userDAO.updateLastLogin(user.getUserId());

        return user;
    }

    private User findUserForLogin(String email) throws Exception {
        try {
            return userDAO.getUserByEmailForLogin(email);
        } catch (SQLException e) {
            throw new Exception("The login service cannot access the database. Check DB_URL, DB_USER, and DB_PASSWORD in your local .env file, then restart the app.", e);
        }
    }
}

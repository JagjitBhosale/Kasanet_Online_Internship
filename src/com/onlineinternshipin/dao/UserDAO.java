package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for User entity.
 * Aligned with the production 'student' table schema via StudentDAO delegation.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final StudentDAO studentDAO = new StudentDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    public User registerUser(User user) {
        if (user == null) return null;
        Student student;
        if (user instanceof Student) {
            student = (Student) user;
        } else {
            student = new Student();
            student.setFname(user.getFirstName());
            student.setLname(user.getLastName());
            student.setEmail(user.getEmail());
            student.setPassword(user.getPasswordHash());
            student.setPhone(user.getMobile());
            student.setCity(user.getCity());
            student.setGradCollegeName(user.getCollegeName());
            student.setGradStream(user.getStream() != null ? user.getStream() : user.getBranch());
            student.setReferalCode(user.getReferralCode());
            student.setActive(user.isActive() ? 1 : 0);
        }
        return studentDAO.registerStudent(student);
    }

    public User getUserByEmail(String email) {
        User user = studentDAO.getStudentByEmail(email);
        if (user == null) {
            user = adminDAO.getAdminByEmail(email);
        }
        return user;
    }

    public User getUserByEmailForLogin(String email) throws SQLException {
        return getUserByEmail(email);
    }

    public User getUserById(int userId) {
        User user = studentDAO.getStudentById(userId);
        if (user == null) {
            user = adminDAO.getAdminById(userId);
        }
        return user;
    }

    public User getAdminById(int adminId) {
        return adminDAO.getAdminById(adminId);
    }

    public boolean emailExists(String email) {
        return studentDAO.emailExists(email);
    }

    public boolean mobileExists(String mobile) {
        return studentDAO.phoneExists(mobile);
    }

    public boolean updateUser(User user) {
        if (user == null || user.getUserId() <= 0) return false;
        Student s = studentDAO.getStudentById(user.getUserId());
        String gender = s != null ? s.getGender() : null;
        String dob = s != null ? s.getDob() : null;
        String address = s != null ? s.getAddress() : null;
        String state = s != null ? s.getState() : null;

        return studentDAO.updatePersonalDetails(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                gender,
                dob,
                user.getMobile(),
                address,
                user.getCity(),
                state
        );
    }

    public boolean updateLastLogin(int userId) {
        return studentDAO.updateLastLogin(userId);
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>(studentDAO.getAllStudents());
        list.addAll(adminDAO.getAllAdmins());
        return list;
    }

    public List<User> getAllStudents() {
        return new ArrayList<>(studentDAO.getAllStudents());
    }

    public boolean deleteUser(int userId) {
        return studentDAO.deleteStudent(userId);
    }

    public boolean deleteUsers(List<Integer> userIds) {
        return studentDAO.deleteStudents(userIds);
    }
}

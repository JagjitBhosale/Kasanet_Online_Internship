package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.model.StudentProfile;

import java.sql.Date;

/**
 * Data Access Object for student profile data.
 * Directly reads and writes against the production 'student' table
 * via StudentDAO, eliminating dependency on non-production tables.
 */
public class ProfileDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public StudentProfile getProfileByUserId(int userId) {
        Student student = studentDAO.getStudentById(userId);
        if (student == null) {
            return null;
        }
        return mapStudentToProfile(student);
    }

    public boolean createProfile(int userId) {
        // In the production schema, the student record itself contains the profile fields.
        return studentDAO.getStudentById(userId) != null;
    }

    public boolean updatePersonalDetails(StudentProfile profile) {
        if (profile == null || profile.getUserId() <= 0) return false;
        Student s = studentDAO.getStudentById(profile.getUserId());
        if (s == null) return false;

        String fname = s.getFname();
        String lname = s.getLname();
        String phone = s.getPhone();
        String city = s.getCity();

        String gender = profile.getGender() != null ? profile.getGender() : s.getGender();
        Date dobDate = profile.getDateOfBirth();
        String dob = dobDate != null ? dobDate.toString() : s.getDob();
        String address = profile.getAddress() != null ? profile.getAddress() : s.getAddress();
        String state = profile.getState() != null ? profile.getState() : s.getState();

        return studentDAO.updatePersonalDetails(
                profile.getUserId(),
                fname,
                lname,
                gender,
                dob,
                phone,
                address,
                city,
                state
        );
    }

    public int calculateProfileCompletion(int userId) {
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return 0;
        return studentDAO.calculateProfileCompletion(s);
    }

    public boolean updateProfileCompletion(int userId, int completion) {
        // Profile completion is dynamically derived from filled student table columns.
        return true;
    }

    private StudentProfile mapStudentToProfile(Student s) {
        StudentProfile p = new StudentProfile();
        p.setProfileId(s.getId());
        p.setUserId(s.getId());
        p.setGender(s.getGender());
        p.setDateOfBirth(s.getDateOfBirth());
        p.setAddress(s.getAddress());
        p.setState(s.getState());
        p.setProfileCompletion(studentDAO.calculateProfileCompletion(s));
        return p;
    }
}

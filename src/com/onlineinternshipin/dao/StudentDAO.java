package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Student;
import com.onlineinternshipin.utility.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the production 'student' table
 * defined in Database/onlineinternship.sql.
 *
 * Implements full CRUD, authentication queries, and section-by-section
 * profile updates using parameterized PreparedStatements.
 */
public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    // -------------------------------------------------------------------------
    // 1. Registration
    // -------------------------------------------------------------------------
    public Student registerStudent(Student student) {
        if (student == null) return null;

        final String sql = "INSERT INTO `student` ("
                + "`fname`, `lname`, `phone`, `email`, `password`, `city`, "
                + "`gradcollegename`, `gradstream`, `referalCode`, `Referal_text`, "
                + "`country_code`, `active`, `datetime`"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getFname());
            ps.setString(2, student.getLname());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getEmail() != null ? student.getEmail().trim().toLowerCase() : null);
            ps.setString(5, student.getPassword());
            ps.setString(6, student.getCity());
            ps.setString(7, student.getGradCollegeName());
            ps.setString(8, student.getGradStream());
            ps.setString(9, student.getReferalCode());
            ps.setString(10, student.getReferalText());

            Integer cc = student.getCountryCodeInt();
            if (cc != null) {
                ps.setInt(11, cc);
            } else {
                ps.setInt(11, 91);
            }

            Integer active = student.getActive();
            ps.setInt(12, active != null ? active : 1);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                    student.syncToUser();
                }
                return student;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "registerStudent failed for email=" + student.getEmail(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // 2. Lookup & Authentication
    // -------------------------------------------------------------------------
    public Student getStudentByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        final String sql = "SELECT * FROM `student` WHERE LOWER(`email`) = LOWER(?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getStudentByEmail failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public Student getStudentById(int id) {
        if (id <= 0) return null;
        final String sql = "SELECT * FROM `student` WHERE `id` = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getStudentById failed for id=" + id, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public Student getStudentByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        final String sql = "SELECT * FROM `student` WHERE `phone` = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getStudentByPhone failed for phone=" + phone, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        final String sql = "SELECT COUNT(*) FROM `student` WHERE LOWER(`email`) = LOWER(?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "emailExists query failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    public boolean phoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        final String sql = "SELECT COUNT(*) FROM `student` WHERE `phone` = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "phoneExists query failed for phone=" + phone, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // 3. Profile Updates
    // -------------------------------------------------------------------------
    public boolean updatePersonalDetails(int id, String fname, String lname, String gender,
                                         String dob, String phone, String address, String city, String state) {
        final String sql = "UPDATE `student` SET "
                + "`fname` = ?, `lname` = ?, `gender` = ?, `dob` = ?, `phone` = ?, "
                + "`address` = ?, `city` = ?, `state` = ? WHERE `id` = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, gender);
            ps.setString(4, dob);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setString(7, city);
            ps.setString(8, state);
            ps.setInt(9, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updatePersonalDetails failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateEducationDetails(Student s) {
        if (s == null || s.getId() <= 0) return false;

        final String sql = "UPDATE `student` SET "
                + "`10schoolname` = ?, `10board` = ?, `10passingyear` = ?, `10percentage` = ?, "
                + "`12schoolname` = ?, `12board` = ?, `12stream` = ?, `12passinyear` = ?, `12percentage` = ?, "
                + "`diplomaschoolname` = ?, `diplomaboard` = ?, `diplomastream` = ?, `diplomapassingyear` = ?, `diplomapercentage` = ?, "
                + "`gradcollegename` = ?, `gradunivercity` = ?, `gradstream` = ?, `gradpassingyear` = ?, `gradpercentage` = ?, "
                + "`mascollegename` = ?, `masunivercity` = ?, `masstream` = ?, `maspassingyear` = ?, `maspercentage` = ? "
                + "WHERE `id` = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            // 10th
            ps.setString(1, s.getSchool10Name());
            ps.setString(2, s.getBoard10());
            ps.setString(3, s.getPassingYear10());
            ps.setString(4, s.getPercentage10());
            // 12th
            ps.setString(5, s.getSchool12Name());
            ps.setString(6, s.getBoard12());
            ps.setString(7, s.getStream12());
            ps.setString(8, s.getPassingYear12());
            ps.setString(9, s.getPercentage12());
            // Diploma
            ps.setString(10, s.getDiplomaSchoolName());
            ps.setString(11, s.getDiplomaBoard());
            ps.setString(12, s.getDiplomaStream());
            ps.setString(13, s.getDiplomaPassingYear());
            ps.setString(14, s.getDiplomaPercentage());
            // Graduation
            ps.setString(15, s.getGradCollegeName());
            ps.setString(16, s.getGradUniversity());
            ps.setString(17, s.getGradStream());
            ps.setString(18, s.getGradPassingYear());
            ps.setString(19, s.getGradPercentage());
            // Master
            ps.setString(20, s.getMasCollegeName());
            ps.setString(21, s.getMasUniversity());
            ps.setString(22, s.getMasStream());
            ps.setString(23, s.getMasPassingYear());
            ps.setString(24, s.getMasPercentage());
            ps.setInt(25, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateEducationDetails failed for id=" + s.getId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateSkills(int id, String technicalSkill, String softSkill, String domainSkill) {
        final String sql = "UPDATE `student` SET `technicalskill` = ?, `softskill` = ?, `domain` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, technicalSkill);
            ps.setString(2, softSkill);
            ps.setString(3, domainSkill);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateSkills failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateProjects(int id, String projectName, String projectDetails) {
        final String sql = "UPDATE `student` SET `Projectname` = ?, `projectdetails` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, projectName);
            ps.setString(2, projectDetails);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateProjects failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateCertifications(int id, String cert, String certName, String certTech) {
        final String sql = "UPDATE `student` SET `certification` = ?, `certificationname` = ?, `certificationtechnology` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, cert);
            ps.setString(2, certName);
            ps.setString(3, certTech);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateCertifications failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateExtraActivities(int id, String activities) {
        final String sql = "UPDATE `student` SET `ExtraActivities` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, activities);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateExtraActivities failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateLastLogin(int id) {
        final String sql = "UPDATE `student` SET `previouslogin` = `datetime`, `datetime` = NOW() WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateLastLogin failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updatePassword(int id, String passwordHash) {
        final String sql = "UPDATE `student` SET `password` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, passwordHash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updatePassword failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        final String sql = "SELECT * FROM `student` ORDER BY `id` DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllStudents failed", e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    public boolean deleteStudent(int id) {
        final String sql = "DELETE FROM `student` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteStudent failed for id=" + id, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean deleteStudents(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return false;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        final String sql = "DELETE FROM `student` WHERE `id` IN (" + placeholders + ")";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteStudents failed", e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // 4. Dynamic Profile Completion Calculation
    // -------------------------------------------------------------------------
    public int calculateProfileCompletion(Student s) {
        if (s == null) return 0;
        int score = 0;

        // Basic Account Created: 20%
        if (s.getId() > 0 && s.getEmail() != null) {
            score += 20;
        }

        // Personal Details (Gender, DOB, Address, City/State): 20%
        if ((s.getGender() != null && !s.getGender().trim().isEmpty()) ||
            (s.getDob() != null && !s.getDob().trim().isEmpty()) ||
            (s.getAddress() != null && !s.getAddress().trim().isEmpty())) {
            score += 20;
        }

        // Education details (SSC, HSC, Diploma, Graduation): 25%
        boolean hasEdu = (s.getSchool10Name() != null && !s.getSchool10Name().trim().isEmpty()) ||
                         (s.getSchool12Name() != null && !s.getSchool12Name().trim().isEmpty()) ||
                         (s.getDiplomaSchoolName() != null && !s.getDiplomaSchoolName().trim().isEmpty()) ||
                         (s.getGradCollegeName() != null && !s.getGradCollegeName().trim().isEmpty());
        if (hasEdu) {
            score += 25;
        }

        // Skills (Technical / Soft / Domain): 15%
        boolean hasSkills = (s.getTechnicalSkill() != null && !s.getTechnicalSkill().trim().isEmpty()) ||
                            (s.getSoftSkill() != null && !s.getSoftSkill().trim().isEmpty());
        if (hasSkills) {
            score += 15;
        }

        // Projects: 10%
        if (s.getProjectName() != null && !s.getProjectName().trim().isEmpty()) {
            score += 10;
        }

        // Certifications or Extra Activities: 10%
        boolean hasCertOrExtra = (s.getCertification() != null && !s.getCertification().trim().isEmpty()) ||
                                 (s.getExtraActivities() != null && !s.getExtraActivities().trim().isEmpty());
        if (hasCertOrExtra) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    // -------------------------------------------------------------------------
    // 5. ResultSet Mapping
    // -------------------------------------------------------------------------
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setFname(rs.getString("fname"));
        s.setLname(rs.getString("lname"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setGender(rs.getString("gender"));
        s.setDob(rs.getString("dob"));
        s.setAddress(rs.getString("address"));
        s.setCity(rs.getString("city"));
        s.setState(rs.getString("state"));

        // 10th
        s.setSchool10Name(rs.getString("10schoolname"));
        s.setBoard10(rs.getString("10board"));
        s.setPassingYear10(rs.getString("10passingyear"));
        s.setPercentage10(rs.getString("10percentage"));

        // 12th
        s.setSchool12Name(rs.getString("12schoolname"));
        s.setBoard12(rs.getString("12board"));
        s.setStream12(rs.getString("12stream"));
        s.setPassingYear12(rs.getString("12passinyear"));
        s.setPercentage12(rs.getString("12percentage"));

        // Diploma
        s.setDiplomaSchoolName(rs.getString("diplomaschoolname"));
        s.setDiplomaBoard(rs.getString("diplomaboard"));
        s.setDiplomaStream(rs.getString("diplomastream"));
        s.setDiplomaPassingYear(rs.getString("diplomapassingyear"));
        s.setDiplomaPercentage(rs.getString("diplomapercentage"));

        // Graduation
        s.setGradCollegeName(rs.getString("gradcollegename"));
        s.setGradUniversity(rs.getString("gradunivercity"));
        s.setGradStream(rs.getString("gradstream"));
        s.setGradPassingYear(rs.getString("gradpassingyear"));
        s.setGradPercentage(rs.getString("gradpercentage"));

        // Master
        s.setMasCollegeName(rs.getString("mascollegename"));
        s.setMasUniversity(rs.getString("masunivercity"));
        s.setMasStream(rs.getString("masstream"));
        s.setMasPassingYear(rs.getString("maspassingyear"));
        s.setMasPercentage(rs.getString("maspercentage"));

        // Tracking & Meta
        s.setDatetime(rs.getString("datetime"));
        s.setPreviousLogin(rs.getString("previouslogin"));
        s.setSpendTime(rs.getString("spendtime"));

        // Certifications & Projects
        s.setCertification(rs.getString("certification"));
        s.setCertificationName(rs.getString("certificationname"));
        s.setCertificationTechnology(rs.getString("certificationtechnology"));
        s.setProjectName(rs.getString("Projectname"));
        s.setProjectDetails(rs.getString("projectdetails"));
        s.setExtraActivities(rs.getString("ExtraActivities"));

        // Skills
        s.setTechnicalSkill(rs.getString("technicalskill"));
        s.setSoftSkill(rs.getString("softskill"));
        s.setDomainSkill(rs.getString("domain"));

        // Referral & Status
        s.setReferalCode(rs.getString("referalCode"));
        int rCount = rs.getInt("rCodeCount");
        if (!rs.wasNull()) s.setRCodeCount(rCount);

        int cCode = rs.getInt("country_code");
        if (!rs.wasNull()) s.setCountryCodeInt(cCode);

        int act = rs.getInt("active");
        if (!rs.wasNull()) s.setActive(act);

        s.setReferalText(rs.getString("Referal_text"));

        s.setPdomain(rs.getString("pdomain"));
        s.setTechnology(rs.getString("technology"));
        s.setApplication(rs.getString("application"));
        s.setDuration(rs.getString("duration"));
        s.setModel(rs.getString("model"));
        s.setReq(rs.getString("req"));
        s.setAna(rs.getString("ana"));
        s.setDes(rs.getString("des"));
        s.setCod(rs.getString("cod"));
        s.setTest(rs.getString("test"));
        s.setCost(rs.getString("cost"));
        s.setTestType(rs.getString("testType"));
        s.setTestDur(rs.getString("testDur"));
        s.setNoIssue(rs.getString("no_issue"));
        s.setKnowledgeReq(rs.getString("knowledge_req"));
        s.setKnowledgeAna(rs.getString("knowledge_ana"));
        s.setKnowledgeDes(rs.getString("knowledge_des"));
        s.setKnowledgeCod(rs.getString("knowledge_cod"));
        s.setKnowledgeTest(rs.getString("knowledge_test"));
        s.setKnowledgePostImpl(rs.getString("knowledge_postimpl"));

        s.syncToUser();
        return s;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}

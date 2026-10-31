package com.kasnet.dao;

import com.kasnet.model.Assignment;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AssignmentDAO.class.getName());

    public List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE is_active = 1 ORDER BY due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setAssignmentId(rs.getInt("assignment_id"));
                a.setTitle(rs.getString("title"));
                a.setSubject(rs.getString("subject"));
                a.setDescription(rs.getString("description"));
                a.setInstructionsText(rs.getString("instructions_text"));
                a.setInstructionPdfPath(rs.getString("instruction_pdf_path"));
                a.setAssignmentNumber(rs.getString("assignment_number"));
                a.setDueDate(rs.getDate("due_date"));
                a.setTotalMarks(rs.getInt("total_marks"));
                a.setActive(rs.getBoolean("is_active"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to load active assignments.", e);
        }
        return list;
    }

    public List<Assignment> getActiveAssignments() throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE is_active = 1 "
                + "ORDER BY assignment_number ASC, assignment_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAssignment(rs));
            }
        }
        return list;
    }

    public List<Assignment> getAssignmentsWithSubmissionStatus(int userId) throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, s.submission_id, s.submission_date, s.file_path, s.status, s.marks_obtained, s.remarks, s.resubmitted " +
                     "FROM assignments a " +
                     "LEFT JOIN assignment_submissions s ON s.submission_id = (SELECT MAX(s2.submission_id) " +
                     "FROM assignment_submissions s2 WHERE s2.assignment_id = a.assignment_id AND s2.user_id = ?) " +
                     "WHERE a.is_active = 1 ORDER BY a.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = new Assignment();
                    a.setAssignmentId(rs.getInt("assignment_id"));
                    a.setTitle(rs.getString("title"));
                    a.setSubject(rs.getString("subject"));
                    a.setDescription(rs.getString("description"));
                    a.setInstructionsText(rs.getString("instructions_text"));
                    a.setInstructionPdfPath(rs.getString("instruction_pdf_path"));
                    a.setAssignmentNumber(rs.getString("assignment_number"));
                    a.setDueDate(rs.getDate("due_date"));
                    a.setTotalMarks(rs.getInt("total_marks"));
                    a.setActive(rs.getBoolean("is_active"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));

                    int subId = rs.getInt("submission_id");
                    if (!rs.wasNull()) {
                        a.setSubmissionId(subId);
                        a.setSubmissionDate(rs.getTimestamp("submission_date"));
                        a.setFilePath(rs.getString("file_path"));
                        String status = rs.getString("status");
                        a.setStatus(status == null || status.trim().isEmpty() ? "Submitted" : status);
                        a.setMarksObtained(rs.getInt("marks_obtained"));
                        a.setRemarks(rs.getString("remarks"));
                        a.setResubmitted(rs.getBoolean("resubmitted"));
                    } else {
                        a.setStatus("Pending");
                    }
                    list.add(a);
                }
            }
        }
        return list;
    }

    public Assignment getAssignmentById(int assignmentId) throws SQLException {
        String sql = "SELECT * FROM assignments WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Assignment a = new Assignment();
                    a.setAssignmentId(rs.getInt("assignment_id"));
                    a.setTitle(rs.getString("title"));
                    a.setSubject(rs.getString("subject"));
                    a.setDescription(rs.getString("description"));
                    a.setInstructionsText(rs.getString("instructions_text"));
                    a.setInstructionPdfPath(rs.getString("instruction_pdf_path"));
                    a.setAssignmentNumber(rs.getString("assignment_number"));
                    a.setDueDate(rs.getDate("due_date"));
                    a.setTotalMarks(rs.getInt("total_marks"));
                    a.setActive(rs.getBoolean("is_active"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    return a;
                }
            }
        }
        return null;
    }

    public List<Assignment> getAllAssignmentsForAdmin() throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments ORDER BY created_at DESC, assignment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAssignment(rs));
            }
        }
        return list;
    }

    public boolean assignmentCodeExists(String assignmentNumber, int excludedAssignmentId) throws SQLException {
        String sql = "SELECT 1 FROM assignments WHERE assignment_number = ? AND assignment_id <> ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentNumber);
            ps.setInt(2, excludedAssignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean createAssignment(Assignment assignment) throws SQLException {
        String sql = "INSERT INTO assignments "
                + "(title, subject, description, instructions_text, instruction_pdf_path, assignment_number, due_date, total_marks, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindAssignment(ps, assignment, false);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateAssignment(Assignment assignment) throws SQLException {
        String sql = "UPDATE assignments SET title = ?, subject = ?, description = ?, instructions_text = ?, "
                + "instruction_pdf_path = ?, assignment_number = ?, due_date = ?, total_marks = ?, is_active = ? "
                + "WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindAssignment(ps, assignment, true);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean setAssignmentActive(int assignmentId, boolean active) throws SQLException {
        String sql = "UPDATE assignments SET is_active = ? WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, assignmentId);
            return ps.executeUpdate() == 1;
        }
    }

    private void bindAssignment(PreparedStatement ps, Assignment assignment, boolean includeId) throws SQLException {
        ps.setString(1, assignment.getTitle());
        ps.setString(2, assignment.getSubject());
        ps.setString(3, assignment.getDescription());
        ps.setString(4, assignment.getInstructionsText());
        ps.setString(5, assignment.getInstructionPdfPath());
        ps.setString(6, assignment.getAssignmentNumber());
        ps.setDate(7, assignment.getDueDate());
        ps.setInt(8, assignment.getTotalMarks());
        ps.setBoolean(9, assignment.isActive());
        if (includeId) {
            ps.setInt(10, assignment.getAssignmentId());
        }
    }

    private Assignment mapAssignment(ResultSet rs) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setTitle(rs.getString("title"));
        assignment.setSubject(rs.getString("subject"));
        assignment.setDescription(rs.getString("description"));
        assignment.setInstructionsText(rs.getString("instructions_text"));
        assignment.setInstructionPdfPath(rs.getString("instruction_pdf_path"));
        assignment.setAssignmentNumber(rs.getString("assignment_number"));
        assignment.setDueDate(rs.getDate("due_date"));
        assignment.setTotalMarks(rs.getInt("total_marks"));
        assignment.setActive(rs.getBoolean("is_active"));
        assignment.setCreatedAt(rs.getTimestamp("created_at"));
        return assignment;
    }

    public boolean submitAssignment(int assignmentId, int userId, String filePath) {
        String lockAssignment = "SELECT assignment_id FROM assignments WHERE assignment_id = ? AND is_active = 1 FOR UPDATE";
        String findSubmission = "SELECT submission_id FROM assignment_submissions "
                + "WHERE assignment_id = ? AND user_id = ? ORDER BY submission_id DESC LIMIT 1";
        String updateSubmission = "UPDATE assignment_submissions SET file_path = ?, submission_date = CURRENT_TIMESTAMP, "
                + "status = 'Submitted', resubmitted = 1 WHERE submission_id = ?";
        String insertSubmission = "INSERT INTO assignment_submissions "
                + "(assignment_id, user_id, submission_date, file_path, status) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP, ?, 'Submitted')";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement lock = conn.prepareStatement(lockAssignment)) {
                    lock.setInt(1, assignmentId);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                Integer submissionId = null;
                try (PreparedStatement find = conn.prepareStatement(findSubmission)) {
                    find.setInt(1, assignmentId);
                    find.setInt(2, userId);
                    try (ResultSet rs = find.executeQuery()) {
                        if (rs.next()) {
                            submissionId = rs.getInt("submission_id");
                        }
                    }
                }

                int affected;
                if (submissionId != null) {
                    try (PreparedStatement update = conn.prepareStatement(updateSubmission)) {
                        update.setString(1, filePath);
                        update.setInt(2, submissionId);
                        affected = update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = conn.prepareStatement(insertSubmission)) {
                        insert.setInt(1, assignmentId);
                        insert.setInt(2, userId);
                        insert.setString(3, filePath);
                        affected = insert.executeUpdate();
                    }
                }
                conn.commit();
                return affected > 0;
            } catch (SQLException exception) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "submitAssignment failed for assignmentId=" + assignmentId
                    + ", userId=" + userId, e);
        }
        return false;
    }
}

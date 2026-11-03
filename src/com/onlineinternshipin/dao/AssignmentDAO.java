package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Assignment;
import com.onlineinternshipin.utility.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AssignmentDAO.class.getName());

    private static final String MASTER_COLUMNS =
            "assign_id, subject, assign_name, create_date, assignment_code, description, "
                    + "due_date, instructions_text, total_marks, is_active, instruction_pdf_path";

    private static final String STUDENT_ASSIGNMENT_SELECT =
            "SELECT sad.student_assignment_id, sad.assign_id AS legacy_assignment_reference, "
                    + "sad.assign_status, sad.assign_result, sad.create_date AS assigned_date, "
                    + "a.assign_id, a.subject, a.assign_name, a.create_date, a.assignment_code, "
                    + "a.description, a.due_date, a.instructions_text, a.total_marks, "
                    + "a.is_active, a.instruction_pdf_path "
                    + "FROM student_assignment_data sad "
                    + "JOIN assignment a ON a.assign_id = sad.assignment_master_id ";
    private static final String ASSIGNMENT_CODE_PATTERN =
            "[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*";

    public List<Assignment> getAllAssignments() {
        try {
            return getAllAssignmentsForAdmin();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load assignments.", exception);
            return new ArrayList<>();
        }
    }

    public List<Assignment> getActiveAssignments() throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT " + MASTER_COLUMNS + " FROM assignment WHERE is_active = 1 "
                + "ORDER BY due_date ASC, assign_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAssignment(rs));
            }
        }
        return list;
    }

    public List<Assignment> getAssignmentsForStudent(int studentId) throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = STUDENT_ASSIGNMENT_SELECT
                + "WHERE sad.student_id = ? AND a.is_active = 1 "
                + "ORDER BY sad.create_date DESC, sad.student_assignment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudentAssignment(rs));
                }
            }
        }
        return list;
    }

    public Assignment getAssignmentForStudent(int studentAssignmentId, int studentId) throws SQLException {
        String sql = STUDENT_ASSIGNMENT_SELECT
                + "WHERE sad.student_assignment_id = ? AND sad.student_id = ? AND a.is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentAssignmentId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudentAssignment(rs) : null;
            }
        }
    }

    private Assignment mapStudentAssignment(ResultSet rs) throws SQLException {
        Assignment assignment = mapAssignment(rs);
        assignment.setStudentAssignmentId(rs.getInt("student_assignment_id"));
        assignment.setAssignmentReference(rs.getString("legacy_assignment_reference"));
        assignment.setStatus(rs.getString("assign_status"));
        assignment.setRemarks(rs.getString("assign_result"));
        Date assignedDate = rs.getDate("assigned_date");
        assignment.setCreatedAt(assignedDate == null ? null : new java.sql.Timestamp(assignedDate.getTime()));
        return assignment;
    }

    public boolean updateStudentAssignmentStatus(int studentAssignmentId, int studentId,
                                                String expectedStatus, String newStatus)
            throws SQLException {
        boolean validTransition = ("Assigned".equals(expectedStatus) && "Completed".equals(newStatus))
                || ("Completed".equals(expectedStatus) && "Assigned".equals(newStatus));
        if (!validTransition) {
            throw new IllegalArgumentException("Unsupported assignment status transition.");
        }

        String sql = "UPDATE student_assignment_data sad "
                + "JOIN assignment a ON a.assign_id = sad.assignment_master_id "
                + "SET sad.assign_status = ? "
                + "WHERE sad.student_assignment_id = ? AND sad.student_id = ? "
                + "AND sad.assign_status = ? AND a.is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, studentAssignmentId);
            ps.setInt(3, studentId);
            ps.setString(4, expectedStatus);
            return ps.executeUpdate() == 1;
        }
    }

    public List<AssignmentCompletionReport> getAssignmentCompletionReports() throws SQLException {
        Map<Integer, AssignmentCompletionReport> reports =
                new LinkedHashMap<Integer, AssignmentCompletionReport>();
        Map<Integer, Map<Integer, StudentCompletion>> studentsByAssignment =
                new LinkedHashMap<Integer, Map<Integer, StudentCompletion>>();
        String sql = "SELECT a.assign_id, a.assignment_code, a.assign_name, a.subject, "
                + "sad.student_assignment_id, sad.student_id, sad.assign_status, s.fname, s.lname "
                + "FROM assignment a "
                + "LEFT JOIN student_assignment_data sad ON sad.assignment_master_id = a.assign_id "
                + "LEFT JOIN student s ON s.id = sad.student_id "
                + "ORDER BY a.assign_id, sad.student_id, sad.student_assignment_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int assignmentId = rs.getInt("assign_id");
                AssignmentCompletionReport report = reports.get(assignmentId);
                if (report == null) {
                    report = new AssignmentCompletionReport();
                    report.setAssignmentId(assignmentId);
                    report.setAssignmentReference(rs.getString("assignment_code"));
                    report.setTitle(rs.getString("assign_name"));
                    report.setSubject(rs.getString("subject"));
                    reports.put(assignmentId, report);
                    studentsByAssignment.put(assignmentId,
                            new LinkedHashMap<Integer, StudentCompletion>());
                }

                int allocationId = rs.getInt("student_assignment_id");
                if (rs.wasNull()) {
                    continue;
                }
                int studentId = rs.getInt("student_id");
                if (rs.wasNull() || studentId <= 0) {
                    report.setUnassignedAllocationCount(report.getUnassignedAllocationCount() + 1);
                    continue;
                }

                Map<Integer, StudentCompletion> students = studentsByAssignment.get(assignmentId);
                StudentCompletion student = students.get(studentId);
                if (student == null) {
                    student = new StudentCompletion();
                    student.setStudentId(studentId);
                    String firstName = rs.getString("fname");
                    String lastName = rs.getString("lname");
                    String fullName = ((firstName == null ? "" : firstName.trim()) + " "
                            + (lastName == null ? "" : lastName.trim())).trim();
                    student.setName(fullName.isEmpty() ? "Student #" + studentId : fullName);
                    student.setCompleted(true);
                    students.put(studentId, student);
                }
                if (!"Completed".equalsIgnoreCase(rs.getString("assign_status") == null
                        ? "" : rs.getString("assign_status").trim())) {
                    student.setCompleted(false);
                }
            }
        }

        for (Map.Entry<Integer, AssignmentCompletionReport> entry : reports.entrySet()) {
            AssignmentCompletionReport report = entry.getValue();
            List<StudentCompletion> students = new ArrayList<StudentCompletion>(
                    studentsByAssignment.get(entry.getKey()).values());
            int completed = 0;
            for (StudentCompletion student : students) {
                if (student.isCompleted()) {
                    completed++;
                }
            }
            report.setStudents(students);
            report.setTotalAssigned(students.size());
            report.setCompleted(completed);
            report.setPending(students.size() - completed);
            report.setCompletionPercentage(students.isEmpty()
                    ? 0 : (int) Math.round(completed * 100.0 / students.size()));
        }
        return new ArrayList<AssignmentCompletionReport>(reports.values());
    }

    public static final class AssignmentCompletionReport {
        private int assignmentId;
        private String assignmentReference;
        private String title;
        private String subject;
        private int totalAssigned;
        private int completed;
        private int pending;
        private int completionPercentage;
        private int unassignedAllocationCount;
        private List<StudentCompletion> students = new ArrayList<StudentCompletion>();

        public int getAssignmentId() { return assignmentId; }
        public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
        public String getAssignmentReference() { return assignmentReference; }
        public void setAssignmentReference(String assignmentReference) { this.assignmentReference = assignmentReference; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public int getTotalAssigned() { return totalAssigned; }
        public void setTotalAssigned(int totalAssigned) { this.totalAssigned = totalAssigned; }
        public int getCompleted() { return completed; }
        public void setCompleted(int completed) { this.completed = completed; }
        public int getPending() { return pending; }
        public void setPending(int pending) { this.pending = pending; }
        public int getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(int completionPercentage) {
            this.completionPercentage = completionPercentage;
        }
        public int getUnassignedAllocationCount() { return unassignedAllocationCount; }
        public void setUnassignedAllocationCount(int unassignedAllocationCount) {
            this.unassignedAllocationCount = unassignedAllocationCount;
        }
        public List<StudentCompletion> getStudents() { return students; }
        public void setStudents(List<StudentCompletion> students) { this.students = students; }
    }

    public static final class StudentCompletion {
        private int studentId;
        private String name;
        private boolean completed;

        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    public Assignment getAssignmentById(int assignmentId) throws SQLException {
        String sql = "SELECT " + MASTER_COLUMNS + " FROM assignment WHERE assign_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAssignment(rs) : null;
            }
        }
    }

    public List<Assignment> getAllAssignmentsForAdmin() throws SQLException {
        List<Assignment> list = new ArrayList<Assignment>();
        String sql = "SELECT " + MASTER_COLUMNS + " FROM assignment ORDER BY create_date DESC, assign_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAssignment(rs));
            }
        }
        return list;
    }

    public List<StudentOption> getStudentsForAllocation() throws SQLException {
        List<StudentOption> students = new ArrayList<StudentOption>();
        String sql = "SELECT id, fname, lname FROM student ORDER BY fname, lname, id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StudentOption student = new StudentOption();
                student.setStudentId(rs.getInt("id"));
                String firstName = rs.getString("fname");
                String lastName = rs.getString("lname");
                String name = ((firstName == null ? "" : firstName.trim()) + " "
                        + (lastName == null ? "" : lastName.trim())).trim();
                student.setName(name.isEmpty() ? "Student #" + student.getStudentId() : name);
                students.add(student);
            }
        }
        return students;
    }

    public static final class StudentOption {
        private int studentId;
        private String name;
        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public boolean assignmentCodeExists(String assignmentCode, int excludedAssignmentId) throws SQLException {
        String sql = "SELECT 1 FROM assignment WHERE assignment_code = ? AND assign_id <> ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentCode);
            ps.setInt(2, excludedAssignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int createAssignmentAndDistribute(Assignment assignment, String distributionMode,
                                             List<Integer> selectedStudentIds) throws SQLException {
        validateDistribution(distributionMode, selectedStudentIds);
        String sql = "INSERT INTO assignment "
                + "(subject, assign_name, create_date, assignment_code, description, due_date, "
                + "instructions_text, total_marks, is_active, instruction_pdf_path) "
                + "VALUES (?, ?, CURRENT_DATE, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int assignmentId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bindAssignment(ps, assignment);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Assignment master row was not created.");
                    }
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The database did not return the new assignment ID.");
                        }
                        assignmentId = keys.getInt(1);
                    }
                }

                if (!"none".equals(distributionMode)) {
                    insertAllocations(conn, assignmentId, assignment, distributionMode, selectedStudentIds);
                }
                conn.commit();
                assignment.setAssignmentId(assignmentId);
                return assignmentId;
            } catch (SQLException | RuntimeException exception) {
                rollback(conn, exception);
                throw exception;
            }
        }
    }

    public boolean updateAssignment(Assignment assignment) throws SQLException {
        String sql = "UPDATE assignment SET subject = ?, assign_name = ?, assignment_code = ?, "
                + "description = ?, due_date = ?, instructions_text = ?, total_marks = ?, "
                + "is_active = ?, instruction_pdf_path = ? WHERE assign_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement lock = conn.prepareStatement(
                        "SELECT assign_id FROM assignment WHERE assign_id = ? FOR UPDATE")) {
                    lock.setInt(1, assignment.getAssignmentId());
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bindAssignment(ps, assignment);
                    ps.setInt(10, assignment.getAssignmentId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE student_assignment_data SET assign_id = ? WHERE assignment_master_id = ?")) {
                    ps.setString(1, assignment.getAssignmentNumber());
                    ps.setInt(2, assignment.getAssignmentId());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException exception) {
                rollback(conn, exception);
                throw exception;
            }
        }
    }

    public boolean setAssignmentActive(int assignmentId, boolean active) throws SQLException {
        String sql = "UPDATE assignment SET is_active = ? WHERE assign_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, assignmentId);
            return ps.executeUpdate() == 1;
        }
    }

    public int distributeAssignment(int assignmentId, String distributionMode,
                                    List<Integer> selectedStudentIds) throws SQLException {
        validateDistribution(distributionMode, selectedStudentIds);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Assignment assignment;
                String lockSql = "SELECT " + MASTER_COLUMNS + " FROM assignment WHERE assign_id = ? FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockSql)) {
                    ps.setInt(1, assignmentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return -1;
                        }
                        assignment = mapAssignment(rs);
                    }
                }
                int inserted = insertAllocations(
                        conn, assignmentId, assignment, distributionMode, selectedStudentIds);
                conn.commit();
                return inserted;
            } catch (SQLException | RuntimeException exception) {
                rollback(conn, exception);
                throw exception;
            }
        }
    }

    private int insertAllocations(Connection conn, int assignmentId, Assignment assignment,
                                  String distributionMode, List<Integer> selectedStudentIds)
            throws SQLException {
        validateAssignmentCode(assignment.getAssignmentNumber());
        if (!assignment.isActive()) {
            throw new IllegalArgumentException("Activate the assignment before distributing it.");
        }
        Set<Integer> studentIds = new LinkedHashSet<Integer>();
        if ("selected".equals(distributionMode)) {
            for (Integer studentId : selectedStudentIds) {
                if (studentId == null || studentId <= 0) {
                    throw new IllegalArgumentException("Select valid students for assignment distribution.");
                }
                studentIds.add(studentId);
            }
            if (studentIds.isEmpty()) {
                throw new IllegalArgumentException("Select at least one student to distribute this assignment.");
            }
            verifyStudentsExist(conn, studentIds);
        } else if (!"all".equals(distributionMode)) {
            throw new IllegalArgumentException("Choose all students or selected students.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO student_assignment_data "
                + "(examination_id, student_id, assign_id, assignment_master_id, company_name, "
                + "assign_subject, assign_level, assign_topic, assign_descrition, assign_status, create_date) "
                + "SELECT NULL, s.id, ?, ?, NULL, ?, NULL, ?, NULL, 'Assigned', CURRENT_DATE "
                + "FROM student s WHERE ");
        if ("selected".equals(distributionMode)) {
            sql.append("s.id IN (");
            appendPlaceholders(sql, studentIds.size());
            sql.append(") AND ");
        }
        sql.append("NOT EXISTS (SELECT 1 FROM student_assignment_data existing "
                + "WHERE existing.assignment_master_id = ? AND existing.student_id = s.id)");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameter = 1;
            ps.setString(parameter++, assignment.getAssignmentNumber());
            ps.setInt(parameter++, assignmentId);
            ps.setString(parameter++, assignment.getSubject());
            ps.setString(parameter++, assignment.getTitle());
            if ("selected".equals(distributionMode)) {
                for (Integer studentId : studentIds) {
                    ps.setInt(parameter++, studentId);
                }
            }
            ps.setInt(parameter, assignmentId);
            return ps.executeUpdate();
        }
    }

    private void verifyStudentsExist(Connection conn, Set<Integer> studentIds) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT id) FROM student WHERE id IN (");
        appendPlaceholders(sql, studentIds.size());
        sql.append(')');
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameter = 1;
            for (Integer studentId : studentIds) {
                ps.setInt(parameter++, studentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != studentIds.size()) {
                    throw new IllegalArgumentException(
                            "One or more selected students no longer exist. Reload the page and try again.");
                }
            }
        }
    }

    private void validateDistribution(String distributionMode, List<Integer> selectedStudentIds) {
        if ("none".equals(distributionMode)) {
            return;
        }
        if ("all".equals(distributionMode)) {
            return;
        }
        if (!"selected".equals(distributionMode) || selectedStudentIds == null
                || selectedStudentIds.isEmpty()) {
            throw new IllegalArgumentException("Choose all students or select at least one student.");
        }
    }

    private void appendPlaceholders(StringBuilder sql, int count) {
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
    }

    private void rollback(Connection conn, Exception original) {
        try {
            conn.rollback();
        } catch (SQLException rollbackException) {
            original.addSuppressed(rollbackException);
        }
    }

    private void bindAssignment(PreparedStatement ps, Assignment assignment) throws SQLException {
        validateAssignmentCode(assignment.getAssignmentNumber());
        ps.setString(1, assignment.getSubject());
        ps.setString(2, assignment.getTitle());
        ps.setString(3, assignment.getAssignmentNumber());
        ps.setString(4, assignment.getDescription());
        ps.setDate(5, assignment.getDueDate());
        ps.setString(6, assignment.getInstructionsText());
        ps.setInt(7, assignment.getTotalMarks());
        ps.setBoolean(8, assignment.isActive());
        ps.setString(9, assignment.getInstructionPdfPath());
    }

    private void validateAssignmentCode(String assignmentCode) {
        if (assignmentCode == null || assignmentCode.length() > 50
                || !assignmentCode.matches(ASSIGNMENT_CODE_PATTERN)) {
            throw new IllegalArgumentException(
                    "Assignment code must contain letters or numbers separated by hyphens and be at most 50 characters.");
        }
    }

    private Assignment mapAssignment(ResultSet rs) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(rs.getInt("assign_id"));
        assignment.setTitle(rs.getString("assign_name"));
        assignment.setSubject(rs.getString("subject"));
        assignment.setDescription(rs.getString("description"));
        assignment.setInstructionsText(rs.getString("instructions_text"));
        assignment.setInstructionPdfPath(rs.getString("instruction_pdf_path"));
        assignment.setAssignmentNumber(rs.getString("assignment_code"));
        assignment.setDueDate(rs.getDate("due_date"));
        assignment.setTotalMarks(rs.getInt("total_marks"));
        assignment.setActive(rs.getBoolean("is_active"));
        Date created = rs.getDate("create_date");
        assignment.setCreatedAt(created == null ? null : new java.sql.Timestamp(created.getTime()));
        return assignment;
    }
}

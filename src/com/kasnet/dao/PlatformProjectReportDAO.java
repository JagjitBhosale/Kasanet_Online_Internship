package com.kasnet.dao;

import com.kasnet.model.PlatformProjectReport;
import com.kasnet.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlatformProjectReportDAO {

    private static final String COLUMNS = "project_name, scope_details, project_domain, project_technology, "
            + "project_application, project_duration, project_model, requirement_gathering_time, "
            + "project_analysis_time, project_designing_time, project_coding_time, project_testing_time, "
            + "project_cost, testing_type, testing_duration, issues_found, knowledge_requirement, "
            + "knowledge_analysis, knowledge_designing, knowledge_coding, knowledge_testing, "
            + "knowledge_post_implementation";

    public List<PlatformProjectReport> getByUserId(int userId) throws SQLException {
        List<PlatformProjectReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM platform_project_reports WHERE user_id = ? ORDER BY report_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    reports.add(mapRow(result));
                }
            }
        }
        return reports;
    }

    public List<PlatformProjectReport> getAllWithStudent() throws SQLException {
        List<PlatformProjectReport> reports = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS student_name, "
                + "u.email AS student_email, u.mobile AS student_mobile FROM platform_project_reports r "
                + "LEFT JOIN users u ON r.user_id = u.user_id ORDER BY r.created_at DESC, r.report_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                PlatformProjectReport report = mapRow(result);
                String name = result.getString("student_name");
                report.setStudentName(name == null || name.trim().isEmpty()
                        ? "Student #" + report.getUserId() : name.trim());
                report.setStudentEmail(result.getString("student_email"));
                report.setStudentMobile(result.getString("student_mobile"));
                reports.add(report);
            }
        }
        return reports;
    }

    public boolean add(PlatformProjectReport report) throws SQLException {
        String[] columns = COLUMNS.split(", ");
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) placeholders.append(", ");
            placeholders.append('?');
        }
        String sql = "INSERT INTO platform_project_reports (user_id, " + COLUMNS + ") VALUES (?, " + placeholders + ")";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, report.getUserId());
            statement.setString(2, report.getProjectName());
            statement.setString(3, report.getScopeDetails());
            statement.setString(4, report.getProjectDomain());
            statement.setString(5, report.getProjectTechnology());
            statement.setString(6, report.getProjectApplication());
            statement.setString(7, report.getProjectDuration());
            statement.setString(8, report.getProjectModel());
            statement.setString(9, report.getRequirementGatheringTime());
            statement.setString(10, report.getProjectAnalysisTime());
            statement.setString(11, report.getProjectDesigningTime());
            statement.setString(12, report.getProjectCodingTime());
            statement.setString(13, report.getProjectTestingTime());
            if (report.getProjectCost() == null) statement.setNull(14, java.sql.Types.DECIMAL);
            else statement.setBigDecimal(14, report.getProjectCost());
            statement.setString(15, report.getTestingType());
            statement.setString(16, report.getTestingDuration());
            statement.setInt(17, report.getIssuesFound());
            statement.setString(18, report.getKnowledgeRequirement());
            statement.setString(19, report.getKnowledgeAnalysis());
            statement.setString(20, report.getKnowledgeDesigning());
            statement.setString(21, report.getKnowledgeCoding());
            statement.setString(22, report.getKnowledgeTesting());
            statement.setString(23, report.getKnowledgePostImplementation());
            if (statement.executeUpdate() != 1) return false;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) report.setReportId(keys.getInt(1));
            }
            return true;
        }
    }

    public boolean delete(int reportId, int userId) throws SQLException {
        String sql = "DELETE FROM platform_project_reports WHERE report_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reportId);
            statement.setInt(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private PlatformProjectReport mapRow(ResultSet result) throws SQLException {
        PlatformProjectReport report = new PlatformProjectReport();
        report.setReportId(result.getInt("report_id"));
        report.setUserId(result.getInt("user_id"));
        report.setProjectName(result.getString("project_name"));
        report.setScopeDetails(result.getString("scope_details"));
        report.setProjectDomain(result.getString("project_domain"));
        report.setProjectTechnology(result.getString("project_technology"));
        report.setProjectApplication(result.getString("project_application"));
        report.setProjectDuration(result.getString("project_duration"));
        report.setProjectModel(result.getString("project_model"));
        report.setRequirementGatheringTime(result.getString("requirement_gathering_time"));
        report.setProjectAnalysisTime(result.getString("project_analysis_time"));
        report.setProjectDesigningTime(result.getString("project_designing_time"));
        report.setProjectCodingTime(result.getString("project_coding_time"));
        report.setProjectTestingTime(result.getString("project_testing_time"));
        report.setProjectCost(result.getBigDecimal("project_cost"));
        report.setTestingType(result.getString("testing_type"));
        report.setTestingDuration(result.getString("testing_duration"));
        report.setIssuesFound(result.getInt("issues_found"));
        report.setKnowledgeRequirement(result.getString("knowledge_requirement"));
        report.setKnowledgeAnalysis(result.getString("knowledge_analysis"));
        report.setKnowledgeDesigning(result.getString("knowledge_designing"));
        report.setKnowledgeCoding(result.getString("knowledge_coding"));
        report.setKnowledgeTesting(result.getString("knowledge_testing"));
        report.setKnowledgePostImplementation(result.getString("knowledge_post_implementation"));
        report.setCreatedAt(result.getDate("created_at"));
        return report;
    }
}

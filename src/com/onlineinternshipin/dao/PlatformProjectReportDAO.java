package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.PlatformProjectReport;
import com.onlineinternshipin.utility.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlatformProjectReportDAO {

    private static final Logger LOGGER = Logger.getLogger(PlatformProjectReportDAO.class.getName());

    private static final String STUDENT_REPORT_COLUMNS =
            "`Projectname`, `projectdetails`, `pdomain`, `technology`, `application`, `duration`, `model`, "
                    + "`req`, `ana`, `des`, `cod`, `test`, `cost`, `testType`, `testDur`, `no_issue`, "
                    + "`knowledge_req`, `knowledge_ana`, `knowledge_des`, `knowledge_cod`, "
                    + "`knowledge_test`, `knowledge_postimpl`";

    public List<PlatformProjectReport> getByUserId(int userId) throws SQLException {
        List<PlatformProjectReport> reports = new ArrayList<>();
        String sql = "SELECT `id` AS user_id, " + STUDENT_REPORT_COLUMNS
                + " FROM `student` WHERE `id` = ? AND `model` IS NOT NULL AND TRIM(`model`) <> ''";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    reports.add(mapRow(result));
                }
            }
        }
        return reports;
    }

    public List<PlatformProjectReport> getAllWithStudent() throws SQLException {
        List<PlatformProjectReport> reports = new ArrayList<>();
        String sql = "SELECT `id` AS user_id, `fname`, `lname`, `email`, `phone`, "
                + STUDENT_REPORT_COLUMNS
                + " FROM `student` WHERE `model` IS NOT NULL AND TRIM(`model`) <> '' "
                + "ORDER BY `id` DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                PlatformProjectReport report = mapRow(result);
                String firstName = result.getString("fname");
                String lastName = result.getString("lname");
                String name = ((firstName == null ? "" : firstName) + " "
                        + (lastName == null ? "" : lastName)).trim();
                report.setStudentName(name.isEmpty() ? "Student #" + report.getUserId() : name);
                report.setStudentEmail(result.getString("email"));
                report.setStudentMobile(result.getString("phone"));
                reports.add(report);
            }
        }
        return reports;
    }

    public boolean add(PlatformProjectReport report) throws SQLException {
        String sql = "UPDATE `student` SET `Projectname` = ?, `projectdetails` = ?, `pdomain` = ?, "
                + "`technology` = ?, `application` = ?, `duration` = ?, `model` = ?, `req` = ?, "
                + "`ana` = ?, `des` = ?, `cod` = ?, `test` = ?, `cost` = ?, `testType` = ?, "
                + "`testDur` = ?, `no_issue` = ?, `knowledge_req` = ?, `knowledge_ana` = ?, "
                + "`knowledge_des` = ?, `knowledge_cod` = ?, `knowledge_test` = ?, "
                + "`knowledge_postimpl` = ? WHERE `id` = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, report.getProjectName());
            statement.setString(2, report.getScopeDetails());
            statement.setString(3, report.getProjectDomain());
            statement.setString(4, report.getProjectTechnology());
            statement.setString(5, report.getProjectApplication());
            statement.setString(6, report.getProjectDuration());
            statement.setString(7, report.getProjectModel());
            statement.setString(8, report.getRequirementGatheringTime());
            statement.setString(9, report.getProjectAnalysisTime());
            statement.setString(10, report.getProjectDesigningTime());
            statement.setString(11, report.getProjectCodingTime());
            statement.setString(12, report.getProjectTestingTime());
            statement.setString(13, report.getProjectCost() == null
                    ? null : report.getProjectCost().toPlainString());
            statement.setString(14, report.getTestingType());
            statement.setString(15, report.getTestingDuration());
            statement.setString(16, String.valueOf(report.getIssuesFound()));
            statement.setString(17, report.getKnowledgeRequirement());
            statement.setString(18, report.getKnowledgeAnalysis());
            statement.setString(19, report.getKnowledgeDesigning());
            statement.setString(20, report.getKnowledgeCoding());
            statement.setString(21, report.getKnowledgeTesting());
            statement.setString(22, report.getKnowledgePostImplementation());
            statement.setInt(23, report.getUserId());

            if (statement.executeUpdate() > 0) {
                return true;
            }
            return studentExists(connection, report.getUserId());
        }
    }

    public boolean delete(int reportId, int userId) throws SQLException {
        if (reportId != userId) {
            return false;
        }
        String sql = "UPDATE `student` SET `pdomain` = NULL, `technology` = NULL, `application` = NULL, "
                + "`duration` = NULL, `model` = NULL, `req` = NULL, `ana` = NULL, `des` = NULL, "
                + "`cod` = NULL, `test` = NULL, `cost` = NULL, `testType` = NULL, `testDur` = NULL, "
                + "`no_issue` = NULL, `knowledge_req` = NULL, `knowledge_ana` = NULL, "
                + "`knowledge_des` = NULL, `knowledge_cod` = NULL, `knowledge_test` = NULL, "
                + "`knowledge_postimpl` = NULL WHERE `id` = ? "
                + "AND `model` IS NOT NULL AND TRIM(`model`) <> ''";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private boolean studentExists(Connection connection, int userId) throws SQLException {
        String sql = "SELECT 1 FROM `student` WHERE `id` = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private PlatformProjectReport mapRow(ResultSet result) throws SQLException {
        PlatformProjectReport report = new PlatformProjectReport();
        int userId = result.getInt("user_id");
        report.setReportId(userId);
        report.setUserId(userId);
        report.setProjectName(result.getString("Projectname"));
        report.setScopeDetails(result.getString("projectdetails"));
        report.setProjectDomain(result.getString("pdomain"));
        report.setProjectTechnology(result.getString("technology"));
        report.setProjectApplication(result.getString("application"));
        report.setProjectDuration(result.getString("duration"));
        report.setProjectModel(result.getString("model"));
        report.setRequirementGatheringTime(result.getString("req"));
        report.setProjectAnalysisTime(result.getString("ana"));
        report.setProjectDesigningTime(result.getString("des"));
        report.setProjectCodingTime(result.getString("cod"));
        report.setProjectTestingTime(result.getString("test"));
        report.setProjectCost(parseCost(result.getString("cost"), userId));
        report.setTestingType(result.getString("testType"));
        report.setTestingDuration(result.getString("testDur"));
        report.setIssuesFound(parseIssues(result.getString("no_issue"), userId));
        report.setKnowledgeRequirement(result.getString("knowledge_req"));
        report.setKnowledgeAnalysis(result.getString("knowledge_ana"));
        report.setKnowledgeDesigning(result.getString("knowledge_des"));
        report.setKnowledgeCoding(result.getString("knowledge_cod"));
        report.setKnowledgeTesting(result.getString("knowledge_test"));
        report.setKnowledgePostImplementation(result.getString("knowledge_postimpl"));
        return report;
    }

    private BigDecimal parseCost(String value, int userId) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Ignoring invalid project cost for userId=" + userId, exception);
            return null;
        }
    }

    private int parseIssues(String value, int userId) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Ignoring invalid project issue count for userId=" + userId, exception);
            return 0;
        }
    }
}

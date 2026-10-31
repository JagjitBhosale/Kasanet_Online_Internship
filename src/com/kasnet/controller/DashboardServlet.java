package com.kasnet.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.wp.usermodel.HeaderFooterType;

import com.kasnet.dao.AchievementDAO;
import com.kasnet.dao.AssignmentDAO;
import com.kasnet.dao.CertificationDAO;
import com.kasnet.dao.CompetitionDAO;
import com.kasnet.dao.EducationDAO;
import com.kasnet.dao.HackathonDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.ProjectDAO;
import com.kasnet.dao.PlatformCertificateDAO;
import com.kasnet.dao.PlatformProjectReportDAO;
import com.kasnet.dao.SkillDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.Achievement;
import com.kasnet.model.Assignment;
import com.kasnet.model.Certification;
import com.kasnet.model.Competition;
import com.kasnet.model.Education;
import com.kasnet.model.Hackathon;
import com.kasnet.model.Project;
import com.kasnet.model.PlatformCertificate;
import com.kasnet.model.PlatformProjectReport;
import com.kasnet.model.Skill;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;
import com.kasnet.util.SessionUtil;

public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();
    private final EducationDAO educationDAO = new EducationDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final CertificationDAO certificationDAO = new CertificationDAO();
    private final PlatformCertificateDAO platformCertificateDAO = new PlatformCertificateDAO();
    private final PlatformProjectReportDAO platformProjectReportDAO = new PlatformProjectReportDAO();
    private final AchievementDAO achievementDAO = new AchievementDAO();
    private final HackathonDAO hackathonDAO = new HackathonDAO();
    private final CompetitionDAO competitionDAO = new CompetitionDAO();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        // Check login
        if (!SessionUtil.requireLogin(req, resp)) {
            return;
        }
        if (!SessionUtil.isStudent(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer userId = SessionUtil.getUserId(req);

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            SessionUtil.invalidateSession(req);
            resp.sendRedirect(req.getContextPath() + "/login?error=invalid-session");
            return;
        }

        StudentProfile profile = profileDAO.getProfileByUserId(userId);
        List<Education> educationList = educationDAO.getEducationByUserId(userId);
        List<Skill> skillList = skillDAO.getSkillsByUserId(userId);
        Map<String, List<Skill>> skillsByType = groupSkillsByType(skillList);
        List<Project> projectList = projectDAO.getProjectsByUserId(userId);
        List<Certification> certificationList = certificationDAO.getCertificationsByUserId(userId);
        List<PlatformCertificate> platformCertificateList;
        try {
            platformCertificateList = platformCertificateDAO.getByUserId(userId);
        } catch (SQLException e) {
            throw new ServletException("Unable to load platform certificates.", e);
        }
        List<PlatformProjectReport> platformProjectReportList;
        try {
            platformProjectReportList = platformProjectReportDAO.getByUserId(userId);
        } catch (SQLException e) {
            throw new ServletException("Unable to load platform project reports.", e);
        }
        List<Achievement> achievementList = achievementDAO.getAchievementsByUserId(userId);
        List<Hackathon> hackathonList = hackathonDAO.getHackathonsByUserId(userId);
        List<Competition> competitionList = competitionDAO.getCompetitionsByUserId(userId);
        List<Assignment> assignmentList;
        try {
            assignmentList = assignmentDAO.getActiveAssignments();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load dashboard assignments for userId=" + userId, exception);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        int profileCompletion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, profileCompletion);
        if (profile != null) {
            profile.setProfileCompletion(profileCompletion);
        }

        if ("report".equals(req.getParameter("download"))) {
            String format = req.getParameter("format");
            List<String[]> reportRows = createReportRows(user, profile, educationList, skillList, projectList,
                    certificationList, platformCertificateList, platformProjectReportList, achievementList,
                    hackathonList, competitionList, assignmentList, user.getBranch(), profileCompletion);
            if ("pdf".equalsIgnoreCase(format)) {
                writePdfReport(resp, reportRows);
            } else if ("word".equalsIgnoreCase(format)) {
                writeWordReport(resp, reportRows);
            } else if ("csv".equalsIgnoreCase(format)) {
                writeCsvReport(resp, reportRows);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported report format.");
            }
            return;
        }

        String activeSection = req.getParameter("section");
        if (activeSection == null || activeSection.trim().isEmpty()) {
            activeSection = "student-profile";
        }
        if (!isDashboardSection(activeSection)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("user", user);
        req.setAttribute("profile", profile);
        req.setAttribute("educationList", educationList);
        req.setAttribute("skillList", skillList);
        req.setAttribute("skillsByType", skillsByType);
        req.setAttribute("projectList", projectList);
        req.setAttribute("certificationList", certificationList);
        req.setAttribute("platformCertificateList", platformCertificateList);
        req.setAttribute("platformProjectReportList", platformProjectReportList);
        req.setAttribute("achievementList", achievementList);
        req.setAttribute("hackathonList", hackathonList);
        req.setAttribute("competitionList", competitionList);
        req.setAttribute("assignmentList", assignmentList);
        req.setAttribute("profileCompletion", profileCompletion);
        req.setAttribute("activeSection", activeSection);
        req.getRequestDispatcher(
                "/views/dashboard.jsp"
        ).forward(req, resp);
    }

    private boolean isDashboardSection(String section) {
        return "student-profile".equals(section)
                || "assignment".equals(section)
                || "projects".equals(section)
                || "certificates".equals(section)
                || "report-details".equals(section);
    }

    private List<String[]> createReportRows(User user, StudentProfile profile,
                                            List<Education> educationList, List<Skill> skillList,
                                            List<Project> projectList, List<Certification> certificationList,
                                            List<PlatformCertificate> platformCertificateList,
                                            List<PlatformProjectReport> platformProjectReportList,
                                            List<Achievement> achievementList, List<Hackathon> hackathonList,
                                            List<Competition> competitionList, List<Assignment> assignmentList,
                                            String branch, int profileCompletion) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Section", "Record", "Details"});
        rows.add(new String[]{"Student Profile", "Name", user.getFullName()});
        rows.add(new String[]{"Student Profile", "Email", user.getEmail()});
        rows.add(new String[]{"Student Profile", "Mobile", user.getMobile()});
        rows.add(new String[]{"Student Profile", "Gender", profile == null ? "" : profile.getGender()});
        rows.add(new String[]{"Student Profile", "Date of Birth",
                profile == null || profile.getDateOfBirth() == null ? "" : profile.getDateOfBirth().toString()});
        rows.add(new String[]{"Student Profile", "Branch", branch});
        rows.add(new String[]{"Student Profile", "Stream", user.getStream()});
        rows.add(new String[]{"Student Profile", "College Name", user.getCollegeName()});
        rows.add(new String[]{"Student Profile", "Address", profile == null ? "" : profile.getAddress()});
        rows.add(new String[]{"Student Profile", "City", user.getCity()});
        rows.add(new String[]{"Student Profile", "State", profile == null ? "" : profile.getState()});
        rows.add(new String[]{"Student Profile", "Profile Completion", profileCompletion + "%"});

        for (Education educationRecord : educationList) {
            rows.add(new String[]{"Education", educationRecord.getLevel(), joinValues(
                    labelledValue("Institution", educationRecord.getSchoolCollegeName()),
                    labelledValue("Board / University", educationRecord.getBoardUniversity()),
                    labelledValue("Field of study", educationRecord.getStream()),
                    educationRecord.getPassingYear() == 0 ? "" : "Year: " + educationRecord.getPassingYear(),
                    educationRecord.getPercentage() <= 0 ? "" : "Score: " + educationRecord.getPercentage() + "%")});
        }
        for (Map.Entry<String, List<Skill>> skillGroup : groupSkillsByType(skillList).entrySet()) {
            List<String> skillNames = new ArrayList<>();
            for (Skill skill : skillGroup.getValue()) {
                if (skill.getSkillName() != null && !skill.getSkillName().trim().isEmpty()) {
                    skillNames.add(skill.getSkillName().trim());
                }
            }
            if (!skillNames.isEmpty()) {
                rows.add(new String[]{"Skills", skillGroup.getKey(), String.join(", ", skillNames)});
            }
        }
        for (Assignment assignment : assignmentList) {
            rows.add(new String[]{"Assignments", assignment.getTitle(), joinValues(
                    labelledValue("Subject", assignment.getSubject()),
                    labelledValue("Description", assignment.getDescription()),
                    assignment.getDueDate() == null ? "" : "Due date: " + assignment.getDueDate(),
                    assignment.getTotalMarks() == 0 ? "" : "Maximum marks: " + assignment.getTotalMarks())});
        }
        for (Project project : projectList) {
            rows.add(new String[]{"Projects", "Portfolio: " + project.getProjectName(), joinValues(
                    labelledValue("Description", project.getDescription()),
                    labelledValue("Technologies", project.getTechnologiesUsed()),
                    labelledValue("Role", project.getRole()),
                    labelledValue("Project type", project.getProjectType()),
                    project.getStartDate() == null ? "" : "Start date: " + project.getStartDate(),
                    project.getEndDate() == null ? "" : "End date: " + project.getEndDate(),
                    labelledValue("Project link", project.getProjectUrl()),
                    labelledValue("Source code", project.getGithubUrl()))});
        }
        for (PlatformProjectReport report : platformProjectReportList) {
            rows.add(new String[]{"Projects", "Project report: " + report.getProjectName(), joinValues(
                    labelledValue("Scope", report.getScopeDetails()),
                    labelledValue("Domain", report.getProjectDomain()),
                    labelledValue("Technology", report.getProjectTechnology()),
                    labelledValue("Application", report.getProjectApplication()),
                    labelledValue("Duration", report.getProjectDuration()),
                    labelledValue("Model", report.getProjectModel()),
                    labelledValue("Requirement gathering", report.getRequirementGatheringTime()),
                    labelledValue("Data analysis", report.getProjectAnalysisTime()),
                    labelledValue("Data cleaning", report.getProjectDesigningTime()),
                    labelledValue("Data modeling", report.getProjectCodingTime()),
                    labelledValue("Data visualization", report.getProjectTestingTime()),
                    report.getProjectCost() == null ? "" : "Estimated project cost: " + report.getProjectCost().toPlainString(),
                    labelledValue("Testing type", report.getTestingType()),
                    labelledValue("Testing duration", report.getTestingDuration()),
                    "Issues found: " + report.getIssuesFound(),
                    labelledValue("Knowledge gained (requirement phase)", report.getKnowledgeRequirement()),
                    labelledValue("Knowledge gained (analysis phase)", report.getKnowledgeAnalysis()),
                    labelledValue("Knowledge gained (designing phase)", report.getKnowledgeDesigning()),
                    labelledValue("Knowledge gained (coding phase)", report.getKnowledgeCoding()),
                    labelledValue("Knowledge gained (testing phase)", report.getKnowledgeTesting()),
                    labelledValue("Knowledge gained (post-implementation)", report.getKnowledgePostImplementation()))});
        }
        for (PlatformCertificate certificate : platformCertificateList) {
            rows.add(new String[]{"Global Certification", certificate.getExamName(), joinValues(
                    "Score: " + certificate.getScoreObtained().stripTrailingZeros().toPlainString() + "/1000",
                    "Achievement date: " + certificate.getAchievementDate())});
        }
        for (Certification certification : certificationList) {
            rows.add(new String[]{"Additional Certification", certification.getCertName(), joinValues(
                    labelledValue("Provider", certification.getProvider()),
                    labelledValue("Technology", certification.getTechnology()),
                    certification.getIssueDate() == null ? "" : "Issue date: " + certification.getIssueDate(),
                    certification.getExpiryDate() == null ? "" : "Expiry date: " + certification.getExpiryDate(),
                    labelledValue("Credential", certification.getCredentialUrl()))});
        }
        for (Achievement achievement : achievementList) {
            rows.add(new String[]{"Additional Activities & Achievements", achievement.getTitle(), joinValues(
                    labelledValue("Type", achievement.getAchievementType()),
                    labelledValue("Description", achievement.getDescription()),
                    labelledValue("Technology", achievement.getTechnology()),
                    labelledValue("Activity", achievement.getExtraActivities()))});
        }
        for (Hackathon hackathon : hackathonList) {
            rows.add(new String[]{"Additional Activities & Achievements", hackathon.getHackathonName(), joinValues(
                    labelledValue("Event", "Hackathon"),
                    labelledValue("Organizer", hackathon.getOrganizer()),
                    hackathon.getEventDate() == null ? "" : "Date: " + hackathon.getEventDate(),
                    labelledValue("Result", hackathon.getPositionResult()),
                    labelledValue("Description", hackathon.getDescription()))});
        }
        for (Competition competition : competitionList) {
            rows.add(new String[]{"Additional Activities & Achievements", competition.getCompetitionName(), joinValues(
                    labelledValue("Event", "Competition"),
                    labelledValue("Organizer", competition.getCollegeOrganizer()),
                    competition.getEventDate() == null ? "" : "Date: " + competition.getEventDate(),
                    labelledValue("Project", competition.getProjectName()),
                    labelledValue("Result", competition.getPositionResult()),
                    labelledValue("Description", competition.getDescription()))});
        }
        return rows;
    }

    private String labelledValue(String label, String value) {
        return value == null || value.trim().isEmpty() ? "" : label + ": " + value.trim();
    }

    private Map<String, List<Skill>> groupSkillsByType(List<Skill> skills) {
        Map<String, List<Skill>> groups = new LinkedHashMap<>();
        for (Skill skill : skills) {
            String type = skill.getSkillType() == null || skill.getSkillType().trim().isEmpty()
                    ? "Other" : skill.getSkillType().trim();
            groups.computeIfAbsent(type, key -> new ArrayList<>()).add(skill);
        }
        return groups;
    }

    private void writeCsvReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-progress-report.csv\"");
        resp.setHeader("Cache-Control", "no-store");

        try (PrintWriter writer = resp.getWriter()) {
            writer.write('\ufeff');
            for (String[] row : rows) {
                writeCsvRow(writer, row);
            }
        }
    }

    private void writeWordReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        ByteArrayOutputStream report = new ByteArrayOutputStream();
        try (XWPFDocument document = new XWPFDocument()) {
            addWordBanner(document);

            for (Map.Entry<String, List<String[]>> section : groupReportRows(rows).entrySet()) {
                if ("Student Profile".equals(section.getKey())) {
                    addWordSection(document, "Personal Information", section.getValue(), true);
                    continue;
                }
                addWordSection(document, section.getKey(), section.getValue(), false);
            }
            XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
            XWPFParagraph footerParagraph = footer.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun footerRun = footerParagraph.createRun();
            footerRun.setText("My Internship Technologies  |  Student Progress Report");
            footerRun.setFontFamily("Aptos");
            footerRun.setFontSize(8);
            footerRun.setColor("64748B");

            document.write(report);
        }
        resp.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-progress-report.docx\"");
        resp.setHeader("Cache-Control", "no-store");
        resp.setContentLength(report.size());
        report.writeTo(resp.getOutputStream());
    }

    private void writePdfReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-progress-report.pdf\"");
        resp.setHeader("Cache-Control", "no-store");
        try (PDDocument document = new PDDocument()) {
            PdfReportPage reportPage = new PdfReportPage(document);
            for (Map.Entry<String, List<String[]>> section : groupReportRows(rows).entrySet()) {
                if ("Student Profile".equals(section.getKey())) {
                    reportPage.drawPersonalInformation(section.getValue());
                    continue;
                }
                reportPage.drawSection(section.getKey());
                reportPage.drawColumnHeadings();
                int rowIndex = 0;
                for (String[] row : section.getValue()) {
                    reportPage.drawRecord(row[1], row[2], rowIndex++);
                }
            }
            reportPage.close();
            addPdfFooters(document);
            try (OutputStream output = resp.getOutputStream()) {
                document.save(output);
            }
        }
    }

    private Map<String, List<String[]>> groupReportRows(List<String[]> rows) {
        Map<String, List<String[]>> sections = new LinkedHashMap<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            sections.computeIfAbsent(nullToEmpty(row[0]), key -> new ArrayList<>()).add(row);
        }
        return sections;
    }

    private void addWordBanner(XWPFDocument document) {
        XWPFTable banner = document.createTable(1, 1);
        banner.setWidth("100%");
        banner.setCellMargins(120, 160, 100, 160);
        XWPFTableCell cell = banner.getRow(0).getCell(0);
        cell.setWidth("100%");
        cell.setColor("1A237E");

        XWPFParagraph title = cell.getParagraphs().get(0);
        title.setSpacingAfter(30);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("STUDENT PROGRESS REPORT");
        titleRun.setFontFamily("Aptos");
        titleRun.setFontSize(10);
        titleRun.setBold(true);
        titleRun.setColor("D2DCFF");

        XWPFParagraph subtitle = cell.addParagraph();
        XWPFRun subtitleRun = subtitle.createRun();
        subtitleRun.setText("STUDENT RECORD  |  GENERATED " + LocalDate.now());
        subtitleRun.setFontFamily("Aptos");
        subtitleRun.setFontSize(8);
        subtitleRun.setColor("E1E8FF");
    }

    private void addWordSection(XWPFDocument document, String title, List<String[]> rows,
                                boolean personal) {
        XWPFTable sectionHeading = document.createTable(1, 1);
        sectionHeading.setWidth("100%");
        sectionHeading.setCellMargins(70, 100, 70, 100);
        XWPFTableCell headingCell = sectionHeading.getRow(0).getCell(0);
        headingCell.setWidth("100%");
        headingCell.setColor("1E3A8A");
        XWPFParagraph heading = headingCell.getParagraphs().get(0);
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(title);
        headingRun.setFontFamily("Aptos");
        headingRun.setFontSize(11);
        headingRun.setBold(true);
        headingRun.setColor("FFFFFF");
        addWordTable(document, rows, personal);
    }

    private void addWordTable(XWPFDocument document, List<String[]> rows, boolean personal) {
        XWPFTable table = document.createTable(1, 2);
        table.setWidth("100%");
        table.setCellMargins(55, 85, 55, 85);
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setWidth("28%");
        header.getCell(1).setWidth("72%");
        setWordCell(header.getCell(0), "Record", "E2EAF8", true);
        setWordCell(header.getCell(1), "Details", "E2EAF8", true);
        int rowIndex = 0;
        for (String[] row : rows) {
            if (personal && nullToEmpty(row[2]).trim().isEmpty()) {
                continue;
            }
            XWPFTableRow tableRow = table.createRow();
            tableRow.getCell(0).setWidth("28%");
            tableRow.getCell(1).setWidth("72%");
            String rowBackground = rowIndex++ % 2 == 0 ? "F8FAFD" : "FFFFFF";
            setWordCell(tableRow.getCell(0), nullToEmpty(row[1]), rowBackground, true);
            setWordCell(tableRow.getCell(1), nullToEmpty(row[2]), rowBackground, false);
        }
    }

    private void setWordCell(XWPFTableCell cell, String text, String fillColor, boolean bold) {
        cell.setColor(fillColor);
        if (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }
        String[] detailLines = text.split("\\s+\\|\\s+");
        for (String detailLine : detailLines) {
            XWPFParagraph paragraph = cell.addParagraph();
            paragraph.setSpacingAfter(35);
            int labelEnd = bold ? detailLine.indexOf(':') : -1;
            if (labelEnd > 0 && labelEnd < 36) {
                XWPFRun labelRun = paragraph.createRun();
                labelRun.setText(detailLine.substring(0, labelEnd + 1) + " ");
                labelRun.setFontFamily("Aptos");
                labelRun.setFontSize(9);
                labelRun.setBold(true);
                labelRun.setColor("24324A");
                XWPFRun valueRun = paragraph.createRun();
                valueRun.setText(detailLine.substring(labelEnd + 1).trim());
                valueRun.setFontFamily("Aptos");
                valueRun.setFontSize(9);
                valueRun.setColor("24324A");
            } else {
                XWPFRun run = paragraph.createRun();
                run.setText(detailLine);
                run.setFontFamily("Aptos");
                run.setFontSize(9);
                run.setBold(bold);
                run.setColor("1E3A8A".equals(fillColor) ? "FFFFFF" : "24324A");
            }
        }
    }

    private void addPdfFooters(PDDocument document) throws IOException {
        int pageNumber = 1;
        for (PDPage page : document.getPages()) {
            try (PDPageContentStream footer = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                footer.setStrokingColor(new Color(220, 228, 240));
                footer.moveTo(PdfReportPage.LEFT, PdfReportPage.BOTTOM - 10);
                footer.lineTo(page.getMediaBox().getWidth() - PdfReportPage.RIGHT, PdfReportPage.BOTTOM - 10);
                footer.stroke();
                footer.beginText();
                footer.setFont(PDType1Font.HELVETICA, 8);
                footer.setNonStrokingColor(new Color(100, 116, 139));
                footer.newLineAtOffset(PdfReportPage.LEFT, PdfReportPage.BOTTOM - 25);
                footer.showText("My Internship Technologies  |  Student Progress Report");
                footer.newLineAtOffset(page.getMediaBox().getWidth() - PdfReportPage.LEFT - 80, 0);
                footer.showText("Page " + pageNumber++);
                footer.endText();
            }
        }
    }

    private static final class PdfReportPage {
        private static final float LEFT = 48;
        private static final float RIGHT = 48;
        private static final float BOTTOM = 48;
        private static final float ITEM_WIDTH = 150;
        private static final float LINE_HEIGHT = 12;

        private final PDDocument document;
        private PDPageContentStream content;
        private PDPage page;
        private float y;
        private String currentSection = "";

        private PdfReportPage(PDDocument document) throws IOException {
            this.document = document;
            startPage(true);
        }

        private void drawPersonalInformation(List<String[]> rows) throws IOException {
            drawSection("Personal Information");
            drawColumnHeadings();
            int rowIndex = 0;
            for (String[] row : rows) {
                if (nullToEmpty(row[2]).trim().isEmpty()) {
                    continue;
                }
                drawRecord(row[1], row[2], rowIndex++);
            }
        }

        private void drawSection(String section) throws IOException {
            currentSection = safeText(section);
            if (y < BOTTOM + 38) {
                startPage(false);
            }
            content.setNonStrokingColor(new Color(30, 58, 138));
            content.addRect(LEFT, y - 26, page.getMediaBox().getWidth() - LEFT - RIGHT, 26);
            content.fill();
            drawText(currentSection, LEFT + 10, y - 17, PDType1Font.HELVETICA_BOLD, 11, 255, 255, 255);
            y -= 34;
        }

        private void drawColumnHeadings() throws IOException {
            if (y < BOTTOM + 30) {
                startPage(false);
                drawSection(currentSection);
            }
            float width = page.getMediaBox().getWidth() - LEFT - RIGHT;
            content.setNonStrokingColor(new Color(226, 234, 248));
            content.addRect(LEFT, y - 24, width, 24);
            content.fill();
            drawText("Record", LEFT + 8, y - 16, PDType1Font.HELVETICA_BOLD, 9, 30, 58, 138);
            drawText("Details", LEFT + ITEM_WIDTH + 8, y - 16, PDType1Font.HELVETICA_BOLD, 9, 30, 58, 138);
            y -= 24;
        }

        private void drawRecord(String item, String details, int rowIndex) throws IOException {
            List<String> itemLines = wrapPdfLine(safeText(item), 27);
            List<String> detailLines = wrapPdfDetails(safeText(details), 75);
            int lineCount = Math.max(1, Math.max(itemLines.size(), detailLines.size()));
            int lineOffset = 0;
            while (lineOffset < lineCount) {
                float available = y - BOTTOM - 10;
                int linesOnPage = Math.min(lineCount - lineOffset, (int) (available / LINE_HEIGHT));
                if (linesOnPage < 1) {
                    startPage(false);
                    drawSection(currentSection);
                    drawColumnHeadings();
                    continue;
                }
                float rowHeight = linesOnPage * LINE_HEIGHT + 10;
                float width = page.getMediaBox().getWidth() - LEFT - RIGHT;
                content.setNonStrokingColor(rowIndex % 2 == 0
                        ? new Color(248, 250, 253) : Color.WHITE);
                content.addRect(LEFT, y - rowHeight, width, rowHeight);
                content.fill();
                content.setStrokingColor(new Color(226, 234, 248));
                content.addRect(LEFT, y - rowHeight, width, rowHeight);
                content.stroke();
                drawLines(itemLines, lineOffset, linesOnPage, LEFT + 8, y - 15);
                drawLines(detailLines, lineOffset, linesOnPage, LEFT + ITEM_WIDTH + 8, y - 15);
                y -= rowHeight;
                lineOffset += linesOnPage;
            }
            y -= 3;
        }

        private void drawLines(List<String> lines, int start, int count, float x, float baseline)
                throws IOException {
            for (int i = 0; i < count && start + i < lines.size(); i++) {
                drawText(lines.get(start + i), x, baseline - i * LINE_HEIGHT,
                        PDType1Font.HELVETICA, 9, 36, 50, 74);
            }
        }

        private void drawText(String text, float x, float baseline, PDType1Font font, int size,
                              int red, int green, int blue) throws IOException {
            content.beginText();
            content.setFont(font, size);
            content.setNonStrokingColor(new Color(red, green, blue));
            content.newLineAtOffset(x, baseline);
            content.showText(text);
            content.endText();
        }

        private void startPage(boolean firstPage) throws IOException {
            if (content != null) {
                content.close();
            }
            page = new PDPage();
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            float width = page.getMediaBox().getWidth() - LEFT - RIGHT;
            y = page.getMediaBox().getHeight() - 44;
            if (firstPage) {
                content.setNonStrokingColor(new Color(26, 35, 126));
                content.addRect(LEFT, y - 76, width, 76);
                content.fill();
                drawText("STUDENT PROGRESS REPORT", LEFT + 14, y - 19,
                        PDType1Font.HELVETICA_BOLD, 10, 210, 220, 255);
                drawText("STUDENT RECORD  |  GENERATED " + LocalDate.now(),
                        LEFT + 14, y - 45, PDType1Font.HELVETICA, 8, 225, 232, 255);
                y -= 96;
            } else {
                drawText("STUDENT PROGRESS REPORT",
                        LEFT, y, PDType1Font.HELVETICA_BOLD, 9, 30, 58, 138);
                y -= 18;
            }
        }

        private void close() throws IOException {
            if (content != null) {
                content.close();
            }
        }
    }

    private static List<String> wrapPdfLine(String line, int maxCharacters) {
        List<String> lines = new ArrayList<>();
        String remaining = line;
        while (remaining.length() > maxCharacters) {
            int splitAt = remaining.lastIndexOf(' ', maxCharacters);
            if (splitAt <= 0) {
                splitAt = maxCharacters;
            }
            lines.add(remaining.substring(0, splitAt));
            remaining = remaining.substring(splitAt).trim();
        }
        lines.add(remaining);
        return lines;
    }

    private static List<String> wrapPdfDetails(String details, int maxCharacters) {
        List<String> lines = new ArrayList<>();
        for (String detail : details.split("\\s+\\|\\s+")) {
            if (!detail.trim().isEmpty()) {
                lines.addAll(wrapPdfLine(detail.trim(), maxCharacters));
            }
        }
        return lines;
    }

    private static String safeText(String value) {
        return nullToEmpty(value)
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2212', '-')
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String joinValues(String... values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                if (result.length() > 0) {
                    result.append(" | ");
                }
                result.append(value);
            }
        }
        return result.toString();
    }

    private void writeCsvRow(PrintWriter writer, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(',');
            }
            String value = values[i] == null ? "" : values[i];
            int firstVisibleCharacter = 0;
            while (firstVisibleCharacter < value.length()
                    && Character.isWhitespace(value.charAt(firstVisibleCharacter))) {
                firstVisibleCharacter++;
            }
            if (firstVisibleCharacter < value.length()
                    && "=+-@".indexOf(value.charAt(firstVisibleCharacter)) >= 0) {
                value = "'" + value;
            }
            writer.write('"');
            writer.write(value.replace("\"", "\"\""));
            writer.write('"');
        }
        writer.println();
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }
}
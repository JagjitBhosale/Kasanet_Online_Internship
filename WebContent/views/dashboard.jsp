<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (request.getAttribute("activeSection") == null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
%>
<c:set var="selectedSection" value="${requestScope.activeSection}" />
<c:if test="${empty selectedSection}">
    <c:set var="selectedSection" value="student-profile" />
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Dashboard - My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root { --font: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; }
        body { font-family: var(--font); background-color: #f8fafc; color: #1e293b; line-height: 1.5; }
        .navbar { position: fixed; z-index: 1000; top: 0; left: 0; right: 0; background: linear-gradient(135deg, #1a237e 0%, #283593 100%); color: #ffffff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.12); }
        .navbar-branding { display: flex; align-items: center; flex: 0 0 auto; }
        .menu-toggle { width: 34px; height: 34px; display: grid; align-content: center; justify-items: center; gap: 5px; flex: 0 0 auto; margin-right: 10px; border: 0; border-radius: 6px; color: #fff; background: rgba(255,255,255,0.2); cursor: pointer; }
        .menu-toggle:focus-visible { outline: 3px solid #bfdbfe; outline-offset: 2px; }
        .menu-toggle span { width: 19px; height: 2px; border-radius: 2px; background: currentColor; }
        .navbar-brand { display: flex; align-items: center; gap: 12px; font-size: 1.15rem; font-weight: 700; text-decoration: none; color: #ffffff; }
        .navbar-menu { display: flex; align-items: center; gap: 14px; }
        .nav-link { color: rgba(255,255,255,0.85); text-decoration: none; font-size: 0.88rem; padding: 6px 12px; border-radius: 6px; transition: all 0.2s; }
        .nav-link:hover, .nav-link.active { color: #ffffff; background: rgba(255,255,255,0.15); }
        .btn-logout { background: #dc2626; color: #ffffff; padding: 6px 14px; border-radius: 6px; text-decoration: none; font-size: 0.85rem; font-weight: 600; }
        .btn-logout:hover { background: #b91c1c; }
        .layout { width: 100%; min-height: calc(100vh - 58px); display: grid; grid-template-columns: 270px minmax(0, 1fr); align-items: stretch; gap: 0; margin: 58px 0 0; padding: 0; transition: grid-template-columns .25s ease; }
        .sidebar { position: fixed; z-index: 900; top: 58px; bottom: 0; left: 0; width: 270px; overflow-y: auto; padding: 24px 18px; border: 0; border-right: 1px solid #dce4f1; border-radius: 0; background: #f9fbff; box-shadow: 4px 0 18px rgba(30, 41, 86, .045); transition: transform .25s ease; }
        .sidebar-title { margin: 0 8px 18px; padding: 0 4px 14px; border-bottom: 1px solid #e1e8f3; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; }
        .section-link { display: block; margin: 8px 0; padding: 13px 14px; border: 1px solid #dfe6f1; border-radius: 10px; color: #25345f; background: #fff; box-shadow: 0 2px 5px rgba(30, 41, 86, .035); font-size: 14px; font-weight: 700; line-height: 1.4; text-decoration: none; transition: color .18s, background .18s, border-color .18s, box-shadow .18s; }
        .section-link:hover, .section-link:focus { border-color: #b8c9f2; color: #1d4ed8; background: #f5f8ff; }
        .section-link.active { border-color: #b8c9f2; color: #1e40af; background: #eaf1ff; box-shadow: 0 4px 12px rgba(37, 99, 235, .08); }
        .content { grid-column: 2; display: grid; align-content: start; gap: 20px; min-width: 0; padding: 28px; transition: padding .25s ease; }
        .panel { min-height: 230px; padding: 30px; animation: panel-in .24s ease both; }
        .panel { border: 1px solid #e1e8f3; border-radius: 16px; background: #fff; box-shadow: 0 8px 24px rgba(30, 41, 86, .07); }
        @keyframes panel-in { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
        .sidebar-hidden .layout { grid-template-columns: minmax(0, 1fr); }
        .sidebar-hidden .sidebar { transform: translateX(-100%); pointer-events: none; }
        .sidebar-hidden .content { grid-column: 1; width: 100%; max-width: 1180px; justify-self: center; }
        h1 { margin: 0 0 8px; color: #172554; font-size: 28px; }
        h2 { margin: 0 0 18px; font-size: 21px; }
        h3 { margin: 26px 0 14px; padding-bottom: 10px; border-bottom: 1px solid #dce5f3; color: #1e3a8a; font-size: 19px; }
        .intro, .muted { color: #64748b; line-height: 1.6; }
        .profile-heading { margin-bottom: 8px; }
        .profile-heading h1 { margin: 0; }
        .summary-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
        .personal-info-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
        .summary-item { display: flex; flex-direction: column; align-items: flex-start; gap: 6px; min-width: 0; min-height: 82px; padding: 16px 18px; border: 1px solid #d9e3f2; border-left: 4px solid #7da4ee; border-radius: 12px; background: linear-gradient(110deg, #f5f8ff, #fff 70%); box-shadow: 0 2px 8px rgba(30, 41, 86, .045); transition: border-color .18s, box-shadow .18s, transform .18s; }
        .summary-item:hover, .profile-record-list .record:hover, .skill-group:hover { border-color: #a9c2ef; box-shadow: 0 5px 14px rgba(30, 58, 138, .1); transform: translateY(-1px); }
        .summary-label { display: block; color: #3f506b; font-size: 14px; font-weight: 700; letter-spacing: .035em; text-transform: uppercase; }
        .summary-value { min-width: 0; overflow-wrap: anywhere; color: #172554; font-size: 17px; font-weight: 500; line-height: 1.55; }
        .profile-tabs { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 8px; margin: 20px 0 22px; padding: 6px; border: 1px solid #e1e8f3; border-radius: 12px; background: #f4f7fd; }
        .profile-tab { min-width: 0; padding: 11px 12px; border: 1px solid transparent; border-radius: 8px; color: #1d4ed8; background: transparent; font: inherit; font-size: 16px; font-weight: 500; cursor: pointer; text-align: center; }
        .profile-tab:hover { background: #eaf1ff; }
        .profile-tab[aria-selected="true"] { border-color: #dce4f1; color: #253b7a; background: #fff; box-shadow: 0 2px 5px rgba(30, 41, 86, .07); font-weight: 700; }
        .profile-tab:focus-visible { outline: 3px solid #93c5fd; outline-offset: -3px; }
        .profile-tab-panel { min-height: 0; }
        .profile-tab-panel h3 { margin-top: 0; }
        .record-list { display: grid; gap: 10px; }
        .profile-record-list { grid-template-columns: minmax(0, 1fr); gap: 12px; }
        .record { padding: 13px; border: 1px solid #e1e8f3; border-radius: 10px; background: #fcfdff; }
        .profile-record-list .record { min-width: 0; padding: 19px 22px; border: 1px solid #d9e3f2; border-left: 4px solid #7da4ee; border-radius: 12px; background: linear-gradient(110deg, #f5f8ff, #fff 70%); box-shadow: 0 2px 8px rgba(30, 41, 86, .045); transition: border-color .18s, box-shadow .18s, transform .18s; }
        .profile-record-list .record strong { color: #173779; font-size: 18px; }
        .record strong { display: block; margin-bottom: 5px; }
        .record p { margin: 4px 0; color: #64748b; font-size: 14px; line-height: 1.5; overflow-wrap: anywhere; }
        .project-report-form { display: grid; gap: 18px; margin-top: 20px; }
        .project-report-group { padding: 20px; border: 1px solid #dce5f3; border-radius: 12px; background: linear-gradient(135deg, #f8faff, #fff 72%); }
        .project-report-group h3 { margin: 0 0 16px; padding-bottom: 10px; }
        .project-report-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px 18px; }
        .project-report-field { display: grid; gap: 6px; min-width: 0; color: #334155; font-size: 14px; font-weight: 700; }
        .project-report-field input, .project-report-field select, .project-report-field textarea { width: 100%; min-height: 42px; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 8px; color: #172554; background: #fff; font: inherit; font-weight: 400; }
        .project-report-field textarea { min-height: 100px; resize: vertical; }
        .project-report-field input:focus, .project-report-field select:focus, .project-report-field textarea:focus { outline: 3px solid #bfdbfe; border-color: #3b82f6; }
        .project-report-wide { grid-column: 1 / -1; }
        .project-report-card { padding: 20px; border: 1px solid #d9e3f2; border-left: 4px solid #7da4ee; border-radius: 12px; background: linear-gradient(110deg, #f5f8ff, #fff 70%); }
        .project-report-card h3 { margin: 0 0 8px; padding: 0; border: 0; }
        .project-report-facts { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-top: 16px; }
        .project-report-fact { min-width: 0; padding: 10px 12px; border: 1px solid #e1e8f3; border-radius: 8px; background: #fff; overflow-wrap: anywhere; }
        .project-report-fact strong { display: block; margin-bottom: 4px; color: #3f506b; font-size: 12px; letter-spacing: .04em; text-transform: uppercase; }
        .project-report-extra { margin-top: 14px; padding: 12px 14px; border: 1px solid #dce5f3; border-radius: 9px; background: #fff; }
        .project-report-extra summary { color: #1e40af; cursor: pointer; font-weight: 700; }
        @media (max-width: 620px) { .project-report-grid, .project-report-facts { grid-template-columns: 1fr; } .project-report-wide { grid-column: auto; } .project-report-group { padding: 16px; } }
        .education-record-list .record-facts { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 14px 22px; margin-top: 14px; }
        .achievement-record-list .record-facts { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px 22px; margin-top: 14px; }
        .profile-record-list .record-fact { display: grid; gap: 3px; min-width: 0; overflow-wrap: anywhere; }
        .record-fact-label { color: #3f506b; font-size: 13px; font-weight: 700; letter-spacing: .035em; text-transform: uppercase; }
        .record-fact-value { color: #26364f; font-size: 16px; line-height: 1.55; }
        .profile-record-list .record-description { grid-column: 1 / -1; margin: 0; padding-top: 12px; border-top: 1px solid #dce5f3; color: #334155; font-size: 16px; line-height: 1.6; overflow-wrap: anywhere; }
        .skill-groups { display: grid; grid-template-columns: minmax(0, 1fr); gap: 12px; }
        .skill-group { min-width: 0; padding: 18px 22px; border: 1px solid #d9e3f2; border-left: 4px solid #7da4ee; border-radius: 12px; background: linear-gradient(110deg, #f5f8ff, #fff 70%); box-shadow: 0 2px 8px rgba(30, 41, 86, .045); transition: border-color .18s, box-shadow .18s, transform .18s; }
        .skill-group h4 { margin: 0 0 12px; color: #173779; font-size: 18px; }
        .skill-tags { display: flex; flex-wrap: wrap; align-items: flex-start; gap: 8px; }
        .skill-tag { display: inline-flex; width: fit-content; max-width: 100%; padding: 8px 12px; border: 1px solid #cbd8ed; border-radius: 8px; color: #26364f; background: #fff; font-size: 15px; line-height: 1.5; overflow-wrap: anywhere; }
        .button { display: inline-flex; align-items: center; justify-content: center; min-height: 42px; padding: 10px 16px; border: 0; border-radius: 7px; color: #fff; background: #1d4ed8; font-weight: 700; text-decoration: none; cursor: pointer; }
        .button:hover { background: #1e40af; }
        .assignment-view-button { gap: 8px; margin-top: 8px; }
        .assignment-view-button:focus-visible { outline: 3px solid #93c5fd; outline-offset: 3px; }
        .button-secondary { margin-left: 8px; border: 1px solid #cbd5e1; color: #1e3a8a; background: #fff; }
        .empty { padding: 14px 16px; border: 1px solid #d9e3f2; border-radius: 10px; color: #334155; background: #f5f8ff; font-size: 15px; line-height: 1.6; }
        .report-links { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 20px; }
        @media (max-width: 760px) {
            .layout { grid-template-columns: 1fr; }
            .sidebar { top: 58px; width: min(300px, 86vw); }
            .content, .sidebar-hidden .content { grid-column: 1; padding: 18px 14px; }
            .project-report-facts { grid-template-columns: repeat(2, minmax(0, 1fr)); }
        }
        @media (max-width: 620px) {
            .summary-grid, .profile-record-list, .skill-groups { grid-template-columns: minmax(0, 1fr); }
            .education-record-list .record-facts, .achievement-record-list .record-facts { grid-template-columns: minmax(0, 1fr); }
            .project-report-facts { grid-template-columns: minmax(0, 1fr); }
        }
        @media (max-width: 480px) {
            .summary-grid, .profile-record-list { grid-template-columns: minmax(0, 1fr); }
            .summary-item { grid-template-columns: minmax(0, 1fr); gap: 4px; padding: 12px 14px; }
            .profile-record-list .record, .skill-group { padding: 14px; }
            .profile-tabs { grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 6px; margin: 16px 0 18px; }
            .profile-tab { padding: 10px 7px; font-size: 14px; }
            .panel { min-height: 190px; padding: 20px; }
            .layout { padding: 12px; }
        }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="navbar-branding">
            <button class="menu-toggle" id="sidebarToggle" type="button" aria-label="Hide dashboard sidebar" aria-controls="dashboardSidebar" aria-expanded="true">
                <span></span><span></span><span></span>
            </button>
            <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
            </a>
        </div>
        <nav class="navbar-menu" aria-label="Main navigation">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active" aria-current="page">My Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <span style="font-size: 0.88rem; color: #cbd5e0; margin-left: 6px;">Hi, <c:out value="${user.firstName}"/></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </nav>
    </header>

    <div class="layout">
        <aside class="sidebar" id="dashboardSidebar" aria-label="Dashboard sections">
            <h2 class="sidebar-title">My Dashboard</h2>
            <a href="${pageContext.request.contextPath}/dashboard?section=student-profile" class="section-link ${selectedSection eq 'student-profile' ? 'active' : ''}" aria-current="${selectedSection eq 'student-profile' ? 'page' : 'false'}">Student Profile</a>
            <a href="${pageContext.request.contextPath}/dashboard?section=assignment" class="section-link ${selectedSection eq 'assignment' ? 'active' : ''}" aria-current="${selectedSection eq 'assignment' ? 'page' : 'false'}">Assignment Details</a>
            <a href="${pageContext.request.contextPath}/dashboard?section=projects" class="section-link ${selectedSection eq 'projects' ? 'active' : ''}" aria-current="${selectedSection eq 'projects' ? 'page' : 'false'}">Project</a>
            <a href="${pageContext.request.contextPath}/dashboard?section=certificates" class="section-link ${selectedSection eq 'certificates' ? 'active' : ''}" aria-current="${selectedSection eq 'certificates' ? 'page' : 'false'}">Global Certification</a>
            <a href="${pageContext.request.contextPath}/dashboard?section=report-details" class="section-link ${selectedSection eq 'report-details' ? 'active' : ''}" aria-current="${selectedSection eq 'report-details' ? 'page' : 'false'}">Report</a>
        </aside>

        <main class="content">
            <c:if test="${selectedSection eq 'student-profile'}">
            <section class="panel">
                <div class="profile-heading">
                    <h1>Student Profile</h1>
                </div>
                <p class="intro">View and manage your personal details, education, achievements, and skills.</p>
                <div class="profile-tabs" role="tablist" aria-label="Student profile sections">
                    <button class="profile-tab" id="profile-tab-personal" type="button" role="tab" aria-controls="profile-panel-personal" aria-selected="true" tabindex="0">Personal Details</button>
                    <button class="profile-tab" id="profile-tab-education" type="button" role="tab" aria-controls="profile-panel-education" aria-selected="false" tabindex="-1">Education Details</button>
                    <button class="profile-tab" id="profile-tab-achievement" type="button" role="tab" aria-controls="profile-panel-achievement" aria-selected="false" tabindex="-1">Achievement</button>
                    <button class="profile-tab" id="profile-tab-skills" type="button" role="tab" aria-controls="profile-panel-skills" aria-selected="false" tabindex="-1">Skills</button>
                </div>
                <div class="profile-tab-panel" id="profile-panel-personal" role="tabpanel" aria-labelledby="profile-tab-personal">
                <h3>Personal Information</h3>
                <div class="summary-grid personal-info-grid">
                    <div class="summary-item"><span class="summary-label">Name</span><div class="summary-value"><c:out value="${user.fullName}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Email</span><div class="summary-value"><c:out value="${user.email}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Mobile</span><div class="summary-value"><c:out value="${user.mobile}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Gender</span><div class="summary-value"><c:out value="${profile.gender}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Date of Birth</span><div class="summary-value"><c:out value="${profile.dateOfBirth}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Branch</span><div class="summary-value"><c:out value="${user.branch}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Stream</span><div class="summary-value"><c:out value="${user.stream}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">College Name</span><div class="summary-value"><c:out value="${user.collegeName}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Address</span><div class="summary-value"><c:out value="${profile.address}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">City</span><div class="summary-value"><c:out value="${user.city}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">State</span><div class="summary-value"><c:out value="${profile.state}" default="Not provided"/></div></div>
                </div>
                </div>
                <div class="profile-tab-panel" id="profile-panel-education" role="tabpanel" aria-labelledby="profile-tab-education">
                <h3>Education Details</h3>
                <c:choose>
                    <c:when test="${not empty educationList}">
                        <div class="record-list profile-record-list education-record-list">
                            <c:forEach var="education" items="${educationList}">
                                <article class="record">
                                    <strong><c:out value="${education.level}"/></strong>
                                    <div class="record-facts">
                                        <c:if test="${not empty education.schoolCollegeName}">
                                            <div class="record-fact"><span class="record-fact-label">School or College</span><span class="record-fact-value"><c:out value="${education.schoolCollegeName}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty education.boardUniversity}">
                                            <div class="record-fact"><span class="record-fact-label">Board or University</span><span class="record-fact-value"><c:out value="${education.boardUniversity}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty education.stream}">
                                            <div class="record-fact"><span class="record-fact-label">Stream or Field</span><span class="record-fact-value"><c:out value="${education.stream}"/></span></div>
                                        </c:if>
                                        <c:if test="${education.passingYear > 0}">
                                            <div class="record-fact"><span class="record-fact-label">Passing Year</span><span class="record-fact-value"><c:out value="${education.passingYear}"/></span></div>
                                        </c:if>
                                        <c:if test="${education.percentage > 0}">
                                            <div class="record-fact"><span class="record-fact-label">Percentage</span><span class="record-fact-value"><c:out value="${education.percentage}"/>%</span></div>
                                        </c:if>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No education records have been saved yet.</p></c:otherwise>
                </c:choose>
                </div>
                <div class="profile-tab-panel" id="profile-panel-skills" role="tabpanel" aria-labelledby="profile-tab-skills">
                <h3>Skills</h3>
                <c:choose>
                    <c:when test="${not empty skillList}">
                        <div class="skill-groups">
                            <c:forEach var="skillGroup" items="${skillsByType}">
                                <section class="skill-group">
                                    <h4><c:out value="${skillGroup.key}"/></h4>
                                    <div class="skill-tags">
                                        <c:forEach var="skill" items="${skillGroup.value}">
                                            <span class="skill-tag"><c:out value="${skill.skillName}"/></span>
                                        </c:forEach>
                                    </div>
                                </section>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No skills have been saved yet.</p></c:otherwise>
                </c:choose>
                </div>
                <div class="profile-tab-panel" id="profile-panel-achievement" role="tabpanel" aria-labelledby="profile-tab-achievement">
                <h3>Achievement</h3>
                <c:choose>
                    <c:when test="${not empty achievementList or not empty hackathonList or not empty competitionList}">
                        <div class="record-list profile-record-list achievement-record-list">
                            <c:forEach var="achievement" items="${achievementList}">
                                <article class="record">
                                    <strong><c:out value="${achievement.title}"/></strong>
                                    <div class="record-facts">
                                        <c:if test="${not empty achievement.achievementType}">
                                            <div class="record-fact"><span class="record-fact-label">Type</span><span class="record-fact-value"><c:out value="${achievement.achievementType}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty achievement.technology}">
                                            <div class="record-fact"><span class="record-fact-label">Technology</span><span class="record-fact-value"><c:out value="${achievement.technology}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty achievement.extraActivities}">
                                            <div class="record-fact"><span class="record-fact-label">Activity</span><span class="record-fact-value"><c:out value="${achievement.extraActivities}"/></span></div>
                                        </c:if>
                                    </div>
                                    <c:if test="${not empty achievement.description}">
                                        <p class="record-description"><span class="record-fact-label">Description</span><br><c:out value="${achievement.description}"/></p>
                                    </c:if>
                                </article>
                            </c:forEach>
                            <c:forEach var="hackathon" items="${hackathonList}">
                                <article class="record">
                                    <strong><c:out value="${hackathon.hackathonName}"/></strong>
                                    <div class="record-facts">
                                        <c:if test="${not empty hackathon.organizer}">
                                            <div class="record-fact"><span class="record-fact-label">Organizer</span><span class="record-fact-value"><c:out value="${hackathon.organizer}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty hackathon.eventDate}">
                                            <div class="record-fact"><span class="record-fact-label">Event Date</span><span class="record-fact-value"><c:out value="${hackathon.eventDate}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty hackathon.positionResult}">
                                            <div class="record-fact"><span class="record-fact-label">Result</span><span class="record-fact-value"><c:out value="${hackathon.positionResult}"/></span></div>
                                        </c:if>
                                    </div>
                                    <c:if test="${not empty hackathon.description}">
                                        <p class="record-description"><span class="record-fact-label">Description</span><br><c:out value="${hackathon.description}"/></p>
                                    </c:if>
                                </article>
                            </c:forEach>
                            <c:forEach var="competition" items="${competitionList}">
                                <article class="record">
                                    <strong><c:out value="${competition.competitionName}"/></strong>
                                    <div class="record-facts">
                                        <c:if test="${not empty competition.collegeOrganizer}">
                                            <div class="record-fact"><span class="record-fact-label">Organizer</span><span class="record-fact-value"><c:out value="${competition.collegeOrganizer}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty competition.eventDate}">
                                            <div class="record-fact"><span class="record-fact-label">Event Date</span><span class="record-fact-value"><c:out value="${competition.eventDate}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty competition.projectName}">
                                            <div class="record-fact"><span class="record-fact-label">Project</span><span class="record-fact-value"><c:out value="${competition.projectName}"/></span></div>
                                        </c:if>
                                        <c:if test="${not empty competition.positionResult}">
                                            <div class="record-fact"><span class="record-fact-label">Result</span><span class="record-fact-value"><c:out value="${competition.positionResult}"/></span></div>
                                        </c:if>
                                    </div>
                                    <c:if test="${not empty competition.description}">
                                        <p class="record-description"><span class="record-fact-label">Description</span><br><c:out value="${competition.description}"/></p>
                                    </c:if>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No additional activities or achievements have been saved yet.</p></c:otherwise>
                </c:choose>
                </div>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'assignment'}">
            <section class="panel">
                <h2>Assignment Details</h2>
                <p class="intro">Review your active internship assignments, descriptions, instructions, and due dates.</p>
                <c:choose>
                    <c:when test="${not empty assignmentList}">
                        <div class="record-list">
                            <c:forEach var="assignment" items="${assignmentList}">
                                <article class="record">
                                    <strong><c:out value="${assignment.title}"/></strong>
                                    <p><c:if test="${not empty assignment.subject}"><c:out value="${assignment.subject}"/> · </c:if><c:out value="${assignment.description}"/></p>
                                    <p><c:if test="${not empty assignment.dueDate}">Due: <c:out value="${assignment.dueDate}"/> · </c:if><c:if test="${assignment.totalMarks > 0}">Total marks: <c:out value="${assignment.totalMarks}"/></c:if></p>
                                    <a class="button assignment-view-button" href="${pageContext.request.contextPath}/assignment-details?assignmentId=${assignment.assignmentId}">
                                        View full assignment and instructions <span aria-hidden="true">&rarr;</span>
                                    </a>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No active assignments are available.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'projects'}">
            <section class="panel">
                <h2>Project</h2>
                <p class="intro">Capture your project journey, from scope and planning through testing and the skills you gained.</p>
                <c:if test="${param.success eq 'project-saved'}"><p role="status" class="empty">Project report saved successfully.</p></c:if>
                <c:if test="${param.success eq 'project-deleted'}"><p role="status" class="empty">Project report deleted.</p></c:if>
                <c:if test="${not empty param.error}">
                    <p role="alert" style="color:#b91c1c;">
                        <c:choose>
                            <c:when test="${param.error eq 'invalid-project'}">Check required fields and make sure numeric values are valid and non-negative.</c:when>
                            <c:when test="${param.error eq 'project-not-found'}">That project report could not be found.</c:when>
                            <c:otherwise>Unable to save this project report. Please try again.</c:otherwise>
                        </c:choose>
                    </p>
                </c:if>
                <form action="${pageContext.request.contextPath}/platform-project-reports" method="post" class="project-report-form">
                    <input type="hidden" name="action" value="add">
                    <div class="project-report-group">
                        <h3>Project Information</h3>
                        <div class="project-report-grid">
                            <label class="project-report-field">Project name *
                                <input type="text" name="projectName" maxlength="200" placeholder="e.g. Student Internship Portal" required>
                            </label>
                            <label class="project-report-field">Project domain
                                <input type="text" name="projectDomain" maxlength="200" placeholder="e.g. Cloud Computing, AI, Cyber Security">
                            </label>
                            <label class="project-report-field project-report-wide">Project details and scope *
                                <textarea name="scopeDetails" maxlength="5000" placeholder="Describe the problem, objectives, features, and scope of your project" required></textarea>
                            </label>
                            <label class="project-report-field">Project technology
                                <input type="text" name="projectTechnology" maxlength="500" placeholder="e.g. Java, JSP, MySQL">
                            </label>
                            <label class="project-report-field">Project application
                                <input type="text" name="projectApplication" maxlength="500" placeholder="What does the project do or where is it used?">
                            </label>
                            <label class="project-report-field">Project duration
                                <input type="text" name="projectDuration" maxlength="100" placeholder="e.g. 3 months">
                            </label>
                            <label class="project-report-field">Project model
                                <select name="projectModel" id="projectModelSelect" required>
                                    <option value="">Select a model</option>
                                    <option>Waterfall</option><option>V-Model</option><option>Agile</option><option>Spiral</option><option>Other</option>
                                </select>
                            </label>
                            <label class="project-report-field" id="otherProjectModelField" style="display:none;">Other project model
                                <input type="text" name="otherProjectModel" maxlength="100" placeholder="Enter the model used">
                            </label>
                        </div>
                    </div>

                    <div class="project-report-group">
                        <h3>Time Estimation</h3>
                        <div class="project-report-grid">
                            <label class="project-report-field">Requirement gathering
                                <input type="text" name="requirementGatheringTime" maxlength="100" placeholder="e.g. 2 weeks">
                            </label>
                            <label class="project-report-field">Data analysis
                                <input type="text" name="projectAnalysisTime" maxlength="100" placeholder="e.g. 1 week">
                            </label>
                            <label class="project-report-field">Data cleaning
                                <input type="text" name="projectDesigningTime" maxlength="100" placeholder="e.g. 2 weeks">
                            </label>
                            <label class="project-report-field">Data modeling
                                <input type="text" name="projectCodingTime" maxlength="100" placeholder="e.g. 6 weeks">
                            </label>
                            <label class="project-report-field">Data visualization
                                <input type="text" name="projectTestingTime" maxlength="100" placeholder="e.g. 2 weeks">
                            </label>
                            <label class="project-report-field">Estimated project cost
                                <input type="number" name="projectCost" min="0" max="9999999999.99" step="0.01" placeholder="Optional amount">
                            </label>
                        </div>
                    </div>

                    <div class="project-report-group">
                        <h3>Testing</h3>
                        <div class="project-report-grid">
                            <label class="project-report-field">Type of testing
                                <select name="testingType" id="projectTestingTypeSelect">
                                    <option value="">Select testing type</option>
                                    <option>Unit Testing</option><option>Integration Testing</option><option>System Testing</option><option>Acceptance Testing</option><option>Other</option>
                                </select>
                            </label>
                            <label class="project-report-field" id="otherTestingTypeField" style="display:none;">Other testing type
                                <input type="text" name="otherTestingType" maxlength="100" placeholder="Enter testing type">
                            </label>
                            <label class="project-report-field">Duration for testing
                                <input type="text" name="testingDuration" maxlength="100" placeholder="e.g. 10 days">
                            </label>
                            <label class="project-report-field">Number of issues found
                                <input type="number" name="issuesFound" min="0" max="2147483647" step="1" placeholder="0">
                            </label>
                        </div>
                    </div>

                    <div class="project-report-group">
                        <h3>Knowledge Gained in Project Activity</h3>
                        <p class="muted">Rate what you learned during each phase.</p>
                        <div class="project-report-grid">
                            <label class="project-report-field">Requirement phase
                                <select name="knowledgeRequirement"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                            <label class="project-report-field">Analysis phase
                                <select name="knowledgeAnalysis"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                            <label class="project-report-field">Designing phase
                                <select name="knowledgeDesigning"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                            <label class="project-report-field">Coding phase
                                <select name="knowledgeCoding"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                            <label class="project-report-field">Testing phase
                                <select name="knowledgeTesting"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                            <label class="project-report-field">Post-implementation phase
                                <select name="knowledgePostImplementation"><option value="">Select</option><option>Excellent</option><option>Good</option><option>Fair</option><option>Needs Improvement</option><option>Not Applicable</option></select>
                            </label>
                        </div>
                    </div>
                    <div><button class="button" type="submit">Save Project</button></div>
                </form>
            </section>
            <section class="panel">
                <h2>Your Saved Project</h2>
                <c:choose>
                    <c:when test="${not empty platformProjectReportList}">
                        <div class="record-list">
                            <c:forEach var="report" items="${platformProjectReportList}">
                                <article class="project-report-card">
                                    <h3><c:out value="${report.projectName}"/></h3>
                                    <p class="intro"><c:out value="${report.scopeDetails}"/></p>
                                    <details class="project-report-extra">
                                        <summary>View project details</summary>
                                        <div class="project-report-facts">
                                            <c:if test="${not empty report.projectDomain}"><div class="project-report-fact"><strong>Domain</strong><c:out value="${report.projectDomain}"/></div></c:if>
                                            <c:if test="${not empty report.projectTechnology}"><div class="project-report-fact"><strong>Technology</strong><c:out value="${report.projectTechnology}"/></div></c:if>
                                            <c:if test="${not empty report.projectApplication}"><div class="project-report-fact"><strong>Application</strong><c:out value="${report.projectApplication}"/></div></c:if>
                                            <c:if test="${not empty report.projectDuration}"><div class="project-report-fact"><strong>Duration</strong><c:out value="${report.projectDuration}"/></div></c:if>
                                            <div class="project-report-fact"><strong>Model</strong><c:out value="${report.projectModel}"/></div>
                                            <c:if test="${not empty report.projectCost}"><div class="project-report-fact"><strong>Estimated project cost</strong><c:out value="${report.projectCost}"/></div></c:if>
                                            <div class="project-report-fact"><strong>Issues found</strong><c:out value="${report.issuesFound}"/></div>
                                            <c:if test="${not empty report.testingType}"><div class="project-report-fact"><strong>Testing type</strong><c:out value="${report.testingType}"/></div></c:if>
                                            <c:if test="${not empty report.requirementGatheringTime}"><div class="project-report-fact"><strong>Requirement gathering</strong><c:out value="${report.requirementGatheringTime}"/></div></c:if>
                                            <c:if test="${not empty report.projectAnalysisTime}"><div class="project-report-fact"><strong>Data analysis</strong><c:out value="${report.projectAnalysisTime}"/></div></c:if>
                                            <c:if test="${not empty report.projectDesigningTime}"><div class="project-report-fact"><strong>Data cleaning</strong><c:out value="${report.projectDesigningTime}"/></div></c:if>
                                            <c:if test="${not empty report.projectCodingTime}"><div class="project-report-fact"><strong>Data modeling</strong><c:out value="${report.projectCodingTime}"/></div></c:if>
                                            <c:if test="${not empty report.projectTestingTime}"><div class="project-report-fact"><strong>Data visualization</strong><c:out value="${report.projectTestingTime}"/></div></c:if>
                                            <c:if test="${not empty report.testingDuration}"><div class="project-report-fact"><strong>Test duration</strong><c:out value="${report.testingDuration}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgeRequirement}"><div class="project-report-fact"><strong>Requirement phase learning</strong><c:out value="${report.knowledgeRequirement}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgeAnalysis}"><div class="project-report-fact"><strong>Analysis phase learning</strong><c:out value="${report.knowledgeAnalysis}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgeDesigning}"><div class="project-report-fact"><strong>Designing phase learning</strong><c:out value="${report.knowledgeDesigning}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgeCoding}"><div class="project-report-fact"><strong>Coding phase learning</strong><c:out value="${report.knowledgeCoding}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgeTesting}"><div class="project-report-fact"><strong>Testing phase learning</strong><c:out value="${report.knowledgeTesting}"/></div></c:if>
                                            <c:if test="${not empty report.knowledgePostImplementation}"><div class="project-report-fact"><strong>Post-implementation learning</strong><c:out value="${report.knowledgePostImplementation}"/></div></c:if>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/platform-project-reports" method="post" style="margin-top:14px;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="reportId" value="${report.reportId}">
                                            <button class="button button-secondary" type="submit">Delete Project</button>
                                        </form>
                                    </details>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">You have not saved a project report yet. Use the form above to add your first report.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'certificates'}">
            <section class="panel">
                <h2>Global Certification</h2>
                <p class="intro">Record your global certification exam results.</p>
                <c:if test="${param.success eq 'certificate-added'}"><p role="status" style="color:#166534;">Certificate exam result added.</p></c:if>
                <c:if test="${param.success eq 'certificate-deleted'}"><p role="status" style="color:#166534;">Certificate exam result deleted.</p></c:if>
                <c:if test="${not empty param.error}">
                    <p role="alert" style="color:#b91c1c;">
                        <c:choose>
                            <c:when test="${param.error eq 'invalid-certificate'}">Enter a valid exam name, score out of 1000, and achievement date.</c:when>
                            <c:when test="${param.error eq 'certificate-not-found'}">The certificate result could not be found.</c:when>
                            <c:otherwise>Could not save the certificate result. Please try again.</c:otherwise>
                        </c:choose>
                    </p>
                </c:if>
                <form action="${pageContext.request.contextPath}/platform-certificates" method="post" accept-charset="UTF-8"
                      style="display:grid;grid-template-columns:repeat(auto-fit,minmax(190px,1fr));gap:14px;margin:18px 0 24px;">
                    <input type="hidden" name="action" value="add">
                    <label>Exam
                        <select name="examName" id="platformExamName" required style="display:block;width:100%;margin-top:6px;padding:10px;border:1px solid #cbd5e1;border-radius:7px;">
                            <option value="">Select an exam</option>
                            <option>AZ-900 – Microsoft Azure Fundamentals</option>
                            <option>AI-901 – Microsoft Azure AI Fundamentals</option>
                            <option>PL-900 – Microsoft Power Platform Fundamentals</option>
                            <option>DP-900 – Microsoft Azure Data Fundamentals</option>
                            <option>Cisco Certified Cybersecurity Associate</option>
                            <option>Other</option>
                        </select>
                    </label>
                    <label id="otherExamNameField" style="display:none;">Other exam name
                        <input type="text" name="otherExamName" maxlength="200" style="display:block;width:100%;margin-top:6px;padding:10px;border:1px solid #cbd5e1;border-radius:7px;">
                    </label>
                    <label>Score obtained
                        <input type="number" name="scoreObtained" min="0" max="1000" step="1" placeholder="e.g. 840" required style="display:block;width:100%;margin-top:6px;padding:10px;border:1px solid #cbd5e1;border-radius:7px;">
                    </label>
                    <label>Date achieved
                        <input type="date" name="achievementDate" max="<%= java.time.LocalDate.now() %>" required style="display:block;width:100%;margin-top:6px;padding:10px;border:1px solid #cbd5e1;border-radius:7px;">
                    </label>
                    <div style="align-self:end;"><button class="button" type="submit">Add Certificate</button></div>
                </form>
                <c:choose>
                    <c:when test="${not empty platformCertificateList}">
                        <div class="record-list">
                            <c:forEach var="certificate" items="${platformCertificateList}">
                                <article class="record">
                                    <strong><c:out value="${certificate.examName}"/></strong>
                                    <p>Score: <fmt:formatNumber value="${certificate.scoreObtained}" minFractionDigits="0" maxFractionDigits="2" groupingUsed="false"/>/1000</p>
                                    <p>Date achieved: <c:out value="${certificate.achievementDate}"/></p>
                                    <form action="${pageContext.request.contextPath}/platform-certificates" method="post" style="margin-top:8px;">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="certificateId" value="${certificate.certificateId}">
                                        <button type="submit" class="button button-secondary">Delete</button>
                                    </form>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No global certification results have been added yet.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'report-details'}">
            <section class="panel">
                <h2>Report</h2>
                <p class="intro">Download a complete report of your profile and dashboard records, including global certifications and project reports in PDF, Word, or CSV.</p>
                <div class="report-links">
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=pdf">Download PDF</a>
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=word">Download Word</a>
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=csv">Download CSV</a>
                </div>
                <p class="muted" style="margin-top: 14px;">Each format includes your profile details, project reports, global certifications, portfolio projects, and additional certifications as separate sections. PDF and Word use formatted tables; CSV opens in spreadsheet apps.</p>
            </section>
            </c:if>
        </main>
    </div>
<script>
    (function () {
        var modelSelect = document.getElementById('projectModelSelect');
        var otherModelField = document.getElementById('otherProjectModelField');
        var testingSelect = document.getElementById('projectTestingTypeSelect');
        var otherTestingField = document.getElementById('otherTestingTypeField');

        function toggleOther(select, field) {
            if (!select || !field) return;
            var input = field.querySelector('input');
            var isOther = select.value === 'Other';
            field.style.display = isOther ? 'grid' : 'none';
            input.required = isOther;
        }

        if (modelSelect) {
            modelSelect.addEventListener('change', function () {
                toggleOther(modelSelect, otherModelField);
            });
            toggleOther(modelSelect, otherModelField);
        }
        if (testingSelect) {
            testingSelect.addEventListener('change', function () {
                toggleOther(testingSelect, otherTestingField);
            });
            toggleOther(testingSelect, otherTestingField);
        }
    }());

    (function () {
        var examSelect = document.getElementById('platformExamName');
        var otherExamField = document.getElementById('otherExamNameField');
        if (!examSelect || !otherExamField) return;
        var otherExamInput = otherExamField.querySelector('input');
        var updateOtherExamField = function () {
            var isOther = examSelect.value === 'Other';
            otherExamField.style.display = isOther ? 'block' : 'none';
            otherExamInput.required = isOther;
        };
        examSelect.addEventListener('change', updateOtherExamField);
        updateOtherExamField();
    }());

    (function () {
        var toggle = document.getElementById("sidebarToggle");
        function updateToggleState() {
            var expanded = !document.body.classList.contains("sidebar-hidden");
            toggle.setAttribute("aria-expanded", String(expanded));
            toggle.setAttribute("aria-label", expanded ? "Hide dashboard sidebar" : "Show dashboard sidebar");
        }

        updateToggleState();
        toggle.addEventListener("click", function () {
            document.body.classList.toggle("sidebar-hidden");
            updateToggleState();
        });
    }());

    document.querySelectorAll(".profile-tabs").forEach(function (tabList) {
        var tabs = Array.prototype.slice.call(tabList.querySelectorAll('[role="tab"]'));
        var panels = tabs.map(function (tab) {
            return document.getElementById(tab.getAttribute("aria-controls"));
        });

        function activateTab(index, moveFocus) {
            tabs.forEach(function (tab, tabIndex) {
                var selected = tabIndex === index;
                tab.setAttribute("aria-selected", String(selected));
                tab.tabIndex = selected ? 0 : -1;
                panels[tabIndex].hidden = !selected;
            });
            if (moveFocus) {
                tabs[index].focus();
            }
        }

        tabs.forEach(function (tab, index) {
            tab.addEventListener("click", function () {
                activateTab(index, false);
            });
            tab.addEventListener("keydown", function (event) {
                var nextIndex = index;
                if (event.key === "ArrowRight") {
                    nextIndex = (index + 1) % tabs.length;
                } else if (event.key === "ArrowLeft") {
                    nextIndex = (index + tabs.length - 1) % tabs.length;
                } else if (event.key === "Home") {
                    nextIndex = 0;
                } else if (event.key === "End") {
                    nextIndex = tabs.length - 1;
                } else {
                    return;
                }
                event.preventDefault();
                activateTab(nextIndex, true);
            });
        });

        var selectedIndex = tabs.findIndex(function (tab) {
            return tab.getAttribute("aria-selected") === "true";
        });
        activateTab(selectedIndex < 0 ? 0 : selectedIndex, false);
    });
</script>
</body>
</html>

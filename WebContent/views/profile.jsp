<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Profile - My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f7fafc;
            color: #2d3748;
        }

        /* Top Navigation Bar */
        .navbar {
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #fff;
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.12);
        }

        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 1.15rem;
            font-weight: 700;
            text-decoration: none;
            color: #fff;
        }

        .navbar-brand .logo-icon {
            width: 34px;
            height: 34px;
            background: rgba(255,255,255,0.2);
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1rem;
            font-weight: 700;
        }

        .navbar-menu {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .nav-link {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            font-size: 0.88rem;
            padding: 6px 12px;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .nav-link:hover, .nav-link.active {
            color: #fff;
            background: rgba(255,255,255,0.15);
        }

        .btn-logout {
            background: #dc2626;
            color: #fff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
        }

        /* Layout Structure */
        .container {
            display: flex;
            max-width: 1350px;
            margin: 24px auto;
            gap: 24px;
            padding: 0 20px;
        }

        /* Left Sidebar */
        .sidebar {
            width: 300px;
            flex-shrink: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        /* Profile Photo Silhouette Placeholder */
        .avatar-container {
            width: 140px;
            height: 140px;
            background: #ffffff;
            border-radius: 50%;
            border: 3px solid #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            margin-bottom: 20px;
            overflow: hidden;
        }

        .avatar-silhouette {
            width: 85px;
            height: 85px;
            fill: #94a3b8;
        }

        /* Profile Completion Card */
        .completion-card {
            width: 100%;
            background: #22c55e;
            border-radius: 8px;
            overflow: hidden;
            margin-bottom: 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }

        .completion-header {
            padding: 10px 16px;
            font-size: 0.95rem;
            font-weight: 700;
            color: #ffffff;
        }

        .completion-body {
            background: #ffffff;
            padding: 16px;
            color: #333;
        }

        .progress-bar-bg {
            height: 12px;
            background: #e2e8f0;
            border-radius: 6px;
            overflow: hidden;
            margin-bottom: 8px;
        }

        .progress-bar-fill {
            height: 100%;
            background: #16a34a;
            border-radius: 6px;
            transition: width 0.4s ease;
        }

        .completion-text {
            font-size: 0.85rem;
            color: #475569;
            text-align: right;
            font-weight: 700;
        }

        /* Details Card */
        .details-card {
            width: 100%;
            background: #3b82f6;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.06);
        }

        .details-header {
            padding: 10px 16px;
            font-size: 0.95rem;
            font-weight: 700;
            color: #ffffff;
        }

        .details-body {
            background: #ffffff;
            padding: 16px;
        }

        .detail-item {
            margin-bottom: 12px;
        }

        .detail-item:last-child {
            margin-bottom: 0;
        }

        .detail-label {
            font-size: 0.75rem;
            color: #64748b;
            text-transform: uppercase;
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .detail-value {
            font-size: 0.88rem;
            font-weight: 600;
            color: #1e293b;
            margin-top: 2px;
        }

        /* Main Content */
        .main-content {
            flex-grow: 1;
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            padding: 24px;
            border: 1px solid #e2e8f0;
        }

        /* Tabs Navigation Bar - Exactly 5 Sections */
        .tabs-header {
            display: flex;
            border-bottom: 2px solid #e2e8f0;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 4px;
        }

        .tab-btn {
            background: none;
            border: none;
            padding: 12px 18px;
            font-size: 0.92rem;
            font-weight: 600;
            color: #64748b;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            border-bottom: 3px solid transparent;
            margin-bottom: -2px;
            transition: all 0.2s;
        }

        .tab-btn:hover {
            color: #1e3a8a;
            background: #f8fafc;
        }

        .tab-btn.active {
            color: #1e3a8a;
            border-bottom: 3px solid #2563eb;
            font-weight: 700;
        }

        /* Form Controls */
        .form-grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px 20px;
            margin-bottom: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-label {
            font-size: 0.85rem;
            font-weight: 600;
            color: #334155;
            margin-bottom: 6px;
        }

        .form-input, .form-select, .form-textarea {
            padding: 9px 12px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 0.9rem;
            outline: none;
            transition: border-color 0.2s;
            background-color: #ffffff;
        }

        .form-input:focus, .form-select:focus, .form-textarea:focus {
            border-color: #2563eb;
        }

        .form-input[readonly] {
            background-color: #f1f5f9;
            color: #64748b;
            cursor: not-allowed;
        }

        .form-hint {
            font-size: 0.78rem;
            color: #64748b;
            margin-top: 4px;
        }

        .form-section-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: #1e3a8a;
            margin: 28px 0 16px 0;
            padding-bottom: 8px;
            border-bottom: 1px solid #e2e8f0;
        }

        .sub-badge {
            font-size: 0.75rem;
            font-weight: 600;
            background: #e2e8f0;
            color: #475569;
            padding: 2px 8px;
            border-radius: 4px;
            margin-left: 8px;
            text-transform: uppercase;
        }

        /* Buttons */
        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: 24px;
            padding-top: 16px;
            border-top: 1px solid #f1f5f9;
        }

        .btn-save {
            background: #2563eb;
            color: #ffffff;
            border: none;
            padding: 9px 22px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }

        .btn-save:hover {
            background: #1d4ed8;
        }

        .btn-edit {
            background: #ffffff;
            color: #334155;
            border: 1px solid #cbd5e1;
            padding: 9px 20px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-edit:hover {
            background: #f8fafc;
        }

        .btn-next {
            background: #f1f5f9;
            color: #1e3a8a;
            border: 1px solid #cbd5e1;
            padding: 9px 22px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }

        .btn-next:hover {
            background: #e2e8f0;
        }

        .btn-danger-sm {
            background: #dc2626;
            color: #ffffff;
            border: none;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.78rem;
            cursor: pointer;
            font-weight: 600;
        }

        /* Table styles */
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 14px;
            margin-bottom: 24px;
            font-size: 0.88rem;
        }

        .data-table th, .data-table td {
            border: 1px solid #e2e8f0;
            padding: 10px 14px;
            text-align: left;
        }

        .data-table th {
            background: #f8fafc;
            color: #334155;
            font-weight: 600;
        }

        .data-table tr:hover {
            background: #f8fafc;
        }

        .alert-success {
            background: #def7ec;
            color: #03543f;
            border: 1px solid #84e1bc;
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fca5a5;
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }

        .eng-section-box {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 22px;
            margin-bottom: 30px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.04);
        }

        .badge-optional {
            background: #e2e8f0;
            color: #475569;
            font-size: 0.72rem;
            padding: 3px 8px;
            border-radius: 12px;
            font-weight: 600;
            margin-left: 8px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: inline-block;
            vertical-align: middle;
        }

        .badge-score-pct {
            background: #e0f2fe;
            color: #0369a1;
            padding: 4px 8px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.8rem;
            display: inline-block;
        }

        .badge-score-cgpa {
            background: #fef3c7;
            color: #92400e;
            padding: 4px 8px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.8rem;
            display: inline-block;
        }

        .btn-edit-sm {
            background: #2563eb;
            color: #ffffff;
            border: none;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.78rem;
            cursor: pointer;
            font-weight: 600;
            margin-right: 6px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-edit-sm:hover {
            background: #1d4ed8;
        }

        @media (max-width: 900px) {
            .container {
                flex-direction: column;
            }
            .sidebar {
                width: 100%;
            }
            .form-grid-2 {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">My Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link active">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <span style="font-size: 0.88rem; color: #cbd5e0; margin-left: 6px;">Hi, ${user.firstName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container">

        <!-- Left Sidebar -->
        <aside class="sidebar">
            <!-- Silhouette Avatar Placeholder - Photo upload removed per instructions -->
            <div class="avatar-container">
                <svg class="avatar-silhouette" viewBox="0 0 24 24">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
            </div>

            <!-- Profile Completion Card -->
            <div class="completion-card">
                <div class="completion-header">Profile Completion</div>
                <div class="completion-body">
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: ${profile.profileCompletion != null ? profile.profileCompletion : 0}%;"></div>
                    </div>
                    <div class="completion-text">${profile.profileCompletion != null ? profile.profileCompletion : 0}%</div>
                </div>
            </div>

            <!-- Details Card -->
            <div class="details-card">
                <div class="details-header">Details</div>
                <div class="details-body">
                    <div class="detail-item">
                        <div class="detail-label">Account Created Date</div>
                        <div class="detail-value">
                            <fmt:timeZone value="Asia/Kolkata">
                                <fmt:formatDate value="${user.createdAt}" pattern="dd-MM-yyyy hh:mm:ss a" /> IST
                            </fmt:timeZone>
                        </div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Previous Login Date</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${not empty profile.previousLogin}">
                                    <fmt:timeZone value="Asia/Kolkata">
                                        <fmt:formatDate value="${profile.previousLogin}" pattern="dd-MM-yyyy hh:mm:ss a" /> IST
                                    </fmt:timeZone>
                                </c:when>
                                <c:otherwise>First Login Session</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Current Session</div>
                        <div class="detail-value">Active Online</div>
                    </div>
                </div>
            </div>
        </aside>

        <!-- Main Content Area -->
        <main class="main-content">

            <!-- Success notification -->
            <c:if test="${param.success == '1'}">
                <div class="alert-success">
                    ✓ Profile section details updated and saved successfully!
                </div>
            </c:if>
            <c:if test="${param.deleted == '1'}">
                <div class="alert-success">
                    ✓ Record removed successfully!
                </div>
            </c:if>
            <c:if test="${param.error == 'missingMarks'}">
                <div class="alert-error" role="alert">Please provide Year and Score for the engineering performance entry.</div>
            </c:if>
            <c:if test="${param.error == 'invalidScore'}">
                <div class="alert-error" role="alert">Please enter a valid numeric score (e.g. 78.50 or 8.5).</div>
            </c:if>
            <c:if test="${param.error == 'cgpaRange'}">
                <div class="alert-error" role="alert">CGPA score must be between 0.00 and 10.00.</div>
            </c:if>
            <c:if test="${param.error == 'pctRange'}">
                <div class="alert-error" role="alert">Percentage score must be between 0.00 and 100.00.</div>
            </c:if>
            <c:if test="${param.error == 'futureDob'}">
                <div class="alert-error" role="alert">Date of birth cannot be in the future.</div>
            </c:if>
            <c:if test="${param.error == 'invalidDob'}">
                <div class="alert-error" role="alert">Please enter a valid date of birth.</div>
            </c:if>

            <!-- Tabs Navigation Bar - Exactly 5 Requested Sections -->
            <div class="tabs-header">
                <a href="${pageContext.request.contextPath}/profile?tab=personal" class="tab-btn ${activeTab == 'personal' ? 'active' : ''}">1. Personal Information</a>
                <a href="${pageContext.request.contextPath}/profile?tab=education" class="tab-btn ${activeTab == 'education' ? 'active' : ''}">2. Education</a>
                <a href="${pageContext.request.contextPath}/profile?tab=certifications" class="tab-btn ${activeTab == 'certifications' ? 'active' : ''}">3. Certification</a>
                <a href="${pageContext.request.contextPath}/profile?tab=projects" class="tab-btn ${activeTab == 'projects' ? 'active' : ''}">4. Projects</a>
                <a href="${pageContext.request.contextPath}/profile?tab=activities" class="tab-btn ${activeTab == 'activities' ? 'active' : ''}">5. Additional Activities</a>
            </div>

            <!-- =========================================================
                 SECTION 1: PERSONAL INFORMATION
                 (Includes Personal Details + Skills + Achievements)
                 ========================================================= -->
            <c:if test="${activeTab == 'personal'}">
                <!-- Part A: Personal Details -->
                <h3 class="form-section-title" style="margin-top:0;">Personal Details</h3>
                <form action="${pageContext.request.contextPath}/personal-details" method="post">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">First Name *</label>
                            <input type="text" name="firstName" class="form-input" value="${user.firstName}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Last Name *</label>
                            <input type="text" name="lastName" class="form-input" value="${user.lastName}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Gender</label>
                            <select name="gender" class="form-select">
                                <option value="" disabled ${empty profile.gender ? 'selected' : ''}>Select Gender</option>
                                <option value="Male" ${profile.gender == 'Male' ? 'selected' : ''}>Male</option>
                                <option value="Female" ${profile.gender == 'Female' ? 'selected' : ''}>Female</option>
                                <option value="Other" ${profile.gender == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Date of Birth</label>
                            <input type="date" name="dob" class="form-input" value="${profile.dateOfBirth}" max="${maxDateOfBirth}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Mobile (10 Digits) *</label>
                            <input type="tel" name="mobile" class="form-input" value="${user.mobile}" pattern="[0-9]{10}" maxlength="10" placeholder="10-digit mobile" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Email Address</label>
                            <input type="email" name="email" class="form-input" value="${user.email}" readonly>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Address</label>
                            <input type="text" name="address" class="form-input" placeholder="Current address" value="${profile.address}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">City</label>
                            <input type="text" name="city" class="form-input" placeholder="Pune" value="${user.city}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">State</label>
                            <input type="text" name="state" class="form-input" placeholder="Maharashtra" value="${profile.state}">
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">Save Personal Details</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">Reload</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=education" class="btn-next">Next: Education &rarr;</a>
                    </div>
                </form>

                <!-- Part B: Skills (Optional) -->
                <h3 class="form-section-title">Technical &amp; Soft Skills <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/skills" method="post">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Technical Skills</label>
                            <input type="text" id="technicalInput" name="technicalSkills" class="form-input" placeholder="e.g. Java, Python, SQL, HTML, CSS, JavaScript"
                                   value="<c:forEach items='${technicalSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">Separate multiple skills with commas</span>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Soft Skills</label>
                            <input type="text" id="softInput" name="softSkills" class="form-input" placeholder="e.g. Communication, Problem Solving, Teamwork"
                                   value="<c:forEach items='${softSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">e.g. Time Management, Adaptability</span>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Domain Proficiencies</label>
                            <input type="text" id="domainInput" name="domainSkills" class="form-input" placeholder="e.g. Cloud Computing, Full Stack Web, Machine Learning"
                                   value="<c:forEach items='${domainSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">Curriculum domain areas</span>
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">Save Skills</button>
                    </div>
                </form>

                <!-- Part C: Achievements (Optional) -->
                <h3 class="form-section-title">Achievements &amp; Awards <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/achievements" method="post">
                    <input type="hidden" name="action" value="save">

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Has Certification?</label>
                            <select name="hasCertification" class="form-select">
                                <option value="Yes" selected>Yes</option>
                                <option value="No">No</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Certification Title</label>
                            <input type="text" name="certName" class="form-input" placeholder="Certification Title, if applicable">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Technology</label>
                            <input type="text" name="certTech" class="form-input" placeholder="Technology / Domain">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Name</label>
                            <input type="text" name="projectName" class="form-input" placeholder="Project name, if any">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Details</label>
                            <input type="text" name="projectDetails" class="form-input" placeholder="Brief project summary">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Extra Curricular Activities</label>
                            <input type="text" name="extraActivities" class="form-input" placeholder="Activities / Clubs">
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">Save Achievement</button>
                    </div>
                </form>

                <c:if test="${not empty achievementList}">
                    <h4 style="margin-top: 24px; color: #1e293b; font-size: 0.95rem; font-weight: 700;">Saved Achievements Directory</h4>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Title</th>
                                <th>Details</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${achievementList}" var="ach">
                                <tr>
                                    <td><strong>${ach.achievementType}</strong></td>
                                    <td>${ach.title}</td>
                                    <td>${ach.technology != null ? ach.technology : ach.description}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/achievements" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="achievementId" value="${ach.achievementId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this achievement entry?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:if>

            <!-- =========================================================
                 SECTION 2: EDUCATION DETAILS
                 ========================================================= -->
            <c:if test="${activeTab == 'education'}">

                <!-- Part A: Engineering Year-wise Marks (First Year, Second Year, Third Year, Final Year) - BEFORE 10th and 12th -->
                <div class="eng-section-box" id="engineeringSection">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; border-bottom:1px solid #edf2f7; padding-bottom:10px; flex-wrap:wrap; gap:8px;">
                        <div>
                            <h3 class="form-section-title" style="margin:0; display:inline-block;">Engineering Year-wise Performance</h3>
                            <span class="badge-optional">Optional</span>
                        </div>
                        <span style="font-size:0.8rem; color:#64748b;">Add your percentage or CGPA for each engineering year (FE, SE, TE, BE)</span>
                    </div>

                    <!-- Add / Edit Engineering Year Mark Form -->
                    <form action="${pageContext.request.contextPath}/engineering-marks" method="post" id="engMarksForm">
                        <input type="hidden" name="markId" id="engMarkId" value="" />
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label class="form-label">Engineering Year *</label>
                                <select name="yearLevel" id="engYearLevel" class="form-select" required>
                                    <option value="First Year">First Year (FE)</option>
                                    <option value="Second Year">Second Year (SE)</option>
                                    <option value="Third Year">Third Year (TE)</option>
                                    <option value="Final Year">Final Year (BE / B.Tech)</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Evaluation Type *</label>
                                <select name="scoreType" id="engScoreType" class="form-select" onchange="updateScoreConstraints()" required>
                                    <option value="Percentage">Percentage (%)</option>
                                    <option value="CGPA">CGPA (10 Point Scale)</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" id="engScoreLabel">Score Value (0.00 - 100.00 %) *</label>
                                <input type="number" step="0.01" min="0" max="100" name="scoreValue" id="engScoreValue" class="form-input" placeholder="e.g. 82.50" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Passing Year</label>
                                <input type="number" min="1950" max="2035" name="passingYear" id="engPassingYear" class="form-input" placeholder="e.g. 2023">
                            </div>

                            <div class="form-group" style="grid-column: 1 / -1;">
                                <label class="form-label">College / Institute Name (Optional)</label>
                                <input type="text" name="collegeName" id="engCollegeName" class="form-input" placeholder="e.g. College of Engineering, Pune" maxlength="200">
                            </div>
                        </div>

                        <div class="btn-group" style="margin-top:14px; margin-bottom: 8px;">
                            <button type="submit" class="btn-save" id="engSubmitBtn">Save Year Score</button>
                            <button type="button" class="btn-edit" id="engCancelBtn" style="display:none;" onclick="cancelEngEdit()">Cancel Edit</button>
                        </div>
                    </form>

                    <!-- Tabular Display of Added Engineering Year Marks -->
                    <div style="margin-top:22px;">
                        <h4 style="font-size:0.95rem; font-weight:700; color:#1e293b; margin-bottom:10px;">Recorded Engineering Scores</h4>
                        <c:choose>
                            <c:when test="${not empty engineeringMarksList}">
                                <div style="overflow-x:auto;">
                                    <table class="data-table" style="margin-top:6px; margin-bottom:10px;">
                                        <thead>
                                            <tr>
                                                <th>Engineering Year</th>
                                                <th>Evaluation Type</th>
                                                <th>Score / Result</th>
                                                <th>Passing Year</th>
                                                <th>College / Institute</th>
                                                <th style="width:140px; text-align:center;">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${engineeringMarksList}" var="em">
                                                <tr>
                                                    <td><strong>${em.yearLevel}</strong></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${em.scoreType == 'CGPA'}">
                                                                <span class="badge-score-cgpa">CGPA</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge-score-pct">Percentage</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <strong style="color:#0f172a; font-size:0.95rem;">${em.formattedScore}</strong>
                                                    </td>
                                                    <td>${em.passingYear > 0 ? em.passingYear : '-'}</td>
                                                    <td>${not empty em.collegeName ? em.collegeName : '-'}</td>
                                                    <td style="text-align:center; white-space:nowrap;">
                                                        <button type="button" class="btn-edit-sm"
                                                                onclick="editEngMark(${em.markId}, '${em.yearLevel}', '${em.scoreType}', ${em.scoreValue}, ${em.passingYear}, '${em.collegeName}')">
                                                            Edit
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/engineering-marks?action=delete&id=${em.markId}"
                                                           class="btn-danger-sm"
                                                           style="text-decoration:none; display:inline-block;"
                                                           onclick="return confirm('Are you sure you want to delete ${em.yearLevel} record?');">
                                                            Delete
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="padding:14px; background:#f8fafc; border:1px dashed #cbd5e1; border-radius:6px; color:#64748b; font-size:0.86rem; text-align:center;">
                                    No engineering year scores added yet. If you are pursuing or have completed engineering, use the form above to optionally add scores for First Year, Second Year, Third Year, or Final Year.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Part B: Standard Academic History (SSC, HSC, Diploma, Graduation, Master) -->
                <form action="${pageContext.request.contextPath}/education" method="post">
                    <!-- SSC/10th -->
                    <h3 class="form-section-title" style="margin-top:0;">SSC / 10th Standard</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">School Name</label>
                            <input type="text" name="schoolName_SSC" class="form-input" placeholder="School Name" value="${educationMap['SSC'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board</label>
                            <input type="text" name="board_SSC" class="form-input" placeholder="e.g. State Board / CBSE" value="${educationMap['SSC'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="number" min="1950" max="2035" name="year_SSC" class="form-input" placeholder="e.g. 2020" value="${educationMap['SSC'].passingYear > 0 ? educationMap['SSC'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage (0 - 100)</label>
                            <input type="number" step="0.01" min="0" max="100" name="percentage_SSC" class="form-input" placeholder="e.g. 85.5" value="${educationMap['SSC'].percentage > 0 ? educationMap['SSC'].percentage : ''}">
                        </div>
                    </div>

                    <!-- HSC/12th -->
                    <h3 class="form-section-title">HSC / 12th Standard</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Junior College / School Name</label>
                            <input type="text" name="schoolName_HSC" class="form-input" placeholder="College Name" value="${educationMap['HSC'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board</label>
                            <input type="text" name="board_HSC" class="form-input" placeholder="e.g. State Board / CBSE" value="${educationMap['HSC'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_HSC" class="form-select">
                                <option value="" disabled ${empty educationMap['HSC'].stream ? 'selected' : ''}>Select Stream</option>
                                <option value="Science" ${educationMap['HSC'].stream == 'Science' ? 'selected' : ''}>Science</option>
                                <option value="Commerce" ${educationMap['HSC'].stream == 'Commerce' ? 'selected' : ''}>Commerce</option>
                                <option value="Arts" ${educationMap['HSC'].stream == 'Arts' ? 'selected' : ''}>Arts</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="number" min="1950" max="2035" name="year_HSC" class="form-input" placeholder="e.g. 2022" value="${educationMap['HSC'].passingYear > 0 ? educationMap['HSC'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage (0 - 100)</label>
                            <input type="number" step="0.01" min="0" max="100" name="percentage_HSC" class="form-input" placeholder="e.g. 80.0" value="${educationMap['HSC'].percentage > 0 ? educationMap['HSC'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Diploma -->
                    <h3 class="form-section-title">Diploma (If applicable)</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Polytechnic / Institute Name</label>
                            <input type="text" name="schoolName_Diploma" class="form-input" placeholder="Institute Name" value="${educationMap['Diploma'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board / MSBTE</label>
                            <input type="text" name="board_Diploma" class="form-input" placeholder="MSBTE / Board" value="${educationMap['Diploma'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream / Department</label>
                            <input type="text" name="stream_Diploma" class="form-input" placeholder="e.g. Computer Engineering" value="${educationMap['Diploma'].stream}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="number" min="1950" max="2035" name="year_Diploma" class="form-input" placeholder="Passing Year" value="${educationMap['Diploma'].passingYear > 0 ? educationMap['Diploma'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage (0 - 100)</label>
                            <input type="number" step="0.01" min="0" max="100" name="percentage_Diploma" class="form-input" placeholder="Percentage" value="${educationMap['Diploma'].percentage > 0 ? educationMap['Diploma'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Graduation -->
                    <h3 class="form-section-title">Graduation (Undergraduate)</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">College Name</label>
                            <input type="text" name="schoolName_Graduation" class="form-input" placeholder="Engineering / Degree College" value="${educationMap['Graduation'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">University</label>
                            <input type="text" name="board_Graduation" class="form-input" placeholder="Affiliated University" value="${educationMap['Graduation'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_Graduation" class="form-select">
                                <option value="" disabled ${empty educationMap['Graduation'].stream ? 'selected' : ''}>Select Graduation Degree</option>
                                <option value="B.Tech / B.E." ${educationMap['Graduation'].stream == 'B.Tech / B.E.' ? 'selected' : ''}>B.Tech / B.E.</option>
                                <option value="BCA" ${educationMap['Graduation'].stream == 'BCA' ? 'selected' : ''}>BCA</option>
                                <option value="B.Sc CS/IT" ${educationMap['Graduation'].stream == 'B.Sc CS/IT' ? 'selected' : ''}>B.Sc CS/IT</option>
                                <option value="Other" ${educationMap['Graduation'].stream == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="number" min="1950" max="2035" name="year_Graduation" class="form-input" placeholder="e.g. 2025" value="${educationMap['Graduation'].passingYear > 0 ? educationMap['Graduation'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage / Aggregate (0 - 100)</label>
                            <input type="number" step="0.01" min="0" max="100" name="percentage_Graduation" class="form-input" placeholder="Percentage or equivalent CGPA" value="${educationMap['Graduation'].percentage > 0 ? educationMap['Graduation'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Master -->
                    <h3 class="form-section-title">Master (Postgraduate - If applicable)</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">College Name</label>
                            <input type="text" name="schoolName_Master" class="form-input" placeholder="Postgraduate College" value="${educationMap['Master'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">University</label>
                            <input type="text" name="board_Master" class="form-input" placeholder="Affiliated University" value="${educationMap['Master'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_Master" class="form-select">
                                <option value="" disabled ${empty educationMap['Master'].stream ? 'selected' : ''}>Select Master Degree</option>
                                <option value="M.Tech / M.E." ${educationMap['Master'].stream == 'M.Tech / M.E.' ? 'selected' : ''}>M.Tech / M.E.</option>
                                <option value="MCA" ${educationMap['Master'].stream == 'MCA' ? 'selected' : ''}>MCA</option>
                                <option value="M.Sc CS/IT" ${educationMap['Master'].stream == 'M.Sc CS/IT' ? 'selected' : ''}>M.Sc CS/IT</option>
                                <option value="Other" ${educationMap['Master'].stream == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="number" min="1950" max="2035" name="year_Master" class="form-input" placeholder="Passing Year" value="${educationMap['Master'].passingYear > 0 ? educationMap['Master'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage (0 - 100)</label>
                            <input type="number" step="0.01" min="0" max="100" name="percentage_Master" class="form-input" placeholder="Percentage" value="${educationMap['Master'].percentage > 0 ? educationMap['Master'].percentage : ''}">
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">Save Education Details</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">Reload</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=certifications" class="btn-next">Next: Certifications &rarr;</a>
                    </div>
                </form>
            </c:if>

            <!-- =========================================================
                 SECTION 3: CERTIFICATION (Optional)
                 ========================================================= -->
            <c:if test="${activeTab == 'certifications'}">
                <h3 class="form-section-title" style="margin-top:0;">Certifications <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/certifications" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Certification Name *</label>
                            <input type="text" name="certName" class="form-input" placeholder="e.g. Oracle Certified Java Associate" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Issuing Provider / Organization</label>
                            <input type="text" name="provider" class="form-input" placeholder="e.g. Oracle / Coursera / AWS">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Technology / Domain</label>
                            <input type="text" name="technology" class="form-input" placeholder="e.g. Java, Cloud">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Certificate ID / License</label>
                            <input type="text" name="certIdNumber" class="form-input" placeholder="e.g. CERT-12345">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Issue Date</label>
                            <input type="date" name="issueDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Expiry Date (if applicable)</label>
                            <input type="date" name="expiryDate" class="form-input">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Credential Verification URL</label>
                            <input type="url" name="credentialUrl" class="form-input" placeholder="https://www.credly.com/...">
                        </div>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn-save">+ Add Certification</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=projects" class="btn-next">Next: Projects &rarr;</a>
                    </div>
                </form>

                <c:if test="${not empty certificationList}">
                    <h4 style="margin-top: 24px; color: #1e293b; font-size: 0.95rem; font-weight: 700;">Your Saved Certifications</h4>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Tech</th>
                                <th>Issue Date</th>
                                <th>Credential</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${certificationList}" var="c">
                                <tr>
                                    <td><strong>${c.certName}</strong></td>
                                    <td>${c.provider}</td>
                                    <td>${c.technology}</td>
                                    <td>${c.issueDate}</td>
                                    <td>
                                        <c:if test="${not empty c.credentialUrl}">
                                            <a href="${c.credentialUrl}" target="_blank" style="color:#2563eb;">View</a>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/certifications" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="certId" value="${c.certId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this certification?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:if>

            <!-- =========================================================
                 SECTION 4: PROJECTS (Optional)
                 ========================================================= -->
            <c:if test="${activeTab == 'projects'}">
                <h3 class="form-section-title" style="margin-top:0;">Projects Portfolio <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/projects" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Project Name *</label>
                            <input type="text" name="projectName" class="form-input" placeholder="e.g. Student Internship Portal" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Technologies Used</label>
                            <input type="text" name="technologiesUsed" class="form-input" placeholder="e.g. Java, JSP, Servlets, MySQL">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Your Role</label>
                            <input type="text" name="role" class="form-input" placeholder="e.g. Full Stack Developer">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Type</label>
                            <select name="projectType" class="form-select">
                                <option value="Academic">Academic</option>
                                <option value="Personal">Personal</option>
                                <option value="Professional">Professional</option>
                                <option value="Open Source">Open Source</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Size</label>
                            <input type="number" name="teamSize" class="form-input" value="1" min="1">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Start Date</label>
                            <input type="date" name="startDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">End Date</label>
                            <input type="date" name="endDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">GitHub URL</label>
                            <input type="url" name="githubUrl" class="form-input" placeholder="https://github.com/...">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Project Description</label>
                            <textarea name="description" class="form-textarea" rows="3" placeholder="Briefly summarize project features and achievements..."></textarea>
                        </div>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn-save">+ Add Project</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=activities" class="btn-next">Next: Additional Activities &rarr;</a>
                    </div>
                </form>

                <c:if test="${not empty projectList}">
                    <h4 style="margin-top: 24px; color: #1e293b; font-size: 0.95rem; font-weight: 700;">Your Saved Projects</h4>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Project Name</th>
                                <th>Tech Stack</th>
                                <th>Role</th>
                                <th>Type</th>
                                <th>Link</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${projectList}" var="p">
                                <tr>
                                    <td><strong>${p.projectName}</strong></td>
                                    <td>${p.technologiesUsed}</td>
                                    <td>${p.role}</td>
                                    <td>${p.projectType}</td>
                                    <td>
                                        <c:if test="${not empty p.githubUrl}">
                                            <a href="${p.githubUrl}" target="_blank" style="color:#2563eb;">GitHub</a>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/projects" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="projectId" value="${p.projectId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this project?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:if>

            <!-- =========================================================
                 SECTION 5: ADDITIONAL ACTIVITIES (Optional)
                 ========================================================= -->
            <c:if test="${activeTab == 'activities'}">
                <!-- Part A: Hackathons -->
                <h3 class="form-section-title" style="margin-top:0;">A. Hackathons <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/hackathons" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Hackathon Name *</label>
                            <input type="text" name="hackathonName" class="form-input" placeholder="e.g. Smart India Hackathon" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Organizer</label>
                            <input type="text" name="organizer" class="form-input" placeholder="e.g. AICTE / TechFest">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Event Date</label>
                            <input type="date" name="eventDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Position / Result</label>
                            <input type="text" name="positionResult" class="form-input" placeholder="e.g. Finalist / 1st Place">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Name</label>
                            <input type="text" name="teamName" class="form-input" placeholder="e.g. ByteCraft">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Description</label>
                            <input type="text" name="description" class="form-input" placeholder="Problem solved...">
                        </div>
                    </div>
                    <button type="submit" class="btn-save">+ Add Hackathon</button>
                </form>

                <c:if test="${not empty hackathonList}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Hackathon</th>
                                <th>Organizer</th>
                                <th>Date</th>
                                <th>Result</th>
                                <th>Team</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${hackathonList}" var="h">
                                <tr>
                                    <td><strong>${h.hackathonName}</strong></td>
                                    <td>${h.organizer}</td>
                                    <td>${h.eventDate}</td>
                                    <td>${h.positionResult}</td>
                                    <td>${h.teamName}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/hackathons" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="hackathonId" value="${h.hackathonId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this hackathon entry?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <!-- Part B: Inter-College Competitions -->
                <h3 class="form-section-title">B. Inter-College Project Competitions <span class="sub-badge">Optional</span></h3>
                <form action="${pageContext.request.contextPath}/competitions" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Competition Name *</label>
                            <input type="text" name="competitionName" class="form-input" placeholder="e.g. Impetus and Concepts 2026" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Host College / Organizer</label>
                            <input type="text" name="collegeOrganizer" class="form-input" placeholder="e.g. PICT Pune">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Event Date</label>
                            <input type="date" name="eventDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Exhibited</label>
                            <input type="text" name="projectName" class="form-input" placeholder="Project name">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Position / Result</label>
                            <input type="text" name="positionResult" class="form-input" placeholder="e.g. 2nd Prize">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Name</label>
                            <input type="text" name="teamName" class="form-input" placeholder="Team name">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Description</label>
                            <input type="text" name="description" class="form-input" placeholder="Highlights...">
                        </div>
                    </div>
                    <button type="submit" class="btn-save">+ Add Competition</button>
                </form>

                <c:if test="${not empty competitionList}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Competition</th>
                                <th>Organizer</th>
                                <th>Date</th>
                                <th>Project</th>
                                <th>Result</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${competitionList}" var="comp">
                                <tr>
                                    <td><strong>${comp.competitionName}</strong></td>
                                    <td>${comp.collegeOrganizer}</td>
                                    <td>${comp.eventDate}</td>
                                    <td>${comp.projectName}</td>
                                    <td>${comp.positionResult}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/competitions" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="competitionId" value="${comp.competitionId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this competition entry?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <div class="btn-group" style="margin-top: 32px;">
                    <a href="${pageContext.request.contextPath}/home" class="btn-save" style="text-decoration:none;">Go to Home &rarr;</a>
                </div>
            </c:if>

        </main>
    </div>

    <!-- Inline script for dynamic interactive operations -->
    <script>
        function editEngMark(id, year, type, val, passYear, college) {
            document.getElementById('engMarkId').value = id;
            document.getElementById('engYearLevel').value = year;
            document.getElementById('engScoreType').value = type;
            document.getElementById('engScoreValue').value = val;
            document.getElementById('engPassingYear').value = (passYear > 0 ? passYear : '');
            document.getElementById('engCollegeName').value = college || '';
            document.getElementById('engSubmitBtn').textContent = 'Update ' + year + ' Score';
            document.getElementById('engCancelBtn').style.display = 'inline-block';
            updateScoreConstraints();
            var sec = document.getElementById('engineeringSection');
            if (sec) sec.scrollIntoView({ behavior: 'smooth' });
        }

        function cancelEngEdit() {
            document.getElementById('engMarkId').value = '';
            document.getElementById('engYearLevel').value = 'First Year';
            document.getElementById('engScoreType').value = 'Percentage';
            document.getElementById('engScoreValue').value = '';
            document.getElementById('engPassingYear').value = '';
            document.getElementById('engCollegeName').value = '';
            document.getElementById('engSubmitBtn').textContent = 'Save Year Score';
            document.getElementById('engCancelBtn').style.display = 'none';
            updateScoreConstraints();
        }

        function updateScoreConstraints() {
            var typeEl = document.getElementById('engScoreType');
            var inp = document.getElementById('engScoreValue');
            var label = document.getElementById('engScoreLabel');
            if (!typeEl || !inp) return;
            var type = typeEl.value;
            if (type === 'CGPA') {
                inp.max = "10.00";
                inp.placeholder = "e.g. 8.75 (0 - 10 CGPA)";
                if (label) label.textContent = "CGPA Score (0.00 - 10.00) *";
            } else {
                inp.max = "100.00";
                inp.placeholder = "e.g. 82.50 (0 - 100 %)";
                if (label) label.textContent = "Percentage Score (0.00 - 100.00 %) *";
            }
        }
    </script>
</body>
</html>

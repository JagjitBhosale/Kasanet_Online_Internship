<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String loginSuccessParam = request.getParameter("loginSuccess");
    boolean isLoginSuccess = "1".equals(loginSuccessParam) || session.getAttribute("loginSuccessAlert") != null;
    if (session.getAttribute("loginSuccessAlert") != null) {
        session.removeAttribute("loginSuccessAlert");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Home - My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --primary: #1a237e;
            --primary-dark: #121858;
            --accent: #2b6cb0;
            --accent-light: #ebf8ff;
            --text-dark: #1e293b;
            --text-muted: #64748b;
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --border: #e2e8f0;
            --radius: 8px;
            --success: #16a34a;
            --font: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        body {
            font-family: var(--font);
            background-color: var(--bg);
            color: var(--text-dark);
            line-height: 1.5;
        }

        /* Top Navbar */
        .navbar {
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #ffffff;
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
            color: #ffffff;
        }

        .logo-icon {
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
            color: #ffffff;
            background: rgba(255,255,255,0.15);
        }

        .btn-logout {
            background: #dc2626;
            color: #ffffff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .btn-logout:hover {
            background: #b91c1c;
        }

        /* Layout Container */
        .container {
            max-width: 1250px;
            margin: 28px auto;
            padding: 0 20px;
        }

        /* Welcome Banner */
        .welcome-banner {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            border-radius: var(--radius);
            padding: 28px 32px;
            color: #ffffff;
            margin-bottom: 28px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
        }

        .welcome-text h1 {
            font-size: 1.65rem;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .welcome-text p {
            font-size: 0.95rem;
            color: #dbeafe;
        }

        .completion-badge {
            background: rgba(255,255,255,0.15);
            border: 1px solid rgba(255,255,255,0.25);
            padding: 14px 22px;
            border-radius: 8px;
            text-align: center;
        }

        .completion-badge .percent {
            font-size: 1.8rem;
            font-weight: 800;
            color: #ffffff;
            line-height: 1;
        }

        .completion-badge .label {
            font-size: 0.78rem;
            color: #e0e7ff;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-top: 4px;
        }

        /* Section Headings */
        .section-header {
            font-size: 1.15rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 16px;
        }

        /* Core 5 Modules Grid */
        .portal-modules-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
            gap: 22px;
            margin-bottom: 32px;
        }

        .module-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.2s ease;
        }

        .module-card:hover {
            border-color: var(--accent);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
        }

        .module-card-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            margin-bottom: 12px;
        }

        .module-badge {
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            color: var(--accent);
            background: var(--accent-light);
            padding: 4px 8px;
            border-radius: 4px;
        }

        .module-title {
            font-size: 1.2rem;
            font-weight: 700;
            color: #1e3a8a;
            margin-bottom: 8px;
        }

        .module-desc {
            font-size: 0.9rem;
            color: var(--text-muted);
            line-height: 1.5;
            margin-bottom: 18px;
        }

        .module-stats {
            background: #f8fafc;
            border: 1px solid var(--border);
            border-radius: 6px;
            padding: 10px 14px;
            font-size: 0.85rem;
            color: #334155;
            margin-bottom: 18px;
            font-weight: 500;
        }

        .btn-module {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            background: var(--accent);
            color: #ffffff;
            padding: 9px 18px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            text-decoration: none;
            transition: background 0.2s;
            border: none;
            cursor: pointer;
        }

        .btn-module:hover {
            background: #1d4ed8;
        }

        .subject-dropdown-form {
            display: flex;
            gap: 8px;
            margin-top: 4px;
        }

        .subject-dropdown-select {
            flex: 1;
            padding: 8px 10px;
            border: 1px solid var(--border);
            border-radius: 6px;
            font-size: 0.88rem;
            background: #ffffff;
            outline: none;
        }

        /* Overview Stats Row */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 32px;
        }

        .stat-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 18px 20px;
            display: flex;
            flex-direction: column;
        }

        .stat-card .stat-val {
            font-size: 1.6rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .stat-card .stat-lbl {
            font-size: 0.82rem;
            color: var(--text-muted);
            font-weight: 500;
        }

        /* Account Details Card */
        .info-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
        }

        .info-item {
            padding: 12px 14px;
            background: #f8fafc;
            border-radius: 6px;
            border: 1px solid var(--border);
        }

        .info-label {
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            color: #94a3b8;
            letter-spacing: 0.5px;
        }

        .info-value {
            font-size: 0.95rem;
            font-weight: 600;
            color: #1e293b;
            margin-top: 4px;
        }

        /* Modal Popup */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(4px);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 16px;
        }

        .modal-card {
            background: #ffffff;
            border-radius: 10px;
            max-width: 440px;
            width: 100%;
            padding: 28px 24px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
            text-align: center;
            border-top: 4px solid var(--success);
        }

        .modal-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 10px;
        }

        .modal-body {
            font-size: 0.92rem;
            color: var(--text-muted);
            margin-bottom: 22px;
        }

        .btn-confirm {
            background-color: var(--accent);
            color: #ffffff;
            border: none;
            padding: 9px 24px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            cursor: pointer;
        }
    </style>
</head>
<body>

    <!-- Top Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/home" class="nav-link active">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">My Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
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

        <!-- Welcome Banner -->
        <div class="welcome-banner">
            <div class="welcome-text">
                <h1>Welcome back, ${user.firstName} ${user.lastName}!</h1>
                <p>Track your internship tasks, learning progress, certifications, and portfolio details all in one place.</p>
            </div>
            <div class="completion-badge">
                <div class="percent">${stats.profileCompletion}%</div>
                <div class="label">Profile Completion</div>
            </div>
        </div>

        <!-- 5 Core Portal Sections -->
        <div class="section-header">Internship Modules</div>
        <div class="portal-modules-grid">

            <!-- Section 1: Profile -->
            <div class="module-card">
                <div>
                    <div class="module-card-header">
                        <span class="module-badge">Section 1</span>
                    </div>
                    <h3 class="module-title">Student Profile</h3>
                    <p class="module-desc">Maintain your Personal Information, Education records, Certifications, Projects, and Additional Activities.</p>
                    <div class="module-stats">Completion Status: <strong>${stats.profileCompletion}%</strong></div>
                </div>
                <a href="${pageContext.request.contextPath}/profile" class="btn-module">Manage Profile &rarr;</a>
            </div>

            <!-- Section 2: Assignments -->
            <div class="module-card">
                <div>
                    <div class="module-card-header">
                        <span class="module-badge">Section 2</span>
                    </div>
                    <h3 class="module-title">Assignments</h3>
                    <p class="module-desc">View practical internship assignments, due dates, and instructions provided by your admin.</p>
                    <div class="module-stats">Available assignments: <strong>${stats.totalAssignments}</strong></div>
                </div>
                <a href="${pageContext.request.contextPath}/assignments" class="btn-module">View Assignments &rarr;</a>
            </div>

            <!-- Section 3: Lectures -->
            <div class="module-card">
                <div>
                    <div class="module-card-header">
                        <span class="module-badge">Section 3</span>
                    </div>
                    <h3 class="module-title">Lectures</h3>
                    <p class="module-desc">Watch recorded technical tutorials, architecture walkthroughs, and mentor code sessions.</p>
                    <div class="module-stats">Completed: <strong>${stats.completedLectures} / ${stats.totalLectures}</strong></div>
                </div>
                <a href="${pageContext.request.contextPath}/lectures" class="btn-module">Access Lectures &rarr;</a>
            </div>

            <!-- Section 4: Question Bank -->
            <div class="module-card">
                <div>
                    <div class="module-card-header">
                        <span class="module-badge">Section 4</span>
                    </div>
                    <h3 class="module-title">Question Bank</h3>
                    <p class="module-desc">Review technical and interview questions categorized by curriculum subject.</p>
                    <div class="subject-dropdown-form" style="margin-bottom: 14px;">
                        <select id="quickSubjectSelect" class="subject-dropdown-select" onchange="navigateToSubject(this.value)">
                            <option value="">-- Select Subject --</option>
                            <option value="java">Java Full Stack</option>
                            <option value="python">Python &amp; ML</option>
                            <option value="web">Web Development</option>
                            <option value="db">DBMS (MySQL)</option>
                            <option value="cloud">Cloud &amp; DevOps</option>
                        </select>
                        <button type="button" class="btn-module" style="padding: 7px 14px;" onclick="goToSelectedSubject()">Go</button>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/question-bank" class="btn-module" style="background:#2563eb;">Open Question Bank &rarr;</a>
            </div>

            <!-- Section 5: Resume Analysis -->
            <div class="module-card">
                <div>
                    <div class="module-card-header">
                        <span class="module-badge">Section 5</span>
                    </div>
                    <h3 class="module-title">Resume Analysis</h3>
                    <p class="module-desc">Submit your resume for formatting feedback, keyword density evaluation, and ATS score readiness.</p>
                    <div class="module-stats">Status: <strong>Under Active Setup</strong></div>
                </div>
                <a href="${pageContext.request.contextPath}/resume-analysis" class="btn-module">Open Resume Analysis &rarr;</a>
            </div>

        </div>

        <!-- Student Account Information Summary -->
        <div class="section-header">Student Account Details</div>
        <div class="info-card">
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Full Name</div>
                    <div class="info-value">${user.firstName} ${user.lastName}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Email Address</div>
                    <div class="info-value">${user.email}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Mobile Number</div>
                    <div class="info-value">${user.mobile != null ? user.mobile : '-'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Stream</div>
                    <div class="info-value">${user.stream != null ? user.stream : '-'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">College Name</div>
                    <div class="info-value">${user.collegeName != null && not empty user.collegeName ? user.collegeName : '-'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">City</div>
                    <div class="info-value">${user.city != null && not empty user.city ? user.city : '-'}</div>
                </div>
            </div>
        </div>

    </div>

    <!-- Login Success Popup Modal -->
    <% if (isLoginSuccess) { %>
    <div class="modal-overlay" id="loginSuccessModal">
        <div class="modal-card">
            <h3 class="modal-title">Login successful</h3>
            <p class="modal-body">Welcome back, ${user.firstName}! You have logged in successfully to your student portal.</p>
            <button type="button" class="btn-confirm" onclick="closeLoginModal()">Continue</button>
        </div>
    </div>
    <script>
        function closeLoginModal() {
            var m = document.getElementById('loginSuccessModal');
            if (m) m.style.display = 'none';
            if (window.history && window.history.replaceState) {
                var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history.replaceState({}, document.title, cleanUrl);
            }
        }
    </script>
    <% } %>

    <script>
        function navigateToSubject(subjectVal) {
            if (subjectVal && subjectVal.trim().length > 0) {
                window.location.href = "${pageContext.request.contextPath}/question-bank?subject=" + encodeURIComponent(subjectVal);
            }
        }

        function goToSelectedSubject() {
            var val = document.getElementById("quickSubjectSelect").value;
            if (val) {
                navigateToSubject(val);
            } else {
                window.location.href = "${pageContext.request.contextPath}/question-bank";
            }
        }
    </script>

</body>
</html>

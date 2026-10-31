<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Question Bank - My Internship Technologies Portal</title>
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
            gap: 16px;
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

        .container {
            max-width: 1200px;
            margin: 28px auto;
            padding: 0 20px;
        }

        /* Page Header */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 16px;
        }

        .header-title h2 {
            font-size: 1.6rem;
            font-weight: 700;
            color: #0f172a;
        }

        .header-title p {
            font-size: 0.92rem;
            color: var(--text-muted);
        }

        .subject-selector {
            display: flex;
            align-items: center;
            gap: 10px;
            background: var(--card-bg);
            padding: 8px 14px;
            border-radius: var(--radius);
            border: 1px solid var(--border);
        }

        .subject-selector label {
            font-size: 0.88rem;
            font-weight: 600;
            color: #334155;
        }

        .subject-select {
            padding: 8px 12px;
            border: 1px solid var(--border);
            border-radius: 6px;
            font-size: 0.88rem;
            outline: none;
            background-color: #ffffff;
            cursor: pointer;
        }

        /* Card layout */
        .card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 28px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            margin-bottom: 24px;
        }

        .subject-banner {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: #ffffff;
            padding: 24px;
            border-radius: var(--radius);
            margin-bottom: 24px;
        }

        .subject-banner h3 {
            font-size: 1.4rem;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .subject-banner p {
            font-size: 0.92rem;
            color: #e0e7ff;
        }

        /* Modules list */
        .modules-grid {
            display: grid;
            gap: 14px;
            margin-top: 16px;
        }

        .module-item {
            background: #f8fafc;
            border: 1px solid var(--border);
            border-radius: 6px;
            padding: 16px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .module-name {
            font-weight: 600;
            color: #1e293b;
            font-size: 0.95rem;
        }

        .module-status {
            font-size: 0.78rem;
            font-weight: 600;
            color: var(--accent);
            background: var(--accent-light);
            padding: 4px 10px;
            border-radius: 4px;
        }

        /* Subjects Catalog Grid (Landing mode) */
        .catalog-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
        }

        .catalog-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 24px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.2s ease;
        }

        .catalog-card:hover {
            border-color: var(--accent);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
        }

        .catalog-title {
            font-size: 1.15rem;
            font-weight: 700;
            color: #1e3a8a;
            margin-bottom: 8px;
        }

        .catalog-desc {
            font-size: 0.88rem;
            color: var(--text-muted);
            line-height: 1.5;
            margin-bottom: 20px;
        }

        .btn-view {
            display: inline-block;
            background: var(--accent);
            color: #ffffff;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 0.88rem;
            font-weight: 600;
            text-decoration: none;
            text-align: center;
            transition: background 0.2s;
        }

        .btn-view:hover {
            background: #1d4ed8;
        }

        .breadcrumb {
            font-size: 0.85rem;
            color: var(--text-muted);
            margin-bottom: 16px;
        }

        .breadcrumb a {
            color: var(--accent);
            text-decoration: none;
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
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">My Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link active">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container">
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/home">Home</a> &rsaquo;
            <span>Question Bank</span>
            <c:if test="${not empty selectedSubject}">
                &rsaquo; <span>${subjectTitle}</span>
            </c:if>
        </div>

        <div class="page-header">
            <div class="header-title">
                <h2>Subject Question Bank</h2>
                <p>Prepare with domain-specific conceptual and practical questions.</p>
            </div>

            <!-- Subject Dropdown Selector -->
            <div class="subject-selector">
                <label for="subjectSelect">Select Subject:</label>
                <select id="subjectSelect" class="subject-select" onchange="switchSubject(this.value)">
                    <option value="" ${empty selectedSubject ? 'selected' : ''}>-- All Subjects --</option>
                    <option value="java" ${selectedSubject == 'java' ? 'selected' : ''}>Java Full Stack</option>
                    <option value="python" ${selectedSubject == 'python' ? 'selected' : ''}>Python &amp; Machine Learning</option>
                    <option value="web" ${selectedSubject == 'web' ? 'selected' : ''}>Web Development</option>
                    <option value="db" ${selectedSubject == 'db' ? 'selected' : ''}>Database Management (MySQL)</option>
                    <option value="cloud" ${selectedSubject == 'cloud' ? 'selected' : ''}>Cloud Computing &amp; DevOps</option>
                </select>
            </div>
        </div>

        <!-- Subject Detail View or Catalog -->
        <c:choose>
            <c:when test="${not empty selectedSubject}">
                <!-- Dedicated Subject View -->
                <div class="subject-banner">
                    <h3>${subjectTitle}</h3>
                    <p>${subjectDescription}</p>
                </div>

                <div class="card">
                    <h3 style="font-size: 1.15rem; font-weight: 700; color: #0f172a; margin-bottom: 8px;">Question Bank Modules</h3>
                    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 16px;">
                        The question modules for this subject track are organized below. Detailed question-answer sheets and sample tests will be published here.
                    </p>

                    <div class="modules-grid">
                        <c:forEach items="${modules}" var="m">
                            <div class="module-item">
                                <span class="module-name">${m}</span>
                                <span class="module-status">Topic Ready</span>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center;">
                        <a href="${pageContext.request.contextPath}/question-bank" style="font-size: 0.88rem; color: var(--accent); font-weight: 600;">&larr; Back to All Subjects</a>
                        <a href="${pageContext.request.contextPath}/home" style="font-size: 0.88rem; color: var(--text-muted); font-weight: 500;">Go to Home</a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <!-- Catalog Grid when no specific subject is selected -->
                <div class="catalog-grid">
                    <div class="catalog-card">
                        <div>
                            <h3 class="catalog-title">Java Full Stack</h3>
                            <p class="catalog-desc">Core Java, OOP principles, Collections, Multithreading, Servlets, JSP, JDBC, and relational database operations.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/question-bank?subject=java" class="btn-view">Open Java Bank &rarr;</a>
                    </div>

                    <div class="catalog-card">
                        <div>
                            <h3 class="catalog-title">Python &amp; Machine Learning</h3>
                            <p class="catalog-desc">Python fundamentals, data structures, NumPy mathematical computing, Pandas wrangling, and Scikit-Learn.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/question-bank?subject=python" class="btn-view">Open Python Bank &rarr;</a>
                    </div>

                    <div class="catalog-card">
                        <div>
                            <h3 class="catalog-title">Web Development</h3>
                            <p class="catalog-desc">HTML5 semantic markup, CSS3 responsive grid and flexbox, JavaScript ES6+, DOM manipulation, and asynchronous fetch.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/question-bank?subject=web" class="btn-view">Open Web Bank &rarr;</a>
                    </div>

                    <div class="catalog-card">
                        <div>
                            <h3 class="catalog-title">Database Management (MySQL)</h3>
                            <p class="catalog-desc">Relational models, database normalization, indexing, complex joins, subqueries, and transaction management.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/question-bank?subject=db" class="btn-view">Open Database Bank &rarr;</a>
                    </div>

                    <div class="catalog-card">
                        <div>
                            <h3 class="catalog-title">Cloud Computing &amp; DevOps</h3>
                            <p class="catalog-desc">Cloud architecture fundamentals, compute instances, object storage, Docker containerization, and CI/CD pipelines.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/question-bank?subject=cloud" class="btn-view">Open Cloud Bank &rarr;</a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <script>
        function switchSubject(subjectVal) {
            var baseUrl = "${pageContext.request.contextPath}/question-bank";
            if (subjectVal && subjectVal.trim().length > 0) {
                window.location.href = baseUrl + "?subject=" + encodeURIComponent(subjectVal);
            } else {
                window.location.href = baseUrl;
            }
        }
    </script>

</body>
</html>

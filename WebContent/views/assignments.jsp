<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (!com.kasnet.util.SessionUtil.requireLogin(request, response)) {
        return;
    }
    if (!com.kasnet.util.SessionUtil.isStudent(request)) {
        if (com.kasnet.util.SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/assignments");
            return;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Assignments - My Internship Technologies Portal</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            background-color: #f0f2f5;
            color: #1a1a2e;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.5;
        }

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
            width: 36px;
            height: 36px;
            background: rgba(255,255,255,0.2);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
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
            background: #e53935;
            color: #fff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
            transition: background 0.2s;
        }

        .container {
            max-width: 1300px;
            margin: 24px auto;
            padding: 0 20px;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 16px;
        }

        .page-title {
            font-size: 1.8rem;
            font-weight: 800;
            color: #1a237e;
        }

        .page-subtitle {
            font-size: 0.9rem;
            color: #6b7280;
            margin-top: 4px;
        }

        .alert-success {
            background: #def7ec;
            color: #03543f;
            border: 1px solid #84e1bc;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }

        /* Assignment Cards Grid */
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
            gap: 24px;
        }

        .card {
            background: #ffffff;
            border-radius: 14px;
            padding: 24px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
        }

        .card-top {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 12px;
        }

        .assign-num {
            background: #e0e7ff;
            color: #3730a3;
            font-size: 0.75rem;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 20px;
            letter-spacing: 0.5px;
        }

        .card-title {
            font-size: 1.2rem;
            font-weight: 700;
            color: #111827;
            margin-bottom: 6px;
        }

        .card-subject {
            font-size: 0.85rem;
            color: #6366f1;
            font-weight: 600;
            margin-bottom: 12px;
        }

        .view-assignment {
            display: inline-block;
            margin-top: 16px;
            padding: 10px 16px;
            border-radius: 7px;
            background: #1a237e;
            color: #fff;
            font-weight: 650;
            text-align: center;
            text-decoration: none;
        }

        .view-assignment:hover { background: #283593; }
        .alert-error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; }
        .empty-state { background: #fff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 32px; text-align: center; color: #6b7280; }
        @media (max-width: 560px) {
            .navbar { align-items: flex-start; flex-wrap: wrap; gap: 8px; }
            .navbar-menu { flex-wrap: wrap; gap: 4px; }
            .cards-grid { grid-template-columns: 1fr; }
            .card { padding: 18px; }
        }

    </style>
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">My Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link active">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Content -->
    <div class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Internship Assignments</h1>
                <p class="page-subtitle">View your assignment descriptions and instructions provided by your admin.</p>
            </div>
        </div>

        <c:if test="${not empty assignmentLoadError}">
            <div class="alert-error"><c:out value="${assignmentLoadError}"/></div>
        </c:if>

        <div class="cards-grid">
            <c:forEach items="${assignmentList}" var="asn">
                <div class="card">
                    <div>
                        <div class="card-top">
                            <span class="assign-num"><c:out value="${asn.assignmentNumber}"/></span>
                        </div>
                        <h2 class="card-title"><c:out value="${asn.title}"/></h2>
                        <div class="card-subject"><c:out value="${asn.subject}"/></div>
                        <a class="view-assignment" href="${pageContext.request.contextPath}/assignment-details?assignmentId=${asn.assignmentId}">View Assignment</a>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty assignmentList and empty assignmentLoadError}">
                <div class="empty-state">No assignments are currently available. Please check back later.</div>
            </c:if>
        </div>
    </div>

</body>
</html>

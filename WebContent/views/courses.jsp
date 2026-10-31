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
    <title>
        <c:choose>
            <c:when test="${viewMode == 'detail'}">${course.title} - Kasnet Internship Portal</c:when>
            <c:otherwise>Courses - Kasnet Internship Portal</c:otherwise>
        </c:choose>
    </title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f0f2f5;
            color: #1a1a2e;
        }

        /* ──────────── Navbar ──────────── */
        .navbar {
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #fff;
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 1.25rem;
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
            gap: 20px;
        }
        .nav-link {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            font-size: 0.9rem;
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
        .btn-logout:hover { background: #c62828; }

        /* ──────────── Container ──────────── */
        .container {
            max-width: 1320px;
            margin: 0 auto;
            padding: 24px 20px 48px;
        }

        /* ──────────── Page Header ──────────── */
        .page-header {
            margin-bottom: 28px;
        }
        .page-header h1 {
            font-size: 2rem;
            font-weight: 800;
            color: #1a237e;
            margin-bottom: 4px;
        }
        .page-header p {
            color: #64748b;
            font-size: 1rem;
        }
        .breadcrumb {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: #94a3b8;
            margin-bottom: 12px;
        }
        .breadcrumb a {
            color: #3b82f6;
            text-decoration: none;
        }
        .breadcrumb a:hover { text-decoration: underline; }
        .breadcrumb span { color: #94a3b8; }

        /* ──────────── Stats Bar ──────────── */
        .stats-bar {
            display: flex;
            gap: 16px;
            margin-bottom: 28px;
            flex-wrap: wrap;
        }
        .stat-card {
            flex: 1;
            min-width: 180px;
            padding: 18px 20px;
            background: #fff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 2px 12px rgba(0,0,0,0.04);
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.08);
        }
        .stat-card .stat-icon {
            width: 42px;
            height: 42px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            margin-bottom: 10px;
        }
        .stat-card .stat-value {
            font-size: 1.6rem;
            font-weight: 800;
            color: #1e293b;
        }
        .stat-card .stat-label {
            font-size: 0.82rem;
            color: #64748b;
            font-weight: 500;
            margin-top: 2px;
        }
        .icon-courses { background: linear-gradient(135deg, #dbeafe, #bfdbfe); color: #2563eb; }
        .icon-enrolled { background: linear-gradient(135deg, #d1fae5, #a7f3d0); color: #059669; }
        .icon-progress { background: linear-gradient(135deg, #fef3c7, #fde68a); color: #d97706; }

        /* ──────────── Course Grid ──────────── */
        .course-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
            gap: 22px;
        }

        .course-card {
            background: #fff;
            border-radius: 16px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 2px 12px rgba(0,0,0,0.04);
            overflow: hidden;
            transition: transform 0.25s ease, box-shadow 0.25s ease;
            display: flex;
            flex-direction: column;
        }
        .course-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 32px rgba(26,35,126,0.12);
        }

        .card-thumbnail {
            width: 100%;
            height: 180px;
            position: relative;
            overflow: hidden;
        }
        .card-thumbnail .thumb-gradient {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
        }
        .thumb-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .thumb-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        .thumb-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        .thumb-4 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
        .thumb-5 { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
        .thumb-6 { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }

        .card-thumbnail img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .card-badges {
            position: absolute;
            top: 12px;
            left: 12px;
            display: flex;
            gap: 6px;
        }
        .badge {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 0.72rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.04em;
        }
        .badge-difficulty {
            background: rgba(255,255,255,0.92);
            color: #1a237e;
            backdrop-filter: blur(4px);
        }
        .badge-enrolled {
            background: #059669;
            color: #fff;
        }

        .card-body {
            flex: 1;
            padding: 18px 20px;
            display: flex;
            flex-direction: column;
        }
        .card-body h3 {
            font-size: 1.12rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 6px;
            line-height: 1.35;
        }
        .card-body .card-desc {
            color: #64748b;
            font-size: 0.88rem;
            line-height: 1.55;
            margin-bottom: 14px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .card-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 14px;
            font-size: 0.82rem;
            color: #94a3b8;
            margin-bottom: 14px;
        }
        .card-meta span {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .card-progress {
            margin-top: auto;
            margin-bottom: 14px;
        }
        .progress-bar-bg {
            height: 6px;
            background: #e2e8f0;
            border-radius: 20px;
            overflow: hidden;
        }
        .progress-bar-fill {
            height: 100%;
            border-radius: 20px;
            background: linear-gradient(90deg, #3b82f6, #2563eb);
            transition: width 0.4s ease;
        }
        .progress-text {
            display: flex;
            justify-content: space-between;
            font-size: 0.78rem;
            color: #94a3b8;
            margin-top: 4px;
        }

        .card-footer {
            padding: 0 20px 18px;
        }
        .btn-primary {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            padding: 11px 20px;
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 0.92rem;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
            gap: 6px;
        }
        .btn-primary:hover {
            background: linear-gradient(135deg, #1d4ed8, #1e40af);
            box-shadow: 0 4px 14px rgba(37,99,235,0.35);
        }
        .btn-success {
            background: linear-gradient(135deg, #059669, #047857);
        }
        .btn-success:hover {
            background: linear-gradient(135deg, #047857, #065f46);
            box-shadow: 0 4px 14px rgba(5,150,105,0.35);
        }
        .btn-outline {
            background: #fff;
            color: #2563eb;
            border: 2px solid #2563eb;
        }
        .btn-outline:hover {
            background: #eff6ff;
            box-shadow: none;
        }

        /* ──────────── Course Detail ──────────── */
        .detail-grid {
            display: grid;
            grid-template-columns: 1fr 360px;
            gap: 28px;
            align-items: start;
        }

        .detail-card {
            background: #fff;
            border-radius: 16px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 16px rgba(0,0,0,0.06);
            overflow: hidden;
        }

        .detail-hero {
            width: 100%;
            height: 260px;
            position: relative;
        }
        .detail-hero .thumb-gradient {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 72px;
        }

        .detail-body {
            padding: 28px;
        }
        .detail-body h1 {
            font-size: 1.65rem;
            font-weight: 800;
            color: #1e293b;
            margin-bottom: 8px;
        }
        .detail-body .detail-desc {
            color: #475569;
            font-size: 0.95rem;
            line-height: 1.7;
            margin-bottom: 20px;
        }
        .detail-meta-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
            gap: 12px;
            margin-bottom: 24px;
        }
        .meta-item {
            padding: 14px;
            background: #f8fafc;
            border-radius: 10px;
            border: 1px solid #e2e8f0;
            text-align: center;
        }
        .meta-item .meta-value {
            font-size: 1.15rem;
            font-weight: 800;
            color: #1e293b;
        }
        .meta-item .meta-label {
            font-size: 0.78rem;
            color: #94a3b8;
            margin-top: 2px;
        }

        /* ──────────── Sidebar Info ──────────── */
        .sidebar-card {
            background: #fff;
            border-radius: 16px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 16px rgba(0,0,0,0.06);
            padding: 24px;
            position: sticky;
            top: 80px;
        }
        .sidebar-card h3 {
            font-size: 1.05rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid #e2e8f0;
        }
        .sidebar-item {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            font-size: 0.9rem;
        }
        .sidebar-item .label { color: #64748b; }
        .sidebar-item .value { font-weight: 600; color: #1e293b; }
        .sidebar-card .enroll-form {
            margin-top: 20px;
        }

        /* ──────────── Lecture List ──────────── */
        .lecture-section {
            margin-top: 28px;
        }
        .lecture-section h2 {
            font-size: 1.3rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .lecture-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .lecture-item {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 16px 18px;
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            transition: all 0.2s;
        }
        .lecture-item:hover {
            border-color: #bfdbfe;
            box-shadow: 0 4px 12px rgba(37,99,235,0.08);
        }
        .lecture-num {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 0.95rem;
            flex-shrink: 0;
        }
        .num-pending {
            background: #f1f5f9;
            color: #64748b;
        }
        .num-completed {
            background: linear-gradient(135deg, #d1fae5, #a7f3d0);
            color: #059669;
        }
        .num-in-progress {
            background: linear-gradient(135deg, #dbeafe, #bfdbfe);
            color: #2563eb;
        }
        .lecture-info {
            flex: 1;
            min-width: 0;
        }
        .lecture-info h4 {
            font-size: 0.95rem;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 3px;
        }
        .lecture-info .lecture-meta {
            font-size: 0.8rem;
            color: #94a3b8;
            display: flex;
            gap: 12px;
        }
        .lecture-status {
            flex-shrink: 0;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .status-badge {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 0.72rem;
            font-weight: 700;
        }
        .status-completed {
            background: #d1fae5;
            color: #047857;
        }
        .status-in-progress {
            background: #dbeafe;
            color: #1d4ed8;
        }
        .status-locked {
            background: #f1f5f9;
            color: #94a3b8;
        }
        .status-preview {
            background: #fef3c7;
            color: #b45309;
        }

        /* ──────────── Enrolled Banner ──────────── */
        .enrolled-banner {
            background: linear-gradient(135deg, #d1fae5, #a7f3d0);
            border: 1px solid #6ee7b7;
            border-radius: 12px;
            padding: 14px 18px;
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
            font-size: 0.92rem;
            font-weight: 600;
            color: #065f46;
            animation: slideDown 0.3s ease;
        }

        /* ──────────── Empty State ──────────── */
        .empty-state {
            text-align: center;
            padding: 60px 24px;
            color: #94a3b8;
        }
        .empty-state .empty-icon {
            font-size: 56px;
            margin-bottom: 16px;
        }
        .empty-state h3 {
            font-size: 1.15rem;
            color: #64748b;
            margin-bottom: 8px;
        }

        /* ──────────── Animations ──────────── */
        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(12px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-8px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .course-card {
            animation: fadeInUp 0.35s ease both;
        }
        .course-card:nth-child(2) { animation-delay: 0.06s; }
        .course-card:nth-child(3) { animation-delay: 0.12s; }
        .course-card:nth-child(4) { animation-delay: 0.18s; }
        .course-card:nth-child(5) { animation-delay: 0.24s; }
        .course-card:nth-child(6) { animation-delay: 0.30s; }

        .lecture-item {
            animation: fadeInUp 0.3s ease both;
        }
        .lecture-item:nth-child(2) { animation-delay: 0.04s; }
        .lecture-item:nth-child(3) { animation-delay: 0.08s; }
        .lecture-item:nth-child(4) { animation-delay: 0.12s; }
        .lecture-item:nth-child(5) { animation-delay: 0.16s; }

        /* ──────────── Responsive ──────────── */
        @media (max-width: 900px) {
            .detail-grid {
                grid-template-columns: 1fr;
            }
            .sidebar-card {
                position: static;
            }
        }
        @media (max-width: 600px) {
            .course-grid {
                grid-template-columns: 1fr;
            }
            .stats-bar {
                flex-direction: column;
            }
            .navbar-menu {
                gap: 10px;
            }
            .nav-link { font-size: 0.82rem; padding: 4px 8px; }
        }
    </style>
</head>
<body>

<!-- ═══════ Navbar ═══════ -->
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
        <div class="logo-icon">&#9670;</div>
        Kasnet Portal
    </a>
    <div class="navbar-menu">
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
        <a href="${pageContext.request.contextPath}/courses" class="nav-link active">Courses</a>
        <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
        <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
        <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</nav>

<div class="container">

<!-- ═══════════════════════════════════════════════════════════════════════ -->
<!-- ═══════ CATALOG VIEW ═══════ -->
<!-- ═══════════════════════════════════════════════════════════════════════ -->
<c:if test="${viewMode == 'catalog'}">

    <div class="page-header">
        <h1>&#128218; Course Catalog</h1>
        <p>Explore our curated engineering courses and start learning today</p>
    </div>

    <!-- Stats Bar -->
    <div class="stats-bar">
        <div class="stat-card">
            <div class="stat-icon icon-courses">&#128214;</div>
            <div class="stat-value">${courseList.size()}</div>
            <div class="stat-label">Available Courses</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-enrolled">&#9989;</div>
            <div class="stat-value">${enrolledCount}</div>
            <div class="stat-label">Enrolled Courses</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-progress">&#128200;</div>
            <div class="stat-value">
                <c:set var="inProgress" value="0" />
                <c:forEach items="${courseList}" var="c">
                    <c:if test="${c.enrolled && c.userProgress > 0 && c.userProgress < 100}">
                        <c:set var="inProgress" value="${inProgress + 1}" />
                    </c:if>
                </c:forEach>
                ${inProgress}
            </div>
            <div class="stat-label">In Progress</div>
        </div>
    </div>

    <!-- Course Grid -->
    <c:choose>
        <c:when test="${not empty courseList}">
            <div class="course-grid">
                <c:forEach items="${courseList}" var="course" varStatus="loop">
                    <c:set var="thumbIndex" value="${(loop.index % 6) + 1}" />
                    <div class="course-card">
                        <div class="card-thumbnail">
                            <c:choose>
                                <c:when test="${not empty course.thumbnailUrl}">
                                    <img src="${course.thumbnailUrl}" alt="${course.title}">
                                </c:when>
                                <c:otherwise>
                                    <div class="thumb-gradient thumb-${thumbIndex}">
                                        <c:choose>
                                            <c:when test="${thumbIndex == 1}">&#128187;</c:when>
                                            <c:when test="${thumbIndex == 2}">&#128640;</c:when>
                                            <c:when test="${thumbIndex == 3}">&#9729;</c:when>
                                            <c:when test="${thumbIndex == 4}">&#128161;</c:when>
                                            <c:when test="${thumbIndex == 5}">&#127919;</c:when>
                                            <c:otherwise>&#128302;</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <div class="card-badges">
                                <span class="badge badge-difficulty">${course.difficulty}</span>
                                <c:if test="${course.enrolled}">
                                    <span class="badge badge-enrolled">Enrolled</span>
                                </c:if>
                            </div>
                        </div>

                        <div class="card-body">
                            <h3>${course.title}</h3>
                            <p class="card-desc">${course.description}</p>
                            <div class="card-meta">
                                <span>&#128100; ${course.instructorName}</span>
                                <span>&#128218; ${course.lectureCount} Lessons</span>
                                <span>&#128197; ${course.durationWeeks} Weeks</span>
                            </div>

                            <c:if test="${course.enrolled}">
                                <div class="card-progress">
                                    <div class="progress-bar-bg">
                                        <div class="progress-bar-fill" style="width: ${course.userProgress}%"></div>
                                    </div>
                                    <div class="progress-text">
                                        <span>${course.userCompletedLessons} / ${course.lectureCount} lessons</span>
                                        <span>${course.userProgress}%</span>
                                    </div>
                                </div>
                            </c:if>
                        </div>

                        <div class="card-footer">
                            <c:choose>
                                <c:when test="${course.enrolled}">
                                    <a href="${pageContext.request.contextPath}/courses?id=${course.courseId}"
                                       class="btn-primary btn-success">
                                        &#9654; Continue Learning
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/courses?id=${course.courseId}"
                                       class="btn-primary">
                                        View Course &#8594;
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon">&#128218;</div>
                <h3>No Courses Available Yet</h3>
                <p>New courses are being prepared. Check back soon!</p>
            </div>
        </c:otherwise>
    </c:choose>

</c:if>

<!-- ═══════════════════════════════════════════════════════════════════════ -->
<!-- ═══════ DETAIL VIEW ═══════ -->
<!-- ═══════════════════════════════════════════════════════════════════════ -->
<c:if test="${viewMode == 'detail'}">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
        <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        <span>&#8250;</span>
        <a href="${pageContext.request.contextPath}/courses">Courses</a>
        <span>&#8250;</span>
        <span>${course.title}</span>
    </div>

    <!-- Enrolled success banner -->
    <c:if test="${param.enrolled == '1'}">
        <div class="enrolled-banner">
            &#9989; You have successfully enrolled in this course! Start learning below.
        </div>
    </c:if>
    <c:if test="${param.enroll_required == '1'}">
        <div class="enrolled-banner" style="background: #fef3c7; border-color: #fde68a; color: #92400e;">
            &#128274; Free enrollment is required to access complete lecture videos. Click <strong>Enroll in Course Free</strong> in the sidebar to start immediately!
        </div>
    </c:if>

    <div class="detail-grid">
        <!-- ═══ Main Content ═══ -->
        <div>
            <div class="detail-card">
                <div class="detail-hero">
                    <c:set var="detailThumb" value="${(course.courseId % 6) + 1}" />
                    <c:choose>
                        <c:when test="${not empty course.thumbnailUrl}">
                            <img src="${course.thumbnailUrl}" alt="${course.title}"
                                 style="width:100%;height:100%;object-fit:cover;">
                        </c:when>
                        <c:otherwise>
                            <div class="thumb-gradient thumb-${detailThumb}" style="font-size:72px;">
                                <c:choose>
                                    <c:when test="${detailThumb == 1}">&#128187;</c:when>
                                    <c:when test="${detailThumb == 2}">&#128640;</c:when>
                                    <c:when test="${detailThumb == 3}">&#9729;</c:when>
                                    <c:when test="${detailThumb == 4}">&#128161;</c:when>
                                    <c:when test="${detailThumb == 5}">&#127919;</c:when>
                                    <c:otherwise>&#128302;</c:otherwise>
                                </c:choose>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="detail-body">
                    <h1>${course.title}</h1>
                    <p class="detail-desc">${course.description}</p>

                    <div class="detail-meta-grid">
                        <div class="meta-item">
                            <div class="meta-value">${course.lectureCount}</div>
                            <div class="meta-label">Lessons</div>
                        </div>
                        <div class="meta-item">
                            <div class="meta-value">${course.durationWeeks}</div>
                            <div class="meta-label">Weeks</div>
                        </div>
                        <div class="meta-item">
                            <div class="meta-value">${course.difficulty}</div>
                            <div class="meta-label">Level</div>
                        </div>
                        <div class="meta-item">
                            <div class="meta-value">${course.enrollmentCount}</div>
                            <div class="meta-label">Students</div>
                        </div>
                    </div>

                    <c:if test="${course.enrolled}">
                        <div class="card-progress" style="margin-bottom:0;">
                            <div style="display:flex;justify-content:space-between;margin-bottom:6px;">
                                <span style="font-weight:700;font-size:0.9rem;color:#1e293b;">Your Progress</span>
                                <span style="font-weight:700;font-size:0.9rem;color:#2563eb;">${course.userProgress}%</span>
                            </div>
                            <div class="progress-bar-bg" style="height:8px;">
                                <div class="progress-bar-fill" style="width:${course.userProgress}%"></div>
                            </div>
                            <div class="progress-text">
                                <span>${course.userCompletedLessons} of ${course.lectureCount} lessons completed</span>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Lecture List -->
            <div class="lecture-section">
                <h2>&#128218; Course Content</h2>

                <c:choose>
                    <c:when test="${not empty lectures}">
                        <div class="lecture-list">
                            <c:forEach items="${lectures}" var="lec" varStatus="ls">
                                <c:set var="canPlay" value="${course.enrolled || lec.preview || sessionScope.userRole == 'ADMIN'}" />
                                <a href="${canPlay ? pageContext.request.contextPath.concat('/courses?view=player&courseId=').concat(course.courseId).concat('&lectureId=').concat(lec.lectureId) : 'javascript:void(0)'}"
                                   class="lecture-item" style="text-decoration:none; color:inherit; ${!canPlay ? 'opacity:0.65; cursor:not-allowed;' : 'cursor:pointer;'}">
                                    <div class="lecture-num ${lec.completed ? 'num-completed' : (lec.watchProgressPercent > 0 ? 'num-in-progress' : 'num-pending')}">
                                        <c:choose>
                                            <c:when test="${lec.completed}">&#10003;</c:when>
                                            <c:otherwise>${ls.index + 1}</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="lecture-info">
                                        <h4>${lec.title}</h4>
                                        <div class="lecture-meta">
                                            <span>&#9201; ${lec.durationFormatted}</span>
                                            <c:if test="${lec.preview}">
                                                <span style="color:#d97706;font-weight:700;">&#128275; Free Preview</span>
                                            </c:if>
                                            <c:if test="${canPlay}">
                                                <span style="color:#2563eb;font-weight:600;">&#9654; Watch Lesson</span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="lecture-status">
                                        <c:choose>
                                            <c:when test="${lec.completed}">
                                                <span class="status-badge status-completed">Completed</span>
                                            </c:when>
                                            <c:when test="${lec.watchProgressPercent > 0}">
                                                <span class="status-badge status-in-progress">${lec.watchProgressPercent}%</span>
                                            </c:when>
                                            <c:when test="${lec.preview}">
                                                <span class="status-badge status-preview">Preview</span>
                                            </c:when>
                                            <c:when test="${!course.enrolled}">
                                                <span class="status-badge status-locked">&#128274; Locked</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-locked">Not Started</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state" style="padding:30px;">
                            <div class="empty-icon">&#127916;</div>
                            <h3>No Lessons Available Yet</h3>
                            <p>Course content is being prepared by the instructor.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- ═══ Sidebar ═══ -->
        <div class="sidebar-card">
            <h3>Course Information</h3>
            <div class="sidebar-item">
                <span class="label">Instructor</span>
                <span class="value">${course.instructorName}</span>
            </div>
            <div class="sidebar-item">
                <span class="label">Category</span>
                <span class="value">${course.category}</span>
            </div>
            <div class="sidebar-item">
                <span class="label">Difficulty</span>
                <span class="value">${course.difficulty}</span>
            </div>
            <div class="sidebar-item">
                <span class="label">Duration</span>
                <span class="value">${course.durationWeeks} Weeks</span>
            </div>
            <div class="sidebar-item">
                <span class="label">Lessons</span>
                <span class="value">${course.lectureCount}</span>
            </div>
            <div class="sidebar-item">
                <span class="label">Students Enrolled</span>
                <span class="value">${course.enrollmentCount}</span>
            </div>

            <c:choose>
                <c:when test="${course.enrolled}">
                    <div class="enroll-form">
                        <a href="${pageContext.request.contextPath}/courses?view=player&courseId=${course.courseId}${nextIncomplete != null ? '&lectureId='.concat(nextIncomplete.lectureId) : ''}"
                           class="btn-primary btn-success">
                            &#9654; Continue Learning
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty lectures && lectures[0].preview}">
                        <div style="margin-bottom: 10px;">
                            <a href="${pageContext.request.contextPath}/courses?view=player&courseId=${course.courseId}&lectureId=${lectures[0].lectureId}"
                               class="btn-primary" style="background:#d97706; text-decoration:none; display:block; text-align:center;">
                                &#128275; Watch Free Preview
                            </a>
                        </div>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/courses" class="enroll-form">
                        <input type="hidden" name="action" value="enroll" />
                        <input type="hidden" name="courseId" value="${course.courseId}" />
                        <button type="submit" class="btn-primary" style="border:none;">
                            &#128640; Enroll Now — Free
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</c:if>

</div>

</body>
</html>

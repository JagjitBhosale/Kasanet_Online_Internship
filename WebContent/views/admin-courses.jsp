<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    if (!com.onlineinternshipin.utility.SessionUtil.requireAdmin(request, response)) {
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Courses &amp; Video DRM - Admin Portal - My Internship Technologies</title>
    <!-- Google Fonts for sleek modern typography matching Admin Portal -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --sidebar-bg: #263238;
            --sidebar-dark: #1e272c;
            --sidebar-header: #c62828;
            --sidebar-header-hover: #b71c1c;
            --sidebar-text: #cfd8dc;
            --primary: #1e293b;
            --accent: #1976d2;
            --accent-hover: #1565c0;
            --danger: #d32f2f;
            --danger-hover: #c62828;
            --success: #2e7d32;
            --bg-page: #f8fafc;
            --card-bg: #ffffff;
            --border-color: #e2e8f0;
            --border-darker: #cbd5e1;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.06);
            --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.08), 0 2px 4px -2px rgba(0,0,0,0.05);
            --font: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
            --radius-md: 8px;
            --radius-lg: 12px;
        }

        body {
            font-family: var(--font);
            background-color: var(--bg-page);
            color: var(--text-main);
            line-height: 1.5;
            min-height: 100vh;
            display: flex;
            overflow-x: hidden;
        }

        /* ================= SIDEBAR ================= */
        .sidebar {
            width: 260px;
            min-height: 100vh;
            background-color: var(--sidebar-bg);
            color: var(--sidebar-text);
            display: flex;
            flex-direction: column;
            flex-shrink: 0;
            transition: all 0.28s ease;
            z-index: 100;
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            box-shadow: 2px 0 10px rgba(0, 0, 0, 0.15);
        }

        .sidebar.collapsed {
            transform: translateX(-260px);
        }

        .sidebar-header {
            background-color: var(--sidebar-header);
            padding: 16px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            color: #ffffff;
            border-bottom: 1px solid rgba(0, 0, 0, 0.12);
        }

        .sidebar-header img {
            width: auto;
            max-width: 44px;
            height: 38px;
            object-fit: contain;
            border-radius: 6px;
            background: #ffffff;
            padding: 3px;
        }

        .sidebar-header-text h2 {
            font-size: 1.05rem;
            font-weight: 700;
            line-height: 1.2;
            letter-spacing: -0.2px;
        }

        .sidebar-header-text span {
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            opacity: 0.85;
        }

        .sidebar-menu {
            list-style: none;
            padding: 14px 0;
            flex: 1;
            overflow-y: auto;
        }

        .sidebar-menu-item {
            margin-bottom: 2px;
        }

        .sidebar-link {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 13px 22px;
            color: var(--sidebar-text);
            text-decoration: none;
            font-size: 0.92rem;
            font-weight: 500;
            transition: all 0.2s ease;
            border-left: 4px solid transparent;
        }

        .sidebar-link-content {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .sidebar-link-icon {
            font-size: 1.15rem;
            display: inline-block;
            width: 22px;
            text-align: center;
        }

        .sidebar-link:hover {
            background-color: rgba(255, 255, 255, 0.06);
            color: #ffffff;
        }

        .sidebar-link.active {
            background-color: rgba(0, 0, 0, 0.28);
            color: #ffffff;
            border-left-color: #ef4444;
            font-weight: 600;
        }

        /* ================= MAIN WRAPPER ================= */
        .main-wrapper {
            flex: 1;
            display: flex;
            flex-direction: column;
            margin-left: 260px;
            min-width: 0;
            transition: margin-left 0.28s ease;
        }

        .main-wrapper.full-width {
            margin-left: 0;
        }

        /* Topbar */
        .topbar {
            height: 56px;
            background: #ffffff;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 24px;
            position: sticky;
            top: 0;
            z-index: 90;
            box-shadow: var(--shadow-sm);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .btn-toggle-sidebar {
            background: none;
            border: 1px solid var(--border-color);
            padding: 6px 10px;
            border-radius: 6px;
            cursor: pointer;
            color: var(--text-main);
            font-size: 1.2rem;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.15s;
        }

        .btn-toggle-sidebar:hover {
            background: #f1f5f9;
            color: var(--accent);
        }

        .portal-breadcrumb {
            font-size: 0.95rem;
            font-weight: 600;
            color: var(--text-main);
        }

        .topbar-right {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .user-pill {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.88rem;
            font-weight: 600;
            color: var(--text-main);
        }

        .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background-color: #e2e8f0;
            color: #475569;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 0.85rem;
        }

        .nav-btn {
            padding: 6px 14px;
            border-radius: 6px;
            font-size: 0.84rem;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            border: none;
            cursor: pointer;
        }

        .nav-btn-outline {
            background: #eff6ff;
            color: var(--accent);
            border: 1px solid #bfdbfe;
        }

        .nav-btn-outline:hover {
            background: #dbeafe;
        }

        .nav-btn-danger {
            background: var(--danger);
            color: #ffffff;
        }

        .nav-btn-danger:hover {
            background: var(--danger-hover);
        }

        /* Page Content */
        .page-content {
            flex: 1;
            padding: 24px 28px;
            max-width: 1320px;
            width: 100%;
            margin: 0 auto;
        }

        .page-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 16px;
        }

        .page-title {
            font-size: 1.55rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 4px;
        }

        .page-subtitle {
            font-size: 0.92rem;
            color: var(--text-muted);
        }

        /* Buttons */
        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 9px 16px;
            border-radius: 6px;
            font-size: 0.88rem;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
            border: none;
        }

        .btn-primary {
            background: var(--accent);
            color: #ffffff;
        }

        .btn-primary:hover {
            background: var(--accent-hover);
            transform: translateY(-1px);
        }

        .btn-outline {
            background: #ffffff;
            color: var(--text-main);
            border: 1px solid var(--border-color);
        }

        .btn-outline:hover {
            background: #f1f5f9;
        }

        .btn-sm {
            padding: 5px 11px;
            font-size: 0.8rem;
            border-radius: 5px;
        }

        .btn-danger {
            background: #fee2e2;
            color: #b91c1c;
            border: 1px solid #fecaca;
        }

        .btn-danger:hover {
            background: #fecaca;
        }

        /* Table Card */
        .table-card {
            background: var(--card-bg);
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow-sm);
            overflow: hidden;
            margin-bottom: 24px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 0.9rem;
        }

        th {
            background: #f8fafc;
            padding: 12px 18px;
            font-weight: 700;
            color: var(--text-muted);
            text-transform: uppercase;
            font-size: 0.76rem;
            letter-spacing: 0.5px;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 14px 18px;
            border-bottom: 1px solid var(--border-color);
            color: var(--text-main);
            vertical-align: middle;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:hover td {
            background: #f8fafc;
        }

        .badge-status {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 999px;
            font-size: 0.74rem;
            font-weight: 700;
            text-transform: uppercase;
        }

        .badge-published {
            background: #dcfce7;
            color: #15803d;
        }

        .badge-draft {
            background: #fef3c7;
            color: #b45309;
        }

        .badge-archived {
            background: #f1f5f9;
            color: #64748b;
        }

        /* Modals */
        .modal-backdrop {
            position: fixed;
            inset: 0;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(4px);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            padding: 20px;
        }

        .modal-backdrop.active {
            display: flex;
        }

        .modal-box {
            background: #ffffff;
            width: 100%;
            max-width: 600px;
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
            animation: modalFadeIn 0.2s ease-out;
        }

        @keyframes modalFadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .modal-header {
            padding: 18px 24px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            background: #f8fafc;
        }

        .modal-header h3 {
            font-size: 1.15rem;
            color: #1e293b;
            font-weight: 700;
        }

        .modal-close {
            background: transparent;
            border: none;
            font-size: 1.4rem;
            color: var(--text-muted);
            cursor: pointer;
            line-height: 1;
        }

        .modal-body {
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 16px;
            max-height: 70vh;
            overflow-y: auto;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .form-group label {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--text-main);
        }

        .form-control {
            padding: 9px 12px;
            border: 1px solid var(--border-color);
            border-radius: 6px;
            font-size: 0.88rem;
            font-family: inherit;
            outline: none;
            transition: border-color 0.2s;
        }

        .form-control:focus {
            border-color: var(--accent);
            outline: 2px solid #bfdbfe;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .modal-footer {
            padding: 16px 24px;
            border-top: 1px solid var(--border-color);
            background: #f8fafc;
            display: flex;
            justify-content: flex-end;
            gap: 12px;
        }

        .upload-progress-box {
            display: none;
            margin-top: 12px;
        }

        .upload-bar-track {
            height: 8px;
            background: #e2e8f0;
            border-radius: 999px;
            overflow: hidden;
            margin-top: 6px;
        }

        .upload-bar-fill {
            height: 100%;
            background: var(--accent);
            width: 0%;
            transition: width 0.2s;
        }

        .sidebar-backdrop {
            display: none;
        }

        @media (max-width: 992px) {
            .sidebar {
                transform: translateX(-260px);
            }
            .sidebar.mobile-open {
                transform: translateX(0);
            }
            .main-wrapper {
                margin-left: 0;
            }
            .sidebar-backdrop {
                position: fixed;
                inset: 0;
                z-index: 99;
                display: none;
                background: rgba(0,0,0,.5);
            }
            .sidebar-backdrop.show {
                display: block;
            }
        }

        @media (max-width: 620px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            .user-pill {
                display: none;
            }
            .portal-breadcrumb {
                font-size: 0.82rem;
            }
        }
    </style>
</head>
<body>

    <!-- Mobile Backdrop -->
    <div class="sidebar-backdrop" id="sidebarBackdrop" onclick="toggleSidebar()"></div>

    <!-- ================= LEFT SIDEBAR ================= -->
    <aside class="sidebar" id="sidebar">
        <!-- Top Red Header Banner -->
        <div class="sidebar-header">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\' width=\'36\' height=\'36\' viewBox=\'0 0 24 24\' fill=\'%23c62828\'><rect width=\'24\' height=\'24\' rx=\'4\' fill=\'white\'/><text x=\'50%25\' y=\'68%25\' text-anchor=\'middle\' font-size=\'14\' font-weight=\'bold\' fill=\'%23c62828\'>M</text></svg>'"/>
            <div class="sidebar-header-text">
                <h2>My Internship</h2>
                <span>Technologies</span>
            </div>
        </div>

        <!-- Sidebar Navigation Menu -->
        <ul class="sidebar-menu">
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin" class="sidebar-link">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">🎓</span>
                        <span>Student Details</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin/assignments" class="sidebar-link">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📝</span>
                        <span>Assignment Management</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin/courses" class="sidebar-link active" aria-current="page">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">🎥</span>
                        <span>Courses &amp; Lectures</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin?tab=certificates" class="sidebar-link">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📜</span>
                        <span>Global Certification</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin?tab=offer-letter" class="sidebar-link">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📄</span>
                        <span>Certification</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin?tab=projects" class="sidebar-link">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📁</span>
                        <span>Project</span>
                    </span>
                </a>
            </li>
        </ul>
    </aside>

    <!-- ================= MAIN WRAPPER ================= -->
    <div class="main-wrapper" id="mainWrapper">

        <!-- Top Header Navigation -->
        <header class="topbar">
            <div class="topbar-left">
                <button type="button" class="btn-toggle-sidebar" onclick="toggleSidebar()" title="Toggle Sidebar">
                    ☰
                </button>
                <div class="portal-breadcrumb">
                    Online Internship Administration / Courses &amp; Video Management
                </div>
            </div>

            <div class="topbar-right">
                <div class="user-pill">
                    <div class="user-avatar">
                        ${not empty sessionScope.userName ? sessionScope.userName.substring(0, 1).toUpperCase() : 'A'}
                    </div>
                    <span>${not empty sessionScope.userName ? sessionScope.userName : 'Admin'}</span>
                </div>
                <a href="${pageContext.request.contextPath}/courses" class="nav-btn nav-btn-outline" target="_blank">
                    <i class="fa-solid fa-arrow-up-right-from-square" style="font-size: 11px;"></i> Student View
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="nav-btn nav-btn-danger">Logout</a>
            </div>
        </header>

        <!-- Main Page Content -->
        <main class="page-content">

            <!-- ─────────────────────────────────────────────────────────────
                 MODE 1: MANAGE LECTURES FOR A COURSE
                 ───────────────────────────────────────────────────────────── -->
            <c:if test="${viewMode == 'manage_lectures'}">
                <div class="page-header">
                    <div>
                        <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-outline btn-sm" style="margin-bottom: 10px;">
                            <i class="fa-solid fa-arrow-left"></i> Back to Courses
                        </a>
                        <h1 class="page-title">${course.title}</h1>
                        <p class="page-subtitle">Manage curriculum lectures, preview access, and Cloudinary video stream assets.</p>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <button type="button" class="btn btn-outline" onclick="openEditCourseModal()">
                            <i class="fa-solid fa-pen"></i> Edit Course Info
                        </button>
                        <button type="button" class="btn btn-primary" onclick="openAddLectureModal()">
                            <i class="fa-solid fa-plus"></i> Add Lecture
                        </button>
                    </div>
                </div>

                <!-- Lectures Table -->
                <div class="table-card">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 60px;">#</th>
                                <th>Lecture Title</th>
                                <th>Status</th>
                                <th>Duration</th>
                                <th>Video DRM Asset</th>
                                <th>Preview</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="lec" items="${lectures}">
                                <tr>
                                    <td style="font-weight: 700;">${lec.lectureNumber}</td>
                                    <td>
                                        <div style="font-weight: 700; color: var(--text-main);">${lec.title}</div>
                                        <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">
                                            ${lec.description != null ? lec.description : 'No description'}
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge-status ${lec.status == 'PUBLISHED' ? 'badge-published' : 'badge-draft'}">
                                            ${lec.status}
                                        </span>
                                    </td>
                                    <td style="font-family: 'JetBrains Mono', monospace; font-size: 12.5px;">
                                        ${lec.durationFormatted}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${lec.cloudinaryPublicId != null && !lec.cloudinaryPublicId.isEmpty()}">
                                                <span style="color: var(--success); font-weight: 700; font-size: 12px;">
                                                    <i class="fa-solid fa-circle-check"></i> Connected
                                                </span>
                                                <div style="font-family: 'JetBrains Mono', monospace; font-size: 10.5px; color: var(--text-muted); max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                                    ${lec.cloudinaryPublicId}
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--danger); font-size: 12px; font-weight: 600;">
                                                    <i class="fa-solid fa-triangle-exclamation"></i> No Video
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${lec.preview}">
                                            <span style="color: #b45309; font-weight: 700; font-size: 12px;">
                                                <i class="fa-solid fa-unlock"></i> Free Preview
                                            </span>
                                        </c:if>
                                        <c:if test="${!lec.preview}">
                                            <span style="color: var(--text-muted); font-size: 12px;">
                                                <i class="fa-solid fa-lock"></i> Enrolled Only
                                            </span>
                                        </c:if>
                                    </td>
                                    <td style="text-align: right; white-space: nowrap;">
                                        <button type="button" class="btn btn-outline btn-sm"
                                                data-lec-id="${lec.lectureId}"
                                                data-title="<c:out value='${lec.title}'/>"
                                                onclick="openUploadModalFromBtn(this)" title="Upload Video">
                                            <i class="fa-solid fa-cloud-arrow-up"></i> Upload Video
                                        </button>
                                        <button type="button" class="btn btn-outline btn-sm"
                                                data-lec-id="${lec.lectureId}"
                                                data-title="<c:out value='${lec.title}'/>"
                                                data-desc="<c:out value='${lec.description}'/>"
                                                data-num="${lec.lectureNumber}"
                                                data-dur="${lec.durationSeconds}"
                                                data-prev="${lec.preview}"
                                                data-status="${lec.status}"
                                                data-cld="${lec.cloudinaryPublicId}"
                                                onclick="openEditLectureModalFromBtn(this)" title="Edit Lecture">
                                            <i class="fa-solid fa-pen"></i>
                                        </button>
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/courses" style="display: inline;" onsubmit="return confirm('Delete this lecture?');">
                                            <input type="hidden" name="action" value="deleteLecture">
                                            <input type="hidden" name="lectureId" value="${lec.lectureId}">
                                            <button type="submit" class="btn btn-danger btn-sm" title="Delete Lecture">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty lectures}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                        No lectures added yet. Click <strong>Add Lecture</strong> above to create the first curriculum lesson!
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <!-- ─────────────────────────────────────────────────────────────
                 MODE 2: COURSE CATALOG OVERVIEW (ADMIN)
                 ───────────────────────────────────────────────────────────── -->
            <c:if test="${viewMode != 'manage_lectures'}">
                <div class="page-header">
                    <div>
                        <h1 class="page-title">Course Management</h1>
                        <p class="page-subtitle">Create, configure courses, manage anti-piracy video streaming, and track enrollments.</p>
                    </div>
                    <button type="button" class="btn btn-primary" onclick="openCreateCourseModal()">
                        <i class="fa-solid fa-plus"></i> Create New Course
                    </button>
                </div>

                <div class="table-card">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 70px;">ID</th>
                                <th>Course Info</th>
                                <th>Instructor</th>
                                <th>Category &amp; Level</th>
                                <th>Status</th>
                                <th>Lectures</th>
                                <th>Students</th>
                                <th style="text-align: right;">Manage</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${courseList}">
                                <tr>
                                    <td style="font-weight: 700; color: var(--text-muted);">#${c.courseId}</td>
                                    <td>
                                        <div style="font-weight: 700; font-size: 14.5px; color: var(--text-main);">${c.title}</div>
                                        <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">
                                            ${c.durationWeeks} Weeks duration
                                        </div>
                                    </td>
                                    <td style="font-weight: 600;">${c.instructorName}</td>
                                    <td>
                                        <div style="font-size: 13px; font-weight: 600;">${c.category}</div>
                                        <div style="font-size: 11.5px; color: var(--text-muted);">${c.difficulty}</div>
                                    </td>
                                    <td>
                                        <span class="badge-status ${c.status == 'PUBLISHED' ? 'badge-published' : (c.status == 'DRAFT' ? 'badge-draft' : 'badge-archived')}">
                                            ${c.status}
                                        </span>
                                    </td>
                                    <td style="font-weight: 700; color: var(--accent);">${c.lectureCount}</td>
                                    <td style="font-weight: 700; color: var(--success);">${c.enrollmentCount}</td>
                                    <td style="text-align: right; white-space: nowrap;">
                                        <a href="${pageContext.request.contextPath}/admin/courses?manage=${c.courseId}" class="btn btn-primary btn-sm">
                                            <i class="fa-solid fa-layer-group"></i> Manage Curriculum
                                        </a>
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/courses" style="display: inline;" onsubmit="return confirm('Delete course and all its lectures?');">
                                            <input type="hidden" name="action" value="deleteCourse">
                                            <input type="hidden" name="courseId" value="${c.courseId}">
                                            <button type="submit" class="btn btn-danger btn-sm" title="Delete Course">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty courseList}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                        No courses found. Click <strong>Create New Course</strong> above to add your first course.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </c:if>

        </main>
    </div>

    <!-- ─── MODAL: CREATE COURSE ─── -->
    <div class="modal-backdrop" id="createCourseModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Create New Course</h3>
                <button type="button" class="modal-close" onclick="closeModal('createCourseModal')">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/admin/courses">
                <input type="hidden" name="action" value="createCourse">
                <div class="modal-body">
                    <div class="form-group">
                        <label>Course Title *</label>
                        <input type="text" name="title" class="form-control" required placeholder="e.g. Modern Full-Stack Development">
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea name="description" class="form-control" rows="3" placeholder="Overview of what students will learn..."></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Instructor Name *</label>
                            <input type="text" name="instructorName" class="form-control" required placeholder="Dr. Rajesh Sharma">
                        </div>
                        <div class="form-group">
                            <label>Category</label>
                            <select name="category" class="form-control">
                                <option value="Engineering">Engineering</option>
                                <option value="Computer Science & IT">Computer Science &amp; IT</option>
                                <option value="Data Engineering">Data Engineering</option>
                                <option value="Electronics & Communication">Electronics &amp; Communication</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Difficulty</label>
                            <select name="difficulty" class="form-control">
                                <option value="Beginner">Beginner</option>
                                <option value="Intermediate">Intermediate</option>
                                <option value="Advanced">Advanced</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Duration (Weeks)</label>
                            <input type="number" name="durationWeeks" class="form-control" value="6" min="1" max="52">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Status</label>
                            <select name="status" class="form-control">
                                <option value="PUBLISHED">PUBLISHED</option>
                                <option value="DRAFT">DRAFT</option>
                                <option value="ARCHIVED">ARCHIVED</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Thumbnail URL</label>
                            <input type="url" name="thumbnailUrl" class="form-control" placeholder="https://images.unsplash.com/...">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" onclick="closeModal('createCourseModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Course</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ─── MODAL: EDIT COURSE ─── -->
    <c:if test="${viewMode == 'manage_lectures'}">
    <div class="modal-backdrop" id="editCourseModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Edit Course Information</h3>
                <button type="button" class="modal-close" onclick="closeModal('editCourseModal')">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/admin/courses">
                <input type="hidden" name="action" value="updateCourse">
                <input type="hidden" name="courseId" value="${course.courseId}">
                <div class="modal-body">
                    <div class="form-group">
                        <label>Course Title *</label>
                        <input type="text" name="title" class="form-control" value="<c:out value='${course.title}'/>" required>
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea name="description" class="form-control" rows="3"><c:out value='${course.description}'/></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Instructor Name *</label>
                            <input type="text" name="instructorName" class="form-control" value="<c:out value='${course.instructorName}'/>" required>
                        </div>
                        <div class="form-group">
                            <label>Category</label>
                            <select name="category" class="form-control">
                                <option value="Engineering" ${course.category == 'Engineering' ? 'selected' : ''}>Engineering</option>
                                <option value="Computer Science & IT" ${course.category == 'Computer Science & IT' ? 'selected' : ''}>Computer Science &amp; IT</option>
                                <option value="Data Engineering" ${course.category == 'Data Engineering' ? 'selected' : ''}>Data Engineering</option>
                                <option value="Electronics & Communication" ${course.category == 'Electronics & Communication' ? 'selected' : ''}>Electronics &amp; Communication</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Difficulty</label>
                            <select name="difficulty" class="form-control">
                                <option value="Beginner" ${course.difficulty == 'Beginner' ? 'selected' : ''}>Beginner</option>
                                <option value="Intermediate" ${course.difficulty == 'Intermediate' ? 'selected' : ''}>Intermediate</option>
                                <option value="Advanced" ${course.difficulty == 'Advanced' ? 'selected' : ''}>Advanced</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Duration (Weeks)</label>
                            <input type="number" name="durationWeeks" class="form-control" value="${course.durationWeeks}" min="1" max="52">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Status</label>
                            <select name="status" class="form-control">
                                <option value="PUBLISHED" ${course.status == 'PUBLISHED' ? 'selected' : ''}>PUBLISHED</option>
                                <option value="DRAFT" ${course.status == 'DRAFT' ? 'selected' : ''}>DRAFT</option>
                                <option value="ARCHIVED" ${course.status == 'ARCHIVED' ? 'selected' : ''}>ARCHIVED</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Thumbnail URL</label>
                            <input type="url" name="thumbnailUrl" class="form-control" value="<c:out value='${course.thumbnailUrl}'/>">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" onclick="closeModal('editCourseModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
    </c:if>

    <!-- ─── MODAL: ADD / EDIT LECTURE ─── -->
    <div class="modal-backdrop" id="lectureModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 id="lectureModalTitle">Add Lecture</h3>
                <button type="button" class="modal-close" onclick="closeModal('lectureModal')">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/admin/courses" id="lectureForm">
                <input type="hidden" name="action" id="lectureAction" value="createLecture">
                <input type="hidden" name="courseId" value="${course.courseId}">
                <input type="hidden" name="lectureId" id="lectureIdField" value="">

                <div class="modal-body">
                    <div class="form-group">
                        <label>Lecture Title *</label>
                        <input type="text" name="title" id="lecTitleInput" class="form-control" required placeholder="e.g. 1. Introduction to Web Development">
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea name="description" id="lecDescInput" class="form-control" rows="3" placeholder="Key topics covered in this lesson..."></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Lecture Order Number</label>
                            <input type="number" name="lectureNumber" id="lecNumInput" class="form-control" value="1" min="1">
                        </div>
                        <div class="form-group">
                            <label>Duration (Seconds)</label>
                            <input type="number" name="durationSeconds" id="lecDurationInput" class="form-control" value="600" min="0">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Cloudinary Public ID (DRM Asset)</label>
                        <input type="text" name="cloudinaryPublicId" id="lecCloudinaryInput" class="form-control" placeholder="kasanet/lectures/sample_video">
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Status</label>
                            <select name="status" id="lecStatusInput" class="form-control">
                                <option value="PUBLISHED">PUBLISHED</option>
                                <option value="DRAFT">DRAFT</option>
                            </select>
                        </div>
                        <div class="form-group" style="justify-content: center; margin-top: 22px;">
                            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                <input type="checkbox" name="isPreview" id="lecPreviewInput" value="true">
                                <span>Free Preview (unlocked without enrollment)</span>
                            </label>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" onclick="closeModal('lectureModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btnSaveLecture">Save Lecture</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ─── MODAL: CLOUDINARY VIDEO UPLOAD ─── -->
    <div class="modal-backdrop" id="uploadModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Upload Video File to Cloudinary</h3>
                <button type="button" class="modal-close" onclick="closeModal('uploadModal')">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/admin/courses" enctype="multipart/form-data" id="uploadForm">
                <input type="hidden" name="action" value="uploadVideo">
                <input type="hidden" name="courseId" value="${course.courseId}">
                <input type="hidden" name="lectureId" id="uploadLectureId" value="">

                <div class="modal-body">
                    <p style="font-size: 13.5px; color: var(--text-muted);">
                        Uploading video for: <strong id="uploadLectureTitle" style="color: var(--text-main);"></strong>
                    </p>
                    <div class="form-group" style="margin-top: 10px;">
                        <label>Select Video File (MP4, WebM, MOV - Max 500MB) *</label>
                        <input type="file" name="videoFile" class="form-control" accept="video/mp4,video/webm,video/quicktime,video/x-matroska" required id="videoFileInput">
                    </div>
                    <div class="upload-progress-box" id="uploadProgressBox">
                        <div style="font-size: 12.5px; font-weight: 700; color: var(--accent);" id="uploadStatusText">Uploading to secure Cloudinary storage...</div>
                        <div class="upload-bar-track">
                            <div class="upload-bar-fill" id="uploadBarFill"></div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" onclick="closeModal('uploadModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btnStartUpload">
                        <i class="fa-solid fa-cloud-arrow-up"></i> Upload &amp; Link Stream
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function toggleSidebar() {
            var sidebar = document.getElementById('sidebar');
            var mainWrapper = document.getElementById('mainWrapper');
            var backdrop = document.getElementById('sidebarBackdrop');

            if (window.innerWidth <= 992) {
                sidebar.classList.toggle('mobile-open');
                if (backdrop) backdrop.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                if (mainWrapper) mainWrapper.classList.toggle('full-width');
            }
        }

        function openCreateCourseModal() {
            document.getElementById('createCourseModal').classList.add('active');
        }

        function openEditCourseModal() {
            var modal = document.getElementById('editCourseModal');
            if (modal) modal.classList.add('active');
        }

        function closeModal(id) {
            var el = document.getElementById(id);
            if (el) el.classList.remove('active');
        }

        function openAddLectureModal() {
            document.getElementById('lectureModalTitle').textContent = 'Add New Lecture';
            document.getElementById('lectureAction').value = 'createLecture';
            document.getElementById('lectureIdField').value = '';
            document.getElementById('lecTitleInput').value = '';
            document.getElementById('lecDescInput').value = '';
            document.getElementById('lecNumInput').value = '${lectures != null ? lectures.size() + 1 : 1}';
            document.getElementById('lecDurationInput').value = '600';
            document.getElementById('lecCloudinaryInput').value = '';
            document.getElementById('lecPreviewInput').checked = false;
            document.getElementById('lectureModal').classList.add('active');
        }

        function openEditLectureModalFromBtn(btn) {
            document.getElementById('lectureModalTitle').textContent = 'Edit Lecture';
            document.getElementById('lectureAction').value = 'updateLecture';
            document.getElementById('lectureIdField').value = btn.dataset.lecId;
            document.getElementById('lecTitleInput').value = btn.dataset.title || '';
            document.getElementById('lecDescInput').value = btn.dataset.desc || '';
            document.getElementById('lecNumInput').value = btn.dataset.num || '1';
            document.getElementById('lecDurationInput').value = btn.dataset.dur || '600';
            document.getElementById('lecCloudinaryInput').value = btn.dataset.cld || '';
            document.getElementById('lecStatusInput').value = btn.dataset.status || 'PUBLISHED';
            document.getElementById('lecPreviewInput').checked = (btn.dataset.prev === 'true');
            document.getElementById('lectureModal').classList.add('active');
        }

        function openUploadModalFromBtn(btn) {
            document.getElementById('uploadLectureId').value = btn.dataset.lecId;
            document.getElementById('uploadLectureTitle').textContent = btn.dataset.title || '';
            document.getElementById('uploadProgressBox').style.display = 'none';
            document.getElementById('uploadModal').classList.add('active');
        }

        var uploadForm = document.getElementById('uploadForm');
        if (uploadForm) {
            uploadForm.addEventListener('submit', function() {
                var progressBox = document.getElementById('uploadProgressBox');
                var startBtn = document.getElementById('btnStartUpload');
                var barFill = document.getElementById('uploadBarFill');
                if (progressBox) progressBox.style.display = 'block';
                if (startBtn) startBtn.disabled = true;
                if (barFill) barFill.style.width = '70%';
            });
        }
    </script>
</body>
</html>

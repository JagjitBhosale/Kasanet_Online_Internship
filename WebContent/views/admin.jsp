<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String loginSuccessParam = request.getParameter("loginSuccess");
    boolean isLoginSuccess = "1".equals(loginSuccessParam) || session.getAttribute("loginSuccessAlert") != null;
    if (session.getAttribute("loginSuccessAlert") != null) {
        session.removeAttribute("loginSuccessAlert");
    }

    String flashMsg = (String) session.getAttribute("adminFlashMsg");
    String flashType = (String) session.getAttribute("adminFlashType");
    if (flashMsg != null) {
        session.removeAttribute("adminFlashMsg");
        session.removeAttribute("adminFlashType");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Industrial Training Details - Admin Portal - My Internship Technologies</title>
    <!-- Google Fonts for sleek modern typography -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
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
            --sidebar-active: #1e293b;
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
            height: 38px;
            width: auto;
            max-width: 44px;
            border-radius: 6px;
            background: #ffffff;
            padding: 3px;
            object-fit: contain;
        }

        .sidebar-header-text h2 {
            font-size: 1.05rem;
            font-weight: 700;
            line-height: 1.2;
            letter-spacing: -0.2px;
        }

        .sidebar-header-text span {
            font-size: 0.75rem;
            opacity: 0.85;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .sidebar-menu {
            list-style: none;
            padding: 14px 0;
            flex: 1;
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
            width: 22px;
            text-align: center;
            display: inline-block;
        }

        .sidebar-link:hover {
            color: #ffffff;
            background-color: rgba(255, 255, 255, 0.06);
        }

        .sidebar-link.active {
            color: #ffffff;
            background-color: rgba(0, 0, 0, 0.28);
            border-left-color: #ef4444;
            font-weight: 600;
        }

        .sidebar-link .chevron {
            font-size: 0.8rem;
            opacity: 0.6;
            transition: transform 0.2s;
        }

        /* ================= MAIN WRAPPER ================= */
        .main-wrapper {
            flex: 1;
            margin-left: 260px;
            display: flex;
            flex-direction: column;
            min-width: 0;
            transition: margin-left 0.28s ease;
        }

        .main-wrapper.full-width {
            margin-left: 0;
        }

        /* ================= TOP NAVBAR ================= */
        .topbar {
            height: 56px;
            background-color: #ffffff;
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
            border-radius: 6px;
            padding: 6px 10px;
            cursor: pointer;
            font-size: 1.2rem;
            color: var(--text-main);
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.15s;
        }

        .btn-toggle-sidebar:hover {
            background-color: #f1f5f9;
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
            color: var(--text-main);
            font-weight: 600;
        }

        .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #475569;
            font-weight: 700;
            font-size: 0.85rem;
        }

        .nav-btn {
            font-size: 0.84rem;
            font-weight: 600;
            text-decoration: none;
            padding: 6px 14px;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .nav-btn-outline {
            color: var(--accent);
            border: 1px solid #bfdbfe;
            background: #eff6ff;
        }
        .nav-btn-outline:hover {
            background: #dbeafe;
        }

        .nav-btn-danger {
            color: #ffffff;
            background: var(--danger);
        }
        .nav-btn-danger:hover {
            background: var(--danger-hover);
        }

        /* ================= PAGE CONTAINER ================= */
        .page-content {
            padding: 24px 28px;
            flex: 1;
        }

        /* Flash Message Alert */
        .alert-banner {
            padding: 12px 18px;
            border-radius: 6px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 0.9rem;
            font-weight: 500;
            box-shadow: var(--shadow-sm);
        }
        .alert-success {
            background-color: #ecfdf5;
            color: #065f46;
            border: 1px solid #a7f3d0;
        }
        .alert-danger {
            background-color: #fef2f2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        /* Header Area matching screenshot */
        .content-header {
            margin-bottom: 20px;
        }

        .page-title {
            font-size: 1.55rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 4px;
        }

        .student-counter {
            font-size: 0.92rem;
            color: #475569;
            font-weight: 600;
            margin-bottom: 18px;
        }

        .student-counter span {
            color: #1e293b;
            font-weight: 700;
        }

        /* Top Action Buttons (Delete button in photo) */
        .top-action-bar {
            margin-bottom: 14px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .btn-top-delete {
            background-color: #ffffff;
            color: #334155;
            border: 1px solid #cbd5e1;
            padding: 6px 18px;
            font-size: 0.88rem;
            font-weight: 600;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.18s;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
        }

        .btn-top-delete:hover {
            background-color: #fef2f2;
            color: #dc2626;
            border-color: #f87171;
        }

        .selected-counter-badge {
            font-size: 0.8rem;
            background: #fee2e2;
            color: #b91c1c;
            padding: 2px 7px;
            border-radius: 10px;
            font-weight: 700;
            display: none;
        }

        /* Controls row: Export to EXCEL, Show X rows, Search */
        .table-controls-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 14px;
            flex-wrap: wrap;
            gap: 14px;
        }

        .controls-left {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn-export-excel {
            background-color: #ffffff;
            color: #334155;
            border: 1px solid #cbd5e1;
            padding: 6px 14px;
            font-size: 0.85rem;
            font-weight: 600;
            border-radius: 4px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: all 0.18s;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
        }

        .btn-export-excel:hover {
            background-color: #f0fdf4;
            color: #15803d;
            border-color: #86efac;
        }

        .btn-export-excel svg {
            width: 15px;
            height: 15px;
            fill: currentColor;
        }

        .rows-select-wrapper {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.88rem;
            color: #475569;
        }

        .rows-select {
            padding: 5px 10px;
            border: 1px solid #cbd5e1;
            border-radius: 4px;
            font-size: 0.86rem;
            background-color: #ffffff;
            color: #334155;
            outline: none;
            cursor: pointer;
            font-weight: 500;
        }

        .rows-select:focus {
            border-color: var(--accent);
        }

        .controls-right {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .search-label {
            font-size: 0.88rem;
            color: #475569;
            font-weight: 500;
        }

        .search-input-field {
            padding: 6px 12px;
            border: 1px solid #cbd5e1;
            border-radius: 4px;
            font-size: 0.88rem;
            width: 220px;
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
            background-color: #ffffff;
        }

        .search-input-field:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.12);
        }

        /* ================= TABLE CONTAINER ================= */
        .table-card {
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 6px;
            box-shadow: var(--shadow-sm);
            overflow: hidden;
            margin-bottom: 16px;
        }

        .table-responsive {
            width: 100%;
            overflow-x: auto;
        }

        .student-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.86rem;
            text-align: left;
            white-space: nowrap;
        }

        .student-table th {
            background-color: #f8fafc;
            color: #334155;
            font-weight: 700;
            padding: 10px 14px;
            border-bottom: 2px solid var(--border-color);
            border-right: 1px solid #edf2f7;
            user-select: none;
        }

        .student-table th:last-child {
            border-right: none;
        }

        .student-table td {
            padding: 11px 14px;
            border-bottom: 1px solid var(--border-color);
            border-right: 1px solid #f1f5f9;
            color: #334155;
            vertical-align: middle;
        }

        .student-table td:last-child {
            border-right: none;
        }

        .student-table tbody tr {
            transition: background-color 0.12s;
        }

        .student-table tbody tr:hover {
            background-color: #f8fafc;
        }

        .student-table tbody tr.row-selected {
            background-color: #eff6ff !important;
        }

        /* Checkbox styling */
        .table-checkbox {
            width: 16px;
            height: 16px;
            cursor: pointer;
            accent-color: #2563eb;
            vertical-align: middle;
        }

        .col-checkbox {
            width: 40px;
            text-align: center;
        }

        .col-srno {
            width: 75px;
            font-weight: 600;
            color: #475569;
        }

        .student-name {
            font-weight: 600;
            color: #1e293b;
        }

        .email-cell {
            color: #2563eb;
        }

        .branch-badge {
            display: inline-block;
            background-color: #f1f5f9;
            color: #475569;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 500;
            max-width: 260px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .project-report-fact {
            min-width: 0;
            padding: 10px 12px;
            border: 1px solid #e1e8f3;
            border-radius: 8px;
            color: #26364f;
            background: #fff;
            overflow-wrap: anywhere;
        }
        .project-report-fact strong {
            display: block;
            margin-bottom: 4px;
            color: #3f506b;
            font-size: 12px;
            letter-spacing: .04em;
            text-transform: uppercase;
        }
        .project-student-list {
            display: grid;
            gap: 10px;
        }
        .project-student-card {
            overflow: hidden;
            border: 1px solid #dbe5f1;
            border-left: 4px solid #4f7fe8;
            border-radius: 10px;
            background: #fff;
            box-shadow: 0 3px 10px rgba(30, 41, 86, .05);
        }
        .project-student-summary {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 14px 18px;
            cursor: pointer;
            list-style: none;
        }
        .project-student-summary::-webkit-details-marker {
            display: none;
        }
        .project-student-identity {
            display: flex;
            flex-wrap: wrap;
            align-items: baseline;
            column-gap: 12px;
            row-gap: 4px;
        }
        .project-student-actions {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-shrink: 0;
        }
        .project-student-toggle {
            color: #2563eb;
            font-size: 1.4rem;
            font-weight: 700;
            line-height: 1;
        }
        .project-student-toggle::before {
            content: '+';
        }
        .project-student-card[open] > .project-student-summary .project-student-toggle::before {
            content: '−';
        }
        .project-student-name {
            margin: 0 0 3px;
            color: #172554;
            font-size: 1rem;
        }
        .project-student-contact {
            color: #64748b;
            font-size: .9rem;
        }
        .project-count-badge {
            flex-shrink: 0;
            padding: 4px 9px;
            border-radius: 999px;
            background: #eff6ff;
            color: #1d4ed8;
            font-size: .82rem;
            font-weight: 600;
        }
        .project-student-reports {
            display: grid;
            gap: 12px;
            padding: 0 16px 16px;
        }
        .project-report-card {
            padding: 16px;
            border: 1px solid #e1e8f3;
            border-radius: 8px;
            background: #f8fafc;
        }
        .project-report-details-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 10px;
            margin-top: 12px;
        }

        /* Action Buttons */
        .btn-row-delete {
            background-color: transparent;
            color: #ef4444;
            border: 1px solid #fecaca;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.78rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.15s;
            display: inline-flex;
            align-items: center;
            gap: 4px;
        }

        .btn-row-delete:hover {
            background-color: #ef4444;
            color: #ffffff;
            border-color: #ef4444;
        }

        /* Empty state */
        .empty-row-td {
            text-align: center;
            padding: 45px 20px !important;
            color: var(--text-muted);
            font-size: 0.95rem;
        }

        /* ================= PAGINATION BAR ================= */
        .table-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 14px 4px;
            flex-wrap: wrap;
            gap: 12px;
            font-size: 0.88rem;
            color: #475569;
        }

        .entries-info {
            font-weight: 500;
        }

        .pagination-container {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .page-btn {
            background-color: #ffffff;
            border: 1px solid #cbd5e1;
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 0.84rem;
            font-weight: 600;
            color: #334155;
            cursor: pointer;
            transition: all 0.15s;
            user-select: none;
        }

        .page-btn:hover:not(:disabled) {
            background-color: #f1f5f9;
            border-color: #94a3b8;
            color: var(--accent);
        }

        .page-btn.active {
            background-color: #2563eb;
            color: #ffffff;
            border-color: #2563eb;
        }

        .page-btn:disabled {
            opacity: 0.45;
            cursor: not-allowed;
        }

        /* ================= MODAL DIALOGS ================= */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(3px);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 16px;
            animation: fadeIn 0.18s ease;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-box {
            background: #ffffff;
            border-radius: 8px;
            max-width: 460px;
            width: 100%;
            padding: 24px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.25);
            animation: scaleIn 0.2s ease;
        }

        .modal-box-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 14px;
        }

        .modal-box-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.25rem;
            flex-shrink: 0;
        }

        .modal-box-icon.danger {
            background-color: #fee2e2;
            color: #dc2626;
        }

        .modal-box-icon.info {
            background-color: #e0f2fe;
            color: #0284c7;
        }

        .modal-box-title {
            font-size: 1.15rem;
            font-weight: 700;
            color: #1e293b;
        }

        .modal-box-body {
            font-size: 0.92rem;
            color: #475569;
            line-height: 1.5;
            margin-bottom: 22px;
        }

        .modal-box-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .modal-btn {
            padding: 8px 18px;
            border-radius: 6px;
            font-size: 0.88rem;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: all 0.15s;
        }

        .modal-btn-cancel {
            background-color: #f1f5f9;
            color: #475569;
            border: 1px solid #cbd5e1;
        }
        .modal-btn-cancel:hover {
            background-color: #e2e8f0;
        }

        .modal-btn-danger {
            background-color: #dc2626;
            color: #ffffff;
        }
        .modal-btn-danger:hover {
            background-color: #b91c1c;
        }

        /* Toast notification */
        .toast-container {
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 10000;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .toast {
            background: #1e293b;
            color: #ffffff;
            padding: 12px 20px;
            border-radius: 6px;
            font-size: 0.88rem;
            font-weight: 500;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.2);
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideUp 0.25s ease;
        }

        .toast-success {
            border-left: 4px solid #22c55e;
        }

        .toast-error {
            border-left: 4px solid #ef4444;
        }

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes scaleIn { from { transform: scale(0.96); opacity: 0; } to { transform: scale(1); opacity: 1; } }
        @keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

        /* Responsive styles */
        @media (max-width: 992px) {
            .project-report-details-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
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
                display: none;
                position: fixed;
                inset: 0;
                background: rgba(0, 0, 0, 0.5);
                z-index: 99;
            }
            .sidebar-backdrop.show {
                display: block;
            }
        }
        @media (max-width: 620px) {
            .project-report-details-grid {
                grid-template-columns: minmax(0, 1fr);
            }
        }
    </style>
</head>
<body>

    <!-- Mobile Sidebar Backdrop -->
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
                <a href="javascript:void(0)" class="sidebar-link active" id="tabNavStudents" onclick="switchAdminTab('students')">
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
                <a href="javascript:void(0)" class="sidebar-link" id="tabNavCerts" onclick="switchAdminTab('certificates')">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📜</span>
                        <span>Global Certification</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="javascript:void(0)" class="sidebar-link" id="tabNavOfferLetter" onclick="switchAdminTab('offer-letter')">
                    <span class="sidebar-link-content">
                        <span class="sidebar-link-icon">📄</span>
                        <span>Certification</span>
                    </span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="javascript:void(0)" class="sidebar-link" id="tabNavProjects" onclick="switchAdminTab('projects')">
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
                <div class="portal-breadcrumb" id="topbarBreadcrumb">
                    Online Internship Administration / Student Details
                </div>
            </div>

            <div class="topbar-right">
                <div class="user-pill">
                    <div class="user-avatar">
                        ${not empty sessionScope.userName ? sessionScope.userName.substring(0, 1).toUpperCase() : 'A'}
                    </div>
                    <span>${not empty sessionScope.userName ? sessionScope.userName : 'Admin'}</span>
                </div>
                <a href="${pageContext.request.contextPath}/" class="nav-btn nav-btn-outline" target="_blank">Public Site</a>
                <button type="button" class="nav-btn nav-btn-danger" onclick="handleAdminLogout()">Logout</button>
            </div>
        </header>

        <!-- Main Page Content -->
        <main class="page-content">

            <!-- Flash Message Banner -->
            <% if (flashMsg != null) { %>
            <div class="alert-banner <%= "danger".equals(flashType) ? "alert-danger" : "alert-success" %>">
                <span><%= flashMsg %></span>
                <button type="button" style="background:none; border:none; cursor:pointer; font-weight:700;" onclick="this.parentElement.remove()">✕</button>
            </div>
            <% } %>

            <!-- ================= STUDENT DETAILS TAB PANE ================= -->
            <div id="sectionStudents" class="admin-tab-pane">
                <!-- Page Title & Student Count -->
                <div class="content-header">
                    <h1 class="page-title">Industrial Training Details</h1>
                    <div class="student-counter">
                        No Of Student: <span id="studentCountDisplay">${studentCount != null ? studentCount : (totalUsers != null ? totalUsers : 0)}</span>
                    </div>
                </div>

                <!-- Top Action Button: Delete -->
                <div class="top-action-bar">
                    <button type="button" class="btn-top-delete" id="topDeleteBtn" onclick="confirmBulkDelete()">
                        🗑 Delete <span class="selected-counter-badge" id="selectedCountBadge">0</span>
                    </button>
                </div>

                <!-- Table Controls Bar: Export to EXCEL, Show X rows, Search -->
                <div class="table-controls-bar">
                    <div class="controls-left">
                        <button type="button" class="btn-export-excel" onclick="exportTableToExcel()">
                            <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
                            Export to EXCEL
                        </button>

                        <div class="rows-select-wrapper">
                            <span>Show</span>
                            <select id="rowsPerPageSelect" class="rows-select" onchange="changeRowsPerPage()">
                                <option value="5">5</option>
                                <option value="10" selected>10</option>
                                <option value="25">25</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                            </select>
                            <span>rows</span>
                        </div>
                    </div>

                    <div class="controls-right">
                        <label for="tableSearchInput" class="search-label">Search:</label>
                        <input type="text" id="tableSearchInput" class="search-input-field" placeholder="Search..." oninput="handleSearch()">
                    </div>
                </div>

                <!-- Student Data Table -->
                <div class="table-card">
                    <div class="table-responsive">
                        <table class="student-table" id="studentsTable">
                            <thead>
                                <tr>
                                    <th class="col-checkbox">
                                        <input type="checkbox" id="selectAllCheckbox" class="table-checkbox" onchange="toggleSelectAll(this)">
                                    </th>
                                    <th class="col-srno">Sr.No</th>
                                    <th>Full Name</th>
                                    <th>Email Id</th>
                                    <th>Contact no</th>
                                    <th>College Name</th>
                                    <th>Stream</th>
                                    <th>Branch</th>
                                    <th style="text-align: center; width: 90px;">Action</th>
                                </tr>
                            </thead>
                            <tbody id="studentsTableBody">
                                <c:choose>
                                    <c:when test="${not empty userList}">
                                        <c:forEach items="${userList}" var="u" varStatus="status">
                                            <c:if test="${(empty u.role || fn:toUpperCase(fn:trim(u.role)) == 'STUDENT') and fn:toUpperCase(fn:trim(u.role)) != 'ADMIN' and not fn:containsIgnoreCase(u.email, 'admin')}">
                                                <%
                                                    com.kasnet.model.User uObj = (com.kasnet.model.User) pageContext.getAttribute("u");
                                                    String branchDisplay = "Other";
                                                    if (uObj != null) {
                                                        try {
                                                            java.lang.reflect.Method m = uObj.getClass().getMethod("getBranch");
                                                            Object bVal = m.invoke(uObj);
                                                            if (bVal != null && !bVal.toString().trim().isEmpty() && !"-".equals(bVal.toString().trim())) {
                                                                branchDisplay = bVal.toString().trim();
                                                            }
                                                        } catch (Throwable ignore) {
                                                            branchDisplay = "Other";
                                                        }
                                                    }
                                                    pageContext.setAttribute("branchDisplay", branchDisplay);
                                                %>
                                                <tr data-user-id="${u.userId}"
                                                    data-srno="${status.count}"
                                                    data-name="${u.firstName} ${u.lastName}"
                                                    data-email="${u.email}"
                                                    data-mobile="${u.mobile}"
                                                    data-college="${u.collegeName}"
                                                    data-stream="${u.stream}"
                                                    data-branch="${branchDisplay}">
                                                    <td class="col-checkbox">
                                                        <input type="checkbox" class="table-checkbox row-select-checkbox" value="${u.userId}" onchange="handleRowSelect(this)">
                                                    </td>
                                                    <td class="col-srno">${status.count}</td>
                                                    <td class="student-name">${u.firstName} ${u.lastName}</td>
                                                    <td class="email-cell">${u.email}</td>
                                                    <td>${u.mobile != null && not empty u.mobile ? u.mobile : '-'}</td>
                                                    <td>${u.collegeName != null && not empty u.collegeName ? u.collegeName : '-'}</td>
                                                    <td>${u.stream != null && not empty u.stream ? u.stream : '-'}</td>
                                                    <td>
                                                        <span class="branch-badge">
                                                            <c:out value="${branchDisplay}" />
                                                        </span>
                                                    </td>
                                                    <td style="text-align: center;">
                                                        <button type="button" class="btn-row-delete" onclick="confirmSingleDelete(${u.userId}, '${u.firstName} ${u.lastName}')" title="Delete Student Record">
                                                            🗑 Delete
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr id="noDataRow">
                                            <td colspan="9" class="empty-row-td">
                                                No student records found in the database.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Table Footer: Showing X to Y of Z entries & Pagination -->
                <div class="table-footer">
                    <div class="entries-info" id="entriesInfo">
                        Showing 0 to 0 of 0 entries
                    </div>

                    <div class="pagination-container" id="paginationControls">
                        <!-- Dynamic page buttons injected by JS -->
                    </div>
                </div>
            </div>

            <!-- ================= OFFER LETTER CERTIFICATION TAB PANE ================= -->
            <div id="sectionOfferLetter" class="admin-tab-pane" style="display:none;">
                <div class="content-header">
                    <h1 class="page-title">Student Offer Letter</h1>
                </div>
                <div class="table-card" style="max-width:1100px;padding:24px;">
                    <form id="offerLetterForm" method="post" action="${pageContext.request.contextPath}/admin/offer-letter" target="_blank" accept-charset="UTF-8"
                          style="display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:18px 24px;">
                        <div>
                            <label for="offerName" style="display:block;font-weight:600;margin-bottom:7px;">Name</label>
                            <input id="offerName" name="name" type="text" maxlength="100" required autocomplete="name"
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerCollege" style="display:block;font-weight:600;margin-bottom:7px;">College</label>
                            <input id="offerCollege" name="college" type="text" maxlength="150" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerBranch" style="display:block;font-weight:600;margin-bottom:7px;">Branch</label>
                            <input id="offerBranch" name="branch" type="text" maxlength="100" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerDesignation" style="display:block;font-weight:600;margin-bottom:7px;">Designation</label>
                            <input id="offerDesignation" name="designation" type="text" maxlength="100" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerDuration" style="display:block;font-weight:600;margin-bottom:7px;">Duration</label>
                            <input id="offerDuration" name="duration" type="text" value="4 Months" pattern="[0-9]{1,2}[ ]*[Mm]onths?" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="internshipStartDate" style="display:block;font-weight:600;margin-bottom:7px;">Internship Start Date</label>
                            <input id="internshipStartDate" name="internshipStartDate" type="date" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerLetterDate" style="display:block;font-weight:600;margin-bottom:7px;">Offer Letter Date</label>
                            <input id="offerLetterDate" name="offerLetterDate" type="date" required
                                   style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;">
                        </div>
                        <div>
                            <label for="offerCertificate" style="display:block;font-weight:600;margin-bottom:7px;">Select Certificate</label>
                            <select id="offerCertificate" name="certificate" required
                                    style="width:100%;height:42px;padding:0 12px;border:1px solid #d5d9e2;border-radius:4px;background:#fff;">
                                <option value="Kasnet Offer Letter">Kasnet Offer Letter</option>
                            </select>
                        </div>
                        <div style="grid-column:1/-1;display:flex;justify-content:flex-end;gap:10px;margin-top:8px;">
                            <button type="submit" class="btn-export-excel" style="height:42px;background:#45b4ad;color:#fff;border:0;padding:0 22px;cursor:pointer;">Generate Offer Letter</button>
                            <button type="reset" class="btn-export-excel" style="height:42px;background:#fff;color:#45b4ad;border:1px solid #45b4ad;padding:0 20px;cursor:pointer;">Reset</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- ================= GLOBAL CERTIFICATION TAB PANE ================= -->
            <div id="sectionCertificates" class="admin-tab-pane" style="display:none;">
                <!-- Page Title & Cert Count -->
                <div class="content-header">
                    <h1 class="page-title">Global Certification</h1>
                    <div class="student-counter" style="background:#e0f2fe; border-color:#bae6fd; color:#0369a1;">
                        No Of Global Certifications: <c:out value="${platformCertificateCount}"/>
                    </div>
                </div>

                <div class="table-controls-bar" id="globalCertificationControls">
                    <div class="controls-left">
                        <button type="button" class="btn-export-excel" onclick="exportGlobalCertsToExcel()">
                            <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
                            Export to EXCEL
                        </button>
                    </div>
                </div>

                <div class="table-card" id="globalCertificationTable">
                    <div class="table-responsive">
                        <table class="student-table" id="globalCertTable">
                            <thead>
                                <tr>
                                    <th class="col-srno">Sr.No</th>
                                    <th>Student Name</th>
                                    <th>Student Email</th>
                                    <th>Exam</th>
                                    <th>Score</th>
                                    <th>Date Achieved</th>
                                </tr>
                            </thead>
                            <tbody id="globalCertTableBody">
                                <c:choose>
                                    <c:when test="${not empty platformCertificateList}">
                                        <c:forEach items="${platformCertificateList}" var="platformCertificate" varStatus="certificateStatus">
                                            <tr>
                                                <td class="col-srno"><c:out value="${certificateStatus.count}"/></td>
                                                <td class="student-name"><c:out value="${platformCertificate.studentName}"/></td>
                                                <td class="email-cell"><c:out value="${platformCertificate.studentEmail}"/></td>
                                                <td><c:out value="${platformCertificate.examName}"/></td>
                                                <td><fmt:formatNumber value="${platformCertificate.scoreObtained}" minFractionDigits="0" maxFractionDigits="2" groupingUsed="false"/>/1000</td>
                                                <td><c:out value="${platformCertificate.achievementDate}"/></td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="6" class="empty-row-td">No global certification results have been recorded.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="content-header" id="additionalCertificateHeader">
                    <h2 class="page-title">Additional Certificate</h2>
                    <div class="student-counter" style="background:#e0f2fe; border-color:#bae6fd; color:#0369a1;">
                        No Of Additional Certificates: <span id="certCountDisplay">${certCount != null ? certCount : 0}</span>
                    </div>
                </div>
                <!-- Table Controls Bar -->
                <div class="table-controls-bar" id="additionalCertificateControls">
                    <div class="controls-left">
                        <button type="button" class="btn-export-excel" onclick="exportCertsToExcel()">
                            <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
                            Export to EXCEL
                        </button>
                        <div class="rows-select-wrapper">
                            <span>Show</span>
                            <select id="certRowsPerPageSelect" class="rows-select" onchange="changeCertRowsPerPage()">
                                <option value="5">5</option>
                                <option value="10" selected>10</option>
                                <option value="25">25</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                            </select>
                            <span>rows</span>
                        </div>
                    </div>

                    <div class="controls-right">
                        <label for="certSearchInput" class="search-label">Search:</label>
                        <input type="text" id="certSearchInput" class="search-input-field" placeholder="Search certificates..." oninput="handleCertSearch()">
                    </div>
                </div>

                <!-- Certificates Data Table -->
                <div class="table-card" id="additionalCertificateTable">
                    <div class="table-responsive">
                        <table class="student-table" id="certsTable">
                            <thead>
                                <tr>
                                    <th class="col-srno">Sr.No</th>
                                    <th>Student Name</th>
                                    <th>Student Email</th>
                                    <th>Certificate Title</th>
                                    <th>Provider / Issuer</th>
                                    <th>Technology</th>
                                    <th>Issue Date</th>
                                    <th>Credential ID / Link</th>
                                    <th style="text-align: center; width: 90px;">Action</th>
                                </tr>
                            </thead>
                            <tbody id="certsTableBody">
                                <c:choose>
                                    <c:when test="${not empty certList}">
                                        <c:forEach items="${certList}" var="c" varStatus="cStatus">
                                            <tr data-cert-id="${c.certId}"
                                                data-student-name="${c.studentName}"
                                                data-student-email="${c.studentEmail}"
                                                data-cert-name="${c.certName}"
                                                data-provider="${c.provider}"
                                                data-tech="${c.technology}">
                                                <td class="col-srno">${cStatus.count}</td>
                                                <td class="student-name">${c.studentName}</td>
                                                <td class="email-cell">${c.studentEmail}</td>
                                                <td style="font-weight: 600;">${c.certName}</td>
                                                <td>${not empty c.provider ? c.provider : '-'}</td>
                                                <td>
                                                    <span class="branch-badge">
                                                        ${not empty c.technology ? c.technology : 'General'}
                                                    </span>
                                                </td>
                                                <td>${not empty c.issueDate ? c.issueDate : '-'}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty c.credentialUrl}">
                                                            <a href="${c.credentialUrl}" target="_blank" style="color:#2563eb; text-decoration:underline; font-weight:500;">
                                                                🔗 View Link
                                                            </a>
                                                        </c:when>
                                                        <c:when test="${not empty c.certIdNumber}">
                                                            <span style="font-family:monospace; font-size:0.84rem; background:#f1f5f9; padding:2px 6px; border-radius:3px;">
                                                                ${c.certIdNumber}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>-</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td style="text-align: center;">
                                                    <button type="button" class="btn-row-delete" onclick="confirmDeleteCert(${c.certId}, '${c.certName}', '${c.studentName}')" title="Delete Certificate Record">
                                                        🗑 Delete
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr id="noCertDataRow">
                                            <td colspan="9" class="empty-row-td">
                                                No student certificate records found.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Table Footer: Showing X to Y of Z entries & Pagination -->
                <div class="table-footer" id="additionalCertificateFooter">
                    <div class="entries-info" id="certEntriesInfo">
                        Showing 0 to 0 of 0 entries
                    </div>
                    <div class="pagination-container" id="certPaginationControls"></div>
                </div>

            </div>

            <div id="sectionProjectReports" class="admin-tab-pane" style="display:none;">
                <div class="content-header">
                    <h1 class="page-title">Student Project Reports</h1>
                    <div class="student-counter" style="background:#eff6ff;border-color:#bfdbfe;color:#1d4ed8;">
                        No Of Reports: <c:out value="${platformProjectReportCount}"/>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty platformProjectReportsByStudent}">
                        <div class="project-student-list">
                            <c:forEach items="${platformProjectReportsByStudent}" var="studentReports">
                                <details class="project-student-card">
                                    <summary class="project-student-summary">
                                        <span class="project-student-identity">
                                            <strong class="project-student-name"><c:out value="${studentReports.value[0].studentName}"/></strong>
                                            <span class="project-student-contact"><c:out value="${studentReports.value[0].studentEmail}"/></span>
                                            <c:if test="${not empty studentReports.value[0].studentMobile}">
                                                <span class="project-student-contact"><c:out value="${studentReports.value[0].studentMobile}"/></span>
                                            </c:if>
                                        </span>
                                        <span class="project-student-actions">
                                            <span class="project-count-badge">
                                                <c:out value="${fn:length(studentReports.value)}"/> project<c:if test="${fn:length(studentReports.value) != 1}">s</c:if>
                                            </span>
                                            <span class="project-student-toggle" aria-hidden="true"></span>
                                        </span>
                                    </summary>
                                    <div class="project-student-reports">
                                        <c:forEach items="${studentReports.value}" var="projectReport">
                                            <article class="project-report-card">
                                                <div style="display:flex;flex-wrap:wrap;justify-content:space-between;gap:8px;align-items:start;">
                                                    <h2 style="margin:0;color:#172554;font-size:1.1rem;"><c:out value="${projectReport.projectName}"/></h2>
                                                </div>
                                                <p style="margin:12px 0;color:#334155;line-height:1.6;white-space:pre-wrap;"><c:out value="${projectReport.scopeDetails}"/></p>
                                                <details style="margin-top:14px;padding:12px;border:1px solid #e1e8f3;border-radius:8px;background:#fff;">
                                                    <summary style="color:#1d4ed8;cursor:pointer;font-weight:700;">View all project details</summary>
                                                    <div class="project-report-details-grid">
                                                        <c:if test="${not empty projectReport.projectDomain}"><div class="project-report-fact"><strong>Domain</strong><c:out value="${projectReport.projectDomain}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectTechnology}"><div class="project-report-fact"><strong>Technology</strong><c:out value="${projectReport.projectTechnology}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectApplication}"><div class="project-report-fact"><strong>Application</strong><c:out value="${projectReport.projectApplication}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectDuration}"><div class="project-report-fact"><strong>Duration</strong><c:out value="${projectReport.projectDuration}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectModel}"><div class="project-report-fact"><strong>Model</strong><c:out value="${projectReport.projectModel}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectCost}"><div class="project-report-fact"><strong>Estimated project cost</strong><c:out value="${projectReport.projectCost}"/></div></c:if>
                                                        <div class="project-report-fact"><strong>Issues found</strong><c:out value="${projectReport.issuesFound}"/></div>
                                                        <c:if test="${not empty projectReport.testingType}"><div class="project-report-fact"><strong>Testing type</strong><c:out value="${projectReport.testingType}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.testingDuration}"><div class="project-report-fact"><strong>Testing duration</strong><c:out value="${projectReport.testingDuration}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.requirementGatheringTime}"><div class="project-report-fact"><strong>Requirement gathering</strong><c:out value="${projectReport.requirementGatheringTime}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectAnalysisTime}"><div class="project-report-fact"><strong>Data analysis</strong><c:out value="${projectReport.projectAnalysisTime}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectDesigningTime}"><div class="project-report-fact"><strong>Data cleaning</strong><c:out value="${projectReport.projectDesigningTime}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectCodingTime}"><div class="project-report-fact"><strong>Data modeling</strong><c:out value="${projectReport.projectCodingTime}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.projectTestingTime}"><div class="project-report-fact"><strong>Data visualization</strong><c:out value="${projectReport.projectTestingTime}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgeRequirement}"><div class="project-report-fact"><strong>Requirement learning</strong><c:out value="${projectReport.knowledgeRequirement}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgeAnalysis}"><div class="project-report-fact"><strong>Analysis learning</strong><c:out value="${projectReport.knowledgeAnalysis}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgeDesigning}"><div class="project-report-fact"><strong>Design learning</strong><c:out value="${projectReport.knowledgeDesigning}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgeCoding}"><div class="project-report-fact"><strong>Coding learning</strong><c:out value="${projectReport.knowledgeCoding}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgeTesting}"><div class="project-report-fact"><strong>Testing learning</strong><c:out value="${projectReport.knowledgeTesting}"/></div></c:if>
                                                        <c:if test="${not empty projectReport.knowledgePostImplementation}"><div class="project-report-fact"><strong>Post-implementation learning</strong><c:out value="${projectReport.knowledgePostImplementation}"/></div></c:if>
                                                    </div>
                                                </details>
                                            </article>
                                        </c:forEach>
                                    </div>
                                </details>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty-row-td">No student project reports have been submitted yet.</p></c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>

    <!-- Hidden forms for fallback POST delete -->
    <form id="hiddenDeleteForm" method="POST" action="${pageContext.request.contextPath}/admin" style="display:none;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="userId" id="deleteFormUserId" value="">
        <input type="hidden" name="userIdsCsv" id="deleteFormUserIdsCsv" value="">
    </form>

    <form id="hiddenCertDeleteForm" method="POST" action="${pageContext.request.contextPath}/admin" style="display:none;">
        <input type="hidden" name="action" value="deleteCert">
        <input type="hidden" name="certId" id="deleteCertFormId" value="">
    </form>

    <!-- Toast Container -->
    <div class="toast-container" id="toastContainer"></div>

    <!-- ================= JAVASCRIPT LOGIC ================= -->
    <script>
        // State variables for Students table
        var allRows = [];
        var filteredRows = [];
        var currentPage = 1;
        var rowsPerPage = 10;
        var selectedUserIds = new Set();

        // State variables for Certificates table
        var allCertRows = [];
        var filteredCertRows = [];
        var certCurrentPage = 1;
        var certRowsPerPage = 10;

        document.addEventListener('DOMContentLoaded', function() {
            initTableData();
            initCertTableData();

            // Restore the selected admin section from the URL.
            var urlParams = new URLSearchParams(window.location.search);
            var activeTab = urlParams.get('tab');
            if (activeTab === 'certificates' || activeTab === 'projects' || activeTab === 'offer-letter') {
                switchAdminTab(activeTab);
            }

            <% if (flashMsg != null && !flashMsg.trim().isEmpty()) { %>
            window.alert("<%= flashMsg.replace("\"", "\\\"").replace("\r", "").replace("\n", " ") %>");
            <% } %>

            <% if (isLoginSuccess) { %>
            window.alert("Login Successful!\n\nWelcome back to the Administrator Console, <%= session.getAttribute("userName") != null ? session.getAttribute("userName") : "Administrator" %>.");
            if (window.history && window.history.replaceState) {
                var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history.replaceState({}, document.title, cleanUrl);
            }
            <% } %>
        });

        // ================= TAB SWITCHING =================
        function switchAdminTab(tabName) {
            var sections = {
                students: document.getElementById('sectionStudents'),
                certificates: document.getElementById('sectionCertificates'),
                'offer-letter': document.getElementById('sectionOfferLetter'),
                projects: document.getElementById('sectionProjectReports')
            };
            var navigation = {
                students: document.getElementById('tabNavStudents'),
                certificates: document.getElementById('tabNavCerts'),
                'offer-letter': document.getElementById('tabNavOfferLetter'),
                projects: document.getElementById('tabNavProjects')
            };
            if (!sections[tabName]) tabName = 'students';
            Object.keys(sections).forEach(function (name) {
                if (sections[name]) sections[name].style.display = name === tabName ? 'block' : 'none';
                if (navigation[name]) navigation[name].classList.toggle('active', name === tabName);
            });
            var breadcrumb = document.getElementById('topbarBreadcrumb');
            var labels = {
                students: 'Student Details',
                certificates: 'Global Certification',
                'offer-letter': 'Certification / Student Offer Letter',
                projects: 'Student Project Reports'
            };
            if (breadcrumb) breadcrumb.textContent = 'Online Internship Administration / ' + labels[tabName];
            if (window.history && window.history.replaceState) {
                var newUrl = window.location.pathname + '?tab=' + encodeURIComponent(tabName);
                window.history.replaceState({ tab: tabName }, '', newUrl);
            }
        }

        // Logout with Chrome confirm dialog
        function handleAdminLogout() {
            if (window.confirm("Are you sure you want to log out from the Administrator Console?")) {
                window.location.href = '${pageContext.request.contextPath}/logout';
            }
        }

        // Toggle Sidebar
        function toggleSidebar() {
            var sidebar = document.getElementById('sidebar');
            var mainWrapper = document.getElementById('mainWrapper');
            var backdrop = document.getElementById('sidebarBackdrop');

            if (window.innerWidth <= 992) {
                sidebar.classList.toggle('mobile-open');
                backdrop.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                mainWrapper.classList.toggle('full-width');
            }
        }

        function initTableData() {
            var tbody = document.getElementById('studentsTableBody');
            if (!tbody) return;
            allRows = Array.from(tbody.querySelectorAll('tr:not(#noDataRow)'));
            updateStudentSerialNumbers();
            filteredRows = allRows.slice();
            currentPage = 1;
            renderTablePage();
        }

        function updateStudentSerialNumbers() {
            allRows.forEach(function(row, index) {
                var serialNumber = index + 1;
                row.dataset.srno = serialNumber;
                var serialCell = row.querySelector('.col-srno');
                if (serialCell) serialCell.textContent = serialNumber;
            });
        }

        // Search filtering across all fields
        function handleSearch() {
            var query = document.getElementById('tableSearchInput').value.trim().toLowerCase();

            if (!query) {
                filteredRows = allRows.slice();
            } else {
                filteredRows = allRows.filter(function(row) {
                    var text = (row.dataset.srno || '') + ' ' +
                               (row.dataset.name || '') + ' ' +
                               (row.dataset.email || '') + ' ' +
                               (row.dataset.mobile || '') + ' ' +
                               (row.dataset.college || '') + ' ' +
                               (row.dataset.stream || '') + ' ' +
                               (row.dataset.branch || '');
                    return text.toLowerCase().indexOf(query) !== -1;
                });
            }

            currentPage = 1;
            renderTablePage();
        }

        // Change rows per page (5, 10, 25, 50, 100)
        function changeRowsPerPage() {
            var select = document.getElementById('rowsPerPageSelect');
            rowsPerPage = parseInt(select.value, 10) || 10;
            currentPage = 1;
            renderTablePage();
        }

        // Render current page rows
        function renderTablePage() {
            var total = filteredRows.length;
            var totalPages = Math.ceil(total / rowsPerPage) || 1;

            if (currentPage > totalPages) currentPage = totalPages;
            if (currentPage < 1) currentPage = 1;

            var startIndex = (currentPage - 1) * rowsPerPage;
            var endIndex = Math.min(startIndex + rowsPerPage, total);

            // Hide all rows first
            allRows.forEach(function(row) {
                row.style.display = 'none';
            });

            // Show only rows for this page
            for (var i = startIndex; i < endIndex; i++) {
                if (filteredRows[i]) {
                    filteredRows[i].style.display = '';
                    // sync checkbox state
                    var cb = filteredRows[i].querySelector('.row-select-checkbox');
                    if (cb) {
                        cb.checked = selectedUserIds.has(cb.value);
                        if (cb.checked) {
                            filteredRows[i].classList.add('row-selected');
                        } else {
                            filteredRows[i].classList.remove('row-selected');
                        }
                    }
                }
            }

            // Empty state check
            var noDataRow = document.getElementById('noDataRow');
            if (total === 0) {
                if (!noDataRow) {
                    var tbody = document.getElementById('studentsTableBody');
                    var tr = document.createElement('tr');
                    tr.id = 'noDataRow';
                    tr.innerHTML = '<td colspan="9" class="empty-row-td">No matching student records found.</td>';
                    tbody.appendChild(tr);
                } else {
                    noDataRow.style.display = '';
                }
            } else if (noDataRow) {
                noDataRow.style.display = 'none';
            }

            // Update entries info text: "Showing 1 to 10 of 25 entries"
            var infoText = total === 0 ? "Showing 0 to 0 of 0 entries" :
                           "Showing " + (startIndex + 1) + " to " + endIndex + " of " + total + " entries";
            document.getElementById('entriesInfo').textContent = infoText;

            // Render pagination buttons
            renderPaginationControls(totalPages);

            // Update master select all checkbox state
            syncSelectAllCheckbox();
        }

        // Render pagination buttons
        function renderPaginationControls(totalPages) {
            var container = document.getElementById('paginationControls');
            container.innerHTML = '';

            // Previous Button
            var prevBtn = document.createElement('button');
            prevBtn.className = 'page-btn';
            prevBtn.textContent = 'Previous';
            prevBtn.disabled = (currentPage === 1);
            prevBtn.onclick = function() {
                if (currentPage > 1) {
                    currentPage--;
                    renderTablePage();
                }
            };
            container.appendChild(prevBtn);

            // Page numbers
            var maxButtons = 5;
            var startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
            var endPage = Math.min(totalPages, startPage + maxButtons - 1);
            if (endPage - startPage + 1 < maxButtons) {
                startPage = Math.max(1, endPage - maxButtons + 1);
            }

            for (var p = startPage; p <= endPage; p++) {
                (function(pageNumber) {
                    var pageBtn = document.createElement('button');
                    pageBtn.className = 'page-btn' + (pageNumber === currentPage ? ' active' : '');
                    pageBtn.textContent = pageNumber;
                    pageBtn.onclick = function() {
                        currentPage = pageNumber;
                        renderTablePage();
                    };
                    container.appendChild(pageBtn);
                })(p);
            }

            // Next Button
            var nextBtn = document.createElement('button');
            nextBtn.className = 'page-btn';
            nextBtn.textContent = 'Next';
            nextBtn.disabled = (currentPage === totalPages || totalPages === 0);
            nextBtn.onclick = function() {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderTablePage();
                }
            };
            container.appendChild(nextBtn);
        }

        // Row checkbox toggle
        function handleRowSelect(cb) {
            var row = cb.closest('tr');
            if (cb.checked) {
                selectedUserIds.add(cb.value);
                if (row) row.classList.add('row-selected');
            } else {
                selectedUserIds.delete(cb.value);
                if (row) row.classList.remove('row-selected');
            }
            updateSelectedBadge();
            syncSelectAllCheckbox();
        }

        // Master Select All checkbox
        function toggleSelectAll(masterCb) {
            var isChecked = masterCb.checked;
            // Apply to all currently visible rows in this page
            var startIndex = (currentPage - 1) * rowsPerPage;
            var endIndex = Math.min(startIndex + rowsPerPage, filteredRows.length);

            for (var i = startIndex; i < endIndex; i++) {
                var row = filteredRows[i];
                var cb = row.querySelector('.row-select-checkbox');
                if (cb) {
                    cb.checked = isChecked;
                    if (isChecked) {
                        selectedUserIds.add(cb.value);
                        row.classList.add('row-selected');
                    } else {
                        selectedUserIds.delete(cb.value);
                        row.classList.remove('row-selected');
                    }
                }
            }
            updateSelectedBadge();
        }

        function syncSelectAllCheckbox() {
            var masterCb = document.getElementById('selectAllCheckbox');
            if (!masterCb) return;

            var startIndex = (currentPage - 1) * rowsPerPage;
            var endIndex = Math.min(startIndex + rowsPerPage, filteredRows.length);
            var visibleCount = endIndex - startIndex;

            if (visibleCount === 0) {
                masterCb.checked = false;
                masterCb.indeterminate = false;
                return;
            }

            var checkedCount = 0;
            for (var i = startIndex; i < endIndex; i++) {
                var cb = filteredRows[i].querySelector('.row-select-checkbox');
                if (cb && cb.checked) {
                    checkedCount++;
                }
            }

            masterCb.checked = (checkedCount === visibleCount && visibleCount > 0);
            masterCb.indeterminate = (checkedCount > 0 && checkedCount < visibleCount);
        }

        function updateSelectedBadge() {
            var badge = document.getElementById('selectedCountBadge');
            var count = selectedUserIds.size;
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }

        // ================= DELETE WITH STANDARD CHROME POPUP =================
        function confirmSingleDelete(userId, userName) {
            var confirmed = window.confirm('Are you sure you want to delete the student entry for "' + userName + '" (ID: #' + userId + ')?\n\nThis will permanently remove their records.');
            if (confirmed) {
                executeDeleteAjax('single', userId);
            }
        }

        function confirmBulkDelete() {
            if (selectedUserIds.size === 0) {
                window.alert("Please select at least one student checkbox to delete.");
                return;
            }
            var confirmed = window.confirm('Are you sure you want to delete the ' + selectedUserIds.size + ' selected student record(s)?\n\nThis action cannot be undone.');
            if (confirmed) {
                executeDeleteAjax('bulk', null);
            }
        }

        function executeDeleteAjax(type, targetId) {
            var formData = new URLSearchParams();
            formData.append('action', 'delete');

            if (type === 'single') {
                formData.append('userId', targetId);
            } else {
                formData.append('userIdsCsv', Array.from(selectedUserIds).join(','));
            }

            fetch('${pageContext.request.contextPath}/admin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                },
                body: formData.toString()
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.success) {
                    window.alert(data.message || 'Student record deleted successfully.');
                    var toDeleteIds = (type === 'single') ? [String(targetId)] : Array.from(selectedUserIds);

                    allRows = allRows.filter(function(row) {
                        var uid = row.dataset.userId;
                        if (toDeleteIds.indexOf(uid) !== -1) {
                            if (row.parentNode) row.parentNode.removeChild(row);
                            return false;
                        }
                        return true;
                    });
                    updateStudentSerialNumbers();

                    toDeleteIds.forEach(function(id) {
                        selectedUserIds.delete(id);
                    });

                    var countDisplay = document.getElementById('studentCountDisplay');
                    if (countDisplay) {
                        countDisplay.textContent = allRows.length;
                    }

                    handleSearch();
                    updateSelectedBadge();
                } else {
                    window.alert(data.message || 'Failed to delete record.');
                }
            })
            .catch(function(err) {
                var form = document.getElementById('hiddenDeleteForm');
                if (type === 'single') {
                    document.getElementById('deleteFormUserId').value = targetId;
                    document.getElementById('deleteFormUserIdsCsv').value = '';
                } else {
                    document.getElementById('deleteFormUserId').value = '';
                    document.getElementById('deleteFormUserIdsCsv').value = Array.from(selectedUserIds).join(',');
                }
                form.submit();
            });
        }

        // ================= EXPORT TO EXCEL =================
        function exportTableToExcel() {
            var rowsToExport = filteredRows;

            if (rowsToExport.length === 0) {
                window.alert('No student records available to export.');
                return;
            }

            window.alert('Exporting ' + rowsToExport.length + ' student records to Excel spreadsheet...');

            var headers = ["Sr.No", "Full Name", "Email Id", "Contact no", "College Name", "Stream", "Branch"];
            var csvContent = "\uFEFF";
            csvContent += headers.map(escapeCsv).join(",") + "\r\n";

            rowsToExport.forEach(function(row) {
                var srNo = row.dataset.srno || "";
                var name = row.dataset.name || "";
                var email = row.dataset.email || "";
                var mobile = row.dataset.mobile || "";
                var college = row.dataset.college || "";
                var stream = row.dataset.stream || "";
                var branch = row.dataset.branch || "";

                var line = [srNo, name, email, mobile, college, stream, branch];
                csvContent += line.map(escapeCsv).join(",") + "\r\n";
            });

            var blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            var url = URL.createObjectURL(blob);
            var link = document.createElement("a");
            var timestamp = new Date().toISOString().slice(0, 10);
            link.setAttribute("href", url);
            link.setAttribute("download", "Industrial_Training_Students_" + timestamp + ".csv");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }

        function exportGlobalCertsToExcel() {
            var tbody = document.getElementById('globalCertTableBody');
            var rowsToExport = tbody ? Array.from(tbody.querySelectorAll('tr')).filter(function(row) {
                return !row.querySelector('.empty-row-td');
            }) : [];

            if (rowsToExport.length === 0) {
                window.alert('No global certification records available to export.');
                return;
            }

            window.alert('Exporting ' + rowsToExport.length + ' certificate records to Excel spreadsheet...');

            var headers = ["Sr.No", "Student Name", "Student Email", "Exam", "Score", "Date Achieved"];
            var csvContent = "\uFEFF";
            csvContent += headers.map(escapeCsv).join(",") + "\r\n";

            rowsToExport.forEach(function(row, index) {
                var values = Array.from(row.cells).map(function(cell) {
                    return cell.textContent.trim();
                });
                values[0] = index + 1;
                csvContent += values.map(escapeCsv).join(",") + "\r\n";
            });

            var blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            var url = URL.createObjectURL(blob);
            var link = document.createElement("a");
            var timestamp = new Date().toISOString().slice(0, 10);
            link.setAttribute("href", url);
            link.setAttribute("download", "Global_Certifications_" + timestamp + ".csv");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }

        // ================= CERTIFICATES TABLE LOGIC =================
        function initCertTableData() {
            var tbody = document.getElementById('certsTableBody');
            if (!tbody) return;
            var trs = tbody.querySelectorAll('tr:not(#noCertDataRow)');
            allCertRows = Array.from(trs);
            filteredCertRows = allCertRows.slice();
            certCurrentPage = 1;
            renderCertTablePage();
        }

        function handleCertSearch() {
            var query = document.getElementById('certSearchInput').value.trim().toLowerCase();

            if (!query) {
                filteredCertRows = allCertRows.slice();
            } else {
                filteredCertRows = allCertRows.filter(function(row) {
                    var text = (row.dataset.studentName || '') + ' ' +
                               (row.dataset.studentEmail || '') + ' ' +
                               (row.dataset.certName || '') + ' ' +
                               (row.dataset.provider || '') + ' ' +
                               (row.dataset.tech || '');
                    return text.toLowerCase().indexOf(query) !== -1;
                });
            }

            certCurrentPage = 1;
            renderCertTablePage();
        }

        function changeCertRowsPerPage() {
            var select = document.getElementById('certRowsPerPageSelect');
            certRowsPerPage = parseInt(select.value, 10) || 10;
            certCurrentPage = 1;
            renderCertTablePage();
        }

        function renderCertTablePage() {
            var tbody = document.getElementById('certsTableBody');
            var noCertRow = document.getElementById('noCertDataRow');
            var totalEntries = filteredCertRows.length;
            var totalPages = Math.ceil(totalEntries / certRowsPerPage) || 1;

            if (certCurrentPage > totalPages) certCurrentPage = totalPages;
            if (certCurrentPage < 1) certCurrentPage = 1;

            allCertRows.forEach(function(row) {
                row.style.display = 'none';
            });

            if (totalEntries === 0) {
                if (!noCertRow) {
                    noCertRow = document.createElement('tr');
                    noCertRow.id = 'noCertDataRow';
                    noCertRow.innerHTML = '<td colspan="9" class="empty-row-td">No matching certificate records found.</td>';
                    tbody.appendChild(noCertRow);
                }
                noCertRow.style.display = '';
                document.getElementById('certEntriesInfo').textContent = 'Showing 0 to 0 of 0 entries';
                renderCertPagination(0, 0);
                return;
            } else {
                if (noCertRow) noCertRow.style.display = 'none';
            }

            var startIndex = (certCurrentPage - 1) * certRowsPerPage;
            var endIndex = Math.min(startIndex + certRowsPerPage, totalEntries);

            for (var i = startIndex; i < endIndex; i++) {
                filteredCertRows[i].style.display = '';
            }

            document.getElementById('certEntriesInfo').textContent =
                'Showing ' + (startIndex + 1) + ' to ' + endIndex + ' of ' + totalEntries + ' entries';

            renderCertPagination(totalPages, certCurrentPage);
        }

        function renderCertPagination(totalPages, activePage) {
            var container = document.getElementById('certPaginationControls');
            container.innerHTML = '';

            var prevBtn = document.createElement('button');
            prevBtn.className = 'page-btn';
            prevBtn.textContent = 'Previous';
            prevBtn.disabled = (certCurrentPage === 1 || totalPages === 0);
            prevBtn.onclick = function() {
                if (certCurrentPage > 1) {
                    certCurrentPage--;
                    renderCertTablePage();
                }
            };
            container.appendChild(prevBtn);

            var maxButtons = 5;
            var startPage = Math.max(1, certCurrentPage - Math.floor(maxButtons / 2));
            var endPage = Math.min(totalPages, startPage + maxButtons - 1);
            if (endPage - startPage + 1 < maxButtons) {
                startPage = Math.max(1, endPage - maxButtons + 1);
            }

            for (var p = startPage; p <= endPage; p++) {
                (function(pageNumber) {
                    var pageBtn = document.createElement('button');
                    pageBtn.className = 'page-btn' + (pageNumber === certCurrentPage ? ' active' : '');
                    pageBtn.textContent = pageNumber;
                    pageBtn.onclick = function() {
                        certCurrentPage = pageNumber;
                        renderCertTablePage();
                    };
                    container.appendChild(pageBtn);
                })(p);
            }

            var nextBtn = document.createElement('button');
            nextBtn.className = 'page-btn';
            nextBtn.textContent = 'Next';
            nextBtn.disabled = (certCurrentPage === totalPages || totalPages === 0);
            nextBtn.onclick = function() {
                if (certCurrentPage < totalPages) {
                    certCurrentPage++;
                    renderCertTablePage();
                }
            };
            container.appendChild(nextBtn);
        }

        function confirmDeleteCert(certId, certName, studentName) {
            var confirmed = window.confirm('Are you sure you want to delete the certificate "' + certName + '" for student ' + studentName + '?\n\nThis action cannot be undone.');
            if (confirmed) {
                executeDeleteCertAjax(certId);
            }
        }

        function executeDeleteCertAjax(certId) {
            var formData = new URLSearchParams();
            formData.append('action', 'deleteCert');
            formData.append('certId', certId);

            fetch('${pageContext.request.contextPath}/admin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                },
                body: formData.toString()
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.success) {
                    window.alert(data.message || 'Certificate record deleted successfully.');
                    allCertRows = allCertRows.filter(function(row) {
                        if (row.dataset.certId == certId) {
                            if (row.parentNode) row.parentNode.removeChild(row);
                            return false;
                        }
                        return true;
                    });
                    var countDisplay = document.getElementById('certCountDisplay');
                    if (countDisplay) {
                        countDisplay.textContent = allCertRows.length;
                    }
                    handleCertSearch();
                } else {
                    window.alert(data.message || 'Failed to delete certificate.');
                }
            })
            .catch(function(err) {
                var form = document.getElementById('hiddenCertDeleteForm');
                document.getElementById('deleteCertFormId').value = certId;
                form.submit();
            });
        }

        function exportCertsToExcel() {
            var rowsToExport = filteredCertRows;

            if (rowsToExport.length === 0) {
                window.alert('No certificate records available to export.');
                return;
            }

            window.alert('Exporting ' + rowsToExport.length + ' certificate records to Excel spreadsheet...');

            var headers = ["Sr.No", "Student Name", "Student Email", "Certificate Title", "Provider", "Technology"];
            var csvContent = "\uFEFF";
            csvContent += headers.map(escapeCsv).join(",") + "\r\n";

            rowsToExport.forEach(function(row, idx) {
                var sName = row.dataset.studentName || "";
                var sEmail = row.dataset.studentEmail || "";
                var cName = row.dataset.certName || "";
                var prov = row.dataset.provider || "";
                var tech = row.dataset.tech || "";

                var line = [idx + 1, sName, sEmail, cName, prov, tech];
                csvContent += line.map(escapeCsv).join(",") + "\r\n";
            });

            var blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            var url = URL.createObjectURL(blob);
            var link = document.createElement("a");
            var timestamp = new Date().toISOString().slice(0, 10);
            link.setAttribute("href", url);
            link.setAttribute("download", "Student_Certificates_" + timestamp + ".csv");
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }

        function escapeCsv(field) {
            if (field === null || field === undefined) field = "";
            field = String(field);
            if (field.search(/("|,|\n)/g) >= 0) {
                field = '"' + field.replace(/"/g, '""') + '"';
            }
            return field;
        }
    </script>

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (!com.kasnet.util.SessionUtil.requireAdmin(request, response)) {
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assignment Management - Kasnet Admin</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root {
            --sidebar-bg: #263238;
            --sidebar-text: #cfd8dc;
            --sidebar-header: #c62828;
            --accent: #1976d2;
            --danger: #d32f2f;
            --danger-hover: #c62828;
            --bg-page: #f8fafc;
            --border-color: #e2e8f0;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.06);
            --font: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
        }
        body { min-height: 100vh; display: flex; overflow-x: hidden; font-family: var(--font); line-height: 1.5; background: var(--bg-page); color: var(--text-main); }
        .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; display: flex; width: 260px; min-height: 100vh; flex-direction: column; background: var(--sidebar-bg); color: var(--sidebar-text); box-shadow: 2px 0 10px rgba(0,0,0,.15); transition: all .28s ease; }
        .sidebar.collapsed { transform: translateX(-260px); }
        .sidebar-header { display: flex; align-items: center; gap: 12px; padding: 16px 20px; border-bottom: 1px solid rgba(0,0,0,.12); color: #fff; background: var(--sidebar-header); }
        .sidebar-header img { width: auto; max-width: 44px; height: 38px; padding: 3px; border-radius: 6px; background: #fff; object-fit: contain; }
        .sidebar-header-text h2 { font-size: 1.05rem; font-weight: 700; line-height: 1.2; letter-spacing: -.2px; }
        .sidebar-header-text span { font-size: .75rem; letter-spacing: .5px; text-transform: uppercase; opacity: .85; }
        .sidebar-menu { flex: 1; padding: 14px 0; list-style: none; }
        .sidebar-menu-item { margin-bottom: 2px; }
        .sidebar-link { display: flex; align-items: center; justify-content: space-between; padding: 13px 22px; border-left: 4px solid transparent; color: var(--sidebar-text); font-size: .92rem; font-weight: 500; text-decoration: none; transition: all .2s ease; }
        .sidebar-link-content { display: flex; align-items: center; gap: 12px; }
        .sidebar-link-icon { display: inline-block; width: 22px; font-size: 1.15rem; text-align: center; }
        .sidebar-link:hover { color: #fff; background: rgba(255,255,255,.06); }
        .sidebar-link.active { border-left-color: #ef4444; color: #fff; background: rgba(0,0,0,.28); font-weight: 600; }
        .main-wrapper { display: flex; min-width: 0; flex: 1; flex-direction: column; margin-left: 260px; transition: margin-left .28s ease; }
        .main-wrapper.full-width { margin-left: 0; }
        .topbar { position: sticky; top: 0; z-index: 90; display: flex; height: 56px; align-items: center; justify-content: space-between; padding: 0 24px; border-bottom: 1px solid var(--border-color); background: #fff; box-shadow: var(--shadow-sm); }
        .topbar-left { display: flex; align-items: center; gap: 16px; }
        .topbar-right { display: flex; align-items: center; gap: 16px; }
        .btn-toggle-sidebar { display: flex; align-items: center; justify-content: center; padding: 6px 10px; border: 1px solid var(--border-color); border-radius: 6px; color: var(--text-main); background: none; font-size: 1.2rem; cursor: pointer; transition: all .15s; }
        .btn-toggle-sidebar:hover { color: var(--accent); background: #f1f5f9; }
        .portal-breadcrumb { color: var(--text-main); font-size: .95rem; font-weight: 600; }
        .user-pill { display: flex; align-items: center; gap: 8px; color: var(--text-main); font-size: .88rem; font-weight: 600; }
        .user-avatar { display: flex; width: 32px; height: 32px; align-items: center; justify-content: center; border-radius: 50%; color: #475569; background: #e2e8f0; font-size: .85rem; font-weight: 700; }
        .nav-btn { padding: 6px 14px; border-radius: 6px; font-size: .84rem; font-weight: 600; text-decoration: none; transition: all .2s; }
        .nav-btn-outline { border: 1px solid #bfdbfe; color: var(--accent); background: #eff6ff; }
        .nav-btn-outline:hover { background: #dbeafe; }
        .nav-btn-danger { color: #fff; background: var(--danger); }
        .nav-btn-danger:hover { background: var(--danger-hover); }
        button.nav-btn { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; border-radius: 6px; font-family: inherit; font-size: .84rem; font-weight: 600; line-height: 1.5; cursor: pointer; }
        .page-content { flex: 1; padding: 24px 28px; }
        .page-main { width: min(1180px, 100%); margin: 0 auto; }
        h1 { margin: 0 0 4px; color: #1e293b; font-size: 1.55rem; }
        .muted { margin: 0 0 22px; color: #64748b; }
        .panel { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; margin-bottom: 24px; box-shadow: 0 4px 14px #0f172a0a; }
        .form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
        label { display: block; font-size: .9rem; font-weight: 650; margin-bottom: 6px; }
        input, textarea { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 7px; font: inherit; }
        input:focus, textarea:focus { outline: 2px solid #93c5fd; border-color: #2563eb; }
        textarea { min-height: 110px; resize: vertical; }
        .full { grid-column: 1 / -1; }
        .hint { color: #64748b; font-size: .82rem; margin: 5px 0 0; }
        .check { display: flex; align-items: center; gap: 9px; }
        .check input { width: auto; }
        button, .button { display: inline-block; border: 0; border-radius: 7px; padding: 10px 15px; font-weight: 650; font-size: .9rem; cursor: pointer; text-decoration: none; }
        .primary { color: #fff; background: #1d4ed8; }
        .secondary { color: #1e3a8a; background: #e0eafe; }
        .danger { color: #991b1b; background: #fee2e2; }
        .actions { display: flex; flex-wrap: wrap; align-items: center; gap: 9px; }
        .notice { border-radius: 8px; padding: 12px 15px; margin-bottom: 16px; }
        .success { color: #065f46; background: #ecfdf5; border: 1px solid #a7f3d0; }
        .error { color: #991b1b; background: #fef2f2; border: 1px solid #fecaca; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; min-width: 760px; }
        th, td { text-align: left; padding: 12px 10px; border-bottom: 1px solid #e2e8f0; vertical-align: top; }
        th { color: #475569; font-size: .82rem; background: #f8fafc; }
        td { font-size: .9rem; }
        .status { font-weight: 700; }
        .status.active { color: #15803d; }
        .status.inactive { color: #b91c1c; }
        @media (max-width: 680px) {
            .panel { padding: 17px; }
            .form-grid { grid-template-columns: 1fr; }
            .full { grid-column: auto; }
        }
        .sidebar-backdrop { display: none; }
        @media (max-width: 992px) {
            .sidebar { transform: translateX(-260px); }
            .sidebar.mobile-open { transform: translateX(0); }
            .main-wrapper { margin-left: 0; }
            .sidebar-backdrop { position: fixed; inset: 0; z-index: 99; display: none; background: rgba(0,0,0,.5); }
            .sidebar-backdrop.show { display: block; }
        }
        @media (max-width: 620px) {
            .topbar { padding: 0 14px; }
            .topbar-right { gap: 8px; }
            .portal-breadcrumb { font-size: .82rem; }
            .user-pill { display: none; }
            .page-content { padding: 18px 14px; }
        }
    </style>
</head>
<body>
<div class="sidebar-backdrop" id="sidebarBackdrop" onclick="toggleSidebar()"></div>
<aside class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo">
        <div class="sidebar-header-text">
            <h2>My Internship</h2>
            <span>Technologies</span>
        </div>
    </div>
    <ul class="sidebar-menu">
        <li class="sidebar-menu-item">
            <a href="${pageContext.request.contextPath}/admin" class="sidebar-link">
                <span class="sidebar-link-content"><span class="sidebar-link-icon">🎓</span><span>Student Details</span></span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="${pageContext.request.contextPath}/admin/assignments" class="sidebar-link active" aria-current="page">
                <span class="sidebar-link-content"><span class="sidebar-link-icon">📝</span><span>Assignment Management</span></span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="${pageContext.request.contextPath}/admin?tab=certificates" class="sidebar-link">
                <span class="sidebar-link-content"><span class="sidebar-link-icon">📜</span><span>Global Certification</span></span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="${pageContext.request.contextPath}/admin?tab=projects" class="sidebar-link">
                <span class="sidebar-link-content"><span class="sidebar-link-icon">📁</span><span>Project</span></span>
            </a>
        </li>
    </ul>
</aside>
<div class="main-wrapper" id="mainWrapper">
<header class="topbar">
    <div class="topbar-left">
        <button type="button" class="btn-toggle-sidebar" onclick="toggleSidebar()" title="Toggle Sidebar" aria-label="Toggle Sidebar">☰</button>
        <div class="portal-breadcrumb">Online Internship Administration / Assignment Management</div>
    </div>
    <div class="topbar-right">
        <div class="user-pill"><span class="user-avatar">${not empty sessionScope.userName ? sessionScope.userName.substring(0, 1).toUpperCase() : 'A'}</span><span>${not empty sessionScope.userName ? sessionScope.userName : 'Admin'}</span></div>
        <a href="${pageContext.request.contextPath}/" class="nav-btn nav-btn-outline" target="_blank">Public Site</a>
        <button type="button" class="nav-btn nav-btn-danger" onclick="handleAdminLogout()">Logout</button>
    </div>
</header>
<main class="page-content">
<div class="page-main">
    <h1>Assignment management</h1>
    <p class="muted">Create, edit, publish, deactivate, and republish student assignments.</p>

    <c:if test="${not empty successMessage}">
        <div class="notice success"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="notice error"><c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty loadError}">
        <div class="notice error"><c:out value="${loadError}"/></div>
    </c:if>
    <c:if test="${flashError}">
        <div class="notice error"><c:out value="${flashMessage}"/></div>
    </c:if>

    <section class="panel">
        <h2><c:choose><c:when test="${formAssignment.assignmentId > 0}">Edit assignment</c:when><c:otherwise>Create assignment</c:otherwise></c:choose></h2>
        <form action="${pageContext.request.contextPath}/admin/assignments" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
            <input type="hidden" name="action" value="save">
            <input type="hidden" name="assignmentId" value="<c:out value='${formAssignment.assignmentId}'/>">
            <div class="form-grid">
                <div>
                    <label for="title">Assignment title *</label>
                    <input id="title" name="title" maxlength="200" required value="<c:out value='${formAssignment.title}'/>">
                </div>
                <div>
                    <label for="assignmentNumber">Assignment number/code *</label>
                    <input id="assignmentNumber" name="assignmentNumber" maxlength="50" required pattern="[A-Za-z0-9]+(-[A-Za-z0-9]+)*" placeholder="ASN-003" value="<c:out value='${formAssignment.assignmentNumber}'/>">
                </div>
                <div>
                    <label for="subject">Subject/course *</label>
                    <input id="subject" name="subject" maxlength="200" required value="<c:out value='${formAssignment.subject}'/>">
                </div>
                <div>
                    <label for="dueDate">Due date *</label>
                    <input id="dueDate" type="date" name="dueDate" required value="<fmt:formatDate value='${formAssignment.dueDate}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="full">
                    <label for="description">Assignment description *</label>
                    <textarea id="description" name="description" maxlength="5000" required><c:out value="${formAssignment.description}"/></textarea>
                </div>
                <div class="full">
                    <label for="instructionsText">Detailed text instructions</label>
                    <textarea id="instructionsText" name="instructionsText" maxlength="16000"><c:out value="${formAssignment.instructionsText}"/></textarea>
                    <p class="hint">Text instructions may be used alone or together with a PDF.</p>
                </div>
                <div>
                    <label for="instructionPdf">Instruction PDF (optional, maximum 5 MB)</label>
                    <input id="instructionPdf" type="file" name="instructionPdf" accept=".pdf,application/pdf">
                    <c:if test="${not empty formAssignment.instructionPdfPath}">
                        <p class="hint">A PDF is already attached. <a href="${pageContext.request.contextPath}/assignment-instructions?assignmentId=${formAssignment.assignmentId}" target="_blank" rel="noopener">Open current PDF</a>. Leave this field empty to keep it.</p>
                        <label class="check"><input type="checkbox" name="removePdf" value="true"> Remove current PDF</label>
                    </c:if>
                </div>
                <div>
                    <label for="totalMarks">Maximum marks *</label>
                    <input id="totalMarks" type="number" name="totalMarks" min="1" max="100000" step="1" required value="<c:out value='${formTotalMarks != null ? formTotalMarks : formAssignment.totalMarks}'/>">
                </div>
                <div class="full check">
                    <input id="isActive" type="checkbox" name="isActive" value="true" <c:if test="${formAssignment.active}">checked</c:if>>
                    <label for="isActive" style="margin:0">Publish assignment immediately (active)</label>
                </div>
            </div>
            <p class="hint">Required: title, code, subject, description, due date, marks, and either text instructions or a PDF.</p>
            <div class="actions" style="margin-top:16px">
                <button class="primary" type="submit"><c:choose><c:when test="${formAssignment.assignmentId > 0}">Save changes</c:when><c:otherwise>Create assignment</c:otherwise></c:choose></button>
                <c:if test="${formAssignment.assignmentId > 0}">
                    <a class="button secondary" href="${pageContext.request.contextPath}/admin/assignments">Cancel edit</a>
                </c:if>
            </div>
        </form>
    </section>

    <section class="panel">
        <h2>Existing assignments</h2>
        <c:choose>
            <c:when test="${empty assignmentList}">
                <p class="muted" style="margin:12px 0 0">No assignments have been created yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Code</th><th>Assignment</th><th>Due date</th><th>Marks</th><th>Status</th><th>Actions</th></tr></thead>
                        <tbody>
                        <c:forEach items="${assignmentList}" var="asn">
                            <tr>
                                <td><c:out value="${asn.assignmentNumber}"/></td>
                                <td><strong><c:out value="${asn.title}"/></strong><br><span class="muted"><c:out value="${asn.subject}"/></span></td>
                                <td><fmt:formatDate value="${asn.dueDate}" pattern="dd MMM yyyy"/></td>
                                <td><c:out value="${asn.totalMarks}"/></td>
                                <td class="status ${asn.active ? 'active' : 'inactive'}"><c:out value="${asn.active ? 'Published' : 'Inactive'}"/></td>
                                <td>
                                    <div class="actions">
                                        <a class="button secondary" href="${pageContext.request.contextPath}/admin/assignments?edit=${asn.assignmentId}">Edit</a>
                                        <form action="${pageContext.request.contextPath}/admin/assignments" method="post">
                                            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="assignmentId" value="${asn.assignmentId}">
                                            <input type="hidden" name="active" value="${!asn.active}">
                                            <button class="${asn.active ? 'danger' : 'primary'}" type="submit"><c:out value="${asn.active ? 'Deactivate' : 'Republish'}"/></button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</main>
</div>
<script>
    function handleAdminLogout() {
        if (window.confirm("Are you sure you want to log out from the Administrator Console?")) {
            window.location.href = '${pageContext.request.contextPath}/logout';
        }
    }

    function toggleSidebar() {
        var sidebar = document.getElementById("sidebar");
        var backdrop = document.getElementById("sidebarBackdrop");
        if (window.innerWidth <= 992) {
            sidebar.classList.toggle("mobile-open");
            backdrop.classList.toggle("show");
        } else {
            sidebar.classList.toggle("collapsed");
            document.getElementById("mainWrapper").classList.toggle("full-width");
        }
    }
</script>
</body>
</html>

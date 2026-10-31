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
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${assignment.title}"/> - Assignment details</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; }
        :root {
            --navy: #1a237e;
            --ink: #172554;
            --muted: #64748b;
            --line: #e2e8f0;
        }
        body {
            min-height: 100vh;
            margin: 0;
            color: var(--ink);
            background: radial-gradient(ellipse at 12% 0%, rgba(219, 231, 255, .7), transparent 35rem), #f5f7fc;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.5;
        }
        .navbar {
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #fff;
            box-shadow: 0 2px 8px rgba(0,0,0,.12);
        }
        .brand { display: flex; align-items: center; gap: 12px; color: #fff; font-size: 1.15rem; font-weight: 700; }
        .brand img { width: auto; height: 32px; margin-right: 10px; padding: 2px; border-radius: 4px; background: #fff; object-fit: contain; }
        .container { width: min(100% - 40px, 1000px); margin: 34px auto 64px; }
        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 9px;
            margin: 0 0 20px 2px;
            padding: 9px 13px;
            border: 1px solid #d7e2fa;
            border-radius: 9px;
            color: #24449c;
            background: rgba(255,255,255,.78);
            font-size: .92rem;
            font-weight: 700;
            text-decoration: none;
            transition: background .18s ease, border-color .18s ease, transform .18s ease;
        }
        .back-link:hover { transform: translateX(-2px); border-color: #a9bfef; background: #fff; }
        .panel { overflow: hidden; border: 1px solid #e1e8f4; border-radius: 18px; background: #fff; box-shadow: 0 16px 42px rgba(30, 50, 100, .09); }
        .panel-header { padding: clamp(24px, 5vw, 42px); background: linear-gradient(135deg, #fff 45%, #f7f9ff); }
        .assignment-code { display: inline-flex; align-items: center; padding: 6px 12px; border: 1px solid #d8e3ff; border-radius: 999px; color: #24449c; background: #edf3ff; font-size: .78rem; font-weight: 800; letter-spacing: .045em; }
        h1 { margin: 16px 0 8px; color: var(--navy); font-size: clamp(1.75rem, 4vw, 2.35rem); line-height: 1.18; letter-spacing: -.035em; overflow-wrap: anywhere; }
        .subject { margin: 0; color: #5268b6; font-size: 1rem; font-weight: 700; }
        .meta { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; margin-top: 28px; }
        .meta-item { display: flex; min-height: 72px; align-items: center; gap: 13px; padding: 14px 16px; border: 1px solid #e5eaf4; border-radius: 12px; background: rgba(255,255,255,.85); }
        .meta-icon { display: grid; width: 40px; height: 40px; flex: 0 0 40px; place-items: center; border-radius: 11px; color: #3156b8; background: #edf3ff; font-size: 1.1rem; }
        .meta-label { display: block; margin-bottom: 3px; color: var(--muted); font-size: .77rem; font-weight: 700; letter-spacing: .04em; text-transform: uppercase; }
        .meta-value { color: var(--ink); font-size: .98rem; font-weight: 700; }
        .panel-body { display: grid; gap: 24px; padding: 0 clamp(24px, 5vw, 42px) clamp(28px, 5vw, 42px); }
        .section { padding-top: 23px; border-top: 1px solid var(--line); }
        .section-heading { display: flex; align-items: center; gap: 10px; margin: 0 0 12px; color: #203982; font-size: 1.08rem; }
        .section-heading::before { width: 4px; height: 20px; border-radius: 4px; background: #4772d0; content: ""; }
        .content { color: #475569; font-size: 1rem; line-height: 1.8; white-space: pre-wrap; overflow-wrap: anywhere; }
        .pdf-actions { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 14px; color: var(--muted); font-size: .9rem; }
        .pdf-link { display: inline-flex; align-items: center; gap: 8px; padding: 9px 13px; border: 1px solid #d5e1fa; border-radius: 8px; color: #24449c; background: #f6f8ff; font-weight: 700; text-decoration: none; }
        .pdf-link:hover { background: #edf3ff; }
        .pdf-viewer { display: block; width: 100%; height: min(70vh, 720px); min-height: 420px; border: 1px solid #dce5f3; border-radius: 12px; background: #f8faff; }
        @media (max-width: 600px) {
            .navbar { padding: 12px 18px; }
            .brand { font-size: 1rem; }
            .container { width: min(100% - 28px, 1000px); margin-top: 22px; }
            .meta { grid-template-columns: 1fr; gap: 9px; margin-top: 22px; }
            .pdf-viewer { height: 58vh; min-height: 340px; }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="brand">
        <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo">
        <span>My Internship Technologies</span>
    </div>
</nav>
<main class="container">
    <a class="back-link" href="${pageContext.request.contextPath}/assignments"><span aria-hidden="true">&larr;</span> Back to assignments</a>
    <article class="panel">
        <header class="panel-header">
            <span class="assignment-code"><c:out value="${assignment.assignmentNumber}"/></span>
            <h1><c:out value="${assignment.title}"/></h1>
            <p class="subject"><c:out value="${assignment.subject}"/></p>
            <div class="meta" aria-label="Assignment details">
                <div class="meta-item">
                    <span class="meta-icon" aria-hidden="true">&#128197;</span>
                    <span><span class="meta-label">Due date</span><span class="meta-value"><fmt:formatDate value="${assignment.dueDate}" pattern="dd MMM yyyy"/></span></span>
                </div>
                <div class="meta-item">
                    <span class="meta-icon" aria-hidden="true">&#9733;</span>
                    <span><span class="meta-label">Maximum marks</span><span class="meta-value"><c:out value="${assignment.totalMarks}"/> marks</span></span>
                </div>
            </div>
        </header>
        <div class="panel-body">
            <section class="section">
                <h2 class="section-heading">Assignment description</h2>
                <div class="content"><c:out value="${assignment.description}"/></div>
            </section>

            <c:if test="${not empty assignment.instructionsText}">
                <section class="section">
                    <h2 class="section-heading">Instructions</h2>
                    <div class="content"><c:out value="${assignment.instructionsText}"/></div>
                </section>
            </c:if>

            <c:if test="${not empty assignment.instructionPdfPath}">
                <section class="section">
                    <h2 class="section-heading">Attached instructions</h2>
                    <div class="pdf-actions">
                        <span>Review the attached PDF below, or open it in a separate tab.</span>
                        <a class="pdf-link"
                           href="${pageContext.request.contextPath}/assignment-instructions?assignmentId=${assignment.assignmentId}"
                           target="_blank" rel="noopener">Open PDF in new tab &#8599;</a>
                    </div>
                    <iframe class="pdf-viewer"
                            src="${pageContext.request.contextPath}/assignment-instructions?assignmentId=${assignment.assignmentId}#toolbar=0&navpanes=0"
                            title="Assignment instructions PDF"></iframe>
                </section>
            </c:if>
        </div>
    </article>
</main>
</body>
</html>

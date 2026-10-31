<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                        <title>Internship Lectures & Curriculum — Kasnet Portal</title>
                        <link rel="preconnect" href="https://fonts.googleapis.com">
                        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                        <link
                            href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"
                            rel="stylesheet">
                        <link rel="stylesheet"
                            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                        <style>
                            :root {
                                --brand-primary: #1a237e;
                                --brand-accent: #2563eb;
                                --brand-accent-hover: #1d4ed8;
                                --success: #10b981;
                                --warning: #f59e0b;
                                --bg-page: #f8fafc;
                                --bg-card: #ffffff;
                                --text-primary: #0f172a;
                                --text-secondary: #64748b;
                                --border-color: #e2e8f0;
                                --radius-md: 12px;
                                --radius-lg: 16px;
                            }

                            * {
                                box-sizing: border-box;
                                margin: 0;
                                padding: 0;
                                font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
                            }

                            body {
                                background-color: var(--bg-page);
                                color: var(--text-primary);
                                min-height: 100vh;
                            }

                            /* ─── Navbar ─── */
                            .navbar {
                                background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
                                color: #fff;
                                padding: 12px 28px;
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                                box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
                                position: sticky;
                                top: 0;
                                z-index: 50;
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

                            .navbar-brand img {
                                height: 32px;
                                width: auto;
                                border-radius: 4px;
                                background: #ffffff;
                                padding: 2px;
                            }

                            .navbar-menu {
                                display: flex;
                                align-items: center;
                                gap: 14px;
                            }

                            .nav-link {
                                color: rgba(255, 255, 255, 0.85);
                                text-decoration: none;
                                font-size: 0.9rem;
                                font-weight: 500;
                                padding: 6px 12px;
                                border-radius: 6px;
                                transition: all 0.2s;
                            }

                            .nav-link:hover,
                            .nav-link.active {
                                color: #fff;
                                background: rgba(255, 255, 255, 0.18);
                            }

                            .btn-logout {
                                background: #ef4444;
                                color: #fff;
                                padding: 6px 14px;
                                border-radius: 6px;
                                text-decoration: none;
                                font-size: 0.85rem;
                                font-weight: 600;
                                transition: background 0.2s;
                            }

                            .btn-logout:hover {
                                background: #dc2626;
                            }

                            /* ─── Container ─── */
                            .container {
                                max-width: 1320px;
                                margin: 0 auto;
                                padding: 28px 24px 60px;
                            }

                            /* ─── Page Header ─── */
                            .page-header-row {
                                display: flex;
                                justify-content: space-between;
                                align-items: flex-end;
                                margin-bottom: 24px;
                                flex-wrap: wrap;
                                gap: 16px;
                            }

                            .page-title {
                                font-size: 1.95rem;
                                font-weight: 800;
                                color: #1e293b;
                                letter-spacing: -0.5px;
                            }

                            .page-subtitle {
                                font-size: 0.95rem;
                                color: var(--text-secondary);
                                margin-top: 4px;
                            }

                            .btn-catalog-cta {
                                display: inline-flex;
                                align-items: center;
                                gap: 8px;
                                background: #ffffff;
                                border: 1px solid var(--border-color);
                                padding: 10px 18px;
                                border-radius: var(--radius-md);
                                color: var(--brand-accent);
                                text-decoration: none;
                                font-size: 0.9rem;
                                font-weight: 700;
                                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
                                transition: all 0.2s;
                            }

                            .btn-catalog-cta:hover {
                                border-color: var(--brand-accent);
                                transform: translateY(-1px);
                                box-shadow: 0 4px 12px rgba(37, 99, 235, 0.12);
                            }

                            /* ─── Alert ─── */
                            .alert-success {
                                background: #ecfdf5;
                                color: #065f46;
                                border: 1px solid #a7f3d0;
                                padding: 12px 18px;
                                border-radius: var(--radius-md);
                                margin-bottom: 24px;
                                font-size: 0.92rem;
                                display: flex;
                                align-items: center;
                                gap: 10px;
                                font-weight: 600;
                            }

                            /* ─── Stats Ribbon ─── */
                            .stats-ribbon {
                                display: grid;
                                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                                gap: 16px;
                                margin-bottom: 30px;
                            }

                            .stat-card {
                                background: #ffffff;
                                border: 1px solid var(--border-color);
                                border-radius: var(--radius-md);
                                padding: 16px 20px;
                                display: flex;
                                align-items: center;
                                gap: 14px;
                                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
                            }

                            .stat-icon {
                                width: 44px;
                                height: 44px;
                                border-radius: 10px;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                font-size: 1.25rem;
                            }

                            .stat-icon.total {
                                background: #eff6ff;
                                color: #2563eb;
                            }

                            .stat-icon.completed {
                                background: #ecfdf5;
                                color: #059669;
                            }

                            .stat-icon.pending {
                                background: #fef3c7;
                                color: #d97706;
                            }

                            .stat-value {
                                font-size: 1.35rem;
                                font-weight: 800;
                                color: #0f172a;
                            }

                            .stat-label {
                                font-size: 0.8rem;
                                color: var(--text-secondary);
                                font-weight: 600;
                            }

                            /* ─── Lectures Grid ─── */
                            .lectures-grid {
                                display: grid;
                                grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
                                gap: 24px;
                            }

                            .lecture-card {
                                background: #ffffff;
                                border-radius: var(--radius-lg);
                                border: 1px solid var(--border-color);
                                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
                                overflow: hidden;
                                display: flex;
                                flex-direction: column;
                                justify-content: space-between;
                                transition: transform 0.2s, box-shadow 0.2s;
                            }

                            .lecture-card:hover {
                                transform: translateY(-4px);
                                box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
                                border-color: #cbd5e1;
                            }

                            /* Thumbnail / Video Banner */
                            .video-placeholder {
                                width: 100%;
                                height: 190px;
                                background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                color: #ffffff;
                                position: relative;
                                cursor: pointer;
                                text-decoration: none;
                                overflow: hidden;
                            }

                            .video-placeholder::before {
                                content: '';
                                position: absolute;
                                inset: 0;
                                background: radial-gradient(circle at center, rgba(37, 99, 235, 0.2) 0%, transparent 70%);
                            }

                            .play-btn {
                                width: 58px;
                                height: 58px;
                                background: rgba(255, 255, 255, 0.18);
                                border: 2px solid rgba(255, 255, 255, 0.85);
                                border-radius: 50%;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                font-size: 20px;
                                color: #ffffff;
                                backdrop-filter: blur(6px);
                                transition: all 0.2s ease;
                                position: relative;
                                z-index: 2;
                            }

                            .video-placeholder:hover .play-btn {
                                transform: scale(1.12);
                                background: #2563eb;
                                border-color: #ffffff;
                                box-shadow: 0 0 24px rgba(37, 99, 235, 0.6);
                            }

                            .duration-tag {
                                position: absolute;
                                bottom: 12px;
                                right: 12px;
                                background: rgba(15, 23, 42, 0.88);
                                color: #f8fafc;
                                padding: 4px 10px;
                                border-radius: 6px;
                                font-size: 0.75rem;
                                font-weight: 700;
                                backdrop-filter: blur(4px);
                                z-index: 2;
                                display: flex;
                                align-items: center;
                                gap: 5px;
                            }

                            .badge-preview {
                                position: absolute;
                                top: 12px;
                                left: 12px;
                                background: rgba(217, 119, 6, 0.95);
                                color: #ffffff;
                                padding: 4px 10px;
                                border-radius: 6px;
                                font-size: 0.72rem;
                                font-weight: 800;
                                letter-spacing: 0.3px;
                                z-index: 2;
                                display: flex;
                                align-items: center;
                                gap: 5px;
                            }

                            /* Card Body */
                            .lecture-body {
                                padding: 20px;
                                flex-grow: 1;
                                display: flex;
                                flex-direction: column;
                                justify-content: space-between;
                            }

                            .lecture-num {
                                font-size: 0.72rem;
                                font-weight: 800;
                                color: var(--brand-accent);
                                text-transform: uppercase;
                                letter-spacing: 0.6px;
                                margin-bottom: 4px;
                            }

<<<<<<< HEAD
    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link active">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>
=======
                            .lecture-title {
                                font-size: 1.15rem;
                                font-weight: 750;
                                color: #0f172a;
                                margin-bottom: 6px;
                                line-height: 1.35;
                            }
>>>>>>> 34dfc21 (Fix database configuration, add course management and lecture streaming, untrack target directory)

                            .lecture-subject {
                                font-size: 0.82rem;
                                font-weight: 600;
                                color: #64748b;
                                margin-bottom: 8px;
                            }

                            .lecture-desc {
                                font-size: 0.85rem;
                                color: #475569;
                                line-height: 1.55;
                                margin-bottom: 16px;
                                display: -webkit-box;
                                -webkit-line-clamp: 2;
                                -webkit-box-orient: vertical;
                                overflow: hidden;
                            }

                            /* Watch progress bar in card */
                            .card-progress-bar {
                                margin-bottom: 16px;
                            }

                            .progress-bar-bg {
                                height: 6px;
                                background: #e2e8f0;
                                border-radius: 3px;
                                overflow: hidden;
                            }

                            .progress-bar-fill {
                                height: 100%;
                                background: var(--brand-accent);
                                border-radius: 3px;
                                transition: width 0.3s;
                            }

                            .progress-meta {
                                display: flex;
                                justify-content: space-between;
                                font-size: 0.75rem;
                                font-weight: 600;
                                color: var(--text-secondary);
                                margin-top: 4px;
                            }

                            /* Card Footer */
                            .lecture-footer {
                                border-top: 1px solid #f1f5f9;
                                padding-top: 14px;
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                                gap: 12px;
                            }

                            .btn-watch {
                                background: var(--brand-accent);
                                color: #ffffff;
                                padding: 8px 14px;
                                border-radius: 8px;
                                text-decoration: none;
                                font-size: 0.85rem;
                                font-weight: 700;
                                display: inline-flex;
                                align-items: center;
                                gap: 6px;
                                transition: all 0.2s ease;
                            }

                            .btn-watch:hover {
                                background: var(--brand-accent-hover);
                                transform: translateY(-1px);
                                box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
                            }

                            .completed-badge {
                                background: #dcfce7;
                                color: #15803d;
                                padding: 6px 12px;
                                border-radius: 20px;
                                font-size: 0.8rem;
                                font-weight: 700;
                                display: inline-flex;
                                align-items: center;
                                gap: 5px;
                            }

                            .btn-mark-complete {
                                background: transparent;
                                color: #059669;
                                border: 1px solid #a7f3d0;
                                padding: 7px 12px;
                                border-radius: 8px;
                                font-size: 0.8rem;
                                font-weight: 700;
                                cursor: pointer;
                                transition: all 0.2s;
                            }

                            .btn-mark-complete:hover {
                                background: #ecfdf5;
                                border-color: #059669;
                            }

                            /* ─── Empty State ─── */
                            .empty-state {
                                text-align: center;
                                padding: 60px 20px;
                                background: #ffffff;
                                border-radius: var(--radius-lg);
                                border: 1px dashed var(--border-color);
                            }

                            .empty-icon {
                                font-size: 3rem;
                                color: #94a3b8;
                                margin-bottom: 12px;
                            }

                            @media (max-width: 768px) {
                                .lectures-grid {
                                    grid-template-columns: 1fr;
                                }

                                .page-header-row {
                                    flex-direction: column;
                                    align-items: flex-start;
                                }
                            }
                        </style>
                    </head>

                    <body>

                        <!-- ═══════ Navbar ═══════ -->
                        <nav class="navbar">
                            <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
                                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Kasnet Logo"
                                    onerror="this.style.display='none'" />
                                <span>Kasnet Internship Portal</span>
                            </a>
                            <div class="navbar-menu">
                                <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
                                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
                                <a href="${pageContext.request.contextPath}/courses" class="nav-link">Courses</a>
                                <a href="${pageContext.request.contextPath}/lectures"
                                    class="nav-link active">Lectures</a>
                                <a href="${pageContext.request.contextPath}/assignments"
                                    class="nav-link">Assignments</a>
                                <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
                                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
                            </div>
                        </nav>

                        <!-- ═══════ Main Container ═══════ -->
                        <div class="container">

                            <div class="page-header-row">
                                <div>
                                    <h1 class="page-title">Curated Internship Lectures</h1>
                                    <p class="page-subtitle">Watch interactive curriculum lectures with anti-piracy DRM
                                        protection and auto-saved progress.</p>
                                </div>
                                <a href="${pageContext.request.contextPath}/courses" class="btn-catalog-cta">
                                    <i class="fa-solid fa-graduation-cap"></i> Browse Course Catalog &rarr;
                                </a>
                            </div>

                            <c:if test="${param.success == '1'}">
                                <div class="alert-success">
                                    <i class="fa-solid fa-circle-check"></i>
                                    <span>Lecture completion status updated successfully!</span>
                                </div>
                            </c:if>

                            <!-- Stats Ribbon -->
                            <c:set var="completedCount" value="0" />
                            <c:set var="inProgressCount" value="0" />
                            <c:forEach items="${lectureList}" var="l">
                                <c:if test="${l.completed}">
                                    <c:set var="completedCount" value="${completedCount + 1}" />
                                </c:if>
                                <c:if test="${!l.completed && l.watchProgressPercent > 0}">
                                    <c:set var="inProgressCount" value="${inProgressCount + 1}" />
                                </c:if>
                            </c:forEach>

                            <div class="stats-ribbon">
                                <div class="stat-card">
                                    <div class="stat-icon total"><i class="fa-solid fa-film"></i></div>
                                    <div>
                                        <div class="stat-value">${lectureList.size()}</div>
                                        <div class="stat-label">Total Curriculum Lectures</div>
                                    </div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-icon completed"><i class="fa-solid fa-circle-check"></i></div>
                                    <div>
                                        <div class="stat-value">${completedCount}</div>
                                        <div class="stat-label">Completed Lectures</div>
                                    </div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-icon pending"><i class="fa-solid fa-clock-rotate-left"></i></div>
                                    <div>
                                        <div class="stat-value">${inProgressCount}</div>
                                        <div class="stat-label">In-Progress Lectures</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Lectures Grid -->
                            <c:choose>
                                <c:when test="${not empty lectureList}">
                                    <div class="lectures-grid">
                                        <c:forEach items="${lectureList}" var="lec">
                                            <c:set var="targetCourseId"
                                                value="${lec.courseId > 0 ? lec.courseId : 1}" />
                                            <c:set var="playerUrl"
                                                value="${pageContext.request.contextPath.concat('/courses?view=player&courseId=').concat(targetCourseId).concat('&lectureId=').concat(lec.lectureId)}" />

                                            <div class="lecture-card">
                                                <!-- Direct link to DRM Player -->
                                                <a href="${playerUrl}" class="video-placeholder"
                                                    title="Play ${lec.title}">
                                                    <c:if test="${lec.preview}">
                                                        <span class="badge-preview">
                                                            <i class="fa-solid fa-unlock"></i> Free Preview
                                                        </span>
                                                    </c:if>
                                                    <div class="play-btn">
                                                        <i class="fa-solid fa-play" style="margin-left: 3px;"></i>
                                                    </div>
                                                    <span class="duration-tag">
                                                        <i class="fa-regular fa-clock"></i>
                                                        <c:choose>
                                                            <c:when test="${not empty lec.durationFormatted}">
                                                                ${lec.durationFormatted}</c:when>
                                                            <c:otherwise>${lec.durationMinutes} mins</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </a>

                                                <div class="lecture-body">
                                                    <div>
                                                        <div class="lecture-num">Lecture #${lec.lectureNumber}</div>
                                                        <h2 class="lecture-title">${lec.title}</h2>
                                                        <c:if test="${not empty lec.subject}">
                                                            <div class="lecture-subject">${lec.subject}</div>
                                                        </c:if>
                                                        <p class="lecture-desc">${lec.description}</p>
                                                    </div>

                                                    <!-- Progress Bar if Started -->
                                                    <c:if test="${lec.watchProgressPercent > 0 && !lec.completed}">
                                                        <div class="card-progress-bar">
                                                            <div class="progress-bar-bg">
                                                                <div class="progress-bar-fill"
                                                                    style="width: ${lec.watchProgressPercent}%;"></div>
                                                            </div>
                                                            <div class="progress-meta">
                                                                <span>Progress</span>
                                                                <span>${lec.watchProgressPercent}%</span>
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <div class="lecture-footer">
                                                        <a href="${playerUrl}" class="btn-watch">
                                                            <i class="fa-solid fa-play"></i> Watch Video
                                                        </a>

                                                        <c:choose>
                                                            <c:when test="${lec.completed}">
                                                                <span class="completed-badge">
                                                                    <i class="fa-solid fa-check"></i> Completed
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form
                                                                    action="${pageContext.request.contextPath}/lectures"
                                                                    method="post" style="display:inline;">
                                                                    <input type="hidden" name="action"
                                                                        value="markComplete">
                                                                    <input type="hidden" name="lectureId"
                                                                        value="${lec.lectureId}">
                                                                    <button type="submit" class="btn-mark-complete"
                                                                        title="Mark as Done">
                                                                        Mark as Done
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <div class="empty-icon"><i class="fa-solid fa-film"></i></div>
                                        <h3>No Lectures Available</h3>
                                        <p style="color: var(--text-secondary); margin-top: 6px;">
                                            Curriculum lectures will appear once published by instructors.
                                        </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                    </body>

                    </html>
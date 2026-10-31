<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course Management | Admin Portal</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --brand-primary: #4f46e5;
            --brand-hover: #4338ca;
            --brand-glow: rgba(79, 70, 229, 0.25);
            --bg-page: #f8fafc;
            --bg-card: #ffffff;
            --border-color: #e2e8f0;
            --text-primary: #0f172a;
            --text-secondary: #64748b;
            --success: #10b981;
            --warning: #f59e0b;
            --danger: #ef4444;
            --radius-lg: 16px;
            --radius-md: 10px;
            --radius-sm: 6px;
            --shadow-card: 0 4px 20px -2px rgba(0, 0, 0, 0.05);
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background-color: var(--bg-page);
            color: var(--text-primary);
            min-height: 100vh;
        }

        .admin-nav {
            background: #fff;
            border-bottom: 1px solid var(--border-color);
            padding: 0 32px;
            height: 68px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 50;
        }

        .nav-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 17px;
            font-weight: 800;
            color: var(--brand-primary);
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .nav-link {
            font-size: 13.5px;
            font-weight: 600;
            color: var(--text-secondary);
            text-decoration: none;
            transition: color 0.2s;
        }

        .nav-link.active, .nav-link:hover {
            color: var(--brand-primary);
        }

        .main-container {
            max-width: 1280px;
            margin: 32px auto;
            padding: 0 24px;
        }

        .page-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 28px;
            flex-wrap: wrap;
            gap: 16px;
        }

        .page-title {
            font-size: 26px;
            font-weight: 800;
            color: var(--text-primary);
        }

        .page-subtitle {
            font-size: 14px;
            color: var(--text-secondary);
            margin-top: 4px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 20px;
            border-radius: var(--radius-md);
            font-size: 13.5px;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
            border: none;
        }

        .btn-primary {
            background: var(--brand-primary);
            color: #fff;
            box-shadow: 0 4px 14px var(--brand-glow);
        }

        .btn-primary:hover {
            background: var(--brand-hover);
            transform: translateY(-1px);
        }

        .btn-outline {
            background: #fff;
            color: var(--text-primary);
            border: 1px solid var(--border-color);
        }

        .btn-outline:hover {
            background: #f1f5f9;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
            border-radius: var(--radius-sm);
        }

        .btn-danger {
            background: #fee2e2;
            color: #b91c1c;
            border: 1px solid #fecaca;
        }

        .btn-danger:hover {
            background: #fecaca;
        }

        /* ─── DATA TABLE ─── */
        .table-card {
            background: var(--bg-card);
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow-card);
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 13.5px;
        }

        th {
            background: #f8fafc;
            padding: 14px 20px;
            font-weight: 700;
            color: var(--text-secondary);
            text-transform: uppercase;
            font-size: 11.5px;
            letter-spacing: 0.5px;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 16px 20px;
            border-bottom: 1px solid var(--border-color);
            color: var(--text-primary);
            vertical-align: middle;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:hover td {
            background: #fbfcfe;
        }

        .badge-status {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 999px;
            font-size: 11px;
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

        /* ─── MODAL ─── */
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
            background: #fff;
            width: 100%;
            max-width: 600px;
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
            animation: modalPop 0.25s ease;
        }

        @keyframes modalPop {
            from { transform: scale(0.95); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }

        .modal-header {
            padding: 18px 24px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .modal-header h3 {
            font-size: 17px;
            font-weight: 800;
        }

        .modal-close {
            background: transparent;
            border: none;
            font-size: 18px;
            color: var(--text-secondary);
            cursor: pointer;
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
            font-size: 13px;
            font-weight: 700;
            color: var(--text-primary);
        }

        .form-control {
            padding: 10px 14px;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            font-size: 13.5px;
            font-family: inherit;
            outline: none;
            transition: border-color 0.2s;
        }

        .form-control:focus {
            border-color: var(--brand-primary);
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

        /* ─── PROGRESS BAR FOR UPLOAD ─── */
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
            background: var(--brand-primary);
            width: 0%;
            transition: width 0.2s;
        }
    </style>
</head>
<body>

    <!-- ─── ADMIN HEADER ─── -->
    <header class="admin-nav">
        <a href="${pageContext.request.contextPath}/admin" class="nav-brand">
            <i class="fa-solid fa-graduation-cap"></i>
            <span>Kasnet Admin Portal</span>
        </a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin" class="nav-link">Users & Activity</a>
            <a href="${pageContext.request.contextPath}/admin/courses" class="nav-link active">Courses & Video DRM</a>
            <a href="${pageContext.request.contextPath}/courses" class="nav-link" target="_blank">Student View <i class="fa-solid fa-arrow-up-right-from-square" style="font-size: 11px;"></i></a>
            <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: var(--danger);">Logout</a>
        </div>
    </header>

    <div class="main-container">

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
                    <button class="btn btn-outline" onclick="openEditCourseModal()">
                        <i class="fa-solid fa-pen"></i> Edit Course Info
                    </button>
                    <button class="btn btn-primary" onclick="openAddLectureModal()">
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
                                    <div style="font-weight: 700; color: var(--text-primary);">${lec.title}</div>
                                    <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">
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
                                            <div style="font-family: 'JetBrains Mono', monospace; font-size: 10.5px; color: var(--text-secondary); max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
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
                                        <span style="color: var(--text-secondary); font-size: 12px;">
                                            <i class="fa-solid fa-lock"></i> Enrolled Only
                                        </span>
                                    </c:if>
                                </td>
                                <td style="text-align: right; white-space: nowrap;">
                                    <button class="btn btn-outline btn-sm"
                                            data-lec-id="${lec.lectureId}"
                                            data-title="<c:out value='${lec.title}'/>"
                                            onclick="openUploadModalFromBtn(this)" title="Upload Video">
                                        <i class="fa-solid fa-cloud-arrow-up"></i> Upload Video
                                    </button>
                                    <button class="btn btn-outline btn-sm"
                                            data-lec-id="${lec.lectureId}"
                                            data-title="<c:out value='${lec.title}'/>"
                                            data-desc="<c:out value='${lec.description}'/>"
                                            data-num="${lec.lectureNumber}"
                                            data-dur="${lec.durationSeconds}"
                                            data-prev="${lec.preview}"
                                            data-status="${lec.status}"
                                            data-cld="${lec.cloudinaryPublicId}"
                                            onclick="openEditLectureModalFromBtn(this)">
                                        <i class="fa-solid fa-pen"></i>
                                    </button>
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/courses" style="display: inline;" onsubmit="return confirm('Delete this lecture?');">
                                        <input type="hidden" name="action" value="deleteLecture">
                                        <input type="hidden" name="lectureId" value="${lec.lectureId}">
                                        <button type="submit" class="btn btn-danger btn-sm">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty lectures}">
                            <tr>
                                <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-secondary);">
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
                <button class="btn btn-primary" onclick="openCreateCourseModal()">
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
                            <th>Category & Level</th>
                            <th>Status</th>
                            <th>Lectures</th>
                            <th>Students</th>
                            <th style="text-align: right;">Manage</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${courseList}">
                            <tr>
                                <td style="font-weight: 700; color: var(--text-secondary);">#${c.courseId}</td>
                                <td>
                                    <div style="font-weight: 800; font-size: 14.5px; color: var(--text-primary);">${c.title}</div>
                                    <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">
                                        ${c.durationWeeks} Weeks duration
                                    </div>
                                </td>
                                <td style="font-weight: 600;">${c.instructorName}</td>
                                <td>
                                    <div style="font-size: 13px; font-weight: 600;">${c.category}</div>
                                    <div style="font-size: 11.5px; color: var(--text-secondary);">${c.difficulty}</div>
                                </td>
                                <td>
                                    <span class="badge-status ${c.status == 'PUBLISHED' ? 'badge-published' : (c.status == 'DRAFT' ? 'badge-draft' : 'badge-archived')}">
                                        ${c.status}
                                    </span>
                                </td>
                                <td style="font-weight: 700; color: var(--brand-primary);">${c.lectureCount}</td>
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
                    </tbody>
                </table>
            </div>
        </c:if>

    </div>

    <!-- ─── MODAL: CREATE COURSE ─── -->
    <div class="modal-backdrop" id="createCourseModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3>Create New Course</h3>
                <button class="modal-close" onclick="closeModal('createCourseModal')">&times;</button>
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
                                <option value="Computer Science & IT">Computer Science & IT</option>
                                <option value="Data Engineering">Data Engineering</option>
                                <option value="Electronics & Communication">Electronics & Communication</option>
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

    <!-- ─── MODAL: ADD / EDIT LECTURE ─── -->
    <div class="modal-backdrop" id="lectureModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 id="lectureModalTitle">Add Lecture</h3>
                <button class="modal-close" onclick="closeModal('lectureModal')">&times;</button>
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
                <button class="modal-close" onclick="closeModal('uploadModal')">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/admin/courses" enctype="multipart/form-data" id="uploadForm">
                <input type="hidden" name="action" value="uploadVideo">
                <input type="hidden" name="courseId" value="${course.courseId}">
                <input type="hidden" name="lectureId" id="uploadLectureId" value="">

                <div class="modal-body">
                    <p style="font-size: 13.5px; color: var(--text-secondary);">
                        Uploading video for: <strong id="uploadLectureTitle" style="color: var(--text-primary);"></strong>
                    </p>
                    <div class="form-group" style="margin-top: 10px;">
                        <label>Select Video File (MP4, WebM, MOV - Max 500MB) *</label>
                        <input type="file" name="videoFile" class="form-control" accept="video/mp4,video/webm,video/quicktime,video/x-matroska" required id="videoFileInput">
                    </div>
                    <div class="upload-progress-box" id="uploadProgressBox">
                        <div style="font-size: 12.5px; font-weight: 700; color: var(--brand-primary);" id="uploadStatusText">Uploading to secure Cloudinary storage...</div>
                        <div class="upload-bar-track">
                            <div class="upload-bar-fill" id="uploadBarFill"></div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" onclick="closeModal('uploadModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="btnStartUpload">
                        <i class="fa-solid fa-cloud-arrow-up"></i> Upload & Link Stream
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function openCreateCourseModal() {
            document.getElementById('createCourseModal').classList.add('active');
        }

        function closeModal(id) {
            document.getElementById(id).classList.remove('active');
        }

        function openAddLectureModal() {
            document.getElementById('lectureModalTitle').textContent = 'Add New Lecture';
            document.getElementById('lectureAction').value = 'createLecture';
            document.getElementById('lectureIdField').value = '';
            document.getElementById('lecTitleInput').value = '';
            document.getElementById('lecDescInput').value = '';
            document.getElementById('lecNumInput').value = '${lectures.size() + 1}';
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

        document.getElementById('uploadForm')?.addEventListener('submit', function() {
            document.getElementById('uploadProgressBox').style.display = 'block';
            document.getElementById('btnStartUpload').disabled = true;
            document.getElementById('uploadBarFill').style.width = '65%';
        });
    </script>
</body>
</html>

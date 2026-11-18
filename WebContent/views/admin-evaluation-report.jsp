<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Internship Evaluation Report - Admin Portal</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; background: #f8fafc; color: #1e293b; font: 14px/1.5 Inter, "Segoe UI", Arial, sans-serif; }
        .sidebar { position: fixed; inset: 0 auto 0 0; width: 260px; background: #263238; color: #cfd8dc; }
        .brand { display: flex; align-items: center; min-height: 72px; padding: 14px 20px; background: #c62828; color: white; font-weight: 700; }
        .brand span { display: block; font-size: 12px; font-weight: 500; }
        .menu { list-style: none; margin: 0; padding: 14px 0; }
        .menu a { display: block; padding: 13px 20px; color: inherit; text-decoration: none; }
        .menu a:hover, .menu a.active { background: #1e293b; color: white; }
        .main { margin-left: 260px; min-height: 100vh; }
        .topbar { display: flex; justify-content: space-between; align-items: center; gap: 20px; padding: 17px 28px; background: white; border-bottom: 1px solid #e2e8f0; }
        .topbar a { color: #334155; text-decoration: none; }
        .page { max-width: 1220px; margin: 28px auto; padding: 0 24px; }
        h1 { margin: 0; font-size: 24px; }
        .intro { margin: 5px 0 20px; color: #64748b; }
        .card { margin-bottom: 18px; padding: 22px; background: white; border: 1px solid #e2e8f0; border-radius: 10px; box-shadow: 0 2px 6px rgba(15, 23, 42, .04); }
        h2 { margin: 0 0 16px; font-size: 17px; }
        .fields { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 15px 20px; }
        label { display: block; margin-bottom: 6px; font-weight: 600; }
        input, select, textarea { width: 100%; min-height: 40px; padding: 9px 11px; border: 1px solid #cbd5e1; border-radius: 6px; background: white; color: #1e293b; font: inherit; }
        textarea { min-height: 84px; resize: vertical; }
        .wide { grid-column: 1 / -1; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 9px 10px; border: 1px solid #dbe2ea; text-align: left; vertical-align: middle; }
        th { background: #f1f5f9; font-size: 13px; }
        td:first-child { min-width: 260px; font-weight: 500; }
        td:nth-child(2) { min-width: 210px; }
        td:last-child { width: 115px; }
        .actions { display: flex; justify-content: flex-end; gap: 10px; }
        button { min-height: 42px; padding: 0 20px; border: 0; border-radius: 6px; cursor: pointer; font: inherit; font-weight: 600; }
        .primary { background: #c62828; color: white; }
        .secondary { background: white; border: 1px solid #cbd5e1; color: #334155; }
        @media (max-width: 900px) {
            .sidebar { position: static; width: 100%; }
            .menu { display: flex; flex-wrap: wrap; }
            .menu a { padding: 10px 12px; }
            .main { margin-left: 0; }
            .topbar { padding: 14px 18px; }
        }
        @media (max-width: 620px) {
            .fields { grid-template-columns: 1fr; }
            .wide { grid-column: auto; }
            .page { padding: 0 12px; margin-top: 18px; }
            .card { padding: 16px; }
            .topbar { align-items: flex-start; flex-direction: column; }
        }
    </style>
</head>
<body>
    <aside class="sidebar">
        <div class="brand">My Internship <span>Technologies</span></div>
        <ul class="menu">
            <li><a href="${pageContext.request.contextPath}/admin">🎓 Student Details</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/assignments">📝 Assignment Management</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/courses">🎥 Courses &amp; Lectures</a></li>
            <li><a class="active" href="${pageContext.request.contextPath}/admin/evaluation-report">📋 Internship Evaluation Report</a></li>
            <li><a href="${pageContext.request.contextPath}/admin?tab=offer-letter">📄 Certification</a></li>
        </ul>
    </aside>
    <div class="main">
        <header class="topbar">
            <strong>Online Internship Administration / Internship Evaluation Report</strong>
            <span>
                <c:out value="${not empty sessionScope.userName ? sessionScope.userName : 'Admin'}"/>
                &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/" target="_blank">Public Site</a>
                &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </span>
        </header>
        <main class="page">
            <h1>Internship Evaluation Report</h1>
            <p class="intro">Enter the evaluation details to generate a three-page PDF matching the uploaded report.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/evaluation-report" target="_blank" accept-charset="UTF-8">
                <section class="card">
                    <h2>1. Student Information</h2>
                    <div class="fields">
                        <div><label for="studentName">Student Name</label><input id="studentName" name="studentName" maxlength="120" required></div>
                        <div><label for="collegeName">College Name</label><input id="collegeName" name="collegeName" maxlength="180" required></div>
                        <div><label for="branchDepartment">Branch / Department</label><input id="branchDepartment" name="branchDepartment" maxlength="120" required></div>
                        <div><label for="city">City</label><input id="city" name="city" maxlength="100" required></div>
                        <div><label for="internshipDuration">Internship Duration</label><input id="internshipDuration" name="internshipDuration" maxlength="100" placeholder="e.g. 3 months" required></div>
                    </div>
                </section>

                <section class="card">
                    <h2>2. Global Certification Details</h2>
                    <div class="fields">
                        <div><label for="certificationName">Global Certification Name</label><input id="certificationName" name="certificationName" maxlength="180" required></div>
                        <div><label for="certificationScore">Certification Score</label><input id="certificationScore" name="certificationScore" maxlength="60" required></div>
                        <div><label for="certificationDate">Date of Achievement</label><input id="certificationDate" name="certificationDate" type="date" required></div>
                    </div>
                </section>

                <section class="card">
                    <h2>3. Internship Performance Evaluation</h2>
                    <div class="table-wrap">
                        <table>
                            <thead><tr><th>Evaluation Criteria</th><th>Status</th><th>Rating (1-5)</th></tr></thead>
                            <tbody>
                                <tr><td>Attendance</td><td><input name="status0" value="100%" maxlength="40" required aria-label="Attendance status"></td><td><select name="rating0" required aria-label="Attendance rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option selected>5</option></select></td></tr>
                                <tr><td>Punctuality &amp; Discipline</td><td><input name="status1" value="100%" maxlength="40" required aria-label="Punctuality and discipline status"></td><td><select name="rating1" required aria-label="Punctuality and discipline rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Understanding of Project Requirements</td><td><input name="status2" value="Completed" maxlength="40" required aria-label="Project requirements status"></td><td><select name="rating2" required aria-label="Project requirements rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Project Assignment</td><td><input name="status3" value="Completed" maxlength="40" required aria-label="Project assignment status"></td><td><select name="rating3" required aria-label="Project assignment rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Project Task Completion</td><td><input name="status4" value="Completed" maxlength="40" required aria-label="Task completion status"></td><td><select name="rating4" required aria-label="Task completion rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Technical Skills &amp; Practical Implementation</td><td><input name="status5" maxlength="40" required aria-label="Technical skills status"></td><td><select name="rating5" required aria-label="Technical skills rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Problem-Solving Ability</td><td><input name="status6" maxlength="40" required aria-label="Problem solving status"></td><td><select name="rating6" required aria-label="Problem solving rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Communication &amp; Teamwork</td><td><input name="status7" maxlength="40" required aria-label="Communication and teamwork status"></td><td><select name="rating7" required aria-label="Communication and teamwork rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Internship Report Submission</td><td><input name="status8" value="Completed" maxlength="40" required aria-label="Report submission status"></td><td><select name="rating8" required aria-label="Report submission rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>College Internship Presentation</td><td><input name="status9" value="Completed / Pending" maxlength="40" required aria-label="College presentation status"></td><td><select name="rating9" required aria-label="College presentation rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                                <tr><td>Global Certification Performance</td><td><input name="status10" value="Achieved / Pending" maxlength="40" required aria-label="Certification performance status"></td><td><select name="rating10" required aria-label="Certification performance rating"><option value="">Select</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select></td></tr>
                            </tbody>
                        </table>
                    </div>
                    <div style="max-width:260px;margin-top:16px;">
                        <label for="overallRating">Overall Rating (out of 5)</label>
                        <select id="overallRating" name="overallRating" required><option value="">Select rating</option><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select>
                    </div>
                </section>

                <section class="card">
                    <h2>6. Final Recommendation</h2>
                    <div class="fields">
                        <div>
                            <label for="internshipStatus">Internship Status</label>
                            <select id="internshipStatus" name="internshipStatus" required>
                                <option value="Successfully Completed">Successfully Completed</option>
                                <option value="Pending">Pending</option>
                            </select>
                        </div>
                        <div>
                            <label for="companyRecommendation">Company Recommendation</label>
                            <select id="companyRecommendation" name="companyRecommendation" required>
                                <option value="Recommended">Recommended</option>
                                <option value="Recommended with Improvement Areas">Recommended with Improvement Areas</option>
                                <option value="Not Recommended">Not Recommended</option>
                            </select>
                        </div>
                        <div class="wide"><label for="keyStrengths">Key Strengths</label><textarea id="keyStrengths" name="keyStrengths" maxlength="500" required></textarea></div>
                        <div class="wide"><label for="improvementAreas">Areas for Improvement</label><textarea id="improvementAreas" name="improvementAreas" maxlength="500" required></textarea></div>
                        <div class="wide"><label for="evaluatorRemarks">Evaluator's Remarks</label><textarea id="evaluatorRemarks" name="evaluatorRemarks" maxlength="1000" required></textarea></div>
                    </div>
                </section>

                <section class="card">
                    <h2>Evaluator Details</h2>
                    <div class="fields">
                        <div><label for="evaluatorName">Evaluator Name</label><input id="evaluatorName" name="evaluatorName" maxlength="120" required></div>
                        <div><label for="designation">Designation</label><input id="designation" name="designation" maxlength="120" required></div>
                        <div><label for="signature">Signature (typed)</label><input id="signature" name="signature" maxlength="120" required></div>
                        <div><label for="evaluationDate">Date</label><input id="evaluationDate" name="evaluationDate" type="date" required></div>
                    </div>
                </section>
                <div class="actions">
                    <button class="secondary" type="reset">Reset</button>
                    <button class="primary" type="submit">Generate PDF</button>
                </div>
            </form>
        </main>
    </div>
</body>
</html>

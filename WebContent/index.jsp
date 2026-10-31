<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String registeredParam = request.getParameter("registered");
    boolean isRegisteredSuccess = "1".equals(registeredParam) || "success".equalsIgnoreCase(registeredParam)
            || session.getAttribute("registrationSuccess") != null;
    if (session.getAttribute("registrationSuccess") != null) {
        session.removeAttribute("registrationSuccess");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Internship Technologies Portal - Practical Learning &amp; Industry Experience</title>
    <style>
        /* =========================================================
           RESET & CORE TYPOGRAPHY
           ========================================================= */
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --primary: #1a365d;
            --primary-hover: #2a4365;
            --accent: #2b6cb0;
            --accent-light: #ebf8ff;
            --text-dark: #2d3748;
            --text-muted: #718096;
            --bg-light: #f7fafc;
            --white: #ffffff;
            --border: #e2e8f0;
            --border-hover: #cbd5e0;
            --success: #2f855a;
            --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.1);
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            --radius: 8px;
            --font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        html, body {
            font-family: var(--font-family);
            color: var(--text-dark);
            background-color: var(--white);
            line-height: 1.6;
            -webkit-font-smoothing: antialiased;
        }

        a {
            color: var(--accent);
            text-decoration: none;
            transition: color 0.2s ease;
        }

        a:hover {
            color: var(--primary);
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 24px;
        }

        /* =========================================================
           TOP NAVIGATION BAR
           ========================================================= */
        .navbar {
            background-color: var(--white);
            border-bottom: 1px solid var(--border);
            position: sticky;
            top: 0;
            z-index: 100;
            box-shadow: var(--shadow-sm);
        }

        .navbar-inner {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 72px;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            text-decoration: none;
        }

        .brand-badge {
            width: 40px;
            height: 40px;
            background: var(--primary);
            color: var(--white);
            border-radius: var(--radius);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 1.1rem;
        }

        .brand-text {
            display: flex;
            flex-direction: column;
        }

        .brand-title {
            font-size: 1.15rem;
            font-weight: 700;
            color: var(--primary);
            line-height: 1.2;
        }

        .brand-subtitle {
            font-size: 0.75rem;
            color: var(--text-muted);
            letter-spacing: 0.5px;
            text-transform: uppercase;
        }

        .nav-links {
            display: flex;
            align-items: center;
            gap: 28px;
            list-style: none;
        }

        .nav-links a {
            color: var(--text-dark);
            font-weight: 500;
            font-size: 0.95rem;
        }

        .nav-links a:hover {
            color: var(--accent);
        }

        .nav-auth {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 0.95rem;
            padding: 10px 22px;
            border-radius: 6px;
            border: 1px solid transparent;
            cursor: pointer;
            transition: all 0.2s ease;
            text-decoration: none;
        }

        .btn-outline {
            background-color: transparent;
            border-color: var(--accent);
            color: var(--accent);
        }

        .btn-outline:hover {
            background-color: var(--accent-light);
            color: var(--primary);
            border-color: var(--primary);
        }

        .btn-primary {
            background-color: var(--accent);
            color: var(--white);
        }

        .btn-primary:hover {
            background-color: var(--primary);
            color: var(--white);
        }

        .btn-large {
            padding: 12px 28px;
            font-size: 1.05rem;
        }

        /* =========================================================
           HERO SECTION
           ========================================================= */
        .hero {
            background: linear-gradient(180deg, var(--bg-light) 0%, var(--white) 100%);
            padding: 72px 0 60px;
            border-bottom: 1px solid var(--border);
            text-align: center;
        }

        .hero-inner {
            max-width: 860px;
            margin: 0 auto;
        }

        .hero-tag {
            display: inline-block;
            background-color: var(--accent-light);
            color: var(--accent);
            font-size: 0.82rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            padding: 6px 14px;
            border-radius: 20px;
            margin-bottom: 20px;
            border: 1px solid #bee3f8;
        }

        .hero h1 {
            font-size: 2.6rem;
            font-weight: 800;
            color: var(--primary);
            line-height: 1.25;
            margin-bottom: 20px;
        }

        .hero p {
            font-size: 1.15rem;
            color: var(--text-muted);
            margin-bottom: 34px;
            line-height: 1.7;
        }

        .hero-actions {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 16px;
            flex-wrap: wrap;
        }

        /* =========================================================
           STATS SECTION
           ========================================================= */
        .stats-bar {
            background-color: var(--white);
            padding: 44px 0;
            border-bottom: 1px solid var(--border);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 24px;
            text-align: center;
        }

        .stat-card {
            padding: 16px;
        }

        .stat-value {
            font-size: 2.2rem;
            font-weight: 800;
            color: var(--primary);
            margin-bottom: 6px;
        }

        .stat-label {
            font-size: 0.95rem;
            color: var(--text-muted);
            font-weight: 500;
        }

        /* =========================================================
           TRACKS & DOMAINS
           ========================================================= */
        .section {
            padding: 64px 0;
        }

        .section-header {
            text-align: center;
            max-width: 680px;
            margin: 0 auto 48px;
        }

        .section-title {
            font-size: 1.9rem;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 12px;
        }

        .section-subtitle {
            font-size: 1rem;
            color: var(--text-muted);
        }

        .tracks-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 24px;
        }

        .track-card {
            background: var(--white);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 28px 24px;
            box-shadow: var(--shadow-sm);
            transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .track-card:hover {
            border-color: var(--accent);
            box-shadow: var(--shadow-md);
            transform: translateY(-2px);
        }

        .track-badge {
            display: inline-block;
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--accent);
            background: var(--accent-light);
            padding: 4px 10px;
            border-radius: 4px;
            margin-bottom: 14px;
            width: fit-content;
        }

        .track-title {
            font-size: 1.2rem;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 10px;
        }

        .track-desc {
            font-size: 0.92rem;
            color: var(--text-muted);
            line-height: 1.5;
            margin-bottom: 18px;
        }

        .track-skills {
            font-size: 0.82rem;
            color: var(--primary);
            background: var(--bg-light);
            padding: 8px 12px;
            border-radius: 4px;
            font-weight: 500;
        }

        /* =========================================================
           PROGRAM FEATURES
           ========================================================= */
        .features-section {
            background-color: var(--bg-light);
            border-top: 1px solid var(--border);
            border-bottom: 1px solid var(--border);
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 32px;
        }

        .feature-item {
            display: flex;
            align-items: flex-start;
            gap: 16px;
        }

        .feature-icon-box {
            width: 44px;
            height: 44px;
            border-radius: 8px;
            background: var(--accent-light);
            color: var(--accent);
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1rem;
            flex-shrink: 0;
            border: 1px solid #bee3f8;
        }

        .feature-content h4 {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 6px;
        }

        .feature-content p {
            font-size: 0.92rem;
            color: var(--text-muted);
            line-height: 1.55;
        }

        /* =========================================================
           CALL TO ACTION BANNER
           ========================================================= */
        .cta-banner {
            background-color: var(--primary);
            color: var(--white);
            padding: 56px 0;
            text-align: center;
        }

        .cta-banner h2 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 14px;
        }

        .cta-banner p {
            font-size: 1.05rem;
            color: #cbd5e0;
            margin-bottom: 28px;
            max-width: 620px;
            margin-left: auto;
            margin-right: auto;
        }

        .cta-btn-white {
            background-color: var(--white);
            color: var(--primary);
            border: 1px solid var(--white);
            font-weight: 700;
        }

        .cta-btn-white:hover {
            background-color: #f7fafc;
            color: var(--primary);
        }

        /* =========================================================
           FOOTER
           ========================================================= */
        .footer {
            background-color: #1a202c;
            color: #a0aec0;
            padding: 40px 0 24px;
            font-size: 0.88rem;
        }

        .footer-inner {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
            border-bottom: 1px solid #2d3748;
            padding-bottom: 24px;
            margin-bottom: 20px;
        }

        .footer-links {
            display: flex;
            gap: 20px;
            list-style: none;
        }

        .footer-links a {
            color: #cbd5e0;
        }

        .footer-links a:hover {
            color: var(--white);
        }

        .footer-copy {
            text-align: center;
            font-size: 0.82rem;
            color: #718096;
        }

        /* =========================================================
           POPUP / MODAL
           ========================================================= */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(4px);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 16px;
        }

        .modal-card {
            background: var(--white);
            border-radius: 12px;
            max-width: 480px;
            width: 100%;
            padding: 32px 28px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 10px 10px -5px rgba(0, 0, 0, 0.1);
            text-align: center;
            border-top: 5px solid var(--success);
        }

        .modal-title {
            font-size: 1.35rem;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 12px;
        }

        .modal-body {
            font-size: 0.95rem;
            color: var(--text-muted);
            line-height: 1.6;
            margin-bottom: 26px;
        }

        .modal-actions {
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        @media (max-width: 768px) {
            .navbar-inner {
                flex-direction: column;
                height: auto;
                padding: 14px 0;
                gap: 14px;
            }
            .nav-links {
                display: none;
            }
            .hero h1 {
                font-size: 2rem;
            }
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
    </style>
</head>
<body>

    <!-- =========================================================
         TOP NAVIGATION BAR
         ========================================================= -->
    <header class="navbar">
        <div class="container navbar-inner">
            <a href="${pageContext.request.contextPath}/" class="brand">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 38px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 6px; background: #ffffff; padding: 2px;" />
                <div class="brand-text">
                    <span class="brand-title">My Internship Technologies</span>
                    <span class="brand-subtitle">Skill Development Platform</span>
                </div>
            </a>

            <ul class="nav-links">
                <li><a href="#overview">Overview</a></li>
                <li><a href="#domains">Domains</a></li>
                <li><a href="#how-it-works">How It Works</a></li>
                <li><a href="#features">Features</a></li>
            </ul>

            <div class="nav-auth">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Sign In</a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Sign Up</a>
            </div>
        </div>
    </header>

    <!-- =========================================================
         HERO SECTION
         ========================================================= -->
    <section class="hero" id="overview">
        <div class="container hero-inner">
            <span class="hero-tag">Empowering Practical Engineering Education</span>
            <h1>Gain Real-World Industry Experience Through Online Internships</h1>
            <p>Work on mentored industry tasks, develop end-to-end full stack and cloud projects, access domain lecture repositories, and earn verified credentials from home.</p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-large">Register as Student</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline btn-large">Student Login</a>
            </div>
        </div>
    </section>

    <!-- =========================================================
         STATISTICS BAR
         ========================================================= -->
    <section class="stats-bar">
        <div class="container">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value">15,000+</div>
                    <div class="stat-label">Students Mentored</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">100+</div>
                    <div class="stat-label">Live Project Tracks</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">50+</div>
                    <div class="stat-label">Industry Mentors</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">100%</div>
                    <div class="stat-label">Practical Self-Paced</div>
                </div>
            </div>
        </div>
    </section>

    <!-- =========================================================
         INTERNSHIP DOMAINS
         ========================================================= -->
    <section class="section" id="domains">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title">Industry Internship Tracks</h2>
                <p class="section-subtitle">Choose from hands-on curriculum tracks curated to match top tech job requirements.</p>
            </div>

            <div class="tracks-grid">
                <div class="track-card">
                    <div>
                        <span class="track-badge">Full Stack</span>
                        <h3 class="track-title">Java Full Stack Development</h3>
                        <p class="track-desc">Build complete enterprise web applications using Java, Servlets, JSP, JDBC, MVC architecture, and MySQL.</p>
                    </div>
                    <div class="track-skills">Core Java &bull; MVC &bull; Servlets &bull; JSP &bull; MySQL</div>
                </div>

                <div class="track-card">
                    <div>
                        <span class="track-badge">AI &amp; Data</span>
                        <h3 class="track-title">Python &amp; Machine Learning</h3>
                        <p class="track-desc">Implement predictive models, data processing pipelines, and data analytics workflows using Python tools.</p>
                    </div>
                    <div class="track-skills">Python &bull; NumPy &bull; Pandas &bull; Scikit-Learn</div>
                </div>

                <div class="track-card">
                    <div>
                        <span class="track-badge">Web Tech</span>
                        <h3 class="track-title">Web &amp; Frontend Technologies</h3>
                        <p class="track-desc">Design modern, responsive client interfaces, interactive state handling, and clean standards-compliant UI components.</p>
                    </div>
                    <div class="track-skills">HTML5 &bull; CSS3 &bull; Modern JS &bull; REST APIs</div>
                </div>

                <div class="track-card">
                    <div>
                        <span class="track-badge">Infrastructure</span>
                        <h3 class="track-title">Cloud Computing &amp; DevOps</h3>
                        <p class="track-desc">Learn scalable cloud services, server provisioning, containerized workflows, and automated deployment pipelines.</p>
                    </div>
                    <div class="track-skills">Cloud Architecture &bull; CI/CD &bull; Containerization</div>
                </div>
            </div>
        </div>
    </section>

    <!-- =========================================================
         HOW IT WORKS
         ========================================================= -->
    <section class="section features-section" id="how-it-works">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title">How The Internship Works</h2>
                <p class="section-subtitle">A straightforward four-step learning model designed for college students and recent graduates.</p>
            </div>

            <div class="features-grid">
                <div class="feature-item">
                    <div class="feature-icon-box">1</div>
                    <div class="feature-content">
                        <h4>Create Account &amp; Profile</h4>
                        <p>Register with your student details, academic stream, and complete your profile sections to get started.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">2</div>
                    <div class="feature-content">
                        <h4>Access Lectures &amp; Question Bank</h4>
                        <p>Watch focused video sessions and prepare using domain-specific interview and technical question banks.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">3</div>
                    <div class="feature-content">
                        <h4>Complete Practical Assignments</h4>
                        <p>Submit weekly task milestones, build project modules, and receive feedback on your code and solutions.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">4</div>
                    <div class="feature-content">
                        <h4>Earn Verified Certificate</h4>
                        <p>Receive your verifiable internship completion certificate and performance evaluation upon finishing requirements.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- =========================================================
         PORTAL FEATURES
         ========================================================= -->
    <section class="section" id="features">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title">Portal Capabilities</h2>
                <p class="section-subtitle">A centralized student workspace designed to keep all internship requirements organized.</p>
            </div>

            <div class="features-grid">
                <div class="feature-item">
                    <div class="feature-icon-box">P</div>
                    <div class="feature-content">
                        <h4>Structured Student Profile</h4>
                        <p>Organize personal info, education records, verified certifications, portfolio projects, and extracurricular activities.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">A</div>
                    <div class="feature-content">
                        <h4>Assignment Tracking</h4>
                        <p>View upcoming tasks, deadline notifications, and review mentor feedback directly in your portal Home.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">Q</div>
                    <div class="feature-content">
                        <h4>Subject Question Bank</h4>
                        <p>Structured interview and conceptual questions across Java, Python, Web Development, Databases, and Cloud.</p>
                    </div>
                </div>

                <div class="feature-item">
                    <div class="feature-icon-box">R</div>
                    <div class="feature-content">
                        <h4>Resume Analysis</h4>
                        <p>Dedicated resume module with ATS review support and technical formatting tips to prepare you for campus placements.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- =========================================================
         CALL TO ACTION BANNER
         ========================================================= -->
    <section class="cta-banner">
        <div class="container">
            <h2>Ready to Begin Your Internship?</h2>
            <p>Register today to begin your hands-on journey, develop practical software engineering skills, and prepare for industry placements.</p>
            <div style="display: flex; justify-content: center; gap: 14px; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/register" class="btn cta-btn-white btn-large">Create Student Account</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline btn-large" style="border-color: #cbd5e0; color: #fff;">Existing Student Login</a>
            </div>
        </div>
    </section>

    <!-- =========================================================
         FOOTER
         ========================================================= -->
    <footer class="footer">
        <div class="container">
            <div class="footer-inner">
                <div>
                    <strong style="color: #fff;">My Internship Technologies Portal</strong>
                    <div style="margin-top: 4px; font-size: 0.8rem;">My Internship Technologies &bull; Pune, Maharashtra</div>
                </div>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/login">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/register">Register</a></li>
                    <li><a href="#overview">Overview</a></li>
                    <li><a href="#domains">Tracks</a></li>
                </ul>
            </div>
            <p class="footer-copy">&copy; 2026 My Internship Technologies. All rights reserved. Designed for student internship enablement.</p>
        </div>
    </footer>

    <!-- =========================================================
         REGISTRATION SUCCESS MODAL POPUP
         ========================================================= -->
    <% if (isRegisteredSuccess) { %>
    <div class="modal-overlay" id="registrationSuccessModal">
        <div class="modal-card">
            <h3 class="modal-title">Congratulations! Account created successfully.</h3>
            <p class="modal-body">Your student registration has been completed. You can now sign in using your registered email and password to access your internship Home.</p>
            <div class="modal-actions">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Proceed to Login</a>
                <button type="button" class="btn btn-outline" onclick="closeSuccessModal()">Close</button>
            </div>
        </div>
    </div>
    <script>
        function closeSuccessModal() {
            var modal = document.getElementById('registrationSuccessModal');
            if (modal) {
                modal.style.display = 'none';
            }
            // Clean URL query parameter
            if (window.history && window.history.replaceState) {
                var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history.replaceState({}, document.title, cleanUrl);
            }
        }
    </script>
    <% } %>

</body>
</html>

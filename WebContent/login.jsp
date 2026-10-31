<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
    Login Page – My Internship Technologies Portal
    Redirects authenticated users straight to /home.
--%>
<%
    /* ── Session guard ── */
    if (session.getAttribute("userId") != null) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }

    /* ── Consume one-time success message ── */
    String successMsg = (String) session.getAttribute("successMessage");
    if (successMsg != null) {
        session.removeAttribute("successMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login – My Internship Technologies</title>

    <style>
        /* ═══════════════════════════════════════════════════
           RESET & BASE
        ═══════════════════════════════════════════════════ */
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --navy:        #1a237e;
            --indigo:      #283593;
            --indigo-mid:  #3949ab;
            --indigo-lt:   #5c6bc0;
            --accent:      #3f51b5;
            --accent-hvr:  #303f9f;
            --white:       #ffffff;
            --off-white:   #f5f6fa;
            --border:      #e0e3ef;
            --text-dark:   #1c1f3a;
            --text-mid:    #4a4f72;
            --text-lt:     #8e93b0;
            --error-bg:    #fff0f0;
            --error-bdr:   #f44336;
            --error-txt:   #c62828;
            --success-bg:  #f0fff4;
            --success-bdr: #43a047;
            --success-txt: #2e7d32;
            --shadow:      0 20px 60px rgba(26, 35, 126, 0.18);
            --radius:      14px;
            --transition:  0.25s ease;
            --font:        -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
                           "Helvetica Neue", Arial, sans-serif;
        }

        html, body {
            height: 100%;
            font-family: var(--font);
            background: var(--off-white);
            color: var(--text-dark);
            -webkit-font-smoothing: antialiased;
        }

        /* ═══════════════════════════════════════════════════
           PAGE WRAPPER
        ═══════════════════════════════════════════════════ */
        .page-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px 16px;
            background:
                radial-gradient(ellipse at 20% 50%, rgba(63, 81, 181, 0.12) 0%, transparent 60%),
                radial-gradient(ellipse at 80% 20%, rgba(26, 35, 126, 0.10) 0%, transparent 55%),
                var(--off-white);
        }

        /* ═══════════════════════════════════════════════════
           CARD (split panel)
        ═══════════════════════════════════════════════════ */
        .card {
            display: flex;
            width: 100%;
            max-width: 960px;
            min-height: 580px;
            border-radius: var(--radius);
            overflow: hidden;
            box-shadow: var(--shadow);
        }

        /* ═══════════════════════════════════════════════════
           LEFT – BRANDING PANEL
        ═══════════════════════════════════════════════════ */
        .brand-panel {
            flex: 0 0 42%;
            background: linear-gradient(145deg, var(--navy) 0%, var(--indigo-mid) 60%, var(--indigo-lt) 100%);
            color: var(--white);
            padding: 52px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            position: relative;
            overflow: hidden;
        }

        /* decorative circles */
        .brand-panel::before,
        .brand-panel::after {
            content: "";
            position: absolute;
            border-radius: 50%;
            opacity: 0.08;
            background: var(--white);
        }
        .brand-panel::before {
            width: 320px; height: 320px;
            top: -100px; right: -120px;
        }
        .brand-panel::after {
            width: 200px; height: 200px;
            bottom: -60px; left: -70px;
        }

        /* logo */
        .logo-wrap {
            display: flex;
            align-items: center;
            gap: 14px;
            margin-bottom: 36px;
        }
        .brand-logo-img {
            max-width: 58px;
            max-height: 58px;
            height: auto;
            object-fit: contain;
            border-radius: 12px;
            background: #ffffff;
            padding: 4px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.25);
            flex-shrink: 0;
        }
        .logo-icon {
            width: 58px;
            height: 58px;
            border-radius: 50%;
            background: rgba(255,255,255,0.18);
            border: 2.5px solid rgba(255,255,255,0.45);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            font-weight: 800;
            letter-spacing: -1px;
            flex-shrink: 0;
            box-shadow: 0 4px 16px rgba(0,0,0,0.25);
        }
        .logo-text-wrap {
            display: flex;
            flex-direction: column;
        }
        .logo-name {
            font-size: 15px;
            font-weight: 700;
            letter-spacing: 0.04em;
            line-height: 1.2;
            text-transform: uppercase;
        }
        .logo-sub {
            font-size: 11px;
            opacity: 0.72;
            letter-spacing: 0.08em;
            margin-top: 2px;
        }

        /* portal title */
        .brand-title {
            font-size: 26px;
            font-weight: 800;
            line-height: 1.25;
            margin-bottom: 10px;
            letter-spacing: -0.3px;
        }
        .brand-tagline {
            font-size: 14px;
            opacity: 0.80;
            line-height: 1.6;
            margin-bottom: 32px;
        }

        /* bullet features */
        .feature-list {
            list-style: none;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }
        .feature-list li {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            font-size: 13.5px;
            opacity: 0.88;
            line-height: 1.5;
        }
        .feature-icon {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: rgba(255,255,255,0.15);
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            color: #ffffff;
            margin-top: 1px;
        }

        /* footer badge */
        .brand-footer {
            margin-top: 44px;
            font-size: 11px;
            opacity: 0.50;
            letter-spacing: 0.06em;
        }

        /* ═══════════════════════════════════════════════════
           RIGHT – FORM PANEL
        ═══════════════════════════════════════════════════ */
        .form-panel {
            flex: 1;
            background: var(--white);
            padding: 52px 48px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            overflow-y: auto;
        }

        /* heading */
        .form-heading {
            font-size: 28px;
            font-weight: 800;
            color: var(--text-dark);
            letter-spacing: -0.4px;
            margin-bottom: 6px;
        }
        .form-subheading {
            font-size: 14px;
            color: var(--text-mid);
            margin-bottom: 32px;
            line-height: 1.5;
        }

        /* alerts */
        .alert {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            padding: 12px 16px;
            border-radius: 8px;
            font-size: 13.5px;
            font-weight: 500;
            margin-bottom: 22px;
            line-height: 1.5;
            animation: slideDown 0.3s ease;
        }
        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-8px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        .alert-error {
            background: var(--error-bg);
            border: 1px solid var(--error-bdr);
            color: var(--error-txt);
        }
        .alert-success {
            background: var(--success-bg);
            border: 1px solid var(--success-bdr);
            color: var(--success-txt);
        }
        .alert-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            margin-top: 1px;
        }

        /* form groups */
        .form-group {
            margin-bottom: 20px;
        }
        .form-label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: var(--text-mid);
            margin-bottom: 7px;
            letter-spacing: 0.02em;
        }
        .form-label .required {
            color: #e53935;
            margin-left: 2px;
        }

        /* inputs */
        .input-wrap {
            position: relative;
        }
        .form-control {
            width: 100%;
            height: 46px;
            padding: 0 14px;
            border: 1.5px solid var(--border);
            border-radius: 8px;
            font-size: 14.5px;
            font-family: var(--font);
            color: var(--text-dark);
            background: var(--off-white);
            transition: border-color var(--transition), box-shadow var(--transition), background var(--transition);
            outline: none;
            appearance: none;
            -webkit-appearance: none;
        }
        .form-control:focus {
            border-color: var(--accent);
            background: var(--white);
            box-shadow: 0 0 0 3px rgba(63, 81, 181, 0.13);
        }
        .form-control.is-invalid {
            border-color: var(--error-bdr);
            background: var(--error-bg);
        }
        .form-control.is-invalid:focus {
            box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.12);
        }

        /* password field */
        .pw-wrap {
            position: relative;
        }
        .pw-wrap .form-control {
            padding-right: 46px;
        }
        .pw-toggle {
            position: absolute;
            right: 13px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: var(--text-lt);
            padding: 4px;
            display: flex;
            align-items: center;
            transition: color var(--transition);
            line-height: 1;
        }
        .pw-toggle:hover { color: var(--accent); }
        .pw-toggle svg { width: 20px; height: 20px; display: block; pointer-events: none; }

        /* select */
        select.form-control {
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%238e93b0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 13px center;
            background-size: 16px;
            padding-right: 40px;
            cursor: pointer;
        }

        /* CAPTCHA */
        .captcha-row {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .captcha-box {
            flex: 0 0 auto;
            height: 46px;
            padding: 0 18px;
            background: linear-gradient(135deg, #e8eaf6, #c5cae9);
            border: 2px dashed var(--indigo-lt);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            font-weight: 800;
            letter-spacing: 8px;
            color: var(--navy);
            font-family: "Courier New", Courier, monospace;
            user-select: none;
            -webkit-user-select: none;
            text-shadow: 1px 1px 0 rgba(255,255,255,0.6);
            position: relative;
            overflow: hidden;
            min-width: 148px;
        }
        /* noise overlay on captcha */
        .captcha-box::after {
            content: "";
            position: absolute;
            inset: 0;
            background-image: repeating-linear-gradient(
                0deg,
                transparent,
                transparent 3px,
                rgba(63,81,181,0.06) 3px,
                rgba(63,81,181,0.06) 4px
            );
            pointer-events: none;
        }
        .captcha-input { flex: 1; }

        /* inline field error */
        .field-error {
            display: none;
            font-size: 12px;
            color: var(--error-txt);
            margin-top: 5px;
            font-weight: 500;
        }

        /* submit button */
        .btn-submit {
            width: 100%;
            height: 48px;
            background: linear-gradient(135deg, var(--indigo) 0%, var(--accent) 100%);
            color: var(--white);
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 700;
            font-family: var(--font);
            cursor: pointer;
            letter-spacing: 0.04em;
            transition: background var(--transition), transform var(--transition), box-shadow var(--transition);
            box-shadow: 0 4px 16px rgba(63, 81, 181, 0.35);
            margin-top: 8px;
        }
        .btn-submit:hover {
            background: linear-gradient(135deg, var(--accent-hvr) 0%, var(--indigo) 100%);
            transform: translateY(-1px);
            box-shadow: 0 6px 22px rgba(63, 81, 181, 0.45);
        }
        .btn-submit:active {
            transform: translateY(0);
            box-shadow: 0 2px 10px rgba(63, 81, 181, 0.30);
        }

        /* register link */
        .register-row {
            text-align: center;
            margin-top: 22px;
            font-size: 13.5px;
            color: var(--text-mid);
        }
        .register-row a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 600;
            transition: color var(--transition);
        }
        .register-row a:hover {
            color: var(--accent-hvr);
            text-decoration: underline;
        }

        /* divider */
        .divider {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 4px 0 24px;
            color: var(--text-lt);
            font-size: 12px;
            letter-spacing: 0.06em;
        }
        .divider::before, .divider::after {
            content: "";
            flex: 1;
            height: 1px;
            background: var(--border);
        }

        /* ═══════════════════════════════════════════════════
           RESPONSIVE
        ═══════════════════════════════════════════════════ */
        @media (max-width: 720px) {
            .card {
                flex-direction: column;
                max-width: 480px;
            }
            .brand-panel {
                flex: 0 0 auto;
                padding: 32px 28px;
                border-radius: 0;
            }
            .brand-panel::before { width: 200px; height: 200px; top: -60px; right: -70px; }
            .brand-panel::after  { display: none; }
            .brand-title { font-size: 20px; }
            .brand-tagline,
            .feature-list { display: none; }
            .brand-footer { margin-top: 20px; }
            .form-panel {
                padding: 36px 28px;
            }
            .form-heading { font-size: 22px; }
        }

        @media (max-width: 420px) {
            .form-panel { padding: 28px 20px; }
            .captcha-box { min-width: 120px; font-size: 18px; letter-spacing: 5px; }
        }
    </style>
</head>
<body>

<div class="page-wrapper">
    <div class="card">

        <!-- ══════════════════════════════════════
             LEFT – BRANDING PANEL
        ══════════════════════════════════════ -->
        <aside class="brand-panel">
            <div class="logo-wrap">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" class="brand-logo-img" />
                <div class="logo-text-wrap">
                    <span class="logo-name">My Internship Technologies</span>
                    <span class="logo-sub">Online Internship Portal</span>
                </div>
            </div>

            <h1 class="brand-title">My Internship<br />Technologies</h1>
            <p class="brand-tagline">
                Your gateway to real-world experience —<br />
                bridging talent with opportunity.
            </p>

            <ul class="feature-list">
                <li>
                    <span class="feature-icon">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                    </span>
                    <span>Browse and apply to curated internship programs across industries.</span>
                </li>
                <li>
                    <span class="feature-icon">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><rect x="8" y="2" width="8" height="4" rx="1" ry="1"/><path d="M9 12l2 2 4-4"/></svg>
                    </span>
                    <span>Track your applications and receive real-time status updates.</span>
                </li>
                <li>
                    <span class="feature-icon">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                    </span>
                    <span>Secure, role-based access for Students and Administrators.</span>
                </li>
                <li>
                    <span class="feature-icon">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
                    </span>
                    <span>Admin Home for managing listings, applicants &amp; reports.</span>
                </li>
            </ul>

            <p class="brand-footer">&copy; 2026 My Internship Technologies &bull; All rights reserved</p>
        </aside>

        <!-- ══════════════════════════════════════
             RIGHT – FORM PANEL
        ══════════════════════════════════════ -->
        <main class="form-panel">
            <h2 class="form-heading">Welcome Back!</h2>
            <p class="form-subheading">Sign in to continue to your internship portal account.</p>

            <%-- ── Error alert ── --%>
            <c:if test="${not empty requestScope.errorMessage}">
                <div class="alert alert-error" id="errorAlert">
                    <span class="alert-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    </span>
                    <span>${requestScope.errorMessage}</span>
                </div>
            </c:if>

            <%-- ── Success alert (one-time from session, already removed above) ── --%>
            <%
                if (successMsg != null && !successMsg.isEmpty()) {
            %>
                <div class="alert alert-success" id="successAlert">
                    <span class="alert-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                    </span>
                    <span><%= successMsg %></span>
                </div>
            <%
                }
            %>

            <div class="divider">SIGN IN TO YOUR ACCOUNT</div>

            <form
                id="loginForm"
                method="post"
                action="${pageContext.request.contextPath}/login"
                novalidate
            >

                <%-- 1. Email --%>
                <div class="form-group">
                    <label class="form-label" for="email">
                        Email Address <span class="required">*</span>
                    </label>
                    <div class="input-wrap">
                        <input
                            type="email"
                            id="email"
                            name="email"
                            class="form-control"
                            placeholder="you@example.com"
                            autocomplete="email"
                            required
                            value="${not empty param.email ? fn:escapeXml(param.email) : ''}"
                        />
                    </div>
                    <span class="field-error" id="emailError">Please enter a valid email address.</span>
                </div>

                <%-- 2. Password --%>
                <div class="form-group">
                    <label class="form-label" for="password">
                        Password <span class="required">*</span>
                    </label>
                    <div class="pw-wrap">
                        <input
                            type="password"
                            id="password"
                            name="password"
                            class="form-control"
                            placeholder="Enter your password"
                            autocomplete="current-password"
                            required
                        />
                        <button
                            type="button"
                            class="pw-toggle"
                            id="pwToggle"
                            aria-label="Toggle password visibility"
                            title="Show / Hide password"
                        >
                            <%-- Eye open icon (default) --%>
                            <svg id="eyeOpen" xmlns="http://www.w3.org/2000/svg"
                                 viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2"
                                 stroke-linecap="round" stroke-linejoin="round">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                            <%-- Eye closed icon (hidden by default) --%>
                            <svg id="eyeClosed" xmlns="http://www.w3.org/2000/svg"
                                 viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2"
                                 stroke-linecap="round" stroke-linejoin="round"
                                 style="display:none;">
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8
                                         a18.45 18.45 0 0 1 5.06-5.94"/>
                                <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8
                                         a18.5 18.5 0 0 1-2.16 3.19"/>
                                <line x1="1" y1="1" x2="23" y2="23"/>
                            </svg>
                        </button>
                    </div>
                    <span class="field-error" id="passwordError">Password cannot be empty.</span>
                </div>

                <%-- 3. Login As --%>
                <div class="form-group">
                    <label class="form-label" for="role">
                        Login As <span class="required">*</span>
                    </label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="" disabled selected>— Select your role —</option>
                        <option value="student"
                            ${param.role == 'student' ? 'selected' : ''}>
                            Student
                        </option>
                        <option value="admin"
                            ${param.role == 'admin' ? 'selected' : ''}>
                            Admin
                        </option>
                    </select>
                    <span class="field-error" id="roleError">Please select your role.</span>
                </div>

                <%-- 4. CAPTCHA --%>
                <div class="form-group">
                    <label class="form-label">
                        Security Code <span class="required">*</span>
                    </label>
                    <div class="captcha-row">
                        <div class="captcha-box" aria-label="CAPTCHA code" title="Enter this code below">
                            ${sessionScope.captcha}
                        </div>
                        <input
                            type="text"
                            id="captchaInput"
                            name="captchaInput"
                            class="form-control captcha-input"
                            placeholder="Type the code"
                            maxlength="10"
                            autocomplete="off"
                            required
                        />
                    </div>
                    <span class="field-error" id="captchaError">Please enter the security code.</span>
                </div>

                <%-- 5. Submit --%>
                <button type="submit" class="btn-submit" id="submitBtn">
                    Sign In &rarr;
                </button>

            </form>

            <%-- 6. Register link --%>
            <p class="register-row">
                Don&rsquo;t have an account?
                <a href="${pageContext.request.contextPath}/register">Create an Account</a>
            </p>
        </main>

    </div><%-- /.card --%>
</div><%-- /.page-wrapper --%>

<!-- ══════════════════════════════════════
     JAVASCRIPT
══════════════════════════════════════ -->
<script>
(function () {
    "use strict";

    /* ── 1. Password show/hide toggle ── */
    var pwField  = document.getElementById("password");
    var pwToggle = document.getElementById("pwToggle");
    var eyeOpen  = document.getElementById("eyeOpen");
    var eyeClosed = document.getElementById("eyeClosed");

    pwToggle.addEventListener("click", function () {
        var isPassword = pwField.type === "password";
        pwField.type   = isPassword ? "text" : "password";
        eyeOpen.style.display  = isPassword ? "none"  : "block";
        eyeClosed.style.display = isPassword ? "block" : "none";
        pwField.focus();
    });

    /* ── 2. Clear success message after 3 s ── */
    var successAlert = document.getElementById("successAlert");
    if (successAlert) {
        setTimeout(function () {
            successAlert.style.transition = "opacity 0.5s ease";
            successAlert.style.opacity    = "0";
            setTimeout(function () {
                successAlert.style.display = "none";
            }, 500);
        }, 3000);
    }

    var errorAlert = document.getElementById("errorAlert");
    if (errorAlert && errorAlert.textContent) {
        window.alert(errorAlert.textContent.trim());
    }

    /* ── 3. Client-side form validation ── */
    var form         = document.getElementById("loginForm");
    var emailField   = document.getElementById("email");
    var roleField    = document.getElementById("role");
    var captchaField = document.getElementById("captchaInput");

    /* helper – show/hide field error */
    function setError(field, errorId, show) {
        var errEl = document.getElementById(errorId);
        if (show) {
            field.classList.add("is-invalid");
            errEl.style.display = "block";
        } else {
            field.classList.remove("is-invalid");
            errEl.style.display = "none";
        }
    }

    /* live validation – clear error on typing */
    [emailField, pwField, captchaField].forEach(function (f) {
        f.addEventListener("input", function () {
            f.classList.remove("is-invalid");
            var id = f.id + "Error";
            var el = document.getElementById(id);
            if (el) el.style.display = "none";
        });
    });
    roleField.addEventListener("change", function () {
        roleField.classList.remove("is-invalid");
        document.getElementById("roleError").style.display = "none";
    });

    /* email regex */
    var emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    form.addEventListener("submit", function (e) {
        var valid = true;

        /* email */
        if (!emailField.value.trim() || !emailRe.test(emailField.value.trim())) {
            setError(emailField, "emailError", true);
            valid = false;
        } else {
            setError(emailField, "emailError", false);
        }

        /* password */
        if (!pwField.value) {
            setError(pwField, "passwordError", true);
            valid = false;
        } else {
            setError(pwField, "passwordError", false);
        }

        /* role */
        if (!roleField.value) {
            setError(roleField, "roleError", true);
            valid = false;
        } else {
            setError(roleField, "roleError", false);
        }

        /* captcha */
        if (!captchaField.value.trim()) {
            setError(captchaField, "captchaError", true);
            valid = false;
        } else {
            setError(captchaField, "captchaError", false);
        }

        if (!valid) {
            e.preventDefault();
            /* scroll to first error */
            var firstInvalid = form.querySelector(".is-invalid");
            if (firstInvalid) {
                firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
                firstInvalid.focus();
            }
        }
    });

})();
</script>

</body>
</html>

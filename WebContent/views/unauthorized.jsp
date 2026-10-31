<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>401 - Unauthorized | My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #fdf4ff 0%, #f5f0ff 50%, #fafafe 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .error-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            min-height: 100vh;
            position: relative;
            overflow: hidden;
        }

        .bg-shape {
            position: fixed;
            border-radius: 50%;
            opacity: 0.07;
            pointer-events: none;
        }
        .bg-shape-1 {
            width: 380px; height: 380px;
            background: #7c3aed;
            top: -100px; right: -80px;
        }
        .bg-shape-2 {
            width: 280px; height: 280px;
            background: #6d28d9;
            bottom: -80px; left: -60px;
        }
        .bg-shape-3 {
            width: 160px; height: 160px;
            background: #8b5cf6;
            top: 30%; left: 5%;
        }

        .error-card {
            background: #ffffff;
            border-radius: 24px;
            box-shadow: 0 20px 60px rgba(124, 58, 237, 0.12), 0 4px 16px rgba(0,0,0,0.06);
            padding: 60px 50px;
            max-width: 520px;
            width: 100%;
            text-align: center;
            position: relative;
            z-index: 10;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        /* CSS Lock Illustration */
        .lock-wrapper {
            width: 90px; height: 100px;
            margin: 0 auto 28px;
            position: relative;
        }
        .lock-body {
            width: 64px; height: 52px;
            background: linear-gradient(135deg, #7c3aed, #8b5cf6);
            border-radius: 10px;
            position: absolute;
            bottom: 0; left: 50%;
            transform: translateX(-50%);
            box-shadow: 0 6px 18px rgba(124, 58, 237, 0.35);
        }
        .lock-body::before {
            content: '';
            position: absolute;
            width: 20px; height: 20px;
            background: rgba(255,255,255,0.3);
            border-radius: 50%;
            top: 50%; left: 50%;
            transform: translate(-50%, -60%);
        }
        .lock-body::after {
            content: '';
            position: absolute;
            width: 6px; height: 12px;
            background: rgba(255,255,255,0.8);
            border-radius: 3px;
            top: 50%; left: 50%;
            transform: translate(-50%, -20%);
        }
        .lock-shackle {
            width: 40px; height: 36px;
            border: 7px solid #7c3aed;
            border-bottom: none;
            border-radius: 20px 20px 0 0;
            position: absolute;
            top: 0; left: 50%;
            transform: translateX(-50%);
        }

        /* Pulse ring animation */
        .lock-wrapper::before {
            content: '';
            position: absolute;
            width: 90px; height: 90px;
            border-radius: 50%;
            border: 2px solid #c4b5fd;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            animation: pulse 2s ease-in-out infinite;
        }
        @keyframes pulse {
            0%   { transform: translate(-50%, -50%) scale(0.8); opacity: 0.8; }
            50%  { transform: translate(-50%, -50%) scale(1.2); opacity: 0.2; }
            100% { transform: translate(-50%, -50%) scale(0.8); opacity: 0.8; }
        }

        .error-badge {
            display: inline-block;
            background: #f5f3ff;
            color: #6d28d9;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 2px;
            text-transform: uppercase;
            padding: 6px 16px;
            border-radius: 20px;
            border: 1px solid #ddd6fe;
            margin-bottom: 14px;
        }

        .error-code {
            font-size: 90px;
            font-weight: 900;
            line-height: 1;
            letter-spacing: -4px;
            margin-bottom: 8px;
            background: linear-gradient(135deg, #6d28d9, #7c3aed);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .error-title {
            font-size: 22px;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 14px;
        }

        .divider {
            width: 60px; height: 4px;
            background: linear-gradient(90deg, #7c3aed, #a78bfa);
            border-radius: 2px;
            margin: 0 auto 20px;
        }

        .error-message {
            font-size: 15px;
            color: #64748b;
            line-height: 1.7;
            margin-bottom: 12px;
            max-width: 360px;
            margin-left: auto;
            margin-right: auto;
        }

        .session-info {
            background: #fdf4ff;
            border: 1px solid #e9d5ff;
            border-radius: 10px;
            padding: 14px 18px;
            margin: 0 auto 32px;
            max-width: 360px;
        }
        .session-info p {
            font-size: 13px;
            color: #6b21a8;
            line-height: 1.6;
        }
        .session-info strong { color: #5b21b6; }

        .btn-group {
            display: flex;
            gap: 14px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 32px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            border: none;
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }
        .btn:hover { transform: translateY(-2px); }

        .btn-primary {
            background: linear-gradient(135deg, #6d28d9, #7c3aed);
            color: #ffffff;
            box-shadow: 0 4px 14px rgba(109, 40, 217, 0.35);
        }
        .btn-primary:hover { box-shadow: 0 6px 20px rgba(109, 40, 217, 0.45); }

        .btn-outline {
            background: transparent;
            color: #7c3aed;
            border: 2px solid #ddd6fe;
        }
        .btn-outline:hover { background: #f5f3ff; border-color: #a78bfa; }

        .btn-icon { font-size: 16px; line-height: 1; }

        .footer-note {
            margin-top: 32px;
            font-size: 12px;
            color: #94a3b8;
        }

        @media (max-width: 480px) {
            .error-card { padding: 40px 24px; }
            .error-code  { font-size: 72px; }
            .error-title { font-size: 18px; }
            .btn-group   { flex-direction: column; align-items: center; }
            .btn         { width: 100%; justify-content: center; }
        }
    </style>
</head>
<body>
    <div class="error-wrapper">
        <div class="bg-shape bg-shape-1"></div>
        <div class="bg-shape bg-shape-2"></div>
        <div class="bg-shape bg-shape-3"></div>

        <div class="error-card">
            <!-- CSS Lock illustration with pulse animation -->
            <div class="lock-wrapper">
                <div class="lock-shackle"></div>
                <div class="lock-body"></div>
            </div>

            <div class="error-badge">401 &mdash; Unauthorized</div>

            <div class="error-code">401</div>
            <h1 class="error-title">Session Expired or Unauthorized Access</h1>
            <div class="divider"></div>

            <p class="error-message">
                Please log in to continue. Your session may have expired
                or you do not have permission to view this page.
            </p>

            <div class="session-info">
                <p>
                    <strong>&#9432; Note:</strong> Sessions expire after 30 minutes of inactivity
                    to keep your account secure. Please sign in again to continue.
                </p>
            </div>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                    <span class="btn-icon">&#128274;</span> Log In
                </a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-outline">
                    <span class="btn-icon">&#128100;</span> Register
                </a>
            </div>

            <p class="footer-note">Error 401 &bull; My Internship Technologies Portal</p>
        </div>
    </div>
</body>
</html>

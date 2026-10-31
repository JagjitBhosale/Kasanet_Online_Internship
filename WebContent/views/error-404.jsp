<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found | My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f0f4ff 0%, #e8edf8 50%, #f5f7ff 100%);
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

        /* Decorative background shapes */
        .bg-shape {
            position: fixed;
            border-radius: 50%;
            opacity: 0.06;
            pointer-events: none;
        }
        .bg-shape-1 {
            width: 400px; height: 400px;
            background: #1e3a8a;
            top: -100px; left: -100px;
        }
        .bg-shape-2 {
            width: 300px; height: 300px;
            background: #3b5bdb;
            bottom: -80px; right: -80px;
        }
        .bg-shape-3 {
            width: 200px; height: 200px;
            background: #4f46e5;
            top: 50%; left: 80%;
            transform: translate(-50%, -50%);
        }

        .error-card {
            background: #ffffff;
            border-radius: 24px;
            box-shadow: 0 20px 60px rgba(30, 58, 138, 0.12), 0 4px 16px rgba(0,0,0,0.06);
            padding: 60px 50px;
            max-width: 560px;
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

        /* CSS Illustration */
        .illustration {
            position: relative;
            width: 140px;
            height: 100px;
            margin: 0 auto 28px;
        }
        .illus-page {
            width: 80px; height: 96px;
            background: #e8edf8;
            border-radius: 8px;
            position: absolute;
            top: 0; left: 50%;
            transform: translateX(-50%);
            border: 2px solid #c7d2fe;
        }
        .illus-page::before {
            content: '';
            position: absolute;
            top: 14px; left: 12px; right: 12px; height: 6px;
            background: #c7d2fe; border-radius: 3px;
        }
        .illus-page::after {
            content: '';
            position: absolute;
            top: 28px; left: 12px; width: 60%; height: 6px;
            background: #dde4f5; border-radius: 3px;
            box-shadow: 0 10px 0 #dde4f5, 0 20px 0 #dde4f5;
        }
        .illus-x {
            position: absolute;
            width: 36px; height: 36px;
            background: #ef4444;
            border-radius: 50%;
            bottom: 0; right: 14px;
            display: flex; align-items: center; justify-content: center;
        }
        .illus-x::before, .illus-x::after {
            content: '';
            position: absolute;
            width: 16px; height: 3px;
            background: white;
            border-radius: 2px;
        }
        .illus-x::before { transform: rotate(45deg); }
        .illus-x::after  { transform: rotate(-45deg); }

        .error-code {
            font-size: 100px;
            font-weight: 900;
            color: #1e3a8a;
            line-height: 1;
            letter-spacing: -4px;
            margin-bottom: 8px;
            background: linear-gradient(135deg, #1e3a8a, #3b5bdb);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .error-title {
            font-size: 26px;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 14px;
        }

        .divider {
            width: 60px; height: 4px;
            background: linear-gradient(90deg, #3b5bdb, #6366f1);
            border-radius: 2px;
            margin: 0 auto 20px;
        }

        .error-message {
            font-size: 15px;
            color: #64748b;
            line-height: 1.7;
            margin-bottom: 36px;
            max-width: 380px;
            margin-left: auto;
            margin-right: auto;
        }

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
            padding: 12px 28px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            border: none;
            transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
        }
        .btn:hover {
            transform: translateY(-2px);
        }
        .btn-primary {
            background: linear-gradient(135deg, #1e3a8a, #3b5bdb);
            color: #ffffff;
            box-shadow: 0 4px 14px rgba(59, 91, 219, 0.35);
        }
        .btn-primary:hover {
            box-shadow: 0 6px 20px rgba(59, 91, 219, 0.45);
        }
        .btn-outline {
            background: transparent;
            color: #3b5bdb;
            border: 2px solid #c7d2fe;
        }
        .btn-outline:hover {
            background: #eef2ff;
            border-color: #818cf8;
        }

        .btn-icon {
            font-size: 16px;
            line-height: 1;
        }

        .footer-note {
            margin-top: 32px;
            font-size: 12px;
            color: #94a3b8;
        }

        @media (max-width: 480px) {
            .error-card { padding: 40px 24px; }
            .error-code  { font-size: 80px; }
            .error-title { font-size: 20px; }
            .btn-group   { flex-direction: column; align-items: center; }
            .btn         { width: 100%; justify-content: center; }
        }
    </style>
</head>
<body>
    <div class="error-wrapper">
        <!-- Background decorative shapes -->
        <div class="bg-shape bg-shape-1"></div>
        <div class="bg-shape bg-shape-2"></div>
        <div class="bg-shape bg-shape-3"></div>

        <div class="error-card">
            <!-- CSS Illustration -->
            <div class="illustration">
                <div class="illus-page"></div>
                <div class="illus-x"></div>
            </div>

            <div class="error-code">404</div>
            <h1 class="error-title">Page Not Found</h1>
            <div class="divider"></div>
            <p class="error-message">
                The page you are looking for does not exist or has been moved.
                It may have been deleted, renamed, or is temporarily unavailable.
            </p>

            <div class="btn-group">
                <c:if test="${not empty sessionScope.userId}">
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                        <span class="btn-icon">&#8962;</span> Go to Home
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/login" class="btn ${not empty sessionScope.userId ? 'btn-outline' : 'btn-primary'}">
                    <span class="btn-icon">&#128274;</span> Go to Login
                </a>
            </div>

            <p class="footer-note">Error 404 &bull; My Internship Technologies Portal</p>
        </div>
    </div>
</body>
</html>

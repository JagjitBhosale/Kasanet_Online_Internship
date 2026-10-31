<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Server Error | My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #fff7f0 0%, #fef3eb 50%, #fff8f5 100%);
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

        /* Background shapes */
        .bg-shape {
            position: fixed;
            border-radius: 50%;
            opacity: 0.07;
            pointer-events: none;
        }
        .bg-shape-1 {
            width: 420px; height: 420px;
            background: #c2410c;
            top: -120px; left: -120px;
        }
        .bg-shape-2 {
            width: 320px; height: 320px;
            background: #ea580c;
            bottom: -90px; right: -90px;
        }
        .bg-shape-3 {
            width: 180px; height: 180px;
            background: #f97316;
            top: 60%; right: 10%;
        }

        .error-card {
            background: #ffffff;
            border-radius: 24px;
            box-shadow: 0 20px 60px rgba(194, 65, 12, 0.12), 0 4px 16px rgba(0,0,0,0.06);
            padding: 60px 50px;
            max-width: 580px;
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

        /* CSS Gear Illustration */
        .illustration {
            width: 100px; height: 100px;
            margin: 0 auto 28px;
            position: relative;
        }
        .gear {
            width: 70px; height: 70px;
            background: #fed7aa;
            border-radius: 50%;
            border: 5px solid #fdba74;
            position: absolute;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            animation: spin 6s linear infinite;
        }
        .gear::before {
            content: '';
            position: absolute;
            inset: -14px;
            border-radius: 50%;
            border: 8px dashed #fb923c;
            animation: spin 6s linear infinite reverse;
        }
        .gear::after {
            content: '!';
            position: absolute;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            font-size: 24px;
            font-weight: 900;
            color: #c2410c;
            -webkit-text-fill-color: #c2410c;
            background: none;
        }
        @keyframes spin {
            from { transform: translate(-50%, -50%) rotate(0deg); }
            to   { transform: translate(-50%, -50%) rotate(360deg); }
        }

        .error-code {
            font-size: 100px;
            font-weight: 900;
            line-height: 1;
            letter-spacing: -4px;
            margin-bottom: 8px;
            background: linear-gradient(135deg, #c2410c, #ea580c);
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
            background: linear-gradient(90deg, #ea580c, #f97316);
            border-radius: 2px;
            margin: 0 auto 20px;
        }

        .error-message {
            font-size: 15px;
            color: #64748b;
            line-height: 1.7;
            margin-bottom: 16px;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }

        .support-box {
            background: #fff7ed;
            border: 1px solid #fed7aa;
            border-radius: 12px;
            padding: 16px 20px;
            margin: 0 auto 32px;
            max-width: 380px;
        }
        .support-box p {
            font-size: 13px;
            color: #92400e;
            line-height: 1.6;
        }
        .support-box a {
            color: #c2410c;
            font-weight: 600;
            text-decoration: none;
        }
        .support-box a:hover { text-decoration: underline; }

        .support-icon {
            font-size: 20px;
            margin-bottom: 6px;
            display: block;
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
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }
        .btn:hover { transform: translateY(-2px); }

        .btn-primary {
            background: linear-gradient(135deg, #c2410c, #ea580c);
            color: #ffffff;
            box-shadow: 0 4px 14px rgba(194, 65, 12, 0.35);
        }
        .btn-primary:hover { box-shadow: 0 6px 20px rgba(194, 65, 12, 0.45); }

        .btn-outline {
            background: transparent;
            color: #c2410c;
            border: 2px solid #fed7aa;
        }
        .btn-outline:hover { background: #fff7ed; border-color: #fb923c; }

        .btn-icon { font-size: 16px; line-height: 1; }

        .retry-btn {
            background: none;
            border: none;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 28px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 600;
            color: #c2410c;
            border: 2px solid #fed7aa;
            transition: transform 0.15s ease, background 0.15s ease;
            text-decoration: none;
        }
        .retry-btn:hover { transform: translateY(-2px); background: #fff7ed; }

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
            .btn, .retry-btn { width: 100%; justify-content: center; }
        }
    </style>
</head>
<body>
    <div class="error-wrapper">
        <div class="bg-shape bg-shape-1"></div>
        <div class="bg-shape bg-shape-2"></div>
        <div class="bg-shape bg-shape-3"></div>

        <div class="error-card">
            <!-- Animated gear illustration -->
            <div class="illustration">
                <div class="gear"></div>
            </div>

            <div class="error-code">500</div>
            <h1 class="error-title">Something Went Wrong</h1>
            <div class="divider"></div>

            <p class="error-message">
                An internal server error occurred. Our team has been notified
                and is working to resolve the issue. Please try again later.
            </p>

            <!-- Support contact box -->
            <div class="support-box">
                <span class="support-icon">&#128222;</span>
                <p>
                    If this problem persists, please contact our support team at
                    <a href="mailto:support@myinternshiptech.com">support@myinternshiptech.com</a>
                    or call <strong>+91 98765 43210</strong>.
                </p>
            </div>

            <div class="btn-group">
                <!-- Retry button -->
                <a href="javascript:history.back()" class="retry-btn">
                    <span class="btn-icon">&#8635;</span> Try Again
                </a>

                <c:if test="${not empty sessionScope.userId}">
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                        <span class="btn-icon">&#8962;</span> Go to Home
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/login" class="btn ${not empty sessionScope.userId ? 'btn-outline' : 'btn-primary'}">
                    <span class="btn-icon">&#128274;</span> Go to Login
                </a>
            </div>

            <p class="footer-note">Error 500 &bull; My Internship Technologies Portal &bull; Please note your time of visit for support</p>
        </div>
    </div>
</body>
</html>

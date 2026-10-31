<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${activeLecture.title} — ${course.title} | Kasnet Portal</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --brand-primary: #4f46e5;
            --brand-hover: #4338ca;
            --brand-light: #e0e7ff;
            --brand-glow: rgba(79, 70, 229, 0.35);
            --bg-page: #0b0f19;
            --bg-surface: #111827;
            --bg-surface-elevated: #1f2937;
            --border-color: rgba(255, 255, 255, 0.08);
            --border-hover: rgba(255, 255, 255, 0.16);
            --text-primary: #f9fafb;
            --text-secondary: #9ca3af;
            --text-muted: #6b7280;
            --success: #10b981;
            --success-glow: rgba(16, 185, 129, 0.25);
            --warning: #f59e0b;
            --danger: #ef4444;
            --radius-lg: 18px;
            --radius-md: 12px;
            --radius-sm: 8px;
            --shadow-player: 0 25px 60px -15px rgba(0, 0, 0, 0.8), 0 0 40px -10px var(--brand-glow);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
            background-color: var(--bg-page);
            color: var(--text-primary);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            overflow-x: hidden;
        }

        /* ─── TOP NAVBAR ─── */
        .player-navbar {
            height: 68px;
            background: rgba(17, 24, 39, 0.85);
            backdrop-filter: blur(16px);
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 28px;
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .nav-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .back-btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 8px 14px;
            border-radius: var(--radius-md);
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid var(--border-color);
            color: var(--text-primary);
            text-decoration: none;
            font-size: 13.5px;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .back-btn:hover {
            background: rgba(255, 255, 255, 0.1);
            border-color: var(--border-hover);
            transform: translateX(-2px);
        }

        .nav-divider {
            width: 1px;
            height: 24px;
            background: var(--border-color);
        }

        .nav-course-title {
            font-size: 14.5px;
            font-weight: 700;
            color: var(--text-primary);
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .nav-course-badge {
            background: rgba(79, 70, 229, 0.2);
            color: #a5b4fc;
            padding: 2px 8px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .nav-right {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .protection-shield-badge {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            background: rgba(16, 185, 129, 0.12);
            border: 1px solid rgba(16, 185, 129, 0.3);
            color: #34d399;
            padding: 6px 12px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;
        }

        .protection-shield-badge i {
            font-size: 13px;
        }

        /* ─── MAIN DUAL-PANE LAYOUT ─── */
        .player-viewport {
            flex: 1;
            display: grid;
            grid-template-columns: 1fr 380px;
            gap: 0;
            min-height: calc(100vh - 68px);
        }

        @media (max-width: 1150px) {
            .player-viewport {
                grid-template-columns: 1fr;
            }
        }

        /* ─── LEFT: VIDEO STAGE ─── */
        .video-stage {
            padding: 28px 36px 48px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 24px;
        }

        .player-wrapper {
            position: relative;
            width: 100%;
            aspect-ratio: 16 / 9;
            background: #000;
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-player);
            border: 1px solid var(--border-color);
            user-select: none;
        }

        video#secureVideo {
            width: 100%;
            height: 100%;
            object-fit: contain;
            display: block;
            background: #000;
        }

        /* ─── FLOATING ANTI-PIRACY WATERMARK ─── */
        .dynamic-watermark {
            position: absolute;
            pointer-events: none;
            z-index: 25;
            background: rgba(0, 0, 0, 0.38);
            backdrop-filter: blur(4px);
            padding: 6px 12px;
            border-radius: 6px;
            border: 1px solid rgba(255, 255, 255, 0.12);
            color: rgba(255, 255, 255, 0.55);
            font-family: 'JetBrains Mono', monospace;
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 0.3px;
            transition: all 1.2s cubic-bezier(0.25, 1, 0.5, 1);
        }

        /* Predefined positions for 22s rotation */
        .dynamic-watermark.pos-0 { top: 20px; left: 24px; }
        .dynamic-watermark.pos-1 { top: 20px; right: 24px; }
        .dynamic-watermark.pos-2 { bottom: 84px; right: 24px; }
        .dynamic-watermark.pos-3 { bottom: 84px; left: 24px; }
        .dynamic-watermark.pos-4 { top: 45%; left: 35%; }

        /* ─── OVERLAYS ─── */
        .player-overlay {
            position: absolute;
            inset: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            background: rgba(11, 15, 25, 0.88);
            backdrop-filter: blur(12px);
            z-index: 30;
            transition: opacity 0.3s ease;
        }

        .player-overlay.hidden {
            display: none !important;
        }

        .spinner {
            width: 52px;
            height: 52px;
            border: 4px solid rgba(255, 255, 255, 0.1);
            border-top-color: var(--brand-primary);
            border-radius: 50%;
            animation: spin 0.85s linear infinite;
            margin-bottom: 18px;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .overlay-title {
            font-size: 16px;
            font-weight: 700;
            color: #fff;
            margin-bottom: 6px;
        }

        .overlay-subtitle {
            font-size: 13px;
            color: var(--text-secondary);
            text-align: center;
            max-width: 360px;
        }

        .retry-btn {
            margin-top: 18px;
            padding: 9px 22px;
            background: var(--brand-primary);
            color: #fff;
            border: none;
            border-radius: var(--radius-md);
            font-weight: 700;
            font-size: 13px;
            cursor: pointer;
            transition: background 0.2s;
        }

        .retry-btn:hover {
            background: var(--brand-hover);
        }

        /* Big Play Button Overlay */
        .big-play-btn {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 76px;
            height: 76px;
            background: rgba(79, 70, 229, 0.9);
            color: #fff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            padding-left: 5px;
            cursor: pointer;
            z-index: 20;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5), 0 0 25px var(--brand-glow);
            transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
        }

        .big-play-btn:hover {
            transform: translate(-50%, -50%) scale(1.12);
            background: #6366f1;
        }

        .big-play-btn.hidden {
            display: none;
        }

        /* Token renew badge */
        .token-renew-badge {
            position: absolute;
            top: 18px;
            right: 18px;
            background: rgba(0, 0, 0, 0.7);
            border: 1px solid rgba(255, 255, 255, 0.1);
            color: #a5b4fc;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 6px;
            z-index: 22;
            opacity: 0;
            transition: opacity 0.3s ease;
        }

        .token-renew-badge.active {
            opacity: 1;
        }

        /* ─── CUSTOM CONTROLS OVERLAY ─── */
        .player-controls {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 20px 22px 14px;
            background: linear-gradient(to top, rgba(0, 0, 0, 0.92) 0%, rgba(0, 0, 0, 0.65) 60%, transparent 100%);
            z-index: 20;
            opacity: 1;
            transition: opacity 0.35s ease, transform 0.35s ease;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .player-wrapper.playing.controls-hidden .player-controls {
            opacity: 0;
            pointer-events: none;
            transform: translateY(8px);
        }

        /* Seeker Progress Bar */
        .seek-container {
            position: relative;
            width: 100%;
            height: 6px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 999px;
            cursor: pointer;
            transition: height 0.15s ease;
        }

        .seek-container:hover {
            height: 9px;
        }

        .seek-buffer {
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 0%;
            background: rgba(255, 255, 255, 0.35);
            border-radius: 999px;
            pointer-events: none;
        }

        .seek-played {
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 0%;
            background: linear-gradient(90deg, #4f46e5, #818cf8);
            border-radius: 999px;
            pointer-events: none;
        }

        .seek-handle {
            position: absolute;
            top: 50%;
            transform: translate(-50%, -50%);
            width: 14px;
            height: 14px;
            background: #fff;
            border-radius: 50%;
            box-shadow: 0 0 8px rgba(0, 0, 0, 0.6);
            opacity: 0;
            transition: opacity 0.15s ease;
            pointer-events: none;
        }

        .seek-container:hover .seek-handle {
            opacity: 1;
        }

        .seek-tooltip {
            position: absolute;
            bottom: 16px;
            transform: translateX(-50%);
            background: rgba(17, 24, 39, 0.95);
            color: #fff;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-family: 'JetBrains Mono', monospace;
            font-weight: 600;
            pointer-events: none;
            display: none;
            white-space: nowrap;
        }

        /* Controls Bottom Bar */
        .controls-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .controls-left, .controls-right {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .ctrl-btn {
            background: transparent;
            border: none;
            color: #f3f4f6;
            font-size: 16px;
            width: 34px;
            height: 34px;
            border-radius: var(--radius-sm);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .ctrl-btn:hover {
            color: #fff;
            background: rgba(255, 255, 255, 0.12);
        }

        .volume-group {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .volume-slider {
            width: 72px;
            height: 4px;
            appearance: none;
            background: rgba(255, 255, 255, 0.25);
            border-radius: 999px;
            cursor: pointer;
            outline: none;
        }

        .volume-slider::-webkit-slider-thumb {
            appearance: none;
            width: 11px;
            height: 11px;
            border-radius: 50%;
            background: #fff;
            cursor: pointer;
        }

        .time-display {
            font-family: 'JetBrains Mono', monospace;
            font-size: 12.5px;
            color: #e5e7eb;
            font-weight: 500;
        }

        .speed-btn {
            font-family: 'JetBrains Mono', monospace;
            font-size: 12px;
            font-weight: 700;
            padding: 4px 8px;
            border-radius: var(--radius-sm);
            background: rgba(255, 255, 255, 0.1);
            color: #e5e7eb;
            border: none;
            cursor: pointer;
            transition: all 0.2s;
        }

        .speed-btn:hover {
            background: rgba(255, 255, 255, 0.2);
            color: #fff;
        }

        /* ─── RESUME BANNER ─── */
        .resume-notice {
            background: rgba(79, 70, 229, 0.15);
            border: 1px solid rgba(79, 70, 229, 0.35);
            color: #c7d2fe;
            padding: 10px 18px;
            border-radius: var(--radius-md);
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 13.5px;
            animation: fadeIn 0.4s ease;
        }

        .resume-notice-left {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* ─── LECTURE META & CONTROLS ─── */
        .lecture-header-card {
            background: var(--bg-surface);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 24px 28px;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .lecture-badges-row {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .badge-pill {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11.5px;
            font-weight: 700;
            letter-spacing: 0.3px;
        }

        .badge-lecture-num {
            background: rgba(255, 255, 255, 0.08);
            color: #e5e7eb;
        }

        .badge-preview {
            background: rgba(245, 158, 11, 0.15);
            color: #fcd34d;
            border: 1px solid rgba(245, 158, 11, 0.3);
        }

        .badge-completed {
            background: rgba(16, 185, 129, 0.15);
            color: #6ee7b7;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }

        .lecture-title {
            font-size: 22px;
            font-weight: 800;
            line-height: 1.3;
            color: #fff;
        }

        .lecture-desc {
            font-size: 14.5px;
            color: var(--text-secondary);
            line-height: 1.6;
        }

        .lecture-nav-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-top: 14px;
            border-top: 1px solid var(--border-color);
            flex-wrap: wrap;
            gap: 12px;
        }

        .btn-nav-lecture {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 9px 18px;
            border-radius: var(--radius-md);
            font-size: 13.5px;
            font-weight: 700;
            text-decoration: none;
            transition: all 0.2s ease;
        }

        .btn-prev {
            background: rgba(255, 255, 255, 0.06);
            color: #e5e7eb;
            border: 1px solid var(--border-color);
        }

        .btn-prev:hover {
            background: rgba(255, 255, 255, 0.12);
        }

        .btn-next {
            background: var(--brand-primary);
            color: #fff;
            border: 1px solid var(--brand-hover);
        }

        .btn-next:hover {
            background: var(--brand-hover);
            box-shadow: 0 4px 15px var(--brand-glow);
        }

        .btn-nav-disabled {
            opacity: 0.4;
            pointer-events: none;
        }

        /* ─── COMPLETION CARD ─── */
        .celebration-card {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.12), rgba(79, 70, 229, 0.12));
            border: 1px solid rgba(16, 185, 129, 0.35);
            border-radius: var(--radius-lg);
            padding: 20px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            display: none;
            animation: slideUp 0.4s ease;
        }

        .celebration-card.visible {
            display: flex;
        }

        .celebration-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .celebration-icon {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: var(--success);
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        /* ─── RIGHT: CURRICULUM SIDEBAR ─── */
        .curriculum-sidebar {
            background: var(--bg-surface);
            border-left: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            height: calc(100vh - 68px);
            position: sticky;
            top: 68px;
        }

        .sidebar-header {
            padding: 24px 22px 18px;
            border-bottom: 1px solid var(--border-color);
        }

        .sidebar-header h3 {
            font-size: 15.5px;
            font-weight: 800;
            color: #fff;
            margin-bottom: 12px;
        }

        .overall-progress-bar {
            width: 100%;
            height: 7px;
            background: rgba(255, 255, 255, 0.08);
            border-radius: 999px;
            overflow: hidden;
            margin-bottom: 8px;
        }

        .overall-progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #10b981, #059669);
            border-radius: 999px;
            transition: width 0.4s ease;
        }

        .sidebar-progress-label {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            font-weight: 600;
            color: var(--text-secondary);
        }

        /* Lecture List */
        .lecture-list-scroll {
            flex: 1;
            overflow-y: auto;
            padding: 12px;
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .lecture-item-link {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 14px 14px;
            border-radius: var(--radius-md);
            text-decoration: none;
            color: var(--text-primary);
            transition: all 0.2s ease;
            border-left: 3px solid transparent;
            background: transparent;
        }

        .lecture-item-link:hover {
            background: rgba(255, 255, 255, 0.04);
        }

        .lecture-item-link.active {
            background: rgba(79, 70, 229, 0.12);
            border-left-color: var(--brand-primary);
        }

        .lecture-item-link.locked {
            opacity: 0.55;
            cursor: not-allowed;
        }

        .status-dot {
            width: 26px;
            height: 26px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 11px;
            flex-shrink: 0;
            margin-top: 2px;
        }

        .status-dot.completed {
            background: rgba(16, 185, 129, 0.2);
            color: #34d399;
        }

        .status-dot.active {
            background: var(--brand-primary);
            color: #fff;
            animation: pulse-ring 2s infinite;
        }

        @keyframes pulse-ring {
            0% { box-shadow: 0 0 0 0 rgba(79, 70, 229, 0.6); }
            70% { box-shadow: 0 0 0 8px rgba(79, 70, 229, 0); }
            100% { box-shadow: 0 0 0 0 rgba(79, 70, 229, 0); }
        }

        .status-dot.upcoming {
            background: rgba(255, 255, 255, 0.08);
            color: var(--text-muted);
        }

        .status-dot.locked {
            background: rgba(255, 255, 255, 0.05);
            color: var(--text-muted);
        }

        .item-info {
            flex: 1;
            min-width: 0;
        }

        .item-title {
            font-size: 13.5px;
            font-weight: 600;
            line-height: 1.4;
            color: #f3f4f6;
            margin-bottom: 4px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .lecture-item-link.active .item-title {
            color: #a5b4fc;
            font-weight: 700;
        }

        .item-meta {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 11.5px;
            color: var(--text-muted);
        }

        .item-meta .preview-tag {
            color: #fbbf24;
            font-weight: 700;
        }

        /* ─── TOAST NOTIFICATIONS ─── */
        .toast-container {
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 999;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .toast {
            background: rgba(17, 24, 39, 0.95);
            border: 1px solid var(--border-color);
            backdrop-filter: blur(16px);
            color: #fff;
            padding: 12px 18px;
            border-radius: var(--radius-md);
            font-size: 13.5px;
            display: flex;
            align-items: center;
            gap: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
            animation: slideIn 0.3s ease;
        }

        .toast.success {
            border-color: rgba(16, 185, 129, 0.4);
        }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-6px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(12px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body oncontextmenu="return false;">

    <!-- ─── TOP NAVBAR ─── -->
    <header class="player-navbar">
        <div class="nav-left">
            <a href="${pageContext.request.contextPath}/courses?id=${course.courseId}" class="back-btn" id="btnBackToCourse">
                <i class="fa-solid fa-arrow-left"></i>
                <span>Course Details</span>
            </a>
            <div class="nav-divider"></div>
            <div class="nav-course-title">
                <span>${course.title}</span>
                <span class="nav-course-badge">${course.difficulty}</span>
            </div>
        </div>

        <div class="nav-right">
            <div class="protection-shield-badge">
                <i class="fa-solid fa-shield-halved"></i>
                <span>Protected Video Stream</span>
            </div>
        </div>
    </header>

    <!-- ─── MAIN DUAL-PANE VIEWPORT ─── -->
    <main class="player-viewport">

        <!-- ─── LEFT: VIDEO STAGE ─── -->
        <section class="video-stage">

            <!-- Resume notice (if previous progress > 5 seconds) -->
            <c:if test="${activeLecture.progressSeconds > 5 && !activeLecture.completed}">
                <div class="resume-notice" id="resumeNotice">
                    <div class="resume-notice-left">
                        <i class="fa-solid fa-clock-rotate-left"></i>
                        <span>Resuming from <strong>${activeLecture.progressSeconds / 60}:<fmt:formatNumber value="${activeLecture.progressSeconds % 60}" minIntegerDigits="2"/></strong> based on your last watch activity.</span>
                    </div>
                    <button class="ctrl-btn" onclick="document.getElementById('resumeNotice').style.display='none'" style="font-size: 13px;" title="Dismiss">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
            </c:if>

            <!-- ─── SECURE VIDEO PLAYER ─── -->
            <div class="player-wrapper" id="playerWrapper">

                <!-- Dynamic Floating Anti-Piracy Watermark -->
                <div class="dynamic-watermark pos-0" id="antiPiracyWatermark">
                    <i class="fa-solid fa-user-shield" style="margin-right: 4px;"></i>
                    <span>${sessionScope.userName != null ? sessionScope.userName : 'Student'} #${sessionScope.userId != null ? sessionScope.userId : '0'}</span>
                    <span style="opacity: 0.5;"> • KASNET DRM</span>
                </div>

                <!-- Token Refresh Indicator Badge -->
                <div class="token-renew-badge" id="tokenRenewBadge">
                    <i class="fa-solid fa-arrows-rotate fa-spin"></i>
                    <span>Renewing Token...</span>
                </div>

                <!-- HTML5 Video Element (Native Controls Disabled) -->
                <video id="secureVideo"
                       controlsList="nodownload nofullscreen noremoteplayback"
                       disablePictureInPicture="false"
                       playsinline
                       preload="auto">
                    <p>Your browser does not support HTML5 video.</p>
                </video>

                <!-- Center Big Play Button (Visible on Pause) -->
                <div class="big-play-btn" id="bigPlayBtn" title="Play">
                    <i class="fa-solid fa-play"></i>
                </div>

                <!-- Loading Overlay -->
                <div class="player-overlay" id="loadingOverlay">
                    <div class="spinner"></div>
                    <div class="overlay-title">Authenticating Secure Video Stream</div>
                    <div class="overlay-subtitle">Verifying cryptographic session token and student enrollment...</div>
                </div>

                <!-- Error Overlay -->
                <div class="player-overlay hidden" id="errorOverlay">
                    <i class="fa-solid fa-lock" id="errorIcon" style="font-size: 42px; color: var(--danger); margin-bottom: 16px;"></i>
                    <div class="overlay-title" id="errorTitle">Access Denied</div>
                    <div class="overlay-subtitle" id="errorMessage">You must be enrolled in this course to watch this lecture.</div>
                    <button class="retry-btn" id="btnRetryAccess" onclick="initSecureStream()">
                        <i class="fa-solid fa-rotate-right" style="margin-right: 6px;"></i> Retry Stream Access
                    </button>
                </div>

                <!-- Custom Controls Bar -->
                <div class="player-controls" id="playerControls">
                    <!-- Progress Bar (Seeker) -->
                    <div class="seek-container" id="seekContainer">
                        <div class="seek-buffer" id="seekBuffer"></div>
                        <div class="seek-played" id="seekPlayed"></div>
                        <div class="seek-handle" id="seekHandle"></div>
                        <div class="seek-tooltip" id="seekTooltip">00:00</div>
                    </div>

                    <!-- Buttons Row -->
                    <div class="controls-row">
                        <div class="controls-left">
                            <button class="ctrl-btn" id="btnPlayPause" title="Play/Pause (Space)">
                                <i class="fa-solid fa-play" id="playIcon"></i>
                            </button>

                            <div class="volume-group">
                                <button class="ctrl-btn" id="btnMute" title="Mute/Unmute">
                                    <i class="fa-solid fa-volume-high" id="volumeIcon"></i>
                                </button>
                                <input type="range" class="volume-slider" id="volumeSlider" min="0" max="1" step="0.05" value="1">
                            </div>

                            <span class="time-display" id="timeDisplay">0:00 / 0:00</span>
                        </div>

                        <div class="controls-right">
                            <button class="speed-btn" id="btnSpeed" title="Playback Speed">1.0x</button>
                            <button class="ctrl-btn" id="btnPip" title="Picture-in-Picture">
                                <i class="fa-regular fa-clone"></i>
                            </button>
                            <button class="ctrl-btn" id="btnFullscreen" title="Fullscreen (F)">
                                <i class="fa-solid fa-expand" id="fsIcon"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ─── COMPLETION CARD (TRIGGERS AT 90% OR ON VIDEO END) ─── -->
            <div class="celebration-card" id="lectureCelebrationCard">
                <div class="celebration-left">
                    <div class="celebration-icon">
                        <i class="fa-solid fa-check"></i>
                    </div>
                    <div>
                        <h4 style="font-size: 15.5px; font-weight: 800; color: #fff;">Lesson Completed!</h4>
                        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">Your progress has been recorded automatically.</p>
                    </div>
                </div>
                <c:if test="${nextLecture != null}">
                    <a href="${pageContext.request.contextPath}/courses?view=player&courseId=${course.courseId}&lectureId=${nextLecture.lectureId}"
                       class="btn-nav-lecture btn-next" id="btnContinueNext">
                        <span>Next Lesson</span>
                        <i class="fa-solid fa-arrow-right"></i>
                    </a>
                </c:if>
            </div>

            <!-- ─── LECTURE META CARD ─── -->
            <div class="lecture-header-card">
                <div class="lecture-badges-row">
                    <span class="badge-pill badge-lecture-num">Lesson ${activeLecture.lectureNumber}</span>
                    <c:if test="${activeLecture.preview}">
                        <span class="badge-pill badge-preview">
                            <i class="fa-solid fa-unlock"></i> Free Preview
                        </span>
                    </c:if>
                    <c:if test="${activeLecture.completed}">
                        <span class="badge-pill badge-completed" id="badgeCompletedStatus">
                            <i class="fa-solid fa-circle-check"></i> Completed
                        </span>
                    </c:if>
                </div>

                <h1 class="lecture-title">${activeLecture.title}</h1>
                <p class="lecture-desc">
                    ${activeLecture.description != null && !activeLecture.description.isEmpty() ? activeLecture.description : 'In this video lesson, you will master practical concepts and hands-on implementation.'}
                </p>

                <!-- Previous & Next Navigation -->
                <div class="lecture-nav-bar">
                    <c:choose>
                        <c:when test="${prevLecture != null}">
                            <a href="${pageContext.request.contextPath}/courses?view=player&courseId=${course.courseId}&lectureId=${prevLecture.lectureId}"
                               class="btn-nav-lecture btn-prev" id="btnPrevLecture">
                                <i class="fa-solid fa-arrow-left"></i>
                                <span>Previous: Lesson ${prevLecture.lectureNumber}</span>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="btn-nav-lecture btn-prev btn-nav-disabled">
                                <i class="fa-solid fa-arrow-left"></i>
                                <span>First Lesson</span>
                            </span>
                        </c:otherwise>
                    </c:choose>

                    <c:choose>
                        <c:when test="${nextLecture != null}">
                            <a href="${pageContext.request.contextPath}/courses?view=player&courseId=${course.courseId}&lectureId=${nextLecture.lectureId}"
                               class="btn-nav-lecture btn-next" id="btnNextLecture">
                                <span>Next: Lesson ${nextLecture.lectureNumber}</span>
                                <i class="fa-solid fa-arrow-right"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="btn-nav-lecture btn-next btn-nav-disabled">
                                <span>Course Finished</span>
                                <i class="fa-solid fa-flag-checkered"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </section>

        <!-- ─── RIGHT: CURRICULUM SIDEBAR ─── -->
        <aside class="curriculum-sidebar">
            <div class="sidebar-header">
                <h3>Course Curriculum</h3>
                <div class="overall-progress-bar">
                    <div class="overall-progress-fill" id="sidebarProgressFill" style="width: ${progressPercent}%;"></div>
                </div>
                <div class="sidebar-progress-label">
                    <span id="sidebarCompletedCount">${completedCount} of ${totalLectures} completed</span>
                    <span id="sidebarProgressText">${progressPercent}%</span>
                </div>
            </div>

            <div class="lecture-list-scroll">
                <c:forEach var="lec" items="${lectures}">
                    <c:set var="isCurrent" value="${lec.lectureId == activeLecture.lectureId}" />
                    <c:set var="canWatch" value="${enrolled || lec.preview || sessionScope.userRole == 'ADMIN'}" />

                    <a href="${canWatch ? pageContext.request.contextPath.concat('/courses?view=player&courseId=').concat(course.courseId).concat('&lectureId=').concat(lec.lectureId) : 'javascript:void(0)'}"
                       class="lecture-item-link ${isCurrent ? 'active' : ''} ${!canWatch ? 'locked' : ''}"
                       id="lec-item-${lec.lectureId}">

                        <!-- Status Icon -->
                        <div class="status-dot ${lec.completed ? 'completed' : (isCurrent ? 'active' : (canWatch ? 'upcoming' : 'locked'))}">
                            <c:choose>
                                <c:when test="${lec.completed}">
                                    <i class="fa-solid fa-check"></i>
                                </c:when>
                                <c:when test="${isCurrent}">
                                    <i class="fa-solid fa-play"></i>
                                </c:when>
                                <c:when test="${!canWatch}">
                                    <i class="fa-solid fa-lock"></i>
                                </c:when>
                                <c:otherwise>
                                    <i class="fa-regular fa-circle"></i>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="item-info">
                            <div class="item-title">${lec.lectureNumber}. ${lec.title}</div>
                            <div class="item-meta">
                                <span><i class="fa-regular fa-clock" style="margin-right: 4px;"></i>${lec.durationFormatted}</span>
                                <c:if test="${lec.preview}">
                                    <span class="preview-tag">• Free Preview</span>
                                </c:if>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </aside>

    </main>

    <!-- Toast Notifications Container -->
    <div class="toast-container" id="toastContainer"></div>

    <!-- ─── SECURE VIDEO PLAYER ENGINE JAVASCRIPT ─── -->
    <script>
        (function() {
            'use strict';

            // Constants & Configuration
            const LECTURE_ID = ${activeLecture.lectureId};
            const COURSE_ID = ${course.courseId};
            const INITIAL_RESUME_SECONDS = ${activeLecture.progressSeconds > 5 && !activeLecture.completed ? activeLecture.progressSeconds : 0};
            const API_BASE = '${pageContext.request.contextPath}/api/lectures';

            // DOM Elements
            const video = document.getElementById('secureVideo');
            const playerWrapper = document.getElementById('playerWrapper');
            const bigPlayBtn = document.getElementById('bigPlayBtn');
            const btnPlayPause = document.getElementById('btnPlayPause');
            const playIcon = document.getElementById('playIcon');
            const seekContainer = document.getElementById('seekContainer');
            const seekBuffer = document.getElementById('seekBuffer');
            const seekPlayed = document.getElementById('seekPlayed');
            const seekHandle = document.getElementById('seekHandle');
            const seekTooltip = document.getElementById('seekTooltip');
            const timeDisplay = document.getElementById('timeDisplay');
            const btnMute = document.getElementById('btnMute');
            const volumeIcon = document.getElementById('volumeIcon');
            const volumeSlider = document.getElementById('volumeSlider');
            const btnSpeed = document.getElementById('btnSpeed');
            const btnPip = document.getElementById('btnPip');
            const btnFullscreen = document.getElementById('btnFullscreen');
            const fsIcon = document.getElementById('fsIcon');
            const loadingOverlay = document.getElementById('loadingOverlay');
            const errorOverlay = document.getElementById('errorOverlay');
            const tokenRenewBadge = document.getElementById('tokenRenewBadge');
            const celebrationCard = document.getElementById('lectureCelebrationCard');
            const watermark = document.getElementById('antiPiracyWatermark');

            // State variables
            let tokenRefreshTimer = null;
            let autoSaveInterval = null;
            let controlsHideTimeout = null;
            let isSeeking = false;
            let isCompletedTriggered = ${activeLecture.completed};
            let watermarkPosIndex = 0;
            const speeds = [0.5, 0.75, 1.0, 1.25, 1.5, 2.0];
            let currentSpeedIdx = 2; // default 1.0x

            // ─────────────────────────────────────────────────────────────
            // 1. Anti-Piracy Watermark Positioning Rotation (Every 22s)
            // ─────────────────────────────────────────────────────────────
            setInterval(function() {
                watermarkPosIndex = (watermarkPosIndex + 1) % 5;
                watermark.className = 'dynamic-watermark pos-' + watermarkPosIndex;
            }, 22000);

            // UX Keyboard Deterrent
            document.addEventListener('keydown', function(e) {
                if (e.key === 'F12' ||
                   (e.ctrlKey && e.shiftKey && (e.key === 'I' || e.key === 'i' || e.key === 'J' || e.key === 'j' || e.key === 'C' || e.key === 'c')) ||
                   (e.ctrlKey && (e.key === 'U' || e.key === 'u'))) {
                    e.preventDefault();
                    showToast('Protected Content — Developer inspection is disabled.');
                }
                // Spacebar toggles play/pause when focused on viewport
                if (e.code === 'Space' && e.target.tagName !== 'INPUT' && e.target.tagName !== 'TEXTAREA') {
                    e.preventDefault();
                    togglePlay();
                }
            });

            // ─────────────────────────────────────────────────────────────
            // 2. Stream Fetching & Seamless URL Refresh Logic
            // ─────────────────────────────────────────────────────────────
            window.initSecureStream = function() {
                loadingOverlay.classList.remove('hidden');
                errorOverlay.classList.add('hidden');

                fetch(API_BASE + '/' + LECTURE_ID + '/video')
                    .then(res => {
                        if (!res.ok) {
                            return res.json().then(data => { throw new Error(data.message || 'Stream access denied.'); });
                        }
                        return res.json();
                    })
                    .then(data => {
                        if (data.status === 'success' && data.videoUrl) {
                            applyVideoStream(data.videoUrl, data.expiresIn, INITIAL_RESUME_SECONDS);
                        } else {
                            throw new Error('Video stream unavailable.');
                        }
                    })
                    .catch(err => {
                        loadingOverlay.classList.add('hidden');
                        errorOverlay.classList.remove('hidden');
                        document.getElementById('errorMessage').textContent = err.message;
                    });
            };

            function applyVideoStream(url, expiresIn, resumeSeconds) {
                video.src = url;
                loadingOverlay.classList.add('hidden');

                if (resumeSeconds > 0) {
                    video.addEventListener('loadedmetadata', function onLoaded() {
                        video.currentTime = resumeSeconds;
                        video.removeEventListener('loadedmetadata', onLoaded);
                    });
                }

                // Schedule auto-refresh 30s before expiry
                scheduleTokenRefresh(expiresIn);
            }

            function scheduleTokenRefresh(expiresIn) {
                if (tokenRefreshTimer) clearTimeout(tokenRefreshTimer);
                const refreshDelaySeconds = Math.max(30, expiresIn - 30);

                tokenRefreshTimer = setTimeout(function() {
                    renewStreamToken();
                }, refreshDelaySeconds * 1000);
            }

            function renewStreamToken() {
                tokenRenewBadge.classList.add('active');
                const wasPlaying = !video.paused;
                const currentTime = video.currentTime;

                fetch(API_BASE + '/' + LECTURE_ID + '/video')
                    .then(res => res.json())
                    .then(data => {
                        if (data.status === 'success' && data.videoUrl) {
                            // Seamless URL switch without interruption
                            video.src = data.videoUrl;
                            video.currentTime = currentTime;
                            if (wasPlaying) video.play();
                            scheduleTokenRefresh(data.expiresIn);
                        }
                    })
                    .catch(e => console.warn('[DRM] Stream renewal warning:', e))
                    .finally(() => {
                        tokenRenewBadge.classList.remove('active');
                    });
            }

            // ─────────────────────────────────────────────────────────────
            // 3. Playback Controls & User Interaction
            // ─────────────────────────────────────────────────────────────
            function togglePlay() {
                if (video.paused || video.ended) {
                    video.play().catch(e => console.log('Autoplay prevented:', e));
                } else {
                    video.pause();
                }
            }

            bigPlayBtn.addEventListener('click', togglePlay);
            btnPlayPause.addEventListener('click', togglePlay);
            video.addEventListener('click', togglePlay);

            video.addEventListener('play', function() {
                playIcon.className = 'fa-solid fa-pause';
                bigPlayBtn.classList.add('hidden');
                playerWrapper.classList.add('playing');
                resetControlsTimer();
                startProgressAutoSaver();
            });

            video.addEventListener('pause', function() {
                playIcon.className = 'fa-solid fa-play';
                bigPlayBtn.classList.remove('hidden');
                playerWrapper.classList.remove('playing');
                playerWrapper.classList.remove('controls-hidden');
                stopProgressAutoSaver();
                saveProgressToServer(false); // immediate save on pause
            });

            video.addEventListener('ended', function() {
                playIcon.className = 'fa-solid fa-rotate-right';
                bigPlayBtn.classList.remove('hidden');
                playerWrapper.classList.remove('playing');
                playerWrapper.classList.remove('controls-hidden');
                saveProgressToServer(true); // mark complete on end
                showCelebration();
            });

            // Auto-hide controls after 3.2s
            function resetControlsTimer() {
                playerWrapper.classList.remove('controls-hidden');
                if (controlsHideTimeout) clearTimeout(controlsHideTimeout);
                if (!video.paused) {
                    controlsHideTimeout = setTimeout(function() {
                        playerWrapper.classList.add('controls-hidden');
                    }, 3200);
                }
            }

            playerWrapper.addEventListener('mousemove', resetControlsTimer);
            playerWrapper.addEventListener('mouseleave', function() {
                if (!video.paused) playerWrapper.classList.add('controls-hidden');
            });

            // ─────────────────────────────────────────────────────────────
            // 4. Progress Bar & Time Update
            // ─────────────────────────────────────────────────────────────
            video.addEventListener('timeupdate', function() {
                if (isSeeking) return;

                const cur = video.currentTime;
                const dur = video.duration || 1;
                const percent = (cur / dur) * 100;

                seekPlayed.style.width = percent + '%';
                seekHandle.style.left = percent + '%';
                timeDisplay.textContent = formatTime(cur) + ' / ' + formatTime(dur);

                // 90% completion trigger
                if (!isCompletedTriggered && dur > 10 && (cur / dur) >= 0.90) {
                    isCompletedTriggered = true;
                    saveProgressToServer(true);
                    showCelebration();
                    showToast('🎉 Lesson 90% Completed! Recorded to your profile.');
                }
            });

            // Buffer Progress
            video.addEventListener('progress', function() {
                if (video.buffered.length > 0 && video.duration) {
                    const bufferedEnd = video.buffered.end(video.buffered.length - 1);
                    const bufPercent = (bufferedEnd / video.duration) * 100;
                    seekBuffer.style.width = bufPercent + '%';
                }
            });

            // Seek click & scrub
            seekContainer.addEventListener('click', function(e) {
                const rect = seekContainer.getBoundingClientRect();
                const pos = (e.clientX - rect.left) / rect.width;
                if (video.duration) {
                    video.currentTime = pos * video.duration;
                }
            });

            seekContainer.addEventListener('mousemove', function(e) {
                const rect = seekContainer.getBoundingClientRect();
                const pos = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width));
                if (video.duration) {
                    const hoverSec = pos * video.duration;
                    seekTooltip.textContent = formatTime(hoverSec);
                    seekTooltip.style.left = (pos * 100) + '%';
                    seekTooltip.style.display = 'block';
                }
            });

            seekContainer.addEventListener('mouseleave', function() {
                seekTooltip.style.display = 'none';
            });

            // ─────────────────────────────────────────────────────────────
            // 5. Volume & Mute
            // ─────────────────────────────────────────────────────────────
            btnMute.addEventListener('click', function() {
                video.muted = !video.muted;
                updateVolumeUI();
            });

            volumeSlider.addEventListener('input', function() {
                video.volume = parseFloat(volumeSlider.value);
                video.muted = (video.volume === 0);
                updateVolumeUI();
            });

            function updateVolumeUI() {
                volumeSlider.value = video.muted ? 0 : video.volume;
                if (video.muted || video.volume === 0) {
                    volumeIcon.className = 'fa-solid fa-volume-xmark';
                } else if (video.volume < 0.5) {
                    volumeIcon.className = 'fa-solid fa-volume-low';
                } else {
                    volumeIcon.className = 'fa-solid fa-volume-high';
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 6. Playback Speed Selector
            // ─────────────────────────────────────────────────────────────
            btnSpeed.addEventListener('click', function() {
                currentSpeedIdx = (currentSpeedIdx + 1) % speeds.length;
                const spd = speeds[currentSpeedIdx];
                video.playbackRate = spd;
                btnSpeed.textContent = spd.toFixed(1) + 'x';
            });

            // Picture-in-Picture
            btnPip.addEventListener('click', async function() {
                try {
                    if (document.pictureInPictureElement) {
                        await document.exitPictureInPicture();
                    } else if (document.pictureInPictureEnabled) {
                        await video.requestPictureInPicture();
                    }
                } catch (e) {
                    console.log('PiP not supported:', e);
                }
            });

            // Fullscreen Toggle
            btnFullscreen.addEventListener('click', function() {
                if (!document.fullscreenElement) {
                    playerWrapper.requestFullscreen().catch(err => alert(err.message));
                    fsIcon.className = 'fa-solid fa-compress';
                } else {
                    document.exitFullscreen();
                    fsIcon.className = 'fa-solid fa-expand';
                }
            });

            document.addEventListener('fullscreenchange', function() {
                if (!document.fullscreenElement) {
                    fsIcon.className = 'fa-solid fa-expand';
                }
            });

            // ─────────────────────────────────────────────────────────────
            // 7. Auto-Save Progress (Every 10 Seconds & On Events)
            // ─────────────────────────────────────────────────────────────
            function startProgressAutoSaver() {
                if (autoSaveInterval) clearInterval(autoSaveInterval);
                autoSaveInterval = setInterval(function() {
                    if (!video.paused && video.currentTime > 0) {
                        saveProgressToServer(false);
                    }
                }, 10000); // 10 seconds
            }

            function stopProgressAutoSaver() {
                if (autoSaveInterval) {
                    clearInterval(autoSaveInterval);
                    autoSaveInterval = null;
                }
            }

            function saveProgressToServer(forceCompleted) {
                const currentSec = Math.floor(video.currentTime);
                if (currentSec <= 0 && !forceCompleted) return;

                fetch(API_BASE + '/' + LECTURE_ID + '/progress', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        progressSeconds: currentSec,
                        completed: forceCompleted || isCompletedTriggered
                    })
                })
                .then(r => r.json())
                .then(res => {
                    if (res.status === 'success' && res.courseProgressPercentage !== undefined) {
                        updateSidebarProgress(res.courseProgressPercentage);
                    }
                })
                .catch(err => console.warn('[Progress] Auto-save sync note:', err));
            }

            function updateSidebarProgress(percent) {
                const fill = document.getElementById('sidebarProgressFill');
                const text = document.getElementById('sidebarProgressText');
                if (fill) fill.style.width = percent + '%';
                if (text) text.textContent = percent + '%';
            }

            function showCelebration() {
                celebrationCard.classList.add('visible');
                // Update lecture list dot to checkmark
                const item = document.getElementById('lec-item-' + LECTURE_ID);
                if (item) {
                    const dot = item.querySelector('.status-dot');
                    if (dot) {
                        dot.className = 'status-dot completed';
                        dot.innerHTML = '<i class="fa-solid fa-check"></i>';
                    }
                }
            }

            function formatTime(seconds) {
                const s = Math.floor(seconds || 0);
                const m = Math.floor(s / 60);
                const rem = s % 60;
                return m + ':' + (rem < 10 ? '0' : '') + rem;
            }

            function showToast(message) {
                const container = document.getElementById('toastContainer');
                const toast = document.createElement('div');
                toast.className = 'toast success';
                toast.innerHTML = '<i class="fa-solid fa-circle-check" style="color: #34d399;"></i><span>' + message + '</span>';
                container.appendChild(toast);
                setTimeout(function() {
                    toast.style.opacity = '0';
                    setTimeout(function() { toast.remove(); }, 300);
                }, 4000);
            }

            // Start player on page load
            initSecureStream();

        })();
    </script>
</body>
</html>

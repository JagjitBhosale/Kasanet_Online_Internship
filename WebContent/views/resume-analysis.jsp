<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <% if (session.getAttribute("userId")==null) { response.sendRedirect(request.getContextPath() + "/login" );
                return; } %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Resume Analysis & Profile - My Internship Technologies Portal</title>
                    <style>
                        *,
                        *::before,
                        *::after {
                            box-sizing: border-box;
                            margin: 0;
                            padding: 0;
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                        }

                        :root {
                            --primary: #1a237e;
                            --primary-dark: #121858;
                            --accent: #2b6cb0;
                            --accent-light: #ebf8ff;
                            --text-dark: #1e293b;
                            --text-muted: #64748b;
                            --bg: #f8fafc;
                            --card-bg: #ffffff;
                            --border: #e2e8f0;
                            --radius: 8px;
                            --success: #16a34a;
                            --success-bg: #f0fdf4;
                            --warning: #d97706;
                            --warning-bg: #fffbeb;
                            --danger: #dc2626;
                            --danger-bg: #fef2f2;
                        }

                        body {
                            background-color: var(--bg);
                            color: var(--text-dark);
                            line-height: 1.5;
                        }

                        /* Top Navbar */
                        .navbar {
                            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
                            color: #ffffff;
                            padding: 12px 24px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
                        }

                        .navbar-brand {
                            display: flex;
                            align-items: center;
                            gap: 12px;
                            font-size: 1.15rem;
                            font-weight: 700;
                            text-decoration: none;
                            color: #ffffff;
                        }

                        .navbar-menu {
                            display: flex;
                            align-items: center;
                            gap: 14px;
                        }

                        .nav-link {
                            color: rgba(255, 255, 255, 0.85);
                            text-decoration: none;
                            font-size: 0.88rem;
                            padding: 6px 12px;
                            border-radius: 6px;
                            transition: all 0.2s;
                        }

                        .nav-link:hover,
                        .nav-link.active {
                            color: #ffffff;
                            background: rgba(255, 255, 255, 0.15);
                        }

                        .btn-logout {
                            background: #dc2626;
                            color: #ffffff;
                            padding: 6px 14px;
                            border-radius: 6px;
                            text-decoration: none;
                            font-size: 0.85rem;
                            font-weight: 600;
                        }

                        .container {
                            max-width: 1200px;
                            margin: 24px auto;
                            padding: 0 20px;
                        }

                        .breadcrumb {
                            font-size: 0.85rem;
                            color: var(--text-muted);
                            margin-bottom: 16px;
                        }

                        .breadcrumb a {
                            color: var(--accent);
                            text-decoration: none;
                        }

                        /* Top Header & Readiness Bar */
                        .page-header-row {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            flex-wrap: wrap;
                            gap: 16px;
                            margin-bottom: 20px;
                        }

                        .page-header h2 {
                            font-size: 1.6rem;
                            font-weight: 700;
                            color: #0f172a;
                        }

                        .page-header p {
                            font-size: 0.92rem;
                            color: var(--text-muted);
                        }

                        .header-actions {
                            display: flex;
                            align-items: center;
                            gap: 12px;
                        }

                        .btn-analyze-entry {
                            display: inline-flex;
                            align-items: center;
                            gap: 8px;
                            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
                            color: #ffffff;
                            padding: 10px 20px;
                            border-radius: 6px;
                            font-size: 0.92rem;
                            font-weight: 600;
                            text-decoration: none;
                            box-shadow: 0 2px 6px rgba(37, 99, 235, 0.25);
                            transition: all 0.2s;
                            border: none;
                            cursor: pointer;
                        }

                        .btn-analyze-entry:hover {
                            background: linear-gradient(135deg, #172554 0%, #1d4ed8 100%);
                            box-shadow: 0 4px 10px rgba(37, 99, 235, 0.35);
                        }

                        /* Status summary bar */
                        .summary-bar {
                            background: #ffffff;
                            border: 1px solid var(--border);
                            border-radius: var(--radius);
                            padding: 14px 20px;
                            margin-bottom: 24px;
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            flex-wrap: wrap;
                            gap: 16px;
                        }

                        .summary-items {
                            display: flex;
                            align-items: center;
                            flex-wrap: wrap;
                            gap: 20px;
                        }

                        .summary-item {
                            display: flex;
                            flex-direction: column;
                            gap: 2px;
                        }

                        .summary-label {
                            font-size: 0.76rem;
                            text-transform: uppercase;
                            font-weight: 700;
                            letter-spacing: 0.05em;
                            color: var(--text-muted);
                        }

                        .summary-value {
                            font-size: 0.95rem;
                            font-weight: 700;
                            color: #0f172a;
                        }

                        .badge {
                            display: inline-block;
                            padding: 3px 8px;
                            border-radius: 4px;
                            font-size: 0.75rem;
                            font-weight: 600;
                        }

                        .badge-success {
                            background: #dcfce7;
                            color: #15803d;
                        }

                        .badge-warning {
                            background: #fef3c7;
                            color: #b45309;
                        }

                        .badge-info {
                            background: #e0f2fe;
                            color: #0369a1;
                        }

                        .badge-primary {
                            background: #e0e7ff;
                            color: #3730a3;
                        }

                        /* Alerts */
                        .alert-success {
                            background: var(--success-bg);
                            border: 1px solid #bbf7d0;
                            color: #166534;
                            padding: 12px 18px;
                            border-radius: 6px;
                            margin-bottom: 20px;
                            font-size: 0.9rem;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        .alert-error {
                            background: var(--danger-bg);
                            border: 1px solid #fecaca;
                            color: #991b1b;
                            padding: 12px 18px;
                            border-radius: 6px;
                            margin-bottom: 20px;
                            font-size: 0.9rem;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        /* Tabs Navigation */
                        .tabs-header {
                            display: flex;
                            border-bottom: 2px solid var(--border);
                            margin-bottom: 24px;
                            overflow-x: auto;
                            background: #ffffff;
                            border-radius: 8px 8px 0 0;
                            padding: 4px 8px 0 8px;
                            border-top: 1px solid var(--border);
                            border-left: 1px solid var(--border);
                            border-right: 1px solid var(--border);
                        }

                        .tab-btn {
                            padding: 12px 18px;
                            color: #64748b;
                            font-weight: 600;
                            font-size: 0.92rem;
                            text-decoration: none;
                            border-bottom: 3px solid transparent;
                            margin-bottom: -2px;
                            white-space: nowrap;
                            transition: all 0.2s;
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                        }

                        .tab-btn:hover {
                            color: var(--primary);
                        }

                        .tab-btn.active {
                            color: var(--primary);
                            border-bottom-color: var(--primary);
                            background: rgba(26, 35, 126, 0.04);
                            border-radius: 6px 6px 0 0;
                        }

                        /* Cards & Forms */
                        .card {
                            background: var(--card-bg);
                            border: 1px solid var(--border);
                            border-radius: var(--radius);
                            padding: 28px;
                            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
                            margin-bottom: 24px;
                        }

                        .card-header-bar {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 20px;
                            padding-bottom: 14px;
                            border-bottom: 1px solid var(--border);
                        }

                        .card-title {
                            font-size: 1.15rem;
                            font-weight: 700;
                            color: #0f172a;
                        }

                        .card-desc {
                            font-size: 0.88rem;
                            color: var(--text-muted);
                            margin-top: 2px;
                        }

                        .form-grid-2 {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 18px;
                            margin-bottom: 16px;
                        }

                        .form-grid-3 {
                            display: grid;
                            grid-template-columns: repeat(3, 1fr);
                            gap: 18px;
                            margin-bottom: 16px;
                        }

                        .form-group {
                            display: flex;
                            flex-direction: column;
                            gap: 6px;
                            margin-bottom: 16px;
                        }

                        .form-label {
                            font-size: 0.86rem;
                            font-weight: 600;
                            color: #334155;
                        }

                        .form-label .required {
                            color: #dc2626;
                        }

                        .form-input,
                        .form-select,
                        .form-textarea {
                            width: 100%;
                            padding: 9px 12px;
                            border: 1px solid #cbd5e1;
                            border-radius: 6px;
                            font-size: 0.9rem;
                            color: #1e293b;
                            background: #ffffff;
                            transition: border-color 0.15s, box-shadow 0.15s;
                        }

                        .form-input:focus,
                        .form-select:focus,
                        .form-textarea:focus {
                            outline: none;
                            border-color: #2563eb;
                            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
                        }

                        .form-textarea {
                            resize: vertical;
                            min-height: 80px;
                        }

                        .form-help {
                            font-size: 0.8rem;
                            color: var(--text-muted);
                            margin-top: 2px;
                        }

                        .btn-primary {
                            background: #1a237e;
                            color: #ffffff;
                            border: none;
                            padding: 9px 20px;
                            border-radius: 6px;
                            font-size: 0.9rem;
                            font-weight: 600;
                            cursor: pointer;
                            transition: background 0.2s;
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                        }

                        .btn-primary:hover {
                            background: #121858;
                        }

                        .btn-secondary {
                            background: #f1f5f9;
                            color: #334155;
                            border: 1px solid #cbd5e1;
                            padding: 9px 18px;
                            border-radius: 6px;
                            font-size: 0.9rem;
                            font-weight: 600;
                            cursor: pointer;
                            text-decoration: none;
                            display: inline-flex;
                            align-items: center;
                        }

                        .btn-secondary:hover {
                            background: #e2e8f0;
                        }

                        .btn-danger-sm {
                            background: #fee2e2;
                            color: #991b1b;
                            border: 1px solid #fecaca;
                            padding: 5px 10px;
                            border-radius: 4px;
                            font-size: 0.8rem;
                            cursor: pointer;
                            font-weight: 600;
                            transition: all 0.2s;
                        }

                        .btn-danger-sm:hover {
                            background: #dc2626;
                            color: #ffffff;
                        }

                        .btn-edit-sm {
                            background: #eff6ff;
                            color: #1d4ed8;
                            border: 1px solid #bfdbfe;
                            padding: 5px 10px;
                            border-radius: 4px;
                            font-size: 0.8rem;
                            text-decoration: none;
                            font-weight: 600;
                            display: inline-block;
                            transition: all 0.2s;
                        }

                        .btn-edit-sm:hover {
                            background: #2563eb;
                            color: #ffffff;
                        }

                        /* Tables */
                        .data-table {
                            width: 100%;
                            border-collapse: collapse;
                            font-size: 0.88rem;
                            margin-top: 10px;
                        }

                        .data-table th,
                        .data-table td {
                            border: 1px solid #e2e8f0;
                            padding: 11px 14px;
                            text-align: left;
                            vertical-align: middle;
                        }

                        .data-table th {
                            background: #f8fafc;
                            color: #334155;
                            font-weight: 600;
                        }

                        .data-table tr:hover {
                            background: #f8fafc;
                        }

                        .empty-state {
                            text-align: center;
                            padding: 36px 20px;
                            color: var(--text-muted);
                            border: 2px dashed #e2e8f0;
                            border-radius: 6px;
                            margin-top: 14px;
                        }

                        .empty-state-icon {
                            font-size: 2rem;
                            color: #94a3b8;
                            margin-bottom: 8px;
                        }

                        /* Readiness / Analysis view */
                        .readiness-meter {
                            background: #f1f5f9;
                            border-radius: 9999px;
                            height: 14px;
                            overflow: hidden;
                            margin: 12px 0;
                        }

                        .readiness-fill {
                            height: 100%;
                            border-radius: 9999px;
                            transition: width 0.4s ease;
                        }

                        .readiness-checklist {
                            display: flex;
                            flex-direction: column;
                            gap: 10px;
                            margin-top: 16px;
                        }

                        .checklist-item {
                            display: flex;
                            align-items: center;
                            gap: 12px;
                            padding: 10px 14px;
                            background: #f8fafc;
                            border: 1px solid var(--border);
                            border-radius: 6px;
                            font-size: 0.9rem;
                        }

                        .checklist-icon {
                            font-size: 1.1rem;
                            font-weight: 700;
                        }

                        .checklist-icon.pass {
                            color: #16a34a;
                        }

                        .checklist-icon.miss {
                            color: #d97706;
                        }

                        .tag-tech {
                            display: inline-block;
                            background: #f1f5f9;
                            color: #334155;
                            border: 1px solid #cbd5e1;
                            padding: 2px 7px;
                            border-radius: 4px;
                            font-size: 0.78rem;
                            margin-right: 4px;
                            margin-bottom: 4px;
                        }

                        /* AI Analysis Module Styles */
                        .ai-hero-card {
                            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 60%, #1e293b 100%);
                            color: #ffffff;
                            border-radius: 12px;
                            padding: 28px;
                            margin-bottom: 28px;
                            box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.3), 0 8px 10px -6px rgba(15, 23, 42, 0.2);
                            position: relative;
                            overflow: hidden;
                        }

                        .ai-hero-card::after {
                            content: "";
                            position: absolute;
                            top: -50%;
                            right: -20%;
                            width: 380px;
                            height: 380px;
                            background: radial-gradient(circle, rgba(99, 102, 241, 0.18) 0%, rgba(99, 102, 241, 0) 70%);
                            border-radius: 50%;
                            pointer-events: none;
                        }

                        .ai-hero-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            flex-wrap: wrap;
                            gap: 16px;
                            margin-bottom: 20px;
                        }

                        .ai-hero-badge {
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                            background: rgba(99, 102, 241, 0.2);
                            border: 1px solid rgba(129, 140, 248, 0.4);
                            color: #c7d2fe;
                            padding: 4px 12px;
                            border-radius: 9999px;
                            font-size: 0.82rem;
                            font-weight: 600;
                            letter-spacing: 0.02em;
                        }

                        .ai-hero-scores {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                            gap: 16px;
                            margin-top: 20px;
                        }

                        .ai-score-box {
                            background: rgba(255, 255, 255, 0.06);
                            border: 1px solid rgba(255, 255, 255, 0.12);
                            backdrop-filter: blur(8px);
                            border-radius: 10px;
                            padding: 16px 20px;
                            display: flex;
                            flex-direction: column;
                            gap: 6px;
                        }

                        .ai-score-title {
                            font-size: 0.8rem;
                            text-transform: uppercase;
                            letter-spacing: 0.06em;
                            color: #94a3b8;
                            font-weight: 600;
                        }

                        .ai-score-num {
                            font-size: 2.2rem;
                            font-weight: 800;
                            line-height: 1;
                            background: linear-gradient(135deg, #ffffff 0%, #cbd5e1 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }

                        .ai-score-num.highlight {
                            background: linear-gradient(135deg, #4ade80 0%, #22c55e 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }

                        .ai-score-num.market {
                            background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }

                        .ai-summary-card {
                            background: #ffffff;
                            border: 1px solid var(--border);
                            border-radius: 10px;
                            padding: 22px;
                            margin-bottom: 24px;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
                        }

                        .ai-summary-text {
                            font-size: 0.96rem;
                            line-height: 1.65;
                            color: #334155;
                            border-left: 3px solid #3b82f6;
                            padding-left: 14px;
                            margin-top: 10px;
                        }

                        .ai-grid-3 {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
                            gap: 20px;
                            margin-bottom: 28px;
                        }

                        .ai-col-card {
                            background: #ffffff;
                            border: 1px solid var(--border);
                            border-radius: 10px;
                            overflow: hidden;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
                            display: flex;
                            flex-direction: column;
                        }

                        .ai-col-header {
                            padding: 16px 20px;
                            font-weight: 700;
                            font-size: 1rem;
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            border-bottom: 1px solid var(--border);
                        }

                        .ai-col-header.matching {
                            background: #f0fdf4;
                            color: #166534;
                            border-color: #bbf7d0;
                        }

                        .ai-col-header.missing {
                            background: #fef2f2;
                            color: #991b1b;
                            border-color: #fecaca;
                        }

                        .ai-col-header.trending {
                            background: #faf5ff;
                            color: #6b21a8;
                            border-color: #f3e8ff;
                        }

                        .ai-col-body {
                            padding: 16px 20px;
                            flex: 1;
                            display: flex;
                            flex-direction: column;
                            gap: 14px;
                        }

                        .skill-chip-item {
                            padding: 12px;
                            border-radius: 8px;
                            border: 1px solid var(--border);
                            background: #f8fafc;
                            transition: transform 0.15s, box-shadow 0.15s;
                        }

                        .skill-chip-item:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                        }

                        .skill-chip-item.matching-item {
                            background: #ffffff;
                            border-color: #dcfce7;
                        }

                        .skill-chip-item.missing-item {
                            background: #ffffff;
                            border-color: #fee2e2;
                        }

                        .skill-chip-item.trending-item {
                            background: #ffffff;
                            border-color: #f3e8ff;
                        }

                        .skill-chip-title {
                            font-weight: 700;
                            font-size: 0.92rem;
                            color: #0f172a;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 4px;
                        }

                        .skill-chip-desc {
                            font-size: 0.84rem;
                            color: #475569;
                            line-height: 1.45;
                        }

                        .skill-chip-action {
                            margin-top: 6px;
                            font-size: 0.8rem;
                            color: #0284c7;
                            font-weight: 600;
                        }

                        .badge-crit {
                            background: #fee2e2;
                            color: #b91c1c;
                            padding: 2px 7px;
                            border-radius: 4px;
                            font-size: 0.72rem;
                            font-weight: 700;
                            text-transform: uppercase;
                        }

                        .badge-high {
                            background: #fef3c7;
                            color: #b45309;
                            padding: 2px 7px;
                            border-radius: 4px;
                            font-size: 0.72rem;
                            font-weight: 700;
                            text-transform: uppercase;
                        }

                        .badge-trend {
                            background: #ede9fe;
                            color: #6d28d9;
                            padding: 2px 7px;
                            border-radius: 4px;
                            font-size: 0.72rem;
                            font-weight: 700;
                        }

                        .badge-match {
                            background: #dcfce7;
                            color: #15803d;
                            padding: 2px 7px;
                            border-radius: 4px;
                            font-size: 0.72rem;
                            font-weight: 700;
                        }

                        /* Project suggestion card */
                        .project-intel-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
                            gap: 18px;
                            margin-top: 14px;
                        }

                        .project-intel-card {
                            background: #ffffff;
                            border: 1px solid var(--border);
                            border-radius: 8px;
                            padding: 18px 20px;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
                            display: flex;
                            flex-direction: column;
                            gap: 10px;
                        }

                        .project-intel-title {
                            font-size: 1.05rem;
                            font-weight: 700;
                            color: #0f172a;
                        }

                        .project-intel-tech {
                            display: inline-block;
                            background: #f1f5f9;
                            color: #1e293b;
                            font-size: 0.8rem;
                            font-weight: 600;
                            padding: 4px 10px;
                            border-radius: 6px;
                            border: 1px solid #e2e8f0;
                        }

                        .project-intel-impact {
                            background: #f0fdf4;
                            border: 1px solid #bbf7d0;
                            color: #166534;
                            font-size: 0.84rem;
                            padding: 8px 12px;
                            border-radius: 6px;
                            line-height: 1.4;
                        }

                        /* ATS Bullet Card */
                        .bullet-intel-card {
                            background: #ffffff;
                            border: 1px solid var(--border);
                            border-radius: 8px;
                            padding: 16px 20px;
                            margin-bottom: 12px;
                        }

                        .bullet-intel-issue {
                            color: #b91c1c;
                            font-size: 0.88rem;
                            font-weight: 600;
                            margin-bottom: 6px;
                        }

                        .bullet-intel-sol {
                            background: #f8fafc;
                            border-left: 3px solid #22c55e;
                            padding: 10px 14px;
                            font-size: 0.88rem;
                            color: #0f172a;
                            line-height: 1.5;
                        }

                        /* Action Roadmap Timeline */
                        .roadmap-strip {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                            gap: 16px;
                            margin-top: 14px;
                        }

                        .roadmap-box {
                            background: #f8fafc;
                            border: 1px solid var(--border);
                            border-radius: 8px;
                            padding: 18px;
                        }

                        .roadmap-period {
                            font-size: 0.78rem;
                            text-transform: uppercase;
                            letter-spacing: 0.06em;
                            color: #2563eb;
                            font-weight: 800;
                        }

                        .roadmap-focus {
                            font-size: 0.98rem;
                            font-weight: 700;
                            color: #0f172a;
                            margin: 4px 0 10px 0;
                        }

                        .roadmap-task-list {
                            list-style: none;
                            padding-left: 0;
                            display: flex;
                            flex-direction: column;
                            gap: 8px;
                        }

                        .roadmap-task-list li {
                            font-size: 0.85rem;
                            color: #475569;
                            position: relative;
                            padding-left: 18px;
                            line-height: 1.4;
                        }

                        .roadmap-task-list li::before {
                            content: "•";
                            position: absolute;
                            left: 4px;
                            color: #3b82f6;
                            font-weight: bold;
                        }

                        /* Button & Spinner states */
                        .btn-run-ai {
                            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 50%, #4338ca 100%);
                            color: #ffffff;
                            border: none;
                            padding: 14px 28px;
                            border-radius: 8px;
                            font-size: 1.02rem;
                            font-weight: 700;
                            cursor: pointer;
                            display: inline-flex;
                            align-items: center;
                            gap: 10px;
                            box-shadow: 0 4px 14px rgba(37, 99, 235, 0.35);
                            transition: all 0.2s ease;
                        }

                        .btn-run-ai:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 6px 20px rgba(37, 99, 235, 0.45);
                            background: linear-gradient(135deg, #172554 0%, #1d4ed8 50%, #3730a3 100%);
                        }

                        .btn-re-run {
                            background: rgba(255, 255, 255, 0.15);
                            color: #ffffff;
                            border: 1px solid rgba(255, 255, 255, 0.3);
                            padding: 8px 16px;
                            border-radius: 6px;
                            font-size: 0.86rem;
                            font-weight: 600;
                            cursor: pointer;
                            display: inline-flex;
                            align-items: center;
                            gap: 8px;
                            transition: background 0.2s;
                        }

                        .btn-re-run:hover {
                            background: rgba(255, 255, 255, 0.25);
                        }

                        .ai-overlay-spinner {
                            display: none;
                            position: fixed;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            background: rgba(15, 23, 42, 0.7);
                            backdrop-filter: blur(4px);
                            z-index: 9999;
                            justify-content: center;
                            align-items: center;
                            flex-direction: column;
                            color: #ffffff;
                            gap: 16px;
                        }

                        .spinner {
                            width: 50px;
                            height: 50px;
                            border: 4px solid rgba(255, 255, 255, 0.2);
                            border-top-color: #60a5fa;
                            border-radius: 50%;
                            animation: spin 0.8s linear infinite;
                        }

                        @keyframes spin {
                            to { transform: rotate(360deg); }
                        }

                        @media (max-width: 768px) {

                            .form-grid-2,
                            .form-grid-3 {
                                grid-template-columns: 1fr;
                            }

                            .summary-bar {
                                flex-direction: column;
                                align-items: flex-start;
                            }

                            .ai-grid-3 {
                                grid-template-columns: 1fr;
                            }
                        }
                    </style>
                </head>

                <body>

                    <!-- Top Navbar -->
                    <nav class="navbar">
                        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
                            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo"
                                style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
                            <span>My Internship Technologies</span>
                        </a>
                        <div class="navbar-menu">
                            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
                            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">My Dashboard</a>
                            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
                            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
                            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
                            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question
                                Bank</a>
                            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link active">Resume
                                Analysis</a>
                            <span style="font-size: 0.88rem; color: #cbd5e0; margin-left: 6px;">Hi,
                                ${user.firstName}</span>
                            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
                        </div>
                    </nav>

                    <!-- Main Content Container -->
                    <main class="container">
                        <!-- <div class="breadcrumb">
                            <a href="${pageContext.request.contextPath}/home">Home</a> &rsaquo;
                            <span>Resume Analysis &amp; Profile Information</span>
                        </div> -->

                        <!-- Page Header & Action -->
                        <div class="page-header-row">
                            <div class="page-header">
                                <h2>Resume Analysis &amp; Student Profile</h2>
                                <p>Maintain your structured profile data (Career Goal, Skills, Projects, Experience,
                                    Certifications, Education) for intelligent evaluation.</p>
                            </div>
                            <div class="header-actions">
                                <a href="${pageContext.request.contextPath}/resume-analysis?tab=analyze"
                                    class="btn-analyze-entry">
                                    <span>&#9654;</span> Analyze My Profile
                                </a>
                            </div>
                        </div>

                        <!-- Success & Error Messages -->
                        <c:if test="${param.success == '1'}">
                            <div class="alert-success" role="alert">
                                <span>&#10003;</span>
                                <div>Information saved and updated successfully in your profile!</div>
                            </div>
                        </c:if>
                        <c:if test="${param.success == 'analyzed'}">
                            <div class="alert-success" role="alert" style="background: #f0fdf4; border-color: #86efac; color: #166534;">
                                <span style="font-size: 1.15rem;">&#128640;</span>
                                <div><strong>Analysis Completed!</strong> Your profile has been analyzed against live tech industry market requirements for your target role.</div>
                            </div>
                        </c:if>
                        <c:if test="${param.success == 'key_saved'}">
                            <div class="alert-success" role="alert">
                                <span>&#10003;</span>
                                <div>Groq API Key saved successfully in system settings! You can now run AI analyses anytime.</div>
                            </div>
                        </c:if>
                        <c:if test="${not empty param.error}">
                            <div class="alert-error" role="alert">
                                <span>&#9888;</span>
                                <div>${param.error}</div>
                            </div>
                        </c:if>

                        <!-- Summary & Readiness Bar -->
                        <div class="summary-bar">
                            <div class="summary-items">
                                <div class="summary-item">
                                    <span class="summary-label">Target Role</span>
                                    <span class="summary-value">
                                        <c:choose>
                                            <c:when test="${not empty careerGoal.targetRole}">
                                                ${careerGoal.effectiveTargetRole}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">Not Set</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Skills</span>
                                    <span class="summary-value">${skills.size()} declared</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Projects</span>
                                    <span class="summary-value">${projects.size()} recorded</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Experience</span>
                                    <span class="summary-value">${experiences.size()} entries</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Certifications</span>
                                    <span class="summary-value">${certifications.size()} earned</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Education</span>
                                    <span class="summary-value">${educations.size()} qualifications</span>
                                </div>
                            </div>
                            <div>
                                <span class="badge ${readiness.readyForAnalysis ? 'badge-success' : 'badge-warning'}"
                                    style="font-size: 0.82rem; padding: 6px 12px;">
                                    ${readiness.readyForAnalysis ? 'Ready for Analysis' : 'Profile Incomplete'}
                                    (${readiness.readinessScore}%)
                                </span>
                            </div>
                        </div>

                        <!-- Navigation Tabs -->
                        <nav class="tabs-header">
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=career"
                                class="tab-btn ${activeTab == 'career' ? 'active' : ''}">
                                1. Career Goal
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=skills"
                                class="tab-btn ${activeTab == 'skills' ? 'active' : ''}">
                                2. Skills
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=projects"
                                class="tab-btn ${activeTab == 'projects' ? 'active' : ''}">
                                3. Projects
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=experience"
                                class="tab-btn ${activeTab == 'experience' ? 'active' : ''}">
                                4. Experience
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=certifications"
                                class="tab-btn ${activeTab == 'certifications' ? 'active' : ''}">
                                5. Certifications
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=education"
                                class="tab-btn ${activeTab == 'education' ? 'active' : ''}">
                                6. Education
                            </a>
                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=analyze"
                                class="tab-btn ${activeTab == 'analyze' ? 'active' : ''}" style="color: #2563eb;">
                                &#9733; Analyze My Profile
                            </a>
                        </nav>

                        <!-- =============================================================
             TAB 1: CAREER GOAL
             ============================================================= -->
                        <c:if test="${activeTab == 'career'}">
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Career Goal &amp; Target Direction</h3>
                                        <p class="card-desc">Your target career direction is essential for future AI
                                            skill-gap analysis to provide contextual recommendations.</p>
                                    </div>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_career">

                                    <div class="form-grid-2">
                                        <div class="form-group">
                                            <label class="form-label" for="targetRole">Target Role <span
                                                    class="required">*</span></label>
                                            <select name="targetRole" id="targetRole" class="form-select" required
                                                onchange="toggleRoleOther(this.value)">
                                                <option value="" disabled ${empty careerGoal.targetRole ? 'selected'
                                                    : '' }>-- Select Target Role --</option>
                                                <option value="Software Developer"
                                                    ${careerGoal.targetRole=='Software Developer' ? 'selected' : '' }>
                                                    Software Developer</option>
                                                <option value="Backend Developer"
                                                    ${careerGoal.targetRole=='Backend Developer' ? 'selected' : '' }>
                                                    Backend Developer</option>
                                                <option value="Frontend Developer"
                                                    ${careerGoal.targetRole=='Frontend Developer' ? 'selected' : '' }>
                                                    Frontend Developer</option>
                                                <option value="Full Stack Developer"
                                                    ${careerGoal.targetRole=='Full Stack Developer' ? 'selected' : '' }>
                                                    Full Stack Developer</option>
                                                <option value="Data Scientist" ${careerGoal.targetRole=='Data Scientist'
                                                    ? 'selected' : '' }>Data Scientist</option>
                                                <option value="Machine Learning Engineer"
                                                    ${careerGoal.targetRole=='Machine Learning Engineer' ? 'selected'
                                                    : '' }>Machine Learning Engineer</option>
                                                <option value="DevOps Engineer"
                                                    ${careerGoal.targetRole=='DevOps Engineer' ? 'selected' : '' }>
                                                    DevOps Engineer</option>
                                                <option value="Cloud Engineer" ${careerGoal.targetRole=='Cloud Engineer'
                                                    ? 'selected' : '' }>Cloud Engineer</option>
                                                <option value="Mobile App Developer"
                                                    ${careerGoal.targetRole=='Mobile App Developer' ? 'selected' : '' }>
                                                    Mobile App Developer</option>
                                                <option value="QA / Automation Engineer"
                                                    ${careerGoal.targetRole=='QA / Automation Engineer' ? 'selected'
                                                    : '' }>QA / Automation Engineer</option>
                                                <option value="Other" ${careerGoal.targetRole=='Other' ? 'selected' : ''
                                                    }>Other (Specify)</option>
                                            </select>
                                        </div>

                                        <div class="form-group" id="targetRoleOtherGroup"
                                            style="${careerGoal.targetRole == 'Other' ? '' : 'display:none;'}">
                                            <label class="form-label" for="targetRoleOther">Specify Target Role <span
                                                    class="required">*</span></label>
                                            <input type="text" name="targetRoleOther" id="targetRoleOther"
                                                class="form-input" placeholder="e.g. Embedded Systems Engineer"
                                                value="${careerGoal.targetRoleOther}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="preferredDomain">Preferred Domain</label>
                                            <select name="preferredDomain" id="preferredDomain" class="form-select"
                                                onchange="toggleDomainOther(this.value)">
                                                <option value="" ${empty careerGoal.preferredDomain ? 'selected' : '' }>
                                                    -- Select Preferred Domain (Optional) --</option>
                                                <option value="Web & Enterprise Applications"
                                                    ${careerGoal.preferredDomain=='Web & Enterprise Applications'
                                                    ? 'selected' : '' }>Web &amp; Enterprise Applications</option>
                                                <option value="Artificial Intelligence & Machine Learning"
                                                    ${careerGoal.preferredDomain=='Artificial Intelligence & Machine Learning'
                                                    ? 'selected' : '' }>Artificial Intelligence &amp; Machine Learning
                                                </option>
                                                <option value="Cloud & Distributed Systems"
                                                    ${careerGoal.preferredDomain=='Cloud & Distributed Systems'
                                                    ? 'selected' : '' }>Cloud &amp; Distributed Systems</option>
                                                <option value="Data Engineering & Analytics"
                                                    ${careerGoal.preferredDomain=='Data Engineering & Analytics'
                                                    ? 'selected' : '' }>Data Engineering &amp; Analytics</option>
                                                <option value="Mobile App Ecosystems"
                                                    ${careerGoal.preferredDomain=='Mobile App Ecosystems' ? 'selected'
                                                    : '' }>Mobile App Ecosystems</option>
                                                <option value="Cybersecurity & Defense"
                                                    ${careerGoal.preferredDomain=='Cybersecurity & Defense' ? 'selected'
                                                    : '' }>Cybersecurity &amp; Defense</option>
                                                <option value="FinTech & Banking"
                                                    ${careerGoal.preferredDomain=='FinTech & Banking' ? 'selected' : ''
                                                    }>FinTech &amp; Banking</option>
                                                <option value="Other" ${careerGoal.preferredDomain=='Other' ? 'selected'
                                                    : '' }>Other (Specify)</option>
                                            </select>
                                        </div>

                                        <div class="form-group" id="preferredDomainOtherGroup"
                                            style="${careerGoal.preferredDomain == 'Other' ? '' : 'display:none;'}">
                                            <label class="form-label" for="preferredDomainOther">Specify Domain</label>
                                            <input type="text" name="preferredDomainOther" id="preferredDomainOther"
                                                class="form-input" placeholder="e.g. Healthcare Tech"
                                                value="${careerGoal.preferredDomainOther}">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="careerObjective">Career Objective &amp;
                                            Aspirations</label>
                                        <textarea name="careerObjective" id="careerObjective" class="form-textarea"
                                            placeholder="Briefly describe what challenges you want to solve, industries you're targeting, or technologies you wish to master...">${careerGoal.careerObjective}</textarea>
                                        <span class="form-help">Helps contextualize your background for recommendation
                                            explanations.</span>
                                    </div>

                                    <div style="display: flex; gap: 12px; margin-top: 14px;">
                                        <button type="submit" class="btn-primary">Save Career Goal</button>
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=skills"
                                            class="btn-secondary">Next: Skills &rarr;</a>
                                    </div>
                                </form>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 2: SKILLS
             ============================================================= -->
                        <c:if test="${activeTab == 'skills'}">
                            <!-- Form to Add/Edit Skill -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">${not empty editSkill ? 'Edit Skill' : 'Add Technical or
                                            Professional Skill'}</h3>
                                        <p class="card-desc">Declare your skills across programming languages,
                                            frameworks, databases, and tools.</p>
                                    </div>
                                    <c:if test="${not empty editSkill}">
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=skills"
                                            class="btn-secondary">&larr; Cancel Edit</a>
                                    </c:if>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_skill">
                                    <input type="hidden" name="skillId"
                                        value="${editSkill != null ? editSkill.skillId : 0}">

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="skillName">Skill Name <span
                                                    class="required">*</span></label>
                                            <input type="text" name="skillName" id="skillName" class="form-input"
                                                placeholder="e.g. Java, Docker, MySQL, React"
                                                value="${editSkill != null ? editSkill.skillName : ''}" required>
                                            <span class="form-help">Enter a specific language, library, or tool.</span>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="skillType">Category <span
                                                    class="required">*</span></label>
                                            <select name="skillType" id="skillType" class="form-select" required>
                                                <option value="Programming Languages"
                                                    ${editSkill.skillType=='Programming Languages' ? 'selected' : '' }>
                                                    Programming Languages</option>
                                                <option value="Frameworks / Libraries"
                                                    ${editSkill.skillType=='Frameworks / Libraries' ? 'selected' : '' }>
                                                    Frameworks / Libraries</option>
                                                <option value="Databases" ${editSkill.skillType=='Databases'
                                                    ? 'selected' : '' }>Databases</option>
                                                <option value="Tools" ${editSkill.skillType=='Tools' ? 'selected' : ''
                                                    }>Tools &amp; DevOps</option>
                                                <option value="Cloud" ${editSkill.skillType=='Cloud' ? 'selected' : ''
                                                    }>Cloud Platforms</option>
                                                <option value="Technical" ${editSkill.skillType=='Technical'
                                                    ? 'selected' : '' }>General Technical</option>
                                                <option value="Soft" ${editSkill.skillType=='Soft' ? 'selected' : '' }>
                                                    Soft Skills</option>
                                                <option value="Domain" ${editSkill.skillType=='Domain' ? 'selected' : ''
                                                    }>Domain Knowledge</option>
                                                <option value="Other" ${editSkill.skillType=='Other' ? 'selected' : ''
                                                    }>Other</option>
                                            </select>
                                        </div>

                                    </div>

                                    <div>
                                        <button type="submit" class="btn-primary">${not empty editSkill ? 'Update Skill'
                                            : '+ Add Skill'}</button>
                                    </div>
                                </form>
                            </div>

                            <!-- List of Saved Skills -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Declared Skills (${skills.size()})</h3>
                                        <p class="card-desc">All skills linked to your student account.</p>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty skills}">
                                        <table class="data-table">
                                            <thead>
                                                <tr>
                                                    <th>Skill Name</th>
                                                    <th>Category</th>
                                                    <th style="width: 140px; text-align: center;">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="s" items="${skills}">
                                                    <tr>
                                                        <td><strong>
                                                                <c:out value="${s.skillName}" />
                                                            </strong></td>
                                                        <td><span class="badge badge-info">
                                                                <c:out value="${s.skillType}" />
                                                            </span></td>
                                                        <td style="text-align: center;">
                                                            <div style="display: inline-flex; gap: 8px;">
                                                                <a href="${pageContext.request.contextPath}/resume-analysis?tab=skills&editSkillId=${s.skillId}"
                                                                    class="btn-edit-sm">Edit</a>
                                                                <form
                                                                    action="${pageContext.request.contextPath}/resume-analysis"
                                                                    method="post" style="display:inline;"
                                                                    onsubmit="return confirm('Remove skill: ${s.skillName}?');">
                                                                    <input type="hidden" name="action"
                                                                        value="delete_skill">
                                                                    <input type="hidden" name="skillId"
                                                                        value="${s.skillId}">
                                                                    <button type="submit"
                                                                        class="btn-danger-sm">Delete</button>
                                                                </form>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div class="empty-state-icon">&#128295;</div>
                                            <h4>No skills added yet</h4>
                                            <p>Use the form above to add your technical capabilities and programming
                                                proficiencies.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 3: PROJECTS
             ============================================================= -->
                        <c:if test="${activeTab == 'projects'}">
                            <!-- Form to Add/Edit Project -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">${not empty editProject ? 'Edit Project' : 'Add Project'}
                                        </h3>
                                        <p class="card-desc">Projects provide critical empirical evidence of applied
                                            technical skills.</p>
                                    </div>
                                    <c:if test="${not empty editProject}">
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=projects"
                                            class="btn-secondary">&larr; Cancel Edit</a>
                                    </c:if>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_project">
                                    <input type="hidden" name="projectId"
                                        value="${editProject != null ? editProject.projectId : 0}">

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="projectName">Project Title <span
                                                    class="required">*</span></label>
                                            <input type="text" name="projectName" id="projectName" class="form-input"
                                                placeholder="e.g. TODO List"
                                                value="${editProject != null ? editProject.projectName : ''}" required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="projectRole">Your Role / Contribution</label>
                                            <input type="text" name="role" id="projectRole" class="form-input"
                                                placeholder="e.g. Backend Lead, Full Stack Developer"
                                                value="${editProject != null ? editProject.role : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="projectType">Project Type</label>
                                            <select name="projectType" id="projectType" class="form-select">
                                                <option value="Academic" ${editProject.projectType=='Academic'
                                                    ? 'selected' : '' }>Academic</option>
                                                <option value="Personal" ${editProject.projectType=='Personal'
                                                    ? 'selected' : '' }>Personal / Hobby</option>
                                                <option value="Professional" ${editProject.projectType=='Professional'
                                                    ? 'selected' : '' }>Professional</option>
                                                <option value="Open Source" ${editProject.projectType=='Open Source'
                                                    ? 'selected' : '' }>Open Source</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="technologiesUsed">Technologies Used <span
                                                class="required">*</span></label>
                                        <input type="text" name="technologiesUsed" id="technologiesUsed"
                                            class="form-input"
                                            placeholder="e.g. Java, Spring Boot, MySQL, Docker, React"
                                            value="${editProject != null ? editProject.technologiesUsed : ''}" required>
                                        <span class="form-help">Comma-separated list. Used by future skill analyzer as
                                            evidence of demonstrated capability.</span>
                                    </div>

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="teamSize">Team Size</label>
                                            <input type="number" name="teamSize" id="teamSize" class="form-input"
                                                min="1" max="100"
                                                value="${editProject != null ? editProject.teamSize : 1}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="startDate">Start Date</label>
                                            <input type="date" name="startDate" id="startDate" class="form-input"
                                                value="${editProject != null ? editProject.startDate : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="endDate">End Date</label>
                                            <input type="date" name="endDate" id="endDate" class="form-input"
                                                value="${editProject != null ? editProject.endDate : ''}">
                                        </div>
                                    </div>

                                    <div class="form-grid-2">
                                        <div class="form-group">
                                            <label class="form-label" for="projectUrl">Live Demo / Deployed URL</label>
                                            <input type="url" name="projectUrl" id="projectUrl" class="form-input"
                                                placeholder="https://myproject.app"
                                                value="${editProject != null ? editProject.projectUrl : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="githubUrl">GitHub / Source Repository
                                                URL</label>
                                            <input type="url" name="githubUrl" id="githubUrl" class="form-input"
                                                placeholder="https://github.com/username/project"
                                                value="${editProject != null ? editProject.githubUrl : ''}">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="projDescription">Description &amp; Key
                                            Features</label>
                                        <textarea name="description" id="projDescription" class="form-textarea"
                                            placeholder="Detail the architecture, your specific contributions, and problems solved...">${editProject != null ? editProject.description : ''}</textarea>
                                    </div>

                                    <div>
                                        <button type="submit" class="btn-primary">${not empty editProject ? 'Update
                                            Project' : '+ Save Project'}</button>
                                    </div>
                                </form>
                            </div>

                            <!-- List of Saved Projects -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Saved Projects (${projects.size()})</h3>
                                        <p class="card-desc">Evidence catalog for your technical skills.</p>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty projects}">
                                        <div style="display: flex; flex-direction: column; gap: 16px;">
                                            <c:forEach var="p" items="${projects}">
                                                <div
                                                    style="border: 1px solid var(--border); border-radius: 6px; padding: 18px; background: #ffffff;">
                                                    <div
                                                        style="display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; margin-bottom: 8px;">
                                                        <div>
                                                            <h4
                                                                style="font-size: 1.05rem; font-weight: 700; color: #1e3a8a;">
                                                                <c:out value="${p.projectName}" />
                                                            </h4>
                                                            <div
                                                                style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                                                                <span class="badge badge-info">
                                                                    <c:out value="${p.projectType}" />
                                                                </span>
                                                                <c:if test="${not empty p.role}"> &bull; Role: <strong>
                                                                        <c:out value="${p.role}" />
                                                                    </strong></c:if>
                                                                <c:if test="${p.teamSize > 1}"> &bull; Team:
                                                                    ${p.teamSize} members</c:if>
                                                            </div>
                                                        </div>
                                                        <div style="display: flex; gap: 8px;">
                                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=projects&editProjectId=${p.projectId}"
                                                                class="btn-edit-sm">Edit</a>
                                                            <form
                                                                action="${pageContext.request.contextPath}/resume-analysis"
                                                                method="post" style="display:inline;"
                                                                onsubmit="return confirm('Delete project: ${p.projectName}?');">
                                                                <input type="hidden" name="action"
                                                                    value="delete_project">
                                                                <input type="hidden" name="projectId"
                                                                    value="${p.projectId}">
                                                                <button type="submit"
                                                                    class="btn-danger-sm">Delete</button>
                                                            </form>
                                                        </div>
                                                    </div>

                                                    <c:if test="${not empty p.technologiesUsed}">
                                                        <div style="margin: 10px 0;">
                                                            <strong
                                                                style="font-size: 0.82rem; color: var(--text-muted); margin-right: 6px;">Technologies:</strong>
                                                            <c:forTokens items="${p.technologiesUsed}" delims=","
                                                                var="tech">
                                                                <span class="tag-tech">
                                                                    <c:out value="${tech}" />
                                                                </span>
                                                            </c:forTokens>
                                                        </div>
                                                    </c:if>

                                                    <c:if test="${not empty p.description}">
                                                        <p
                                                            style="font-size: 0.88rem; color: #334155; margin-top: 6px; line-height: 1.5;">
                                                            <c:out value="${p.description}" />
                                                        </p>
                                                    </c:if>

                                                    <div
                                                        style="display: flex; gap: 14px; margin-top: 10px; font-size: 0.84rem;">
                                                        <c:if test="${not empty p.projectUrl}">
                                                            <a href="<c:out value='${p.projectUrl}'/>" target="_blank"
                                                                rel="noopener noreferrer"
                                                                style="color: var(--accent); text-decoration: none;">&#127760;
                                                                Live URL &rarr;</a>
                                                        </c:if>
                                                        <c:if test="${not empty p.githubUrl}">
                                                            <a href="<c:out value='${p.githubUrl}'/>" target="_blank"
                                                                rel="noopener noreferrer"
                                                                style="color: var(--accent); text-decoration: none;">&#128025;
                                                                Source Code &rarr;</a>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div class="empty-state-icon">&#128187;</div>
                                            <h4>No projects recorded yet</h4>
                                            <p>Freshers: Add at least 1-2 major projects to provide demonstrable
                                                evidence for your declared skills.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 4: EXPERIENCE
             ============================================================= -->
                        <c:if test="${activeTab == 'experience'}">
                            <!-- Form to Add/Edit Experience -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">${not empty editExperience ? 'Edit Work Experience' :
                                            'Add Professional Experience & Internships'}</h3>
                                        <p class="card-desc">Maintained as a separate entity from projects. Demonstrates
                                            skills in professional environments.</p>
                                    </div>
                                    <c:if test="${not empty editExperience}">
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=experience"
                                            class="btn-secondary">&larr; Cancel Edit</a>
                                    </c:if>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_experience">
                                    <input type="hidden" name="experienceId"
                                        value="${editExperience != null ? editExperience.experienceId : 0}">

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="companyName">Company / Organization <span
                                                    class="required">*</span></label>
                                            <input type="text" name="companyName" id="companyName" class="form-input"
                                                placeholder="e.g. Acme Software Inc."
                                                value="${editExperience != null ? editExperience.companyName : ''}"
                                                required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="expRole">Role / Job Title <span
                                                    class="required">*</span></label>
                                            <input type="text" name="role" id="expRole" class="form-input"
                                                placeholder="e.g. Backend Intern, Junior Developer"
                                                value="${editExperience != null ? editExperience.role : ''}" required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="employmentType">Employment Type</label>
                                            <select name="employmentType" id="employmentType" class="form-select">
                                                <option value="Internship" ${empty editExperience ||
                                                    editExperience.employmentType=='Internship' ? 'selected' : '' }>
                                                    Internship</option>
                                                <option value="Full-time" ${editExperience.employmentType=='Full-time'
                                                    ? 'selected' : '' }>Full-time</option>
                                                <option value="Part-time" ${editExperience.employmentType=='Part-time'
                                                    ? 'selected' : '' }>Part-time</option>
                                                <option value="Freelance" ${editExperience.employmentType=='Freelance'
                                                    ? 'selected' : '' }>Freelance</option>
                                                <option value="Contract" ${editExperience.employmentType=='Contract'
                                                    ? 'selected' : '' }>Contract</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="expTechnologiesUsed">Technologies Used</label>
                                        <input type="text" name="technologiesUsed" id="expTechnologiesUsed"
                                            class="form-input"
                                            placeholder="e.g. Java, Spring Boot, MySQL, REST APIs, Git"
                                            value="${editExperience != null ? editExperience.technologiesUsed : ''}">
                                        <span class="form-help">Comma-separated list of tools and technologies used on
                                            the job.</span>
                                    </div>

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="expStartDate">Start Date</label>
                                            <input type="date" name="startDate" id="expStartDate" class="form-input"
                                                value="${editExperience != null ? editExperience.startDate : ''}">
                                        </div>

                                        <div class="form-group" id="endDateGroup">
                                            <label class="form-label" for="expEndDate">End Date</label>
                                            <input type="date" name="endDate" id="expEndDate" class="form-input"
                                                value="${editExperience != null ? editExperience.endDate : ''}">
                                        </div>

                                        <div class="form-group" style="justify-content: flex-end; padding-bottom: 8px;">
                                            <label
                                                style="display: flex; align-items: center; gap: 8px; font-size: 0.88rem; cursor: pointer;">
                                                <input type="checkbox" name="isCurrent" id="isCurrent" value="true"
                                                    ${editExperience !=null && editExperience.current ? 'checked' : '' }
                                                    onchange="toggleEndDate(this.checked)">
                                                <span>I am currently working here</span>
                                            </label>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="expDescription">Responsibilities &amp;
                                            Achievements</label>
                                        <textarea name="description" id="expDescription" class="form-textarea"
                                            placeholder="Outline your deliverables, systems designed, or team responsibilities...">${editExperience != null ? editExperience.description : ''}</textarea>
                                    </div>

                                    <div>
                                        <button type="submit" class="btn-primary">${not empty editExperience ? 'Update
                                            Experience' : '+ Save Experience'}</button>
                                    </div>
                                </form>
                            </div>

                            <!-- List of Saved Experience -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Professional Experience (${experiences.size()})</h3>
                                        <p class="card-desc">Work and internship experience entries.</p>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty experiences}">
                                        <div style="display: flex; flex-direction: column; gap: 16px;">
                                            <c:forEach var="e" items="${experiences}">
                                                <div
                                                    style="border: 1px solid var(--border); border-radius: 6px; padding: 18px; background: #ffffff;">
                                                    <div
                                                        style="display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; margin-bottom: 8px;">
                                                        <div>
                                                            <h4
                                                                style="font-size: 1.05rem; font-weight: 700; color: #1e3a8a;">
                                                                <c:out value="${e.role}" /> &mdash;
                                                                <c:out value="${e.companyName}" />
                                                            </h4>
                                                            <div
                                                                style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">
                                                                <span class="badge badge-info">
                                                                    <c:out value="${e.employmentType}" />
                                                                </span>
                                                                <c:if test="${e.current}"><span
                                                                        class="badge badge-success">Present</span>
                                                                </c:if>
                                                                <c:if test="${not empty e.startDate}">
                                                                    &bull; <span>${e.startDate} to ${e.current ?
                                                                        'Present' : e.endDate}</span>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                        <div style="display: flex; gap: 8px;">
                                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=experience&editExpId=${e.experienceId}"
                                                                class="btn-edit-sm">Edit</a>
                                                            <form
                                                                action="${pageContext.request.contextPath}/resume-analysis"
                                                                method="post" style="display:inline;"
                                                                onsubmit="return confirm('Delete experience at ${e.companyName}?');">
                                                                <input type="hidden" name="action"
                                                                    value="delete_experience">
                                                                <input type="hidden" name="experienceId"
                                                                    value="${e.experienceId}">
                                                                <button type="submit"
                                                                    class="btn-danger-sm">Delete</button>
                                                            </form>
                                                        </div>
                                                    </div>

                                                    <c:if test="${not empty e.technologiesUsed}">
                                                        <div style="margin: 10px 0;">
                                                            <strong
                                                                style="font-size: 0.82rem; color: var(--text-muted); margin-right: 6px;">Technologies:</strong>
                                                            <c:forTokens items="${e.technologiesUsed}" delims=","
                                                                var="tech">
                                                                <span class="tag-tech">
                                                                    <c:out value="${tech}" />
                                                                </span>
                                                            </c:forTokens>
                                                        </div>
                                                    </c:if>

                                                    <c:if test="${not empty e.description}">
                                                        <p
                                                            style="font-size: 0.88rem; color: #334155; margin-top: 6px; line-height: 1.5;">
                                                            <c:out value="${e.description}" />
                                                        </p>
                                                    </c:if>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div class="empty-state-icon">&#128188;</div>
                                            <h4>No experience entries yet</h4>
                                            <p>Freshers: Professional experience is optional. You may add internship
                                                training or leave this section empty.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 5: CERTIFICATIONS
             ============================================================= -->
                        <c:if test="${activeTab == 'certifications'}">
                            <!-- Form to Add/Edit Certification -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">${not empty editCertification ? 'Edit Certification' :
                                            'Add Professional Certification'}</h3>
                                        <p class="card-desc">Formal accreditations demonstrate verified knowledge in
                                            technologies and domains.</p>
                                    </div>
                                    <c:if test="${not empty editCertification}">
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=certifications"
                                            class="btn-secondary">&larr; Cancel Edit</a>
                                    </c:if>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_certification">
                                    <input type="hidden" name="certId"
                                        value="${editCertification != null ? editCertification.certId : 0}">

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="certName">Certification Title <span
                                                    class="required">*</span></label>
                                            <input type="text" name="certName" id="certName" class="form-input"
                                                placeholder="e.g. AWS Certified Solutions Architect"
                                                value="${editCertification != null ? editCertification.certName : ''}"
                                                required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="provider">Issuing Provider /
                                                Organization</label>
                                            <input type="text" name="provider" id="provider" class="form-input"
                                                placeholder="e.g. Amazon Web Services, Oracle, Google"
                                                value="${editCertification != null ? editCertification.provider : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="certTechnology">Relevant Technology /
                                                Domain</label>
                                            <input type="text" name="technology" id="certTechnology" class="form-input"
                                                placeholder="e.g. Cloud Architecture, Java SE"
                                                value="${editCertification != null ? editCertification.technology : ''}">
                                        </div>
                                    </div>

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="certIdNumber">License / Credential ID</label>
                                            <input type="text" name="certIdNumber" id="certIdNumber" class="form-input"
                                                placeholder="e.g. AWS-12345678"
                                                value="${editCertification != null ? editCertification.certIdNumber : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="issueDate">Issue Date</label>
                                            <input type="date" name="issueDate" id="issueDate" class="form-input"
                                                value="${editCertification != null ? editCertification.issueDate : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="expiryDate">Expiry Date (Optional)</label>
                                            <input type="date" name="expiryDate" id="expiryDate" class="form-input"
                                                value="${editCertification != null ? editCertification.expiryDate : ''}">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label" for="credentialUrl">Verification / Credential
                                            URL</label>
                                        <input type="url" name="credentialUrl" id="credentialUrl" class="form-input"
                                            placeholder="https://www.credly.com/badges/..."
                                            value="${editCertification != null ? editCertification.credentialUrl : ''}">
                                    </div>

                                    <div>
                                        <button type="submit" class="btn-primary">${not empty editCertification ?
                                            'Update Certification' : '+ Save Certification'}</button>
                                    </div>
                                </form>
                            </div>

                            <!-- List of Saved Certifications -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Earned Certifications (${certifications.size()})</h3>
                                        <p class="card-desc">Accredited certifications tagged to your profile.</p>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty certifications}">
                                        <table class="data-table">
                                            <thead>
                                                <tr>
                                                    <th>Certification</th>
                                                    <th>Provider</th>
                                                    <th>Technology</th>
                                                    <th>Issue Date</th>
                                                    <th style="width: 140px; text-align: center;">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="c" items="${certifications}">
                                                    <tr>
                                                        <td>
                                                            <strong>
                                                                <c:out value="${c.certName}" />
                                                            </strong>
                                                            <c:if test="${not empty c.credentialUrl}">
                                                                <div style="font-size: 0.8rem; margin-top: 2px;">
                                                                    <a href="<c:out value='${c.credentialUrl}'/>"
                                                                        target="_blank" rel="noopener noreferrer"
                                                                        style="color: var(--accent); text-decoration: none;">View
                                                                        Credential &rarr;</a>
                                                                </div>
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <c:out value="${c.provider}" />
                                                        </td>
                                                        <td><span class="badge badge-info">
                                                                <c:out value="${c.technology}" />
                                                            </span></td>
                                                        <td>${c.issueDate}</td>
                                                        <td style="text-align: center;">
                                                            <div style="display: inline-flex; gap: 8px;">
                                                                <a href="${pageContext.request.contextPath}/resume-analysis?tab=certifications&editCertId=${c.certId}"
                                                                    class="btn-edit-sm">Edit</a>
                                                                <form
                                                                    action="${pageContext.request.contextPath}/resume-analysis"
                                                                    method="post" style="display:inline;"
                                                                    onsubmit="return confirm('Remove certification: ${c.certName}?');">
                                                                    <input type="hidden" name="action"
                                                                        value="delete_certification">
                                                                    <input type="hidden" name="certId"
                                                                        value="${c.certId}">
                                                                    <button type="submit"
                                                                        class="btn-danger-sm">Delete</button>
                                                                </form>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div class="empty-state-icon">&#127942;</div>
                                            <h4>No certifications added yet</h4>
                                            <p>Certifications are optional. Add any relevant credentials to strengthen
                                                your profile verification.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 6: EDUCATION
             ============================================================= -->
                        <c:if test="${activeTab == 'education'}">
                            <!-- Form to Add/Edit Education -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">${not empty editEducation ? 'Edit Education
                                            Qualification' : 'Add Education Record'}</h3>
                                        <p class="card-desc">Academic background provides foundational context for role
                                            suitability.</p>
                                    </div>
                                    <c:if test="${not empty editEducation}">
                                        <a href="${pageContext.request.contextPath}/resume-analysis?tab=education"
                                            class="btn-secondary">&larr; Cancel Edit</a>
                                    </c:if>
                                </div>

                                <form action="${pageContext.request.contextPath}/resume-analysis" method="post">
                                    <input type="hidden" name="action" value="save_education">

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="eduLevel">Qualification Level <span
                                                    class="required">*</span></label>
                                            <select name="level" id="eduLevel" class="form-select" required>
                                                <option value="Graduation" ${empty editEducation ||
                                                    editEducation.level=='Graduation' ? 'selected' : '' }>Graduation
                                                    (B.Tech / B.E. / BCA / B.Sc)</option>
                                                <option value="Master" ${editEducation.level=='Master' ? 'selected' : ''
                                                    }>Master (M.Tech / MCA / M.Sc)</option>
                                                <option value="Diploma" ${editEducation.level=='Diploma' ? 'selected'
                                                    : '' }>Diploma</option>
                                                <option value="HSC" ${editEducation.level=='HSC' ? 'selected' : '' }>HSC
                                                    (12th Grade)</option>
                                                <option value="SSC" ${editEducation.level=='SSC' ? 'selected' : '' }>SSC
                                                    (10th Grade)</option>
                                            </select>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="schoolCollegeName">School / College Name
                                                <span class="required">*</span></label>
                                            <input type="text" name="schoolCollegeName" id="schoolCollegeName"
                                                class="form-input"
                                                placeholder="e.g. Vishwakarma Institute of Technology"
                                                value="${editEducation != null ? editEducation.schoolCollegeName : ''}"
                                                required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="boardUniversity">Board / University</label>
                                            <input type="text" name="boardUniversity" id="boardUniversity"
                                                class="form-input" placeholder="e.g. Pune University / CBSE"
                                                value="${editEducation != null ? editEducation.boardUniversity : ''}">
                                        </div>
                                    </div>

                                    <div class="form-grid-3">
                                        <div class="form-group">
                                            <label class="form-label" for="stream">Stream / Branch /
                                                Specialization</label>
                                            <input type="text" name="stream" id="stream" class="form-input"
                                                placeholder="e.g. Computer Science, Information Technology"
                                                value="${editEducation != null ? editEducation.stream : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="passingYear">Passing Year</label>
                                            <input type="number" name="passingYear" id="passingYear" class="form-input"
                                                placeholder="e.g. 2025" min="1980" max="2035"
                                                value="${editEducation != null && editEducation.passingYear > 0 ? editEducation.passingYear : ''}">
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label" for="percentage">CGPA</label>
                                            <input type="number" step="0.01" name="percentage" id="percentage"
                                                class="form-input" placeholder="e.g. 8.5" min="0" max="10"
                                                value="${editEducation != null && editEducation.percentage > 0 ? editEducation.percentage : ''}">
                                        </div>
                                    </div>

                                    <div>
                                        <button type="submit" class="btn-primary">${not empty editEducation ? 'Update
                                            Education' : '+ Save Education'}</button>
                                    </div>
                                </form>
                            </div>

                            <!-- List of Saved Education Details -->
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Academic Qualifications (${educations.size()})</h3>
                                        <p class="card-desc">Recorded education levels.</p>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty educations}">
                                        <table class="data-table">
                                            <thead>
                                                <tr>
                                                    <th>Level</th>
                                                    <th>Institution</th>
                                                    <th>Board / University</th>
                                                    <th>Stream</th>
                                                    <th>Year</th>
                                                    <th>Score</th>
                                                    <th style="width: 140px; text-align: center;">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="edu" items="${educations}">
                                                    <tr>
                                                        <td><span class="badge badge-primary">
                                                                <c:out value="${edu.level}" />
                                                            </span></td>
                                                        <td><strong>
                                                                <c:out value="${edu.schoolCollegeName}" />
                                                            </strong></td>
                                                        <td>
                                                            <c:out value="${edu.boardUniversity}" />
                                                        </td>
                                                        <td>
                                                            <c:out value="${edu.stream}" />
                                                        </td>
                                                        <td>${edu.passingYear > 0 ? edu.passingYear : '-'}</td>
                                                        <td>${edu.percentage > 0 ? edu.percentage : '-'}%</td>
                                                        <td style="text-align: center;">
                                                            <div style="display: inline-flex; gap: 8px;">
                                                                <a href="${pageContext.request.contextPath}/resume-analysis?tab=education&editEduId=${edu.eduId}"
                                                                    class="btn-edit-sm">Edit</a>
                                                                <form
                                                                    action="${pageContext.request.contextPath}/resume-analysis"
                                                                    method="post" style="display:inline;"
                                                                    onsubmit="return confirm('Delete ${edu.level} record?');">
                                                                    <input type="hidden" name="action"
                                                                        value="delete_education">
                                                                    <input type="hidden" name="eduId"
                                                                        value="${edu.eduId}">
                                                                    <button type="submit"
                                                                        class="btn-danger-sm">Delete</button>
                                                                </form>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div class="empty-state-icon">&#127891;</div>
                                            <h4>No education records saved yet</h4>
                                            <p>Add at least your ongoing or highest qualification.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- =============================================================
             TAB 7: ANALYZE MY PROFILE (PHASE 2 ENTRY POINT)
             ============================================================= -->
                        <c:if test="${activeTab == 'analyze'}">
                            <div class="card">
                                <div class="card-header-bar">
                                    <div>
                                        <h3 class="card-title">Profile Analysis Readiness Evaluation</h3>
                                        <p class="card-desc">Entry point for the AI Skill-Gap Analysis Engine. Verifies
                                            data completeness before analysis.</p>
                                    </div>
                                    <span
                                        class="badge ${readiness.readyForAnalysis ? 'badge-success' : 'badge-warning'}"
                                        style="font-size: 0.95rem; padding: 6px 14px;">
                                        ${readiness.readinessScore}% Profile Complete
                                    </span>
                                </div>

                                <!-- Readiness Progress Bar -->
                                <div class="readiness-meter">
                                    <div class="readiness-fill"
                                        style="width: ${readiness.readinessScore}%; background: ${readiness.readyForAnalysis ? '#16a34a' : '#d97706'};">
                                    </div>
                                </div>

                                <!-- Status Banner -->
                                <c:choose>
                                    <c:when test="${readiness.readyForAnalysis}">
                                        <div
                                            style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 6px; padding: 18px; margin: 18px 0; color: #166534;">
                                            <div
                                                style="display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 1.05rem;">
                                                <span>&#10003;</span> Profile Structure Ready for Intelligent Analysis
                                            </div>
                                            <p
                                                style="font-size: 0.9rem; margin-top: 6px; color: #15803d; line-height: 1.5;">
                                                Your profile contains all essential entities (Career Goal:
                                                <strong>${careerGoal.effectiveTargetRole}</strong>, ${skills.size()}
                                                declared skills, and academic education records).
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div
                                            style="background: #fffbeb; border: 1px solid #fde68a; border-radius: 6px; padding: 18px; margin: 18px 0; color: #92400e;">
                                            <div
                                                style="display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 1.05rem;">
                                                <span>&#9888;</span> Complete Recommended Sections Before Running
                                                Analysis
                                            </div>
                                            <p
                                                style="font-size: 0.9rem; margin-top: 6px; color: #b45309; line-height: 1.5;">
                                                Some core profile information is missing. To generate personalized,
                                                contextual recommendations, please fill in the missing items listed
                                                below.
                                            </p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <!-- Section Checklist -->
                                <h4
                                    style="font-size: 1rem; font-weight: 700; color: #0f172a; margin-top: 20px; margin-bottom: 12px;">
                                    Profile Evidence Breakdown</h4>
                                <div class="readiness-checklist">
                                    <!-- Career Goal -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.hasCareerGoal ? 'pass' : 'miss'}">${readiness.hasCareerGoal
                                            ? '&#10003;' : '&#10007;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Career Goal:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.hasCareerGoal}">
                                                    Targeting <em>${careerGoal.effectiveTargetRole}</em> (
                                                    <c:out value="${careerGoal.effectivePreferredDomain}" />)
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #b45309;">Not specified &mdash; Needed to
                                                        evaluate skill alignment.</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${!readiness.hasCareerGoal}">
                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=career"
                                                class="btn-edit-sm">Set Goal &rarr;</a>
                                        </c:if>
                                    </div>

                                    <!-- Skills -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.skillCount > 0 ? 'pass' : 'miss'}">${readiness.skillCount
                                            > 0 ? '&#10003;' : '&#10007;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Declared Skills:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.skillCount > 0}">
                                                    ${readiness.skillCount} skills cataloged across categories
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #b45309;">No skills declared &mdash; Needed to
                                                        detect skill proficiencies.</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${readiness.skillCount == 0}">
                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=skills"
                                                class="btn-edit-sm">Add Skills &rarr;</a>
                                        </c:if>
                                    </div>

                                    <!-- Projects -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.projectCount > 0 ? 'pass' : 'miss'}">${readiness.projectCount
                                            > 0 ? '&#10003;' : '&#9675;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Project Evidence:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.projectCount > 0}">
                                                    ${readiness.projectCount} project(s) serving as empirical skill
                                                    evidence
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #64748b;">0 projects (Optional, but strongly
                                                        recommended to prove declared skills)</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${readiness.projectCount == 0}">
                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=projects"
                                                class="btn-edit-sm">Add Project &rarr;</a>
                                        </c:if>
                                    </div>

                                    <!-- Experience -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.experienceCount > 0 ? 'pass' : 'miss'}">${readiness.experienceCount
                                            > 0 ? '&#10003;' : '&#9675;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Work Experience:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.experienceCount > 0}">
                                                    ${readiness.experienceCount} professional experience entry/entries
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #64748b;">0 entries (Optional for
                                                        freshers)</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${readiness.experienceCount == 0}">
                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=experience"
                                                class="btn-edit-sm">+ Experience</a>
                                        </c:if>
                                    </div>

                                    <!-- Certifications -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.certificationCount > 0 ? 'pass' : 'miss'}">${readiness.certificationCount
                                            > 0 ? '&#10003;' : '&#9675;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Certifications:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.certificationCount > 0}">
                                                    ${readiness.certificationCount} verified credential(s)
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #64748b;">0 credentials (Optional)</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <!-- Education -->
                                    <div class="checklist-item">
                                        <span
                                            class="checklist-icon ${readiness.educationCount > 0 ? 'pass' : 'miss'}">${readiness.educationCount
                                            > 0 ? '&#10003;' : '&#10007;'}</span>
                                        <div style="flex: 1;">
                                            <strong>Academic Education:</strong>
                                            <c:choose>
                                                <c:when test="${readiness.educationCount > 0}">
                                                    ${readiness.educationCount} qualification record(s)
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #b45309;">No education added &mdash; Needed for
                                                        academic context.</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:if test="${readiness.educationCount == 0}">
                                            <a href="${pageContext.request.contextPath}/resume-analysis?tab=education"
                                                class="btn-edit-sm">Add Education &rarr;</a>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- AI Resume Analysis Trigger & Production Engine -->
                                <div style="margin-top: 28px; padding: 26px; background: #ffffff; border: 1px solid var(--border); border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.03);">
                                    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; margin-bottom: 18px;">
                                        <div>
                                            <h4 style="font-size: 1.15rem; font-weight: 800; color: #0f172a; margin-bottom: 6px; display: flex; align-items: center; gap: 8px;">
                                                <span>&#129302;</span> AI Resume &amp; Skill-Gap Intelligence Engine
                                            </h4>
                                            <p style="font-size: 0.9rem; color: var(--text-muted); max-width: 680px;">
                                                Evaluates your declared skills against empirical project and experience proof, benchmarks your profile against current 2025-2026 tech market demands for <strong>${not empty careerGoal.targetRole ? careerGoal.effectiveTargetRole : 'your target career'}</strong>, identifies critical missing skills, and synthesizes tailored portfolio recommendations.
                                            </p>
                                        </div>
                                        <div>
                                            <span class="badge badge-success" style="padding: 6px 14px; font-size: 0.82rem; letter-spacing: 0.02em;">
                                                &#10003; AI Engine Online &bull; Ready
                                            </span>
                                        </div>
                                    </div>

                                    <!-- Primary Analysis Action Trigger -->
                                    <c:choose>
                                        <c:when test="${readiness.readyForAnalysis}">
                                            <form action="${pageContext.request.contextPath}/resume-analysis" method="post" onsubmit="showAiLoading()">
                                                <input type="hidden" name="action" value="run_ai_analysis">
                                                <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap;">
                                                    <button type="submit" class="btn-run-ai" id="runAiBtn">
                                                        <span>&#128640;</span> ${not empty analysis ? 'Re-Analyze My Profile &amp; Skills' : 'Analyze My Resume &amp; Skills'}
                                                    </button>
                                                    <span style="font-size: 0.85rem; color: #64748b;">
                                                        Powered by Groq Llama 3.3 70B &amp; Market Intelligence Engine
                                                    </span>
                                                </div>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <div style="background: #fffbeb; border: 1px solid #fde68a; border-radius: 8px; padding: 14px 18px; color: #92400e; font-size: 0.88rem;">
                                                <strong>Cannot run analysis yet:</strong> Complete required core sections (Career Goal, Skills, and Education) shown above to unlock AI evaluation.
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <!-- =========================================================
                                     AI ANALYSIS RESULTS DASHBOARD (PRESENTATION)
                                     ========================================================= -->
                                <c:if test="${not empty analysis}">
                                    <div style="margin-top: 36px;">
                                        <!-- Section Anchor Title -->
                                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 10px;">
                                            <div>
                                                <h3 style="font-size: 1.35rem; font-weight: 800; color: #0f172a; display: flex; align-items: center; gap: 8px;">
                                                    <span>&#9733;</span> AI Profile &amp; Skill-Gap Intelligence Report
                                                </h3>
                                                <p style="font-size: 0.88rem; color: var(--text-muted);">
                                                    Benchmarked for <strong>${analysis.targetRole}</strong> &bull; Generated <fmt:formatDate value="${analysis.createdAt}" pattern="dd MMM yyyy, hh:mm a" />
                                                </p>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/resume-analysis" method="post" onsubmit="showAiLoading()">
                                                <input type="hidden" name="action" value="run_ai_analysis">
                                                <button type="submit" class="btn-secondary" style="font-size: 0.85rem; padding: 6px 14px; display: inline-flex; align-items: center; gap: 6px;">
                                                    <span>&#8635;</span> Refresh Analysis
                                                </button>
                                            </form>
                                        </div>

                                        <!-- Hero Metrics Card -->
                                        <div class="ai-hero-card">
                                            <div class="ai-hero-header">
                                                <div>
                                                    <span class="ai-hero-badge">&#9679; Target Career Track: ${analysis.targetRole}</span>
                                                    <h3 style="font-size: 1.45rem; font-weight: 800; margin-top: 10px; color: #ffffff;">
                                                        Profile Suitability &amp; Market Readiness Score
                                                    </h3>
                                                </div>
                                                <div style="font-size: 0.8rem; color: #94a3b8; text-align: right;">
                                                    Engine: <span style="color: #cbd5e1; font-weight: 600;">${analysis.modelUsed}</span>
                                                </div>
                                            </div>

                                            <div class="ai-hero-scores">
                                                <div class="ai-score-box">
                                                    <span class="ai-score-title">Overall Readiness</span>
                                                    <span class="ai-score-num highlight">${analysis.overallScore}%</span>
                                                    <small style="color: #94a3b8; font-size: 0.78rem;">Profile strength vs industry expectations</small>
                                                </div>
                                                <div class="ai-score-box">
                                                    <span class="ai-score-title">Market Demand Fit</span>
                                                    <span class="ai-score-num market">${analysis.marketFitScore}%</span>
                                                    <small style="color: #94a3b8; font-size: 0.78rem;">Alignment with current 2025 hiring demand</small>
                                                </div>
                                                <div class="ai-score-box">
                                                    <span class="ai-score-title">Declared Skills</span>
                                                    <span class="ai-score-num">${skills.size()}</span>
                                                    <small style="color: #94a3b8; font-size: 0.78rem;">${analysis.matchingSkills.size()} validated market matches</small>
                                                </div>
                                                <div class="ai-score-box">
                                                    <span class="ai-score-title">Critical Missing Skills</span>
                                                    <span class="ai-score-num" style="color: #f87171;">${analysis.missingRequiredSkills.size()}</span>
                                                    <small style="color: #94a3b8; font-size: 0.78rem;">Priority competencies to bridge</small>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Executive Summary & Standout Strengths -->
                                        <div class="ai-summary-card">
                                            <h4 style="font-size: 1.05rem; font-weight: 700; color: #0f172a; margin-bottom: 6px;">
                                                Executive Recruiter Summary
                                            </h4>
                                            <div class="ai-summary-text">
                                                ${analysis.roleFitSummary}
                                            </div>

                                            <c:if test="${not empty analysis.topStrengths}">
                                                <div style="margin-top: 18px; padding-top: 16px; border-top: 1px solid #f1f5f9;">
                                                    <strong style="font-size: 0.88rem; color: #0f172a; display: block; margin-bottom: 8px;">
                                                        &#10004; Verified Standout Strengths:
                                                    </strong>
                                                    <ul style="padding-left: 20px; font-size: 0.88rem; color: #475569; display: flex; flex-direction: column; gap: 6px;">
                                                        <c:forEach var="str" items="${analysis.topStrengths}">
                                                            <li>${str}</li>
                                                        </c:forEach>
                                                    </ul>
                                                </div>
                                            </c:if>
                                        </div>

                                        <!-- 3-Column Skills Intelligence Grid -->
                                        <h4 style="font-size: 1.12rem; font-weight: 800; color: #0f172a; margin-bottom: 14px; display: flex; align-items: center; gap: 8px;">
                                            <span>&#9878;</span> In-Depth Skills Market Alignment &amp; Recommendations
                                        </h4>
                                        <div class="ai-grid-3">
                                            <!-- Column 1: Matching / Possessed Skills -->
                                            <div class="ai-col-card">
                                                <div class="ai-col-header matching">
                                                    <span>&#10003; Skills You Have (Market-Aligned)</span>
                                                    <span class="badge badge-match">${analysis.matchingSkills.size()} Verified</span>
                                                </div>
                                                <div class="ai-col-body">
                                                    <c:choose>
                                                        <c:when test="${not empty analysis.matchingSkills}">
                                                            <c:forEach var="ms" items="${analysis.matchingSkills}">
                                                                <div class="skill-chip-item matching-item">
                                                                    <div class="skill-chip-title">
                                                                        <span>${ms.name}</span>
                                                                        <span class="badge-match">${ms.matchLevel}</span>
                                                                    </div>
                                                                    <div class="skill-chip-desc">${ms.comment}</div>
                                                                </div>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div style="font-size: 0.85rem; color: #64748b; text-align: center; padding: 20px 0;">
                                                                No direct market-aligned skills cataloged. Add technical skills in Tab 2.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <!-- Column 2: Missing Required Skills -->
                                            <div class="ai-col-card">
                                                <div class="ai-col-header missing">
                                                    <span>&#9888; Missing Required Skills for ${analysis.targetRole}</span>
                                                    <span class="badge-crit">${analysis.missingRequiredSkills.size()} Critical Gaps</span>
                                                </div>
                                                <div class="ai-col-body">
                                                    <c:choose>
                                                        <c:when test="${not empty analysis.missingRequiredSkills}">
                                                            <c:forEach var="mis" items="${analysis.missingRequiredSkills}">
                                                                <div class="skill-chip-item missing-item">
                                                                    <div class="skill-chip-title">
                                                                        <span>${mis.name}</span>
                                                                        <span class="${mis.importance == 'Critical' ? 'badge-crit' : 'badge-high'}">${mis.importance}</span>
                                                                    </div>
                                                                    <div class="skill-chip-desc">${mis.reason}</div>
                                                                    <div class="skill-chip-action">&#8594; Action: ${mis.recommendedAction}</div>
                                                                </div>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div style="font-size: 0.85rem; color: #166534; text-align: center; padding: 20px 0;">
                                                                &#10003; Outstanding! No critical core skills are missing for your target role.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <!-- Column 3: Recommended Good & Trending Skills -->
                                            <div class="ai-col-card">
                                                <div class="ai-col-header trending">
                                                    <span>&#10024; Recommended Modern &amp; Trending Skills</span>
                                                    <span class="badge-trend">${analysis.recommendedTrendingSkills.size()} Competitive</span>
                                                </div>
                                                <div class="ai-col-body">
                                                    <c:choose>
                                                        <c:when test="${not empty analysis.recommendedTrendingSkills}">
                                                            <c:forEach var="ts" items="${analysis.recommendedTrendingSkills}">
                                                                <div class="skill-chip-item trending-item">
                                                                    <div class="skill-chip-title">
                                                                        <span>${ts.name}</span>
                                                                        <span class="badge-trend">${ts.priority}</span>
                                                                    </div>
                                                                    <div class="skill-chip-desc">${ts.trendingReason}</div>
                                                                </div>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div style="font-size: 0.85rem; color: #64748b; text-align: center; padding: 20px 0;">
                                                                No additional trending skills suggested at this time.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Empirical Portfolio Project Recommendations -->
                                        <c:if test="${not empty analysis.projectSuggestions}">
                                            <div style="margin-top: 32px;">
                                                <h4 style="font-size: 1.12rem; font-weight: 800; color: #0f172a; margin-bottom: 6px; display: flex; align-items: center; gap: 8px;">
                                                    <span>&#128187;</span> Recommended Portfolio Projects to Prove Missing Skills
                                                </h4>
                                                <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 12px;">
                                                    Building these concrete projects will provide verifiable empirical proof of the missing required skills and elevate your GitHub portfolio.
                                                </p>
                                                <div class="project-intel-grid">
                                                    <c:forEach var="proj" items="${analysis.projectSuggestions}">
                                                        <div class="project-intel-card">
                                                            <div class="project-intel-title">${proj.title}</div>
                                                            <div style="font-size: 0.88rem; color: #475569; line-height: 1.5;">${proj.description}</div>
                                                            <div>
                                                                <span class="project-intel-tech">&#128736; Stack: ${proj.techStack}</span>
                                                            </div>
                                                            <div class="project-intel-impact">
                                                                <strong>Resume Impact:</strong> ${proj.resumeImpact}
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- ATS & Resume Bullet Point Improvements -->
                                        <c:if test="${not empty analysis.resumeBulletImprovements}">
                                            <div style="margin-top: 32px;">
                                                <h4 style="font-size: 1.12rem; font-weight: 800; color: #0f172a; margin-bottom: 6px; display: flex; align-items: center; gap: 8px;">
                                                    <span>&#128221;</span> ATS Resume Optimization &amp; Bullet Improvements
                                                </h4>
                                                <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 12px;">
                                                    Recruiters and automated ATS parsers filter resumes based on quantifiable metrics. Use these Google XYZ improvements:
                                                </p>
                                                <c:forEach var="tip" items="${analysis.resumeBulletImprovements}">
                                                    <div class="bullet-intel-card">
                                                        <div class="bullet-intel-issue">&#9888; <strong>Issue in ${tip.section}:</strong> ${tip.issue}</div>
                                                        <div class="bullet-intel-sol">&#10004; <strong>Recommended High-Impact Formula:</strong> ${tip.recommendedImprovement}</div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:if>

                                        <!-- 30-Day Step-by-Step Action Roadmap -->
                                        <c:if test="${not empty analysis.actionPlan}">
                                            <div style="margin-top: 32px;">
                                                <h4 style="font-size: 1.12rem; font-weight: 800; color: #0f172a; margin-bottom: 6px; display: flex; align-items: center; gap: 8px;">
                                                    <span>&#128197;</span> 30-Day Step-by-Step Interview Readiness Roadmap
                                                </h4>
                                                <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 12px;">
                                                    A phased timeline to systematically close your skill gaps and become interview-ready for <strong>${analysis.targetRole}</strong>.
                                                </p>
                                                <div class="roadmap-strip">
                                                    <c:forEach var="milestone" items="${analysis.actionPlan}">
                                                        <div class="roadmap-box">
                                                            <div class="roadmap-period">${milestone.dayRange}</div>
                                                            <div class="roadmap-focus">${milestone.focus}</div>
                                                            <ul class="roadmap-task-list">
                                                                <c:forEach var="t" items="${milestone.tasks}">
                                                                    <li>${t}</li>
                                                                </c:forEach>
                                                            </ul>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:if>

                                    </div>
                                </c:if>
                            </div>
                        </c:if>

                    </main>

                    <!-- AI Loading Spinner Overlay -->
                    <div class="ai-overlay-spinner" id="aiOverlaySpinner">
                        <div class="spinner"></div>
                        <div style="font-size: 1.25rem; font-weight: 800; text-shadow: 0 2px 4px rgba(0,0,0,0.5);">Evaluating Profile with Groq AI...</div>
                        <div style="font-size: 0.92rem; color: #cbd5e1; max-width: 500px; text-align: center; line-height: 1.5;">
                            Comparing your declared skills, projects, and credentials against current 2025-2026 tech market demands.
                        </div>
                    </div>

                    <script>
                        function showAiLoading() {
                            var spinner = document.getElementById('aiOverlaySpinner');
                            if (spinner) spinner.style.display = 'flex';
                            var btn = document.getElementById('runAiBtn');
                            if (btn) {
                                btn.disabled = true;
                                btn.innerHTML = '<span>&#8987;</span> Analyzing Profile...';
                            }
                        }

                        function toggleRoleOther(val) {
                            var group = document.getElementById('targetRoleOtherGroup');
                            var input = document.getElementById('targetRoleOther');
                            if (val === 'Other') {
                                group.style.display = 'flex';
                                input.required = true;
                            } else {
                                group.style.display = 'none';
                                input.required = false;
                            }
                        }

                        function toggleDomainOther(val) {
                            var group = document.getElementById('preferredDomainOtherGroup');
                            var input = document.getElementById('preferredDomainOther');
                            if (val === 'Other') {
                                group.style.display = 'flex';
                            } else {
                                group.style.display = 'none';
                            }
                        }

                        function toggleEndDate(isCurrent) {
                            var endGroup = document.getElementById('endDateGroup');
                            var endInput = document.getElementById('expEndDate');
                            if (isCurrent) {
                                endInput.value = '';
                                endInput.disabled = true;
                                endGroup.style.opacity = '0.5';
                            } else {
                                endInput.disabled = false;
                                endGroup.style.opacity = '1.0';
                            }
                        }

                        // Initialize state on load
                        window.addEventListener('DOMContentLoaded', function () {
                            var curCheck = document.getElementById('isCurrent');
                            if (curCheck && curCheck.checked) {
                                toggleEndDate(true);
                            }
                        });
                    </script>
                </body>

                </html>
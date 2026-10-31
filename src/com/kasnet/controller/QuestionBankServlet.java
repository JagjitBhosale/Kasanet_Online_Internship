package com.kasnet.controller;

import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * QuestionBankServlet - Handles Subject Question Bank browsing and subject redirects.
 */
public class QuestionBankServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        String subject = req.getParameter("subject");

        if (subject != null && !subject.trim().isEmpty()) {
            subject = subject.trim().toLowerCase();
            String title = "Subject Question Bank";
            String description = "Curated technical interview and practical questions.";
            List<String> modules = Arrays.asList("Module 1: Fundamental Concepts", "Module 2: Advanced Topics", "Module 3: Code Walkthroughs");

            switch (subject) {
                case "java":
                    title = "Java Full Stack Development";
                    description = "Core Java, OOPs, Collections, Multithreading, Servlets, JSP, JDBC, and Design Patterns.";
                    modules = Arrays.asList(
                            "Core Java Fundamentals & OOP Principles",
                            "Java Collections Framework & Generics",
                            "Multithreading, Concurrency & Exception Handling",
                            "Java Servlets, JSP & MVC Web Architecture",
                            "JDBC, Connection Pooling & MySQL Transactions"
                    );
                    break;
                case "python":
                    title = "Python & Machine Learning";
                    description = "Data structures, OOPs, NumPy, Pandas, Data Preprocessing, and Scikit-Learn fundamentals.";
                    modules = Arrays.asList(
                            "Python Syntax, Control Flow & Data Structures",
                            "Object Oriented Programming & Functional Patterns",
                            "NumPy Array Operations & Mathematical Computing",
                            "Pandas Data Wrangling & Exploratory Data Analysis",
                            "Supervised & Unsupervised Machine Learning Models"
                    );
                    break;
                case "web":
                    title = "Web Development & Frontend";
                    description = "Semantic HTML5, Responsive CSS3, JavaScript ES6+, DOM Manipulation, and API Consumption.";
                    modules = Arrays.asList(
                            "Semantic HTML5 & Accessibility (a11y)",
                            "CSS3 Flexbox, Grid & Responsive Layouts",
                            "JavaScript ES6+, Promises & Async/Await",
                            "DOM Manipulation, Browser Events & Storage",
                            "RESTful API Integration & Fetch Patterns"
                    );
                    break;
                case "db":
                    title = "Database Management Systems (MySQL)";
                    description = "Relational Modeling, Normalization, Complex SQL Queries, Joins, Indexes, and ACID Transactions.";
                    modules = Arrays.asList(
                            "Relational Database Schema Design & Normalization (1NF - BCNF)",
                            "SQL Queries: Aggregations, Subqueries & Window Functions",
                            "Inner, Outer & Self Joins Optimization",
                            "Indexes, Query Execution Plans & Performance Tuning",
                            "ACID Properties, Transactions & Locking Mechanisms"
                    );
                    break;
                case "cloud":
                    title = "Cloud Computing & DevOps";
                    description = "Virtualization, Cloud Storage, Docker Containers, CI/CD pipelines, and Deployment architectures.";
                    modules = Arrays.asList(
                            "Cloud Computing Models (IaaS, PaaS, SaaS) & Architecture",
                            "Compute Instances, VPC Networking & Security Groups",
                            "Object Storage, Block Storage & Database Services",
                            "Docker Containerization & Dockerfile Best Practices",
                            "CI/CD Pipelines, Version Control & Automated Releases"
                    );
                    break;
                default:
                    title = "Technical Question Bank";
                    break;
            }

            req.setAttribute("selectedSubject", subject);
            req.setAttribute("subjectTitle", title);
            req.setAttribute("subjectDescription", description);
            req.setAttribute("modules", modules);
        }

        req.getRequestDispatcher("/views/question-bank.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

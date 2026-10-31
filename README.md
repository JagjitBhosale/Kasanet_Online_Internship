# My Internship Technologies Portal

> **Production-Ready Java EE (MVC) Web Application Module**  
> Designed for seamless integration into the hosted platform at [onlineinternship.in](https://www.onlineinternship.in/).  
> Configured with Indian Standard Time (IST / Asia/Kolkata, UTC+05:30), strict input validations, secure BCrypt authentication, and a clean professional UI.

---

## Table of Contents
1. [Tech Stack](#tech-stack)
2. [Prerequisites](#prerequisites)
3. [Quick Setup Guide (Run in 3 Minutes)](#quick-setup-guide-run-in-3-minutes)
4. [Pre-configured Test Credentials](#pre-configured-test-credentials)
5. [Automated Verification Test Suite](#automated-verification-test-suite)
6. [Alternative: Deploying to Standalone Tomcat](#alternative-deploying-to-standalone-tomcat)
7. [Application Sitemap & Routes](#application-sitemap--routes)
8. [Architecture & Project Structure](#architecture--project-structure)
9. [Indian Standard Time (IST) Configuration](#indian-standard-time-ist-configuration)
10. [Troubleshooting Guide](#troubleshooting-guide)
11. [Continuation & Documentation](#continuation--documentation)

---

## Tech Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 8 (JDK 1.8) | Compiled for Java 8 (JDK 1.8) bytecode; compatible with Java 8+ |
| **Framework** | Java Servlets 3.1 & JSP 2.3 | Standard Java EE (`javax.servlet`) specification |
| **Architecture** | MVC Pattern | Strict Model-View-Controller separation |
| **Database** | MySQL 5.7+ / 8.0+ | Compatible with MySQL Workbench 6.3+ (InnoDB, UTF-8) |
| **Persistence** | JDBC with `PreparedStatement` | SQL-injection safe, clean resource closing |
| **Security** | BCrypt (`jbcrypt:0.4`) | Salt rounds = 12, timing-attack safe |
| **Styling** | Per-Page Inline CSS3 & Clean SVGs | **Zero external CSS framework lock-in**, 100% responsive, zero cartoon emojis |
| **Project Format** | Eclipse Dynamic Web Project | Native Java EE DWP (Dynamic Web Module 3.1, Java 8, Tomcat 8.0/8.5), no Maven required |
| **Timezone** | Indian Standard Time (IST) | `Asia/Kolkata` (UTC+05:30) enforced end-to-end |

---

## Prerequisites

Before setting up the project, make sure your device has:
1. **Java Development Kit (JDK 11 or higher)**
   - Check version: `java -version`
2. **MySQL Server 5.7 or 8.0** (running locally on port `3306`)
   - Check status: `mysql --version`
3. **Git** (optional, to clone repository)

---

## Quick Setup Guide (Run in 3 Minutes)

### Step 1: Clone / Open Project Directory
```powershell
# Open terminal in the project folder:
cd "d:\My Workspace\Kasnet_Online_Internship"
```

### Step 2: Initialize MySQL Database
For a brand-new installation, open your MySQL client (Command Line or MySQL Workbench) and run `database.sql`. The schema already includes the assignment instruction columns used by the application.

`database.sql` drops and recreates the tables. Do not run it against a database with data you need to keep.

```powershell
# Using MySQL Command Line:
mysql -u root -p < database.sql
```
*Or open `database.sql` in MySQL Workbench, select all (`Ctrl+A`), and click the Execute lightning button.*

This script automatically creates:
- The database: `online_internship`
- All 15 relational tables with foreign keys and indexes
- Seeded curriculum modules, sample assignments, and sample lectures.

### Step 3: Configure Environment Variables (.env)
Credentials are never hardcoded in the codebase. All configuration is loaded dynamically from `.env` in the project root (or OS environment variables in cloud/production deployments).

1. Copy `.env.example` to `.env`:
   ```powershell
   copy .env.example .env
   ```
2. Open `.env` and fill in your MySQL credentials and Groq API key:
   ```env
   # Database Configuration
   DB_URL=jdbc:mysql://localhost:3306/online_internship?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   DB_USER=root
   DB_PASSWORD=your_mysql_password

   # Groq AI Cloud Configuration (Resume & Skill Gap Engine)
   GROQ_API_KEY=your_groq_api_key_here
   GROQ_MODEL=openai/gpt-oss-120b
   ```

*For Docker / Container / Cloud deployments (AWS, Render, Heroku), you can also pass standard OS environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `GROQ_API_KEY`) directly.*

### Step 4: Run in Eclipse Oxygen / Tomcat
1. Open **Eclipse Oxygen (or Eclipse for Java EE)**.
2. Select **File** -> **Import...** -> **General** -> **Existing Projects into Workspace** -> **Next**.
3. Browse to `d:\My Workspace\Kasnet_Online_Internship` and click **Finish**.
4. In the **Servers** tab, ensure **Apache Tomcat v8.0** (or v8.5) is configured.
5. Right-click `kasnet-internship-portal` -> **Run As** -> **Run on Server** -> Choose **Tomcat v8.0 Server** -> **Finish**.

### Step 5: Open in Your Browser
Open your browser and navigate to:
👉 **`http://localhost:8080/kasnet-internship-portal/`**

### Assignment errors or student dashboard HTTP 500

The current `database.sql` schema includes the assignment instruction columns. If your existing database was created from an older schema, back it up before rebuilding the database from the current schema. Rebuilding drops existing tables and data; do not do this if you need to preserve them.

Instruction PDFs are stored outside the deployed web application at `<catalina.base>/uploads/assignment-instructions`. Change the `assignmentInstructionsUploadDir` context parameter in `WebContent/WEB-INF/web.xml` to another absolute or Tomcat-base-relative upload directory when needed; the application places its `assignment-instructions` subdirectory under that path.

The assignment-instruction servlet allows a 5 MiB file in a 6 MiB multipart request. Student submission uploads
allow 10 MiB files in an 11 MiB request. Multipart text fields are read from servlet parts directly so upload
handling does not depend on Tomcat's URL-parameter parsing limit.

---

## Pre-configured Test Credentials

The fresh-install seed in `database.sql` creates these accounts. To add or reset these demo credentials in an existing database without dropping tables, run `seed_demo_accounts.sql` from the project directory. It only updates the three listed demo email addresses; other accounts are unchanged:

```powershell
mysql -u root -p < seed_demo_accounts.sql
```

The MySQL client prompts for the database password. The demo credentials are:

| Role | Email | Password | Destination Dashboard |
|---|---|---|---|
| **Student (Test ID)** | `test@myinternshiptech.com` | `Test@12345` | Student Portal (`/dashboard`) |
| **System Administrator** | `admin@myinternshiptech.com` | `Admin@12345` | Admin Console (`/admin`) |
| **Legacy administrator alias** | `admin@kasnet.in` | `Admin@12345` | Admin Console (`/admin`) |

You can also click **Register** on the landing page to register a fresh student account. New accounts are immediately persisted in your local MySQL database.

---

## Automated Verification Test Suite

You can verify database connectivity, validations, password hashing, and profile calculations in 5 seconds without starting the web browser:

```powershell
# In PowerShell (using compiled classes in build/classes and WebContent/WEB-INF/lib):
$cp = "build\classes;" + ((Get-ChildItem WebContent\WEB-INF\lib\*.jar | ForEach-Object { $_.FullName }) -join ";")
java -cp $cp com.kasnet.util.SystemVerifier
```

Output:
```text
==================================================
 MY INTERNSHIP TECHNOLOGIES - SYSTEM VERIFICATION
==================================================
[TEST 1] Verifying MySQL connection... PASSED (Connected to online_internship)
[TEST 2] Verifying Input Validations... PASSED (Phone 10-digits, Percentage 0-100, Email, Date verified)
[TEST 3] Verifying Hardcoded Student Test ID (test@myinternshiptech.com / test@kasnet.in)... PASSED
[TEST 4] Verifying Admin Account... PASSED (Authenticated as Admin)
[TEST 5] Verifying Admin User Monitoring Query... PASSED
[TEST 6] Verifying Profile Completeness Calculation... PASSED
[TEST 7] Verifying Education Percentage & Record Persistence... PASSED
==================================================
 ALL VERIFICATION TESTS PASSED SUCCESSFULLY!
==================================================
```

---

## Importing & Running in Eclipse Oxygen (Dynamic Web Project)

This project is configured out-of-the-box as an **Eclipse Dynamic Web Project** (Dynamic Web Module 3.1, JDK 1.8, Apache Tomcat 8.0).

### Step 1: Open Eclipse Oxygen
1. Launch **Eclipse Oxygen (or any Eclipse for Java EE)** with **JDK 1.8**.
2. Click **File** -> **Import...** -> **General** -> **Existing Projects into Workspace** -> **Next**.
3. In **Select root directory**, browse to this project directory:
   d:\My Workspace\Kasnet_Online_Internship
4. Ensure kasnet-internship-portal is checked, and click **Finish**.
5. Eclipse will automatically recognize the project structure:
   - **Java Source:** src
   - **Web Content:** WebContent
   - **Libraries:** WebContent/WEB-INF/lib (via *Web App Libraries*)
   - **Runtime:** Apache Tomcat v8.0 (Dynamic Web Module 3.1, JavaSE-1.8)

### Step 2: Configure Tomcat 8.0 Server in Eclipse
1. If you haven't added Tomcat 8.0 yet:
   - In the **Servers** tab, right-click -> **New** -> **Server**.
   - Select **Apache** -> **Tomcat v8.0 Server** -> click **Next**.
   - Point to your Apache Tomcat 8.0 directory and select **JavaSE-1.8** -> click **Finish**.
2. Right-click the configured **Tomcat v8.0 Server** -> **Add and Remove...**.
3. Select kasnet-internship-portal -> click **Add >** -> **Finish**.

### Step 3: Run the Application
1. Right-click kasnet-internship-portal in the Project Explorer.
2. Select **Run As** -> **Run on Server** -> Choose **Tomcat v8.0 Server** -> **Finish**.
3. Access the portal in Eclipse's internal browser or your external browser:
   👉 **http://localhost:8080/kasnet-internship-portal/**

---

## Alternative: Deploying to Standalone Tomcat

If you are deploying to a standalone **Apache Tomcat 8.0 / 8.5 / 9.0** installation:

1. **Export the WAR file from Eclipse:**
   - In Eclipse, right-click `kasnet-internship-portal` -> **Export** -> **WAR file**.
   - Set the destination to your desired path and click **Finish**.
   *(Or build via CLI: copy `build\classes` into `WebContent\WEB-INF\classes` and run `jar -cf kasnet-internship-portal.war -C WebContent .`)*
2. **Deploy:**
   Copy `kasnet-internship-portal.war` into your Tomcat's `webapps/` folder.
4. **Start Tomcat:**
   - Windows: `C:\path\to\tomcat\bin\startup.bat`
   - Linux/Mac: `/path/to/tomcat/bin/startup.sh`
5. **Access the portal:**
   `http://localhost:8080/kasnet-internship-portal/`

---

## Application Sitemap & Routes

| Path | Component | Access | Description |
|---|---|---|---|
| `/` or `/index.jsp` | `index.jsp` | Public | Landing page with highlights, curriculum tracks, and portal actions. |
| `/register` | `RegisterServlet` | Public | Student signup with 10-digit phone and email validation. |
| `/login` | `LoginServlet` | Public | Split-panel login with CAPTCHA and BCrypt authentication. |
| `/dashboard` | `DashboardServlet` | Student | Central student dashboard with 5 core feature cards and overview metrics. |
| `/profile` | `ProfileServlet` | Student | 5-section profile management with dynamic completeness score in **IST**. |
| `/assignments` | `AssignmentServlet` | Authenticated student | Assignment instructions, deadlines, submission uploads, and graded scores. |
| `/admin/assignments` | `AdminAssignmentServlet` | Admin only | Create, edit, publish, deactivate, and republish assignments. |
| `/assignment-instructions` | `AssignmentInstructionPdfServlet` | Authenticated student/admin | Serves an assignment's private instruction PDF. |
| `/lectures` | `LectureServlet` | Student | Video curriculum, duration tracker, and module completion markers. |
| `/question-bank` | `QuestionBankServlet` | Student | Subject Question Bank with dropdown (`Java`, `Python`, `Web`, `DBMS`, `Cloud`). |
| `/resume-analysis` | `ResumeAnalysisServlet`| Student | Resume document upload dropzone and ATS analysis placeholder. |
| `/admin` | `AdminServlet` | Admin | Clean user monitoring table, registration audit, and search filters. |
| `/logout` | `LogoutServlet` | Authenticated | Secure session termination and redirect to login. |

---

## Architecture & Project Structure

```text
Kasnet_Online_Internship/
├── database.sql                             # Full MySQL schema & seed data
├── README.md                                # Local setup & developer documentation
├── docs/
│   ├── PROJECT_PROGRESS.md                  # Comprehensive technical progress & handover guide
│   └── screenshots/                         # Reference mockups & logo_final.png
├── .project                                  # Eclipse project description (Dynamic Web Project)
├── .classpath                                # Eclipse classpath (JDK 1.8, Tomcat 8.0, Web App Libs)
├── .settings/                                # Eclipse facet settings (Java 1.8, Dynamic Web 3.1)
├── src/                                     # Java source directory (root package: com.kasnet)
│   └── com/kasnet/
│       ├── controller/                      # Servlets (Controllers)
│       │   ├── LoginServlet.java
│       │   ├── RegisterServlet.java
│       │   ├── LogoutServlet.java
│       │   ├── DashboardServlet.java
│       │   ├── ProfileServlet.java
│       │   ├── PersonalDetailsServlet.java
│       │   ├── EducationServlet.java
│       │   ├── SkillsServlet.java
│       │   ├── CertificationServlet.java
│       │   ├── ProjectServlet.java
│       │   ├── HackathonServlet.java
│       │   ├── CompetitionServlet.java
│       │   ├── AssignmentServlet.java
│       │   ├── LectureServlet.java
│       │   ├── QuestionBankServlet.java
│       │   ├── ResumeAnalysisServlet.java
│       │   └── AdminServlet.java
│       ├── model/                           # JavaBeans / POJOs (Models)
│       │   ├── User.java
│       │   ├── StudentProfile.java
│       │   ├── Education.java
│       │   ├── Skill.java
│       │   ├── Certification.java
│       │   ├── Project.java
│       │   ├── Hackathon.java
│       │   ├── Competition.java
│       │   ├── Assignment.java
│       │   ├── Lecture.java
│       │   └── DashboardStats.java
│       ├── dao/                             # JDBC Data Access Objects
│       │   ├── UserDAO.java
│       │   ├── ProfileDAO.java
│       │   ├── EducationDAO.java
│       │   ├── SkillDAO.java
│       │   ├── CertificationDAO.java
│       │   ├── ProjectDAO.java
│       │   ├── HackathonDAO.java
│       │   ├── CompetitionDAO.java
│       │   ├── AssignmentDAO.java
│       │   ├── LectureDAO.java
│       │   └── DashboardDAO.java
│       ├── service/                         # Application Services
│       │   ├── AuthenticationService.java
│       │   ├── ProfileService.java
│       │   └── FileUploadService.java
│       └── util/                            # Utilities
│           ├── DBConnection.java            # JDBC connection manager (Asia/Kolkata timezone)
│           ├── PasswordUtil.java            # BCrypt hashing & verification
│           ├── ValidationUtil.java          # 10-digit phone, regex email, date validation
│           ├── SessionUtil.java             # Session auth verification
│           └── SystemVerifier.java          # Offline automated test suite
└── WebContent/                              # Web Content root (Dynamic Web Module 3.1)
    ├── META-INF/
    │   └── MANIFEST.MF
    ├── assets/
    │   └── images/
    │       ├── logo.png                     # Official brand logo
    │       └── logo_final.png
    ├── images/
    │   └── logo_final.png
    ├── views/
    │   ├── dashboard.jsp                    # Student logged-in homepage
    │   ├── profile.jsp                      # 5-section profile view
    │   ├── assignments.jsp                  # Assignments module
    │   ├── lectures.jsp                     # Lectures module
    │   ├── question-bank.jsp                # Question bank with subject selector
    │   ├── resume-analysis.jsp              # Resume analysis interface
    │   ├── admin.jsp                        # Admin monitoring console
    │   ├── error-404.jsp                    # 404 handler
    │   ├── error-500.jsp                    # 500 handler
    │   └── unauthorized.jsp                 # Session expired handler
    ├── index.jsp                            # Public landing page
    ├── login.jsp                            # Login view
    ├── register.jsp                         # Registration view
    └── WEB-INF/
        ├── lib/                             # All runtime dependency JARs (21 libraries)
        ├── tld/                             # JSTL tag library definitions (c.tld, fmt.tld, fn.tld)
        └── web.xml                          # Deployment descriptor (servlets, filters, IST timezone)
```

---

## Indian Standard Time (IST) Configuration

All user-visible timestamps (e.g., Account Created Date, Previous Login Date, Assignment Submission Date, and Admin Registration Logs) are configured to use **Indian Standard Time (IST / Asia/Kolkata, UTC+05:30)**:

1. **JDBC Connection (`DBConnection.java`):**
   ```text
   jdbc:mysql://localhost:3306/online_internship?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   ```
2. **Servlet Descriptor (`web.xml`):**
   ```xml
   <context-param>
       <param-name>javax.servlet.jsp.jstl.fmt.timeZone</param-name>
       <param-value>Asia/Kolkata</param-value>
   </context-param>
   ```
3. **JSP Formatting Tag (`views/profile.jsp`):**
   ```jsp
   <fmt:timeZone value="Asia/Kolkata">
       <fmt:formatDate value="${user.createdAt}" pattern="dd-MM-yyyy hh:mm:ss a" /> IST
   </fmt:timeZone>
   ```

---

## Troubleshooting Guide

### 1. `Port 8080 already in use`
If port 8080 is used by another application (e.g., Oracle, Jenkins, another Tomcat):
- In Eclipse, double-click your **Tomcat v8.0 Server** in the **Servers** view.
- Under **Ports**, change the **HTTP/1.1** port from `8080` to `8085`.
- Save (`Ctrl+S`) and restart Tomcat.
- Access via `http://localhost:8085/kasnet-internship-portal/`.

### 2. `Access denied for user 'root'@'localhost'`
- Check the password in `src/com/kasnet/util/DBConnection.java`.
- Test connecting in terminal: `mysql -u root -p`.

### 3. `Public Key Retrieval is not allowed`
- This occurs on MySQL 8.0+ when using `caching_sha2_password`. It is already handled in `DBConnection.java` by the `allowPublicKeyRetrieval=true` flag.

### 4. Background Java Process Locking Tomcat on Windows
If Tomcat fails to start or ports remain occupied:
- Stop any running Java process in PowerShell:
  ```powershell
  Get-Process -Name java | Stop-Process -Force
  ```
- Restart the Tomcat server in Eclipse.

---

## Continuation & Documentation

For detailed historical logs, database schema column definitions, and design decisions, refer to:
- [`docs/PROJECT_PROGRESS.md`](docs/PROJECT_PROGRESS.md)

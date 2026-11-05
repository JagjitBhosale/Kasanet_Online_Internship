# Production Database Schema Mapping Specification

## Project: OnLineInternship.in
**Primary Source of Truth**: `Database/onlineinternship.sql`

---

## 1. Executive Summary

This document specifies the exact mapping between the official production MySQL database schema (`onlineinternship.sql`), the Java domain model (`Student`, `User`), Data Access Objects (`StudentDAO`), web controllers (`RegisterServlet`, `LoginServlet`, `LogoutServlet`, `ProfileServlet`, `PersonalDetailsServlet`, `EducationServlet`, `SkillsServlet`), and frontend JSPs (`register.jsp`, `login.jsp`, `views/profile.jsp`).

The application is configured to interact directly with the production `student` and `admin` tables, preserving existing primary keys, foreign-key relationships, and data integrity.

---

## 2. Production Database Architecture

### 2.1 Core Identity & Profile Table: `student`
The production `student` table is the central source of student identity and core academic/profile information.

| Column Name | SQL Data Type | Nullable | Key / Default | Business Purpose / Mapping |
|---|---|---|---|---|
| `id` | `INT(11)` | NO | PRIMARY KEY AUTO_INCREMENT | Unique student identifier (`studentId`, `userId`) |
| `fname` | `VARCHAR(500)` | YES | NULL | First name (`firstName`) |
| `lname` | `VARCHAR(500)` | YES | NULL | Last name (`lastName`) |
| `phone` | `VARCHAR(45)` | YES | NULL | 10-digit mobile contact number (`mobile`, `phone`) |
| `email` | `VARCHAR(500)` | YES | NULL | Primary login identifier & communication email |
| `password` | `VARCHAR(500)` | YES | NULL | Secure BCrypt password hash (`passwordHash`) |
| `gender` | `VARCHAR(45)` | YES | NULL | Gender (`Male`, `Female`, `Other`) |
| `dob` | `VARCHAR(45)` | YES | NULL | Date of birth (`YYYY-MM-DD`) |
| `address` | `VARCHAR(500)` | YES | NULL | Residential address |
| `city` | `VARCHAR(500)` | YES | NULL | Current city |
| `state` | `VARCHAR(500)` | YES | NULL | State of residence |
| `country_code` | `INT(11)` | YES | NULL | Country phone code (e.g. 91) |
| `active` | `INT(11)` | YES | NULL | Account status (1 = Active, 0 = Inactive) |
| `referalCode` | `VARCHAR(45)` | YES | NULL | Student's referral code or used referral code |
| `rCodeCount` | `INT(11)` | YES | NULL | Count of referrals made |
| `Referal_text` | `VARCHAR(50)` | YES | NULL | Referral notes or text code |
| `datetime` | `VARCHAR(45)` | YES | NULL | Account creation timestamp / last login timestamp |
| `previouslogin`| `VARCHAR(45)` | YES | NULL | Previous login timestamp |
| `spendtime` | `VARCHAR(45)` | YES | NULL | Cumulative portal time spent |
| `10schoolname` | `VARCHAR(500)` | YES | NULL | SSC / 10th School name |
| `10board` | `VARCHAR(500)` | YES | NULL | SSC / 10th Educational board |
| `10passingyear`| `VARCHAR(45)` | YES | NULL | SSC / 10th Passing year |
| `10percentage` | `VARCHAR(45)` | YES | NULL | SSC / 10th Percentage or CGPA |
| `12schoolname` | `VARCHAR(500)` | YES | NULL | HSC / 12th Junior college name |
| `12board` | `VARCHAR(500)` | YES | NULL | HSC / 12th Educational board |
| `12stream` | `VARCHAR(500)` | YES | NULL | HSC / 12th Stream (Science/Commerce/Arts) |
| `12passinyear` | `VARCHAR(45)` | YES | NULL | HSC / 12th Passing year |
| `12percentage` | `VARCHAR(45)` | YES | NULL | HSC / 12th Percentage |
| `diplomaschoolname` | `VARCHAR(500)`| YES | NULL | Polytechnic / Diploma institute name |
| `diplomaboard` | `VARCHAR(500)` | YES | NULL | Diploma board (e.g. MSBTE) |
| `diplomastream`| `VARCHAR(500)` | YES | NULL | Diploma department / branch |
| `diplomapassingyear`| `VARCHAR(45)`| YES | NULL | Diploma passing year |
| `diplomapercentage` | `VARCHAR(45)`| YES | NULL | Diploma percentage |
| `gradcollegename` | `VARCHAR(500)` | YES | NULL | Graduation college / institute name |
| `gradunivercity`| `VARCHAR(500)` | YES | NULL | Graduation university name |
| `gradstream` | `VARCHAR(500)` | YES | NULL | Graduation degree & branch |
| `gradpassingyear` | `VARCHAR(500)`| YES | NULL | Graduation completion year |
| `gradpercentage` | `VARCHAR(45)` | YES | NULL | Graduation percentage / CGPA |
| `mascollegename`| `VARCHAR(500)` | YES | NULL | Post-Graduation college name |
| `masunivercity` | `VARCHAR(500)` | YES | NULL | Post-Graduation university name |
| `masstream` | `VARCHAR(500)` | YES | NULL | Post-Graduation stream |
| `maspassingyear`| `VARCHAR(45)` | YES | NULL | Post-Graduation passing year |
| `maspercentage` | `VARCHAR(45)` | YES | NULL | Post-Graduation percentage |
| `technicalskill`| `VARCHAR(5000)`| YES | NULL | Comma-separated technical skills |
| `softskill` | `VARCHAR(5000)`| YES | NULL | Comma-separated soft skills |
| `domain` | `VARCHAR(5000)`| YES | NULL | Specialization / domain skills |
| `Projectname` | `VARCHAR(10000)`| YES| NULL | Capstone / Academic project name |
| `projectdetails`| `VARCHAR(10000)`| YES| NULL | Project description, tech stack, URL |
| `certification` | `VARCHAR(500)` | YES | NULL | Certification title |
| `certificationname` | `VARCHAR(500)`| YES| NULL | Certification issuing organization |
| `certificationtechnology`| `VARCHAR(1000)`| YES| NULL | Skills/technologies certified |
| `ExtraActivities`| `VARCHAR(5000)`| YES| NULL | Hackathons, sports, extracurriculars |
| `profile` | `LONGBLOB` | YES | NULL | Profile photo binary data |
| `resume` | `LONGBLOB` | YES | NULL | Resume document binary data |

### 2.2 Administrative Authentication Table: `admin`
Stores administrative staff accounts.

| Column Name | SQL Data Type | Nullable | Key / Default | Business Purpose |
|---|---|---|---|---|
| `id` | `INT(11)` | NO | PRIMARY KEY AUTO_INCREMENT | Unique admin ID |
| `fname` | `VARCHAR(100)` | NO | | First name |
| `lname` | `VARCHAR(150)` | NO | | Last name |
| `email` | `VARCHAR(150)` | NO | | Administrative login email |
| `password` | `VARCHAR(20)` | NO | | Password credential |

### 2.3 Foreign Key & Relational References to `student`
- `student_assignment_data.student_id` &rarr; `FOREIGN KEY (student_id) REFERENCES student(id)`
- `certificate.uid` / `certificate.sid` &rarr; Relational reference to student
- `Billing_details.email` &rarr; Relational mapping on student email

---

## 3. Java Model & Field Mapping

### 3.1 Model Class: `com.onlineinternshipin.model.Student`
`Student` inherits from `com.onlineinternshipin.model.User` to provide 100% backward compatibility with all session listeners, servlets, and JSPs while reflecting all production columns.

```text
Student (extends User)
├── id / userId                 <---> student.id
├── fname / firstName           <---> student.fname
├── lname / lastName            <---> student.lname
├── phone / mobile              <---> student.phone
├── email                       <---> student.email
├── password / passwordHash     <---> student.password
├── gender                      <---> student.gender
├── dob / dateOfBirth           <---> student.dob
├── address                     <---> student.address
├── city                        <---> student.city
├── state                       <---> student.state
├── countryCode                 <---> student.country_code
├── active / isActive           <---> student.active (1/0)
├── referralCode                <---> student.referalCode
├── referralText                <---> student.Referal_text
├── datetime / createdAt        <---> student.datetime
├── previousLogin               <---> student.previouslogin
├── spendTime                   <---> student.spendtime
├── 10th School / Board / Year / % <-> 10schoolname, 10board, 10passingyear, 10percentage
├── 12th School / Board / Year / % <-> 12schoolname, 12board, 12stream, 12passinyear, 12percentage
├── Diploma School / Board / Year / % <-> diplomaschoolname, diplomaboard, diplomastream, ...
├── Graduation College / Univ / %  <-> gradcollegename, gradunivercity, gradstream, ...
├── Master College / Univ / %      <-> mascollegename, masunivercity, masstream, ...
├── Technical / Soft Skills        <-> technicalskill, softskill, domain
├── Projects                       <-> Projectname, projectdetails
├── Certifications                 <-> certification, certificationname, certificationtechnology
└── Extra Activities               <-> ExtraActivities
```

---

## 4. Module Workflow Mappings

### 4.1 Module 1: Student Registration
- **Source Page**: `WebContent/register.jsp`
- **Controller**: `com.onlineinternshipin.controller.RegisterServlet` (`POST /register`)
- **Service & DAO**: `StudentDAO.registerStudent(Student student)`
- **SQL Operation**:
  ```sql
  INSERT INTO `student` (
      `fname`, `lname`, `phone`, `email`, `password`,
      `city`, `gradcollegename`, `gradstream`,
      `referalCode`, `Referal_text`, `country_code`, `active`, `datetime`
  ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, NOW());
  ```
- **Validation**:
  - Mandatory: First Name, Last Name, Email, Phone (10 digits), Password (min 8 chars, matching confirmPassword), College Name, Stream/Branch, City.
  - Unique Checks: Duplicate email check (`SELECT COUNT(*) FROM student WHERE LOWER(email) = LOWER(?)`) and duplicate mobile check (`SELECT COUNT(*) FROM student WHERE phone = ?`).
  - Password Security: Hashed with BCrypt (cost factor 12) via `PasswordUtil.hashPassword(...)`.

### 4.2 Module 2: Student Login & Logout
- **Source Page**: `WebContent/login.jsp`
- **Controller**: `com.onlineinternshipin.controller.LoginServlet` (`POST /login`), `LogoutServlet` (`/logout`)
- **Student Authentication**:
  - Identifier: Email address
  - Password Verification: `PasswordUtil.checkPassword(plainText, student.getPassword())`
  - Account Active Check: Verify `student.getActive() != 0`
  - Login Tracking: Update `student.previouslogin = student.datetime, student.datetime = NOW()`
  - Session Creation:
    - Session ID regenerated to protect against session fixation.
    - Attributes stored: `userId` (student.id), `userName` (student.fullName), `userFirstName` (student.fname), `user` (Student object), `userEmail`, `userRole` ("STUDENT").
- **Admin Authentication**:
  - Identifier: Email address
  - Query: `SELECT * FROM admin WHERE email = ?`
  - Role: "ADMIN" &rarr; Redirects to `/admin`
- **Logout Workflow**:
  - Session invalidated (`session.invalidate()`).
  - JSESSIONID cookie cleared.
  - Redirects to `/login?logout=1`.

### 4.3 Module 3: Profile Viewing
- **Controller**: `com.onlineinternshipin.controller.ProfileServlet` (`GET /profile`)
- **Data Retrieval**:
  - Retrieves student record using session `userId`: `studentDAO.getStudentById(userId)`.
  - Populates personal information, academic history (SSC, HSC, Diploma, Graduation, Master's), technical/soft skills, projects, and certifications.
  - Computes dynamic profile completion (0-100%) based on completed profile sections.
  - Forwards to `WebContent/views/profile.jsp`.

### 4.4 Module 4: Profile Editing
- **Controllers**:
  - Personal Details: `PersonalDetailsServlet` (`POST /personal-details`)
    - Updates: `fname`, `lname`, `gender`, `dob`, `phone`, `address`, `city`, `state`.
  - Education: `EducationServlet` (`POST /education`)
    - Updates: `10schoolname`, `10board`, `10passingyear`, `10percentage`, `12...`, `diploma...`, `grad...`, `mas...`.
  - Skills: `SkillsServlet` (`POST /skills`)
    - Updates: `technicalskill`, `softskill`, `domain`.
  - Projects: `ProjectServlet` (`POST /projects`)
    - Updates: `Projectname`, `projectdetails`.
  - Certifications: `CertificationServlet` (`POST /certifications`)
    - Updates: `certification`, `certificationname`, `certificationtechnology`.
- **Authorization**: Strict session check ensures student can only modify their own record (`userId == session.userId`).

---

## 5. Schema Extensions & Migrations

### Dashboard data sources
- Student identity, profile, education, skills, portfolio projects, certifications, and achievements are read from the production `student` row and its existing columns.
- Student assignments are read from `student_assignment_data` using `student_id`; detail reads are scoped by both `student_assignment_id` and the signed-in student's `student.id`.
- Project reports reuse `student.Projectname`, `student.projectdetails`, and the existing project-planning and knowledge columns (`pdomain` through `knowledge_postimpl`). The schema supports one current report per student; no duplicate project-report table is required.
- Platform exam results require exam name, numeric score, and achievement date, none of which are available in the legacy `certificate` table (it stores certificate PDF bytes). Apply `V003__create_platform_certificates.sql`; its `user_id` foreign key targets production `student.id`.

### Migration `V001__add_student_indexes.sql`
- **File**: `Database/migrations/V001__add_student_indexes.sql`
- **Reason**: The base `onlineinternship.sql` defines only `PRIMARY KEY (id)` on table `student`. Secondary indexes on `email` and `phone` are essential to eliminate full-table scans during login and registration uniqueness checks.
- **DDL**:
  ```sql
  CREATE INDEX idx_student_email ON `student` (`email`(191));
  CREATE INDEX idx_student_phone ON `student` (`phone`);
  CREATE INDEX idx_student_referalCode ON `student` (`referalCode`);
  ```
- **Safety**: Fully non-destructive and backward-compatible.

### Migration `V002__create_engineering_year_marks.sql`
- **File**: `Database/migrations/V002__create_engineering_year_marks.sql`
- **Reason**: The production `student` table only stores aggregate graduation marks (`gradpercentage`). To support year-wise academic breakdown (First Year, Second Year, Third Year, Final Year) with evaluation types (Percentage vs CGPA), a 1-to-many relationship table linked to `student(id)` is introduced.
- **DDL**:
  ```sql
  CREATE TABLE IF NOT EXISTS `engineering_year_marks` (
      `mark_id` INT(11) NOT NULL AUTO_INCREMENT,
      `user_id` INT(11) NOT NULL,
      `year_level` VARCHAR(50) NOT NULL,
      `score_type` VARCHAR(20) NOT NULL DEFAULT 'Percentage',
      `score_value` DECIMAL(5,2) NOT NULL,
      `passing_year` INT(11) DEFAULT NULL,
      `college_name` VARCHAR(255) DEFAULT NULL,
      `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`mark_id`),
      UNIQUE KEY `uk_student_year` (`user_id`, `year_level`),
      KEY `idx_eng_marks_user` (`user_id`),
      CONSTRAINT `fk_eng_marks_student` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  ```
- **Relationship**: `user_id` foreign key referencing `student(id)` with cascade delete on student deletion.
- **Safety**: Purely additive. Preserves all existing tables and data.

---

## 6. Student Assignment Management Architecture

### 6.1 Assignment Master Table: `assignment`
Stores administrative assignment definitions created by instructors/administrators.

| Column Name | SQL Data Type | Nullable | Default | Business Purpose / Mapping |
|---|---|---|---|---|
| `assign_id` | `INT(11)` | NO | AUTO_INCREMENT | Master primary key (`assignmentId`) |
| `subject` | `VARCHAR(200)` | NO | | Subject/Domain name (e.g. Java, Cloud) (`subject`) |
| `assign_name` | `VARCHAR(350)` | NO | | Assignment display title (`title`) |
| `assignment_code` | `VARCHAR(50)` | YES | NULL | Unique code / slug (e.g. `JAVA-MOD1-A1`) (`assignmentNumber`) |
| `description` | `TEXT` | YES | NULL | Full assignment prompt and problem description (`description`) |
| `instructions_text` | `TEXT` | YES | NULL | Step-by-step submission instructions (`instructionsText`) |
| `instruction_pdf_path` | `VARCHAR(500)` | YES | NULL | Relative path to optional uploaded reference PDF (`instructionPdfPath`) |
| `due_date` | `DATE` | YES | NULL | Submission deadline (`dueDate`) |
| `total_marks` | `INT(11)` | YES | 100 | Maximum achievable score (`totalMarks`) |
| `is_active` | `TINYINT(1)` | NO | 1 | Visibility flag for student dashboard (`isActive`) |
| `create_date` | `DATE` | NO | CURRENT_DATE | Assignment creation date (`createdAt`) |

### 6.2 Student Assignment Allocation Table: `student_assignment_data`
Tracks individual student assignment allocations, statuses, submissions, and grading.

| Column Name | SQL Data Type | Nullable | Constraints & Relational Mapping |
|---|---|---|---|
| `student_assignment_id` | `INT(11)` | NO | PRIMARY KEY AUTO_INCREMENT |
| `student_id` | `INT(11)` | YES | **FK &rarr; `student(id)` ON DELETE CASCADE** (`studentId`) |
| `assignment_master_id` | `INT(11)` | YES | **FK &rarr; `assignment(assign_id)` ON UPDATE CASCADE** (`assignmentId`) |
| `assign_id` | `VARCHAR(255)` | NO | Legacy display identifier / copies `assignment_code` |
| `assign_subject` | `VARCHAR(200)` | NO | Subject mirrored from master assignment row |
| `assign_topic` | `VARCHAR(350)` | NO | Title mirrored from master `assign_name` |
| `assign_code` | `TEXT` | YES | Optional code submission or text payload |
| `assign_status` | `VARCHAR(255)` | YES | Workflow state: `Assigned`, `Completed` |
| `assign_result` | `VARCHAR(350)` | YES | Evaluation notes / grade remarks (`remarks`) |
| `create_date` | `DATE` | NO | Allocation timestamp (`assignedDate`) |

- **Foreign Key Constraints**:
  - `student_id` &rarr; references `student(id)` with ON DELETE CASCADE.
  - `assignment_master_id` &rarr; references `assignment(assign_id)`.
  - `UNIQUE KEY uk_sad_assignment_student (assignment_master_id, student_id)` prevents accidental duplicate allocations while allowing unassigned legacy rows.

---

## 7. Course, Lecture & Video Streaming Architecture

### 7.1 Entity Relationship Overview
The Video Lecture System provides DRM-protected streaming, Cloudinary video integration, student enrollment tracking, and per-lecture watch progress. All student interactions reference the central `student(id)` record.

```text
              ┌───────────────┐
              │    student    │
              │  (Primary ID) │
              └───┬───┬───┬───┘
                  │   │   │
        ┌─────────┘   │   └───────────────┐
        │ user_id     │ user_id           │ user_id
        ▼             ▼                   ▼
┌──────────────┐ ┌──────────────────┐ ┌───────────────────┐
│ enrollments  │ │ lecture_progress │ │ video_access_logs │
└───────┬──────┘ └────────┬─────────┘ └─────────┬─────────┘
        │                 │                     │
        │ course_id       │ lecture_id          │ lecture_id
        ▼                 ▼                     │
┌──────────────┐ ┌──────────────────┐           │
│   courses    │ │     lectures     │◄──────────┘
│              │◄┤ (course_id FK)   │
└──────────────┘ └──────────────────┘
```

### 7.2 Tables Specification

#### 1. `courses` Table
Master catalog of published and draft training courses.
- `id`: INT(11) AUTO_INCREMENT PRIMARY KEY
- `title`: VARCHAR(255) NOT NULL
- `slug`: VARCHAR(255) UNIQUE
- `description`: TEXT
- `thumbnail_url`: VARCHAR(500)
- `instructor_name`: VARCHAR(255)
- `category`: VARCHAR(100) (e.g. 'Engineering')
- `difficulty`: VARCHAR(50) ('Beginner', 'Intermediate', 'Advanced')
- `duration_weeks`: INT NOT NULL DEFAULT 6
- `total_lessons`: INT NOT NULL DEFAULT 0
- `status`: VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' ('DRAFT', 'PUBLISHED', 'ARCHIVED')
- `created_at`, `updated_at`: TIMESTAMP

#### 2. `lectures` Table
Video lectures belonging to a course, referencing Cloudinary assets.
- `id`: INT(11) AUTO_INCREMENT PRIMARY KEY
- `course_id`: INT(11) NOT NULL &rarr; **FK `courses(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `title`: VARCHAR(255) NOT NULL
- `description`: TEXT
- `lecture_number`: INT NOT NULL DEFAULT 1
- `cloudinary_public_id`: VARCHAR(255) (Cloudinary public ID, NOT public URL)
- `duration_seconds`: INT NOT NULL DEFAULT 0
- `is_preview`: TINYINT(1) NOT NULL DEFAULT 0 (1 = previewable without enrollment)
- `status`: VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' ('DRAFT', 'PUBLISHED')
- `created_at`, `updated_at`: TIMESTAMP

#### 3. `enrollments` Table
Tracks student enrollment in courses and calculated completion percentage.
- `id`: INT(11) AUTO_INCREMENT PRIMARY KEY
- `user_id`: INT(11) NOT NULL &rarr; **FK `student(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `course_id`: INT(11) NOT NULL &rarr; **FK `courses(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `progress_percentage`: INT NOT NULL DEFAULT 0
- `completed_lessons`: INT NOT NULL DEFAULT 0
- `enrolled_at`: TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
- `completed_at`: TIMESTAMP NULL DEFAULT NULL
- `UNIQUE KEY uk_enrollment_user_course (user_id, course_id)`

#### 4. `lecture_progress` Table
Tracks granular watch position and completion per student per lecture.
- `id`: INT(11) AUTO_INCREMENT PRIMARY KEY
- `user_id`: INT(11) NOT NULL &rarr; **FK `student(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `lecture_id`: INT(11) NOT NULL &rarr; **FK `lectures(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `progress_seconds`: INT NOT NULL DEFAULT 0
- `completed`: TINYINT(1) NOT NULL DEFAULT 0
- `last_watched_at`: TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- `UNIQUE KEY uk_progress_user_lecture (user_id, lecture_id)`
- **Logic**: Updated via UPSERT (`ON DUPLICATE KEY UPDATE progress_seconds = GREATEST(progress_seconds, ?)`), ensuring watch progress never decreases.

#### 5. `video_access_logs` Table
Audit trail and security rate limiter logging video streaming access per student.
- `id`: INT(11) AUTO_INCREMENT PRIMARY KEY
- `user_id`: INT(11) NOT NULL &rarr; **FK `student(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `lecture_id`: INT(11) NOT NULL &rarr; **FK `lectures(id)` ON DELETE CASCADE ON UPDATE CASCADE**
- `ip_address`: VARCHAR(45)
- `user_agent`: VARCHAR(500)
- `created_at`: TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

---

## 8. Relational Foreign Key Integrity Matrix

| Dependent Table | FK Column | Referenced Parent Table | Parent PK Column | On Delete Action | On Update Action |
|---|---|---|---|---|---|
| `student_assignment_data` | `student_id` | `student` | `id` | `CASCADE` | `CASCADE` |
| `student_assignment_data` | `assignment_master_id` | `assignment` | `assign_id` | `RESTRICT / SET NULL` | `CASCADE` |
| `lectures` | `course_id` | `courses` | `id` | `CASCADE` | `CASCADE` |
| `enrollments` | `user_id` | `student` | `id` | `CASCADE` | `CASCADE` |
| `enrollments` | `course_id` | `courses` | `id` | `CASCADE` | `CASCADE` |
| `lecture_progress` | `user_id` | `student` | `id` | `CASCADE` | `CASCADE` |
| `lecture_progress` | `lecture_id` | `lectures` | `id` | `CASCADE` | `CASCADE` |
| `video_access_logs` | `user_id` | `student` | `id` | `CASCADE` | `CASCADE` |
| `video_access_logs` | `lecture_id` | `lectures` | `id` | `CASCADE` | `CASCADE` |
| `engineering_year_marks` | `user_id` | `student` | `id` | `CASCADE` | `CASCADE` |
| `platform_certificates` | `user_id` | `student` | `id` | `CASCADE` | `CASCADE` |


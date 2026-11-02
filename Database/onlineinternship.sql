-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 02, 2026 at 05:00 AM
-- Server version: 5.7.27
-- PHP Version: 7.0.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `onlineinternship`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `fname` varchar(100) NOT NULL,
  `lname` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `adminassign`
--

CREATE TABLE `adminassign` (
  `uid` int(11) NOT NULL,
  `subject` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `admintest`
--

CREATE TABLE `admintest` (
  `uid` int(11) NOT NULL,
  `sid` int(11) NOT NULL,
  `date` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

CREATE TABLE `assignment` (
  `assign_id` int(11) NOT NULL,
  `subject` varchar(100) NOT NULL,
  `assign_name` varchar(350) NOT NULL,
  `create_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignmentinfo`
--

CREATE TABLE `assignmentinfo` (
  `aid` int(11) NOT NULL,
  `subject` varchar(400) NOT NULL,
  `level` varchar(400) NOT NULL,
  `question` varchar(400) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_1`
--

CREATE TABLE `assignment_1` (
  `aid` int(20) NOT NULL,
  `subject` varchar(100) NOT NULL,
  `question` varchar(200) NOT NULL,
  `Description` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_2`
--

CREATE TABLE `assignment_2` (
  `rid` int(10) NOT NULL,
  `aid` int(10) NOT NULL,
  `sid` int(10) NOT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_detail`
--

CREATE TABLE `assignment_detail` (
  `assignment_detail_id` int(11) NOT NULL,
  `company_name` varchar(255) DEFAULT 'KASNET',
  `assign_subject` varchar(100) NOT NULL,
  `assign_level` varchar(350) NOT NULL,
  `assign_topic` varchar(350) NOT NULL,
  `assign_descrition` varchar(350) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_forum_query`
--

CREATE TABLE `assignment_forum_query` (
  `assignment_forum_id` int(11) NOT NULL,
  `examination_id` varchar(100) NOT NULL,
  `assign_id` varchar(100) NOT NULL,
  `query_details` varchar(350) NOT NULL,
  `file_name` varchar(100) DEFAULT NULL,
  `mimetype` varchar(100) DEFAULT NULL,
  `assign_file` longblob,
  `assign_result` varchar(350) DEFAULT 'Pending',
  `create_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_forum_solution`
--

CREATE TABLE `assignment_forum_solution` (
  `assignment_forum_solution_id` int(11) NOT NULL,
  `solution_details` varchar(350) NOT NULL,
  `file_name` varchar(100) DEFAULT NULL,
  `mimetype` varchar(100) DEFAULT NULL,
  `assign_file` longblob,
  `create_date` date NOT NULL,
  `assignment_forum_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_question`
--

CREATE TABLE `assignment_question` (
  `question_id` int(11) NOT NULL,
  `assign_id` int(11) NOT NULL,
  `question` varchar(10000) NOT NULL,
  `answer` varchar(1) NOT NULL,
  `A` varchar(1000) NOT NULL,
  `B` varchar(1000) NOT NULL,
  `C` varchar(1000) NOT NULL,
  `D` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assignment_subject`
--

CREATE TABLE `assignment_subject` (
  `assignment_subject_id` int(11) NOT NULL,
  `assignment_subject_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `assign_status`
--

CREATE TABLE `assign_status` (
  `assignid` int(11) NOT NULL,
  `aid` int(11) NOT NULL,
  `sid` int(11) NOT NULL,
  `startdate` varchar(400) DEFAULT NULL,
  `enddate` varchar(400) DEFAULT NULL,
  `status` varchar(500) DEFAULT NULL,
  `uploadedcode` longblob,
  `subject` varchar(45) DEFAULT NULL,
  `level` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Billing_details`
--

CREATE TABLE `Billing_details` (
  `bid` int(5) NOT NULL,
  `Name` varchar(500) NOT NULL,
  `College_name` varchar(500) NOT NULL,
  `Branch` varchar(200) NOT NULL,
  `Contact_number` varchar(11) NOT NULL,
  `Email_id` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `certificate`
--

CREATE TABLE `certificate` (
  `id` int(11) NOT NULL,
  `Certificatepdf` longblob,
  `uid` int(11) DEFAULT NULL,
  `sid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `certifiedstudent`
--

CREATE TABLE `certifiedstudent` (
  `cid` int(11) NOT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `college` varchar(200) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `subject` varchar(45) DEFAULT NULL,
  `date1` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `company`
--

CREATE TABLE `company` (
  `id` int(11) NOT NULL,
  `fname` varchar(45) DEFAULT NULL,
  `lname` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `datetime` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `contact`
--

CREATE TABLE `contact` (
  `cid` int(11) NOT NULL,
  `firstname` varchar(500) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `subject` varchar(500) DEFAULT NULL,
  `msg` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `countries`
--

CREATE TABLE `countries` (
  `ID` bigint(20) NOT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Population` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `csrinternship`
--

CREATE TABLE `csrinternship` (
  `iid` int(5) NOT NULL,
  `sname` varchar(150) NOT NULL,
  `emailid` varchar(150) NOT NULL,
  `phoneno` varchar(12) NOT NULL,
  `college` varchar(150) NOT NULL,
  `city` varchar(150) NOT NULL,
  `referedby` varchar(150) NOT NULL,
  `stream` varchar(150) NOT NULL,
  `csrnon` varchar(50) NOT NULL,
  `rtime` varchar(50) NOT NULL,
  `clgname` varchar(200) NOT NULL,
  `clgnumber` varchar(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `enquiry`
--

CREATE TABLE `enquiry` (
  `eid` int(11) NOT NULL,
  `fname` varchar(500) DEFAULT NULL,
  `lname` varchar(500) DEFAULT NULL,
  `college` varchar(500) DEFAULT NULL,
  `contact` varchar(500) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `city` varchar(500) DEFAULT NULL,
  `enquiry` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `fid` int(11) NOT NULL,
  `name` varchar(300) DEFAULT NULL,
  `emailid` varchar(100) DEFAULT NULL,
  `collegename` varchar(300) DEFAULT NULL,
  `branch` varchar(100) DEFAULT NULL,
  `contactno` varchar(15) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `role` varchar(45) DEFAULT NULL,
  `s11` varchar(45) DEFAULT NULL,
  `s12` varchar(45) DEFAULT NULL,
  `s13` varchar(45) DEFAULT NULL,
  `s14` varchar(45) DEFAULT NULL,
  `s15` varchar(45) DEFAULT NULL,
  `s15c` varchar(45) DEFAULT NULL,
  `s16` varchar(300) DEFAULT NULL,
  `s17` varchar(300) DEFAULT NULL,
  `s21b` varchar(45) DEFAULT NULL,
  `s21a` varchar(45) DEFAULT NULL,
  `s22b` varchar(45) DEFAULT NULL,
  `s22a` varchar(45) DEFAULT NULL,
  `s23b` varchar(45) DEFAULT NULL,
  `s23a` varchar(45) DEFAULT NULL,
  `s24b` varchar(45) DEFAULT NULL,
  `s24a` varchar(45) DEFAULT NULL,
  `s25` varchar(300) DEFAULT NULL,
  `s26` varchar(300) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Globalcertification`
--

CREATE TABLE `Globalcertification` (
  `iid` int(11) NOT NULL,
  `sname` varchar(200) DEFAULT NULL,
  `emailid` varchar(300) DEFAULT NULL,
  `phoneno` varchar(12) DEFAULT NULL,
  `college` varchar(300) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `rtime` varchar(50) DEFAULT NULL,
  `referedby` varchar(50) DEFAULT NULL,
  `stream` varchar(50) DEFAULT NULL,
  `branch` varchar(50) DEFAULT NULL,
  `referralcode` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `internship`
--

CREATE TABLE `internship` (
  `iid` int(10) NOT NULL,
  `sname` varchar(200) DEFAULT NULL,
  `emailid` varchar(100) DEFAULT NULL,
  `phoneno` varchar(12) DEFAULT NULL,
  `college` varchar(300) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `rtime` varchar(50) NOT NULL,
  `referedby` varchar(50) DEFAULT NULL,
  `stream` varchar(50) NOT NULL,
  `branch` varchar(200) DEFAULT NULL,
  `referralcode` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `menquiry`
--

CREATE TABLE `menquiry` (
  `eid` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(40) DEFAULT NULL,
  `contact` varchar(11) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `exam` varchar(400) DEFAULT NULL,
  `stime` varchar(100) NOT NULL,
  `college` varchar(150) DEFAULT NULL,
  `referedby` varchar(50) DEFAULT NULL,
  `stream` varchar(50) NOT NULL,
  `referralcode` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `MSBTInternship`
--

CREATE TABLE `MSBTInternship` (
  `iid` int(10) NOT NULL,
  `sname` varchar(200) NOT NULL,
  `emailid` varchar(100) NOT NULL,
  `phoneno` varchar(12) NOT NULL,
  `college` varchar(300) NOT NULL,
  `city` varchar(100) NOT NULL,
  `rtime` varchar(50) NOT NULL,
  `referedby` varchar(50) NOT NULL,
  `stream` varchar(50) NOT NULL,
  `branch` varchar(200) DEFAULT NULL,
  `referralcode` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Mtimeslot`
--

CREATE TABLE `Mtimeslot` (
  `tid` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phoneno` varchar(10) DEFAULT NULL,
  `exam` varchar(400) DEFAULT NULL,
  `dateslot` varchar(10) DEFAULT NULL,
  `stime` varchar(100) NOT NULL,
  `college` varchar(250) NOT NULL,
  `city` varchar(50) NOT NULL,
  `stream` varchar(50) NOT NULL,
  `referralcode` varchar(50) DEFAULT NULL,
  `certiportusername` varchar(100) DEFAULT NULL,
  `certiportpassword` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

CREATE TABLE `question` (
  `qid` int(11) NOT NULL,
  `que` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `fname` varchar(500) DEFAULT NULL,
  `lname` varchar(500) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `password` varchar(500) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `dob` varchar(45) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `city` varchar(500) DEFAULT NULL,
  `state` varchar(500) DEFAULT NULL,
  `10schoolname` varchar(500) DEFAULT NULL,
  `10board` varchar(500) DEFAULT NULL,
  `10passingyear` varchar(45) DEFAULT NULL,
  `10percentage` varchar(45) DEFAULT NULL,
  `12schoolname` varchar(500) DEFAULT NULL,
  `12board` varchar(500) DEFAULT NULL,
  `12stream` varchar(500) DEFAULT NULL,
  `12passinyear` varchar(45) DEFAULT NULL,
  `12percentage` varchar(45) DEFAULT NULL,
  `gradcollegename` varchar(500) DEFAULT NULL,
  `gradunivercity` varchar(500) DEFAULT NULL,
  `gradstream` varchar(500) DEFAULT NULL,
  `gradpassingyear` varchar(500) DEFAULT NULL,
  `gradpercentage` varchar(45) DEFAULT NULL,
  `mascollegename` varchar(500) DEFAULT NULL,
  `masunivercity` varchar(500) DEFAULT NULL,
  `masstream` varchar(500) DEFAULT NULL,
  `maspassingyear` varchar(45) DEFAULT NULL,
  `maspercentage` varchar(45) DEFAULT NULL,
  `diplomaschoolname` varchar(500) DEFAULT NULL,
  `diplomaboard` varchar(500) DEFAULT NULL,
  `diplomastream` varchar(500) DEFAULT NULL,
  `diplomapassingyear` varchar(45) DEFAULT NULL,
  `diplomapercentage` varchar(45) DEFAULT NULL,
  `resume` longblob,
  `profile` longblob,
  `datetime` varchar(45) DEFAULT NULL,
  `previouslogin` varchar(45) DEFAULT NULL,
  `spendtime` varchar(45) DEFAULT NULL,
  `certification` varchar(500) DEFAULT NULL,
  `certificationname` varchar(500) DEFAULT NULL,
  `certificationtechnology` varchar(1000) DEFAULT NULL,
  `Projectname` varchar(10000) DEFAULT NULL,
  `projectdetails` varchar(10000) DEFAULT NULL,
  `ExtraActivities` varchar(5000) DEFAULT NULL,
  `technicalskill` varchar(5000) DEFAULT NULL,
  `softskill` varchar(5000) DEFAULT NULL,
  `domain` varchar(5000) DEFAULT NULL,
  `referalCode` varchar(45) DEFAULT NULL,
  `rCodeCount` int(11) DEFAULT NULL,
  `country_code` int(11) DEFAULT NULL,
  `active` int(11) DEFAULT NULL,
  `pdomain` varchar(500) DEFAULT NULL,
  `technology` varchar(500) DEFAULT NULL,
  `application` varchar(500) DEFAULT NULL,
  `duration` varchar(500) DEFAULT NULL,
  `model` varchar(500) DEFAULT NULL,
  `req` varchar(500) DEFAULT NULL,
  `ana` varchar(500) DEFAULT NULL,
  `des` varchar(500) DEFAULT NULL,
  `cod` varchar(500) DEFAULT NULL,
  `test` varchar(500) DEFAULT NULL,
  `cost` varchar(500) DEFAULT NULL,
  `testType` varchar(500) DEFAULT NULL,
  `testDur` varchar(500) DEFAULT NULL,
  `no_issue` varchar(500) DEFAULT NULL,
  `knowledge_req` varchar(500) DEFAULT NULL,
  `knowledge_ana` varchar(500) DEFAULT NULL,
  `knowledge_des` varchar(500) DEFAULT NULL,
  `knowledge_cod` varchar(500) DEFAULT NULL,
  `knowledge_test` varchar(500) DEFAULT NULL,
  `knowledge_postimpl` varchar(500) DEFAULT NULL,
  `Referal_text` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student_assignment_data`
--

CREATE TABLE `student_assignment_data` (
  `student_assignment_id` int(11) NOT NULL,
  `examination_id` varchar(100) NOT NULL,
  `student_id` int(11) DEFAULT NULL,
  `assign_id` varchar(255) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `assign_subject` varchar(100) NOT NULL,
  `assign_level` varchar(350) NOT NULL,
  `assign_topic` varchar(350) NOT NULL,
  `assign_descrition` varchar(350) NOT NULL,
  `assign_code` text,
  `assign_status` varchar(255) DEFAULT 'Assigned',
  `assign_flag` varchar(100) DEFAULT 'No',
  `assign_result` varchar(350) DEFAULT 'Pending',
  `create_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `studynotes`
--

CREATE TABLE `studynotes` (
  `id` int(11) NOT NULL,
  `lang` varchar(45) NOT NULL,
  `sid` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `studynotes1`
--

CREATE TABLE `studynotes1` (
  `cid` int(10) UNSIGNED NOT NULL,
  `heading` varchar(405) DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `studynotes2`
--

CREATE TABLE `studynotes2` (
  `id` int(10) UNSIGNED NOT NULL,
  `heading` varchar(405) DEFAULT NULL,
  `file` longblob,
  `cid` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `subject_level_table`
--

CREATE TABLE `subject_level_table` (
  `lid` int(11) NOT NULL,
  `sid` int(11) NOT NULL,
  `lname` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `subject_topics_test`
--

CREATE TABLE `subject_topics_test` (
  `tid` int(11) NOT NULL,
  `lid` int(11) NOT NULL,
  `topic_name` varchar(1500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `teacher`
--

CREATE TABLE `teacher` (
  `id` int(11) NOT NULL,
  `fname` varchar(45) DEFAULT NULL,
  `lname` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `datetime` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `test_info_table`
--

CREATE TABLE `test_info_table` (
  `tifid` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `qid` int(11) NOT NULL,
  `answer` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `test_result_table`
--

CREATE TABLE `test_result_table` (
  `rid` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `result` int(50) NOT NULL,
  `lid` int(11) NOT NULL,
  `ans` int(11) NOT NULL,
  `notans` int(11) NOT NULL,
  `sid` int(11) NOT NULL,
  `level` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `test_subject_table`
--

CREATE TABLE `test_subject_table` (
  `sid` int(11) NOT NULL,
  `sname` varchar(145) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `timeslot`
--

CREATE TABLE `timeslot` (
  `tid` int(11) NOT NULL,
  `name` varchar(500) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phoneno` varchar(50) DEFAULT NULL,
  `college` varchar(500) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `exam` varchar(100) DEFAULT NULL,
  `dateslot` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `topics_test_question`
--

CREATE TABLE `topics_test_question` (
  `qid` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `question` varchar(5000) NOT NULL,
  `option1` varchar(1000) NOT NULL,
  `option2` varchar(1000) NOT NULL,
  `option3` varchar(1000) NOT NULL,
  `option4` varchar(1000) NOT NULL,
  `answer` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `unanseredque`
--

CREATE TABLE `unanseredque` (
  `qid` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  `que` varchar(450) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `assignment`
--
ALTER TABLE `assignment`
  ADD PRIMARY KEY (`assign_id`);

--
-- Indexes for table `assignmentinfo`
--
ALTER TABLE `assignmentinfo`
  ADD PRIMARY KEY (`aid`);

--
-- Indexes for table `assignment_1`
--
ALTER TABLE `assignment_1`
  ADD PRIMARY KEY (`aid`);

--
-- Indexes for table `assignment_2`
--
ALTER TABLE `assignment_2`
  ADD PRIMARY KEY (`rid`);

--
-- Indexes for table `assignment_detail`
--
ALTER TABLE `assignment_detail`
  ADD PRIMARY KEY (`assignment_detail_id`);

--
-- Indexes for table `assignment_forum_query`
--
ALTER TABLE `assignment_forum_query`
  ADD PRIMARY KEY (`assignment_forum_id`);

--
-- Indexes for table `assignment_forum_solution`
--
ALTER TABLE `assignment_forum_solution`
  ADD PRIMARY KEY (`assignment_forum_solution_id`),
  ADD KEY `assignment_forum_id` (`assignment_forum_id`);

--
-- Indexes for table `assignment_question`
--
ALTER TABLE `assignment_question`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `assign_id_idx` (`assign_id`);

--
-- Indexes for table `assignment_subject`
--
ALTER TABLE `assignment_subject`
  ADD PRIMARY KEY (`assignment_subject_id`);

--
-- Indexes for table `assign_status`
--
ALTER TABLE `assign_status`
  ADD PRIMARY KEY (`assignid`);

--
-- Indexes for table `Billing_details`
--
ALTER TABLE `Billing_details`
  ADD PRIMARY KEY (`bid`);

--
-- Indexes for table `certificate`
--
ALTER TABLE `certificate`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `certifiedstudent`
--
ALTER TABLE `certifiedstudent`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `company`
--
ALTER TABLE `company`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `contact`
--
ALTER TABLE `contact`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `countries`
--
ALTER TABLE `countries`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `csrinternship`
--
ALTER TABLE `csrinternship`
  ADD PRIMARY KEY (`iid`);

--
-- Indexes for table `enquiry`
--
ALTER TABLE `enquiry`
  ADD PRIMARY KEY (`eid`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`fid`);

--
-- Indexes for table `Globalcertification`
--
ALTER TABLE `Globalcertification`
  ADD PRIMARY KEY (`iid`);

--
-- Indexes for table `internship`
--
ALTER TABLE `internship`
  ADD PRIMARY KEY (`iid`);

--
-- Indexes for table `menquiry`
--
ALTER TABLE `menquiry`
  ADD PRIMARY KEY (`eid`);

--
-- Indexes for table `MSBTInternship`
--
ALTER TABLE `MSBTInternship`
  ADD PRIMARY KEY (`iid`);

--
-- Indexes for table `Mtimeslot`
--
ALTER TABLE `Mtimeslot`
  ADD PRIMARY KEY (`tid`);

--
-- Indexes for table `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`qid`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `student_assignment_data`
--
ALTER TABLE `student_assignment_data`
  ADD PRIMARY KEY (`student_assignment_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `studynotes`
--
ALTER TABLE `studynotes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_foreighnkey_idx` (`sid`);

--
-- Indexes for table `studynotes1`
--
ALTER TABLE `studynotes1`
  ADD PRIMARY KEY (`cid`),
  ADD KEY `id_idx` (`id`);

--
-- Indexes for table `studynotes2`
--
ALTER TABLE `studynotes2`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_studynotes2_1` (`cid`);

--
-- Indexes for table `subject_level_table`
--
ALTER TABLE `subject_level_table`
  ADD PRIMARY KEY (`lid`),
  ADD KEY `foregin_key1_idx` (`sid`);

--
-- Indexes for table `subject_topics_test`
--
ALTER TABLE `subject_topics_test`
  ADD PRIMARY KEY (`tid`),
  ADD KEY `foreign_key2_idx` (`lid`);

--
-- Indexes for table `teacher`
--
ALTER TABLE `teacher`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `test_info_table`
--
ALTER TABLE `test_info_table`
  ADD PRIMARY KEY (`tifid`),
  ADD KEY `fkt3_idx` (`tid`);

--
-- Indexes for table `test_result_table`
--
ALTER TABLE `test_result_table`
  ADD PRIMARY KEY (`rid`),
  ADD KEY `result_fk_idx` (`tid`);

--
-- Indexes for table `test_subject_table`
--
ALTER TABLE `test_subject_table`
  ADD PRIMARY KEY (`sid`);

--
-- Indexes for table `timeslot`
--
ALTER TABLE `timeslot`
  ADD PRIMARY KEY (`tid`);

--
-- Indexes for table `topics_test_question`
--
ALTER TABLE `topics_test_question`
  ADD PRIMARY KEY (`qid`),
  ADD KEY `foreign_key3_idx` (`tid`);

--
-- Indexes for table `unanseredque`
--
ALTER TABLE `unanseredque`
  ADD PRIMARY KEY (`qid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment`
--
ALTER TABLE `assignment`
  MODIFY `assign_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignmentinfo`
--
ALTER TABLE `assignmentinfo`
  MODIFY `aid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_1`
--
ALTER TABLE `assignment_1`
  MODIFY `aid` int(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_2`
--
ALTER TABLE `assignment_2`
  MODIFY `rid` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_detail`
--
ALTER TABLE `assignment_detail`
  MODIFY `assignment_detail_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_forum_query`
--
ALTER TABLE `assignment_forum_query`
  MODIFY `assignment_forum_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_forum_solution`
--
ALTER TABLE `assignment_forum_solution`
  MODIFY `assignment_forum_solution_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_question`
--
ALTER TABLE `assignment_question`
  MODIFY `question_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment_subject`
--
ALTER TABLE `assignment_subject`
  MODIFY `assignment_subject_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assign_status`
--
ALTER TABLE `assign_status`
  MODIFY `assignid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Billing_details`
--
ALTER TABLE `Billing_details`
  MODIFY `bid` int(5) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `certificate`
--
ALTER TABLE `certificate`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `certifiedstudent`
--
ALTER TABLE `certifiedstudent`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `company`
--
ALTER TABLE `company`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `contact`
--
ALTER TABLE `contact`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `countries`
--
ALTER TABLE `countries`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `csrinternship`
--
ALTER TABLE `csrinternship`
  MODIFY `iid` int(5) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `enquiry`
--
ALTER TABLE `enquiry`
  MODIFY `eid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `fid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Globalcertification`
--
ALTER TABLE `Globalcertification`
  MODIFY `iid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `internship`
--
ALTER TABLE `internship`
  MODIFY `iid` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `menquiry`
--
ALTER TABLE `menquiry`
  MODIFY `eid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `MSBTInternship`
--
ALTER TABLE `MSBTInternship`
  MODIFY `iid` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Mtimeslot`
--
ALTER TABLE `Mtimeslot`
  MODIFY `tid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `question`
--
ALTER TABLE `question`
  MODIFY `qid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student_assignment_data`
--
ALTER TABLE `student_assignment_data`
  MODIFY `student_assignment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studynotes`
--
ALTER TABLE `studynotes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studynotes1`
--
ALTER TABLE `studynotes1`
  MODIFY `cid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studynotes2`
--
ALTER TABLE `studynotes2`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subject_level_table`
--
ALTER TABLE `subject_level_table`
  MODIFY `lid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subject_topics_test`
--
ALTER TABLE `subject_topics_test`
  MODIFY `tid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `teacher`
--
ALTER TABLE `teacher`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `test_info_table`
--
ALTER TABLE `test_info_table`
  MODIFY `tifid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `test_result_table`
--
ALTER TABLE `test_result_table`
  MODIFY `rid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `test_subject_table`
--
ALTER TABLE `test_subject_table`
  MODIFY `sid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `timeslot`
--
ALTER TABLE `timeslot`
  MODIFY `tid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `topics_test_question`
--
ALTER TABLE `topics_test_question`
  MODIFY `qid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `unanseredque`
--
ALTER TABLE `unanseredque`
  MODIFY `qid` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `assignment_forum_solution`
--
ALTER TABLE `assignment_forum_solution`
  ADD CONSTRAINT `assignment_forum_solution_ibfk_1` FOREIGN KEY (`assignment_forum_id`) REFERENCES `assignment_forum_query` (`assignment_forum_id`);

--
-- Constraints for table `assignment_question`
--
ALTER TABLE `assignment_question`
  ADD CONSTRAINT `assign_id` FOREIGN KEY (`assign_id`) REFERENCES `assignment` (`assign_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `student_assignment_data`
--
ALTER TABLE `student_assignment_data`
  ADD CONSTRAINT `student_assignment_data_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

--
-- Constraints for table `studynotes1`
--
ALTER TABLE `studynotes1`
  ADD CONSTRAINT `id` FOREIGN KEY (`id`) REFERENCES `studynotes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `studynotes2`
--
ALTER TABLE `studynotes2`
  ADD CONSTRAINT `FK_studynotes2_1` FOREIGN KEY (`cid`) REFERENCES `studynotes1` (`cid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `subject_level_table`
--
ALTER TABLE `subject_level_table`
  ADD CONSTRAINT `foregin_key1` FOREIGN KEY (`sid`) REFERENCES `test_subject_table` (`sid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `subject_topics_test`
--
ALTER TABLE `subject_topics_test`
  ADD CONSTRAINT `foreign_key2` FOREIGN KEY (`lid`) REFERENCES `subject_level_table` (`lid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `test_info_table`
--
ALTER TABLE `test_info_table`
  ADD CONSTRAINT `fkt3` FOREIGN KEY (`tid`) REFERENCES `subject_topics_test` (`tid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `test_result_table`
--
ALTER TABLE `test_result_table`
  ADD CONSTRAINT `result_fk` FOREIGN KEY (`tid`) REFERENCES `subject_topics_test` (`tid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `topics_test_question`
--
ALTER TABLE `topics_test_question`
  ADD CONSTRAINT `foreign_key3` FOREIGN KEY (`tid`) REFERENCES `subject_topics_test` (`tid`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

package com.onlineinternshipin.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Student domain model aligning directly with the production 'student' table
 * defined in Database/onlineinternship.sql.
 *
 * Extends User for backward compatibility with existing session management,
 * authorization filters, dashboard reports, and JSP EL expressions.
 */
public class Student extends User {

    // -------------------------------------------------------------------------
    // Core Identity & Profile Fields (production 'student' columns)
    // -------------------------------------------------------------------------
    private int id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private String password;
    private String gender;
    private String dob;
    private String address;
    private String city;
    private String state;

    // Academic Details - 10th / SSC
    private String school10Name;    // 10schoolname
    private String board10;         // 10board
    private String passingYear10;   // 10passingyear
    private String percentage10;    // 10percentage

    // Academic Details - 12th / HSC
    private String school12Name;    // 12schoolname
    private String board12;         // 12board
    private String stream12;        // 12stream
    private String passingYear12;   // 12passinyear
    private String percentage12;    // 12percentage

    // Academic Details - Diploma
    private String diplomaSchoolName;  // diplomaschoolname
    private String diplomaBoard;       // diplomaboard
    private String diplomaStream;      // diplomastream
    private String diplomaPassingYear; // diplomapassingyear
    private String diplomaPercentage;  // diplomapercentage

    // Academic Details - Graduation
    private String gradCollegeName;    // gradcollegename
    private String gradUniversity;     // gradunivercity
    private String gradStream;         // gradstream
    private String gradPassingYear;    // gradpassingyear
    private String gradPercentage;     // gradpercentage

    // Academic Details - Master / Post-Graduation
    private String masCollegeName;     // mascollegename
    private String masUniversity;      // masunivercity
    private String masStream;          // masstream
    private String masPassingYear;     // maspassingyear
    private String masPercentage;      // maspercentage

    // Metadata, Files & Tracking
    private byte[] resumeData;         // resume (longblob)
    private byte[] profileData;        // profile (longblob)
    private String datetime;           // datetime (VARCHAR(45))
    private String previousLogin;      // previouslogin (VARCHAR(45))
    private String spendTime;          // spendtime (VARCHAR(45))

    // Certifications & Projects
    private String certification;            // certification
    private String certificationName;        // certificationname
    private String certificationTechnology;  // certificationtechnology
    private String projectName;              // Projectname
    private String projectDetails;           // projectdetails
    private String extraActivities;          // ExtraActivities

    // Skills
    private String technicalSkill;           // technicalskill
    private String softSkill;                // softskill
    private String domainSkill;              // domain

    // Referral & Status
    private String referalCode;              // referalCode
    private Integer rCodeCount;              // rCodeCount
    private Integer countryCodeInt;          // country_code (int)
    private Integer active;                  // active (1 = active, 0 = inactive)
    private String referalText;              // Referal_text

    // Optional project planning columns
    private String pdomain;
    private String technology;
    private String application;
    private String duration;
    private String model;
    private String req;
    private String ana;
    private String des;
    private String cod;
    private String test;
    private String cost;
    private String testType;
    private String testDur;
    private String noIssue;
    private String knowledgeReq;
    private String knowledgeAna;
    private String knowledgeDes;
    private String knowledgeCod;
    private String knowledgeTest;
    private String knowledgePostImpl;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public Student() {
        super();
        setRole("STUDENT");
    }

    public Student(int id, String fname, String lname, String phone, String email, String password) {
        this();
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        syncToUser();
    }

    // -------------------------------------------------------------------------
    // Sync helper between Student and inherited User fields
    // -------------------------------------------------------------------------
    public void syncToUser() {
        super.setUserId(this.id);
        super.setFirstName(this.fname);
        super.setLastName(this.lname);
        super.setEmail(this.email);
        super.setPasswordHash(this.password);
        super.setMobile(this.phone);
        super.setCity(this.city);
        super.setCountryCode(this.countryCodeInt != null ? "+" + this.countryCodeInt : "+91");
        super.setReferralCode(this.referalCode != null ? this.referalCode : this.referalText);
        super.setRole("STUDENT");
        super.setActive(this.active == null || this.active != 0);

        String st = this.gradStream != null && !this.gradStream.trim().isEmpty() ? this.gradStream :
                    (this.diplomaStream != null && !this.diplomaStream.trim().isEmpty() ? this.diplomaStream : this.stream12);
        super.setStream(st != null ? st : "Graduation");
        super.setBranch(st != null ? st : "");

        String clg = this.gradCollegeName != null && !this.gradCollegeName.trim().isEmpty() ? this.gradCollegeName :
                     (this.diplomaSchoolName != null && !this.diplomaSchoolName.trim().isEmpty() ? this.diplomaSchoolName : this.school12Name);
        super.setCollegeName(clg != null ? clg : "");
    }

    // -------------------------------------------------------------------------
    // Overridden User Methods for seamless interoperability
    // -------------------------------------------------------------------------
    @Override
    public int getUserId() { return this.id > 0 ? this.id : super.getUserId(); }
    @Override
    public void setUserId(int userId) { this.id = userId; super.setUserId(userId); }

    @Override
    public String getFirstName() { return this.fname != null ? this.fname : super.getFirstName(); }
    @Override
    public void setFirstName(String firstName) { this.fname = firstName; super.setFirstName(firstName); }

    @Override
    public String getLastName() { return this.lname != null ? this.lname : super.getLastName(); }
    @Override
    public void setLastName(String lastName) { this.lname = lastName; super.setLastName(lastName); }

    @Override
    public String getFullName() {
        String f = getFirstName();
        String l = getLastName();
        if (f == null && l == null) return "";
        if (f == null) return l;
        if (l == null) return f;
        return (f + " " + l).trim();
    }

    @Override
    public String getEmail() { return this.email != null ? this.email : super.getEmail(); }
    @Override
    public void setEmail(String email) { this.email = email; super.setEmail(email); }

    @Override
    public String getMobile() { return this.phone != null ? this.phone : super.getMobile(); }
    @Override
    public void setMobile(String mobile) { this.phone = mobile; super.setMobile(mobile); }

    @Override
    public String getPasswordHash() { return this.password != null ? this.password : super.getPasswordHash(); }
    @Override
    public void setPasswordHash(String passwordHash) { this.password = passwordHash; super.setPasswordHash(passwordHash); }

    @Override
    public String getCity() { return this.city != null ? this.city : super.getCity(); }
    @Override
    public void setCity(String city) { this.city = city; super.setCity(city); }

    @Override
    public boolean isActive() {
        return this.active == null || this.active != 0;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active ? 1 : 0;
        super.setActive(active);
    }

    @Override
    public String getReferralCode() {
        if (this.referalCode != null && !this.referalCode.trim().isEmpty()) return this.referalCode;
        if (this.referalText != null && !this.referalText.trim().isEmpty()) return this.referalText;
        return super.getReferralCode();
    }

    @Override
    public void setReferralCode(String referralCode) {
        this.referalCode = referralCode;
        this.referalText = referralCode;
        super.setReferralCode(referralCode);
    }

    @Override
    public String getCollegeName() {
        if (this.gradCollegeName != null && !this.gradCollegeName.trim().isEmpty()) return this.gradCollegeName;
        if (this.diplomaSchoolName != null && !this.diplomaSchoolName.trim().isEmpty()) return this.diplomaSchoolName;
        if (this.school12Name != null && !this.school12Name.trim().isEmpty()) return this.school12Name;
        return super.getCollegeName();
    }

    @Override
    public String getStream() {
        if (this.gradStream != null && !this.gradStream.trim().isEmpty()) return this.gradStream;
        if (this.diplomaStream != null && !this.diplomaStream.trim().isEmpty()) return this.diplomaStream;
        if (this.stream12 != null && !this.stream12.trim().isEmpty()) return this.stream12;
        return super.getStream();
    }

    @Override
    public String getBranch() {
        return getStream();
    }

    // -------------------------------------------------------------------------
    // Getters and Setters matching exact production column names
    // -------------------------------------------------------------------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; super.setUserId(id); }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; super.setFirstName(fname); }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; super.setLastName(lname); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; super.setMobile(phone); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; super.setPasswordHash(password); }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public Date getDateOfBirth() {
        if (dob == null || dob.trim().isEmpty()) return null;
        try {
            return Date.valueOf(dob.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setDateOfBirth(Date date) {
        if (date != null) {
            this.dob = date.toString();
        }
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // 10th
    public String getSchool10Name() { return school10Name; }
    public void setSchool10Name(String school10Name) { this.school10Name = school10Name; }

    public String getBoard10() { return board10; }
    public void setBoard10(String board10) { this.board10 = board10; }

    public String getPassingYear10() { return passingYear10; }
    public void setPassingYear10(String passingYear10) { this.passingYear10 = passingYear10; }

    public String getPercentage10() { return percentage10; }
    public void setPercentage10(String percentage10) { this.percentage10 = percentage10; }

    // 12th
    public String getSchool12Name() { return school12Name; }
    public void setSchool12Name(String school12Name) { this.school12Name = school12Name; }

    public String getBoard12() { return board12; }
    public void setBoard12(String board12) { this.board12 = board12; }

    public String getStream12() { return stream12; }
    public void setStream12(String stream12) { this.stream12 = stream12; }

    public String getPassingYear12() { return passingYear12; }
    public void setPassingYear12(String passingYear12) { this.passingYear12 = passingYear12; }

    public String getPercentage12() { return percentage12; }
    public void setPercentage12(String percentage12) { this.percentage12 = percentage12; }

    // Diploma
    public String getDiplomaSchoolName() { return diplomaSchoolName; }
    public void setDiplomaSchoolName(String diplomaSchoolName) { this.diplomaSchoolName = diplomaSchoolName; }

    public String getDiplomaBoard() { return diplomaBoard; }
    public void setDiplomaBoard(String diplomaBoard) { this.diplomaBoard = diplomaBoard; }

    public String getDiplomaStream() { return diplomaStream; }
    public void setDiplomaStream(String diplomaStream) { this.diplomaStream = diplomaStream; }

    public String getDiplomaPassingYear() { return diplomaPassingYear; }
    public void setDiplomaPassingYear(String diplomaPassingYear) { this.diplomaPassingYear = diplomaPassingYear; }

    public String getDiplomaPercentage() { return diplomaPercentage; }
    public void setDiplomaPercentage(String diplomaPercentage) { this.diplomaPercentage = diplomaPercentage; }

    // Graduation
    public String getGradCollegeName() { return gradCollegeName; }
    public void setGradCollegeName(String gradCollegeName) { this.gradCollegeName = gradCollegeName; }

    public String getGradUniversity() { return gradUniversity; }
    public void setGradUniversity(String gradUniversity) { this.gradUniversity = gradUniversity; }

    public String getGradStream() { return gradStream; }
    public void setGradStream(String gradStream) { this.gradStream = gradStream; }

    public String getGradPassingYear() { return gradPassingYear; }
    public void setGradPassingYear(String gradPassingYear) { this.gradPassingYear = gradPassingYear; }

    public String getGradPercentage() { return gradPercentage; }
    public void setGradPercentage(String gradPercentage) { this.gradPercentage = gradPercentage; }

    // Master
    public String getMasCollegeName() { return masCollegeName; }
    public void setMasCollegeName(String masCollegeName) { this.masCollegeName = masCollegeName; }

    public String getMasUniversity() { return masUniversity; }
    public void setMasUniversity(String masUniversity) { this.masUniversity = masUniversity; }

    public String getMasStream() { return masStream; }
    public void setMasStream(String masStream) { this.masStream = masStream; }

    public String getMasPassingYear() { return masPassingYear; }
    public void setMasPassingYear(String masPassingYear) { this.masPassingYear = masPassingYear; }

    public String getMasPercentage() { return masPercentage; }
    public void setMasPercentage(String masPercentage) { this.masPercentage = masPercentage; }

    // Metadata & Files
    public byte[] getResumeData() { return resumeData; }
    public void setResumeData(byte[] resumeData) { this.resumeData = resumeData; }

    public byte[] getProfileData() { return profileData; }
    public void setProfileData(byte[] profileData) { this.profileData = profileData; }

    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }

    public String getPreviousLogin() { return previousLogin; }
    public void setPreviousLogin(String previousLogin) { this.previousLogin = previousLogin; }

    public String getSpendTime() { return spendTime; }
    public void setSpendTime(String spendTime) { this.spendTime = spendTime; }

    // Certifications & Projects
    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public String getCertificationName() { return certificationName; }
    public void setCertificationName(String certificationName) { this.certificationName = certificationName; }

    public String getCertificationTechnology() { return certificationTechnology; }
    public void setCertificationTechnology(String certificationTechnology) { this.certificationTechnology = certificationTechnology; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDetails() { return projectDetails; }
    public void setProjectDetails(String projectDetails) { this.projectDetails = projectDetails; }

    public String getExtraActivities() { return extraActivities; }
    public void setExtraActivities(String extraActivities) { this.extraActivities = extraActivities; }

    // Skills
    public String getTechnicalSkill() { return technicalSkill; }
    public void setTechnicalSkill(String technicalSkill) { this.technicalSkill = technicalSkill; }

    public String getSoftSkill() { return softSkill; }
    public void setSoftSkill(String softSkill) { this.softSkill = softSkill; }

    public String getDomainSkill() { return domainSkill; }
    public void setDomainSkill(String domainSkill) { this.domainSkill = domainSkill; }

    // Referral
    public String getReferalCode() { return referalCode; }
    public void setReferalCode(String referalCode) { this.referalCode = referalCode; }

    public Integer getRCodeCount() { return rCodeCount; }
    public void setRCodeCount(Integer rCodeCount) { this.rCodeCount = rCodeCount; }

    public Integer getCountryCodeInt() { return countryCodeInt; }
    public void setCountryCodeInt(Integer countryCodeInt) { this.countryCodeInt = countryCodeInt; }

    public Integer getActive() { return active; }
    public void setActive(Integer active) { this.active = active; super.setActive(active == null || active != 0); }

    public String getReferalText() { return referalText; }
    public void setReferalText(String referalText) { this.referalText = referalText; }

    public String getPdomain() { return pdomain; }
    public void setPdomain(String pdomain) { this.pdomain = pdomain; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getReq() { return req; }
    public void setReq(String req) { this.req = req; }

    public String getAna() { return ana; }
    public void setAna(String ana) { this.ana = ana; }

    public String getDes() { return des; }
    public void setDes(String des) { this.des = des; }

    public String getCod() { return cod; }
    public void setCod(String cod) { this.cod = cod; }

    public String getTest() { return test; }
    public void setTest(String test) { this.test = test; }

    public String getCost() { return cost; }
    public void setCost(String cost) { this.cost = cost; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getTestDur() { return testDur; }
    public void setTestDur(String testDur) { this.testDur = testDur; }

    public String getNoIssue() { return noIssue; }
    public void setNoIssue(String noIssue) { this.noIssue = noIssue; }

    public String getKnowledgeReq() { return knowledgeReq; }
    public void setKnowledgeReq(String knowledgeReq) { this.knowledgeReq = knowledgeReq; }

    public String getKnowledgeAna() { return knowledgeAna; }
    public void setKnowledgeAna(String knowledgeAna) { this.knowledgeAna = knowledgeAna; }

    public String getKnowledgeDes() { return knowledgeDes; }
    public void setKnowledgeDes(String knowledgeDes) { this.knowledgeDes = knowledgeDes; }

    public String getKnowledgeCod() { return knowledgeCod; }
    public void setKnowledgeCod(String knowledgeCod) { this.knowledgeCod = knowledgeCod; }

    public String getKnowledgeTest() { return knowledgeTest; }
    public void setKnowledgeTest(String knowledgeTest) { this.knowledgeTest = knowledgeTest; }

    public String getKnowledgePostImpl() { return knowledgePostImpl; }
    public void setKnowledgePostImpl(String knowledgePostImpl) { this.knowledgePostImpl = knowledgePostImpl; }
}

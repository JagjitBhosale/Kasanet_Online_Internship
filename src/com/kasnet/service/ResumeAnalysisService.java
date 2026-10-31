package com.kasnet.service;

import com.kasnet.dao.*;
import com.kasnet.model.*;
import com.kasnet.util.ValidationUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service orchestrating business logic and persistence for the
 * Resume Analysis / Student Profile Information module.
 *
 * Implements strict ownership checking, server-side data validation,
 * and readiness evaluation for Phase 2 AI Analysis integration.
 */
public class ResumeAnalysisService {

    private static final Logger LOGGER = Logger.getLogger(ResumeAnalysisService.class.getName());

    private final CareerGoalDAO      careerGoalDAO;
    private final SkillDAO           skillDAO;
    private final ProjectDAO         projectDAO;
    private final ExperienceDAO      experienceDAO;
    private final CertificationDAO   certificationDAO;
    private final EducationDAO       educationDAO;
    private final ProfileDAO         profileDAO;
    private final ProfileAnalysisDAO profileAnalysisDAO;
    private final GroqAIService      groqAIService;
    private final UserDAO            userDAO;

    public ResumeAnalysisService() {
        // Ensure required tables exist
        ResumeAnalysisDAO.ensureSchema();

        this.careerGoalDAO      = new CareerGoalDAO();
        this.skillDAO           = new SkillDAO();
        this.projectDAO         = new ProjectDAO();
        this.experienceDAO      = new ExperienceDAO();
        this.certificationDAO   = new CertificationDAO();
        this.educationDAO       = new EducationDAO();
        this.profileDAO         = new ProfileDAO();
        this.profileAnalysisDAO = new ProfileAnalysisDAO();
        this.groqAIService      = new GroqAIService();
        this.userDAO            = new UserDAO();
    }

    // -------------------------------------------------------------------------
    // 1. CAREER GOAL
    // -------------------------------------------------------------------------

    public CareerGoal getCareerGoal(int userId) {
        return careerGoalDAO.getCareerGoalByUserId(userId);
    }

    public boolean saveCareerGoal(int userId, String targetRole, String targetRoleOther,
                                  String preferredDomain, String preferredDomainOther,
                                  String careerObjective) throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (targetRole == null || targetRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Target role is required.");
        }
        if ("Other".equalsIgnoreCase(targetRole.trim()) && (targetRoleOther == null || targetRoleOther.trim().isEmpty())) {
            throw new IllegalArgumentException("Please specify your target role.");
        }
        if ("Other".equalsIgnoreCase(preferredDomain != null ? preferredDomain.trim() : "")
                && (preferredDomainOther == null || preferredDomainOther.trim().isEmpty())) {
            throw new IllegalArgumentException("Please specify your preferred domain.");
        }

        CareerGoal goal = new CareerGoal();
        goal.setUserId(userId);
        goal.setTargetRole(targetRole.trim());
        goal.setTargetRoleOther(targetRoleOther != null ? targetRoleOther.trim() : null);
        goal.setPreferredDomain(preferredDomain != null ? preferredDomain.trim() : null);
        goal.setPreferredDomainOther(preferredDomainOther != null ? preferredDomainOther.trim() : null);
        goal.setCareerObjective(careerObjective != null ? careerObjective.trim() : null);

        boolean success = careerGoalDAO.saveOrUpdateCareerGoal(goal);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 2. SKILLS
    // -------------------------------------------------------------------------

    public List<Skill> getSkills(int userId) {
        return skillDAO.getSkillsByUserId(userId);
    }

    public Skill getSkillById(int skillId, int userId) {
        return skillDAO.getSkillById(skillId, userId);
    }

    public boolean saveSkill(int userId, int skillId, String skillName, String skillType)
            throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name is required.");
        }
        if (skillType == null || skillType.trim().isEmpty()) {
            skillType = "Programming Languages";
        }
        String trimmedName = skillName.trim();
        String trimmedType = skillType.trim();

        // Check for duplicate skill for the same user
        if (skillDAO.skillExists(userId, trimmedName, trimmedType, skillId)) {
            throw new IllegalArgumentException("The skill '" + trimmedName + "' is already in your profile.");
        }

        boolean success;
        if (skillId > 0) {
            Skill s = skillDAO.getSkillById(skillId, userId);
            if (s == null) {
                throw new IllegalArgumentException("Skill entry not found or unauthorized.");
            }
            s.setSkillName(trimmedName);
            s.setSkillType(trimmedType);
            success = skillDAO.updateSkill(s);
        } else {
            Skill s = new Skill();
            s.setUserId(userId);
            s.setSkillName(trimmedName);
            s.setSkillType(trimmedType);
            success = skillDAO.addSkill(s);
        }

        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    public boolean deleteSkill(int skillId, int userId) {
        boolean success = skillDAO.deleteSkill(skillId, userId);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 3. PROJECTS
    // -------------------------------------------------------------------------

    public List<Project> getProjects(int userId) {
        return projectDAO.getProjectsByUserId(userId);
    }

    public Project getProjectById(int projectId, int userId) {
        return projectDAO.getProjectById(projectId, userId);
    }

    public boolean saveProject(int userId, int projectId, String projectName, String description,
                               String technologiesUsed, String role, String projectType,
                               int teamSize, String startDateStr, String endDateStr,
                               String projectUrl, String githubUrl) throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project title is required.");
        }

        Project p = new Project();
        p.setUserId(userId);
        p.setProjectName(projectName.trim());
        p.setDescription(description != null ? description.trim() : null);
        p.setTechnologiesUsed(technologiesUsed != null ? technologiesUsed.trim() : null);
        p.setRole(role != null ? role.trim() : null);
        p.setProjectType(projectType != null && !projectType.trim().isEmpty() ? projectType.trim() : "Academic");
        p.setTeamSize(teamSize > 0 ? teamSize : 1);

        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                p.setStartDate(Date.valueOf(startDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                p.setEndDate(Date.valueOf(endDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        }

        p.setProjectUrl(projectUrl != null ? projectUrl.trim() : null);
        p.setGithubUrl(githubUrl != null ? githubUrl.trim() : null);

        boolean success;
        if (projectId > 0) {
            p.setProjectId(projectId);
            success = projectDAO.updateProject(p);
        } else {
            success = projectDAO.addProject(p);
        }

        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    public boolean deleteProject(int projectId, int userId) {
        boolean success = projectDAO.deleteProject(projectId, userId);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 4. EXPERIENCE
    // -------------------------------------------------------------------------

    public List<Experience> getExperiences(int userId) {
        return experienceDAO.getExperienceByUserId(userId);
    }

    public Experience getExperienceById(int experienceId, int userId) {
        return experienceDAO.getExperienceById(experienceId, userId);
    }

    public boolean saveExperience(int userId, int experienceId, String companyName, String role,
                                  String employmentType, String technologiesUsed, String description,
                                  String startDateStr, String endDateStr, boolean isCurrent) throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company / Organization name is required.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role / Title is required.");
        }

        Experience e = new Experience();
        e.setUserId(userId);
        e.setCompanyName(companyName.trim());
        e.setRole(role.trim());
        e.setEmploymentType(employmentType != null && !employmentType.trim().isEmpty() ? employmentType.trim() : "Full-time");
        e.setTechnologiesUsed(technologiesUsed != null ? technologiesUsed.trim() : null);
        e.setDescription(description != null ? description.trim() : null);
        e.setCurrent(isCurrent);

        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                e.setStartDate(Date.valueOf(startDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (!isCurrent && endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                e.setEndDate(Date.valueOf(endDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        } else {
            e.setEndDate(null);
        }

        boolean success;
        if (experienceId > 0) {
            e.setExperienceId(experienceId);
            success = experienceDAO.updateExperience(e);
        } else {
            success = experienceDAO.addExperience(e);
        }

        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    public boolean deleteExperience(int experienceId, int userId) {
        boolean success = experienceDAO.deleteExperience(experienceId, userId);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 5. CERTIFICATIONS
    // -------------------------------------------------------------------------

    public List<Certification> getCertifications(int userId) {
        return certificationDAO.getCertificationsByUserId(userId);
    }

    public Certification getCertificationById(int certId, int userId) {
        return certificationDAO.getCertificationById(certId, userId);
    }

    public boolean saveCertification(int userId, int certId, String certName, String provider,
                                     String technology, String certIdNumber, String issueDateStr,
                                     String expiryDateStr, String credentialUrl) throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (certName == null || certName.trim().isEmpty()) {
            throw new IllegalArgumentException("Certification name is required.");
        }

        Certification c = new Certification();
        c.setUserId(userId);
        c.setCertName(certName.trim());
        c.setProvider(provider != null ? provider.trim() : null);
        c.setTechnology(technology != null ? technology.trim() : null);
        c.setCertIdNumber(certIdNumber != null ? certIdNumber.trim() : null);
        c.setCredentialUrl(credentialUrl != null ? credentialUrl.trim() : null);

        if (issueDateStr != null && !issueDateStr.trim().isEmpty()) {
            try {
                c.setIssueDate(Date.valueOf(issueDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
            try {
                c.setExpiryDate(Date.valueOf(expiryDateStr.trim()));
            } catch (IllegalArgumentException ignored) {}
        }

        boolean success;
        if (certId > 0) {
            c.setCertId(certId);
            success = certificationDAO.updateCertification(c);
        } else {
            success = certificationDAO.addCertification(c);
        }

        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    public boolean deleteCertification(int certId, int userId) {
        boolean success = certificationDAO.deleteCertification(certId, userId);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 6. EDUCATION
    // -------------------------------------------------------------------------

    public List<Education> getEducations(int userId) {
        return educationDAO.getEducationByUserId(userId);
    }

    public Education getEducationById(int eduId, int userId) {
        return educationDAO.getEducationById(eduId, userId);
    }

    public boolean saveEducation(int userId, String level, String schoolCollegeName,
                                 String boardUniversity, String stream, String yearStr,
                                 String percentageStr) throws IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }
        if (level == null || level.trim().isEmpty()) {
            throw new IllegalArgumentException("Qualification level is required.");
        }
        if (schoolCollegeName == null || schoolCollegeName.trim().isEmpty()) {
            throw new IllegalArgumentException("College / School name is required.");
        }

        Education edu = new Education();
        edu.setUserId(userId);
        edu.setLevel(level.trim());
        edu.setSchoolCollegeName(schoolCollegeName.trim());
        edu.setBoardUniversity(boardUniversity != null ? boardUniversity.trim() : null);
        edu.setStream(stream != null ? stream.trim() : null);

        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                edu.setPassingYear(Integer.parseInt(yearStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (percentageStr != null && !percentageStr.trim().isEmpty()) {
            try {
                double pct = Double.parseDouble(percentageStr.trim());
                if (pct >= 0.0 && pct <= 100.0) {
                    edu.setPercentage(pct);
                }
            } catch (NumberFormatException ignored) {}
        }

        boolean success = educationDAO.saveOrUpdateEducation(edu);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    public boolean deleteEducation(int eduId, int userId) {
        boolean success = educationDAO.deleteEducation(eduId, userId);
        if (success) {
            syncProfileCompletion(userId);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // 7. READINESS EVALUATION (For "Analyze My Profile" Button)
    // -------------------------------------------------------------------------

    public ProfileReadiness evaluateReadiness(int userId) {
        ProfileReadiness r = new ProfileReadiness();
        if (userId <= 0) {
            return r;
        }

        CareerGoal goal = careerGoalDAO.getCareerGoalByUserId(userId);
        List<Skill> skills = skillDAO.getSkillsByUserId(userId);
        List<Project> projects = projectDAO.getProjectsByUserId(userId);
        List<Experience> experiences = experienceDAO.getExperienceByUserId(userId);
        List<Certification> certifications = certificationDAO.getCertificationsByUserId(userId);
        List<Education> educations = educationDAO.getEducationByUserId(userId);

        r.setHasCareerGoal(goal != null && goal.getTargetRole() != null && !goal.getTargetRole().trim().isEmpty());
        r.setSkillCount(skills != null ? skills.size() : 0);
        r.setProjectCount(projects != null ? projects.size() : 0);
        r.setExperienceCount(experiences != null ? experiences.size() : 0);
        r.setCertificationCount(certifications != null ? certifications.size() : 0);
        r.setEducationCount(educations != null ? educations.size() : 0);

        int score = 0;
        List<String> completed = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        // Career Goal (Weight: 25%)
        if (r.isHasCareerGoal()) {
            score += 25;
            completed.add("Career Goal (" + goal.getEffectiveTargetRole() + ")");
        } else {
            missing.add("Target Career Goal: specify your desired target role and domain so the analysis can contextualize recommendations.");
        }

        // Skills (Weight: 25%)
        if (r.getSkillCount() > 0) {
            score += 25;
            completed.add("Technical Skills (" + r.getSkillCount() + " declared)");
        } else {
            missing.add("Declared Skills: add at least one technical or professional skill.");
        }

        // Education (Weight: 20%)
        if (r.getEducationCount() > 0) {
            score += 20;
            completed.add("Academic Education (" + r.getEducationCount() + " records)");
        } else {
            missing.add("Education: add your highest qualification or graduation details.");
        }

        // Projects (Weight: 15% - empirical evidence)
        if (r.getProjectCount() > 0) {
            score += 15;
            completed.add("Projects (" + r.getProjectCount() + " cataloged)");
        } else {
            missing.add("Projects (Recommended): add at least 1 project to serve as evidence of your declared skills.");
        }

        // Experience (Weight: 10% - optional for freshers)
        if (r.getExperienceCount() > 0) {
            score += 10;
            completed.add("Professional Experience (" + r.getExperienceCount() + " records)");
        }

        // Certifications (Weight: 5% - optional)
        if (r.getCertificationCount() > 0) {
            score += 5;
            completed.add("Certifications (" + r.getCertificationCount() + " recorded)");
        }

        r.setReadinessScore(Math.min(100, score));
        r.setCompletedSections(completed);
        r.setMissingRecommendations(missing);

        // Core readiness for Phase 2: Career goal, skills, and education are required.
        // Projects, experience, and certifications are optional (freshers allowed).
        boolean coreReady = r.isHasCareerGoal() && r.getSkillCount() > 0 && r.getEducationCount() > 0;
        r.setReadyForAnalysis(coreReady);

        return r;
    }

    // -------------------------------------------------------------------------
    // 8. AI RESUME & SKILL GAP ANALYSIS ENGINE (GROQ LLM)
    // -------------------------------------------------------------------------

    public AIAnalysisResult getLatestAIAnalysis(int userId) {
        if (userId <= 0) return null;
        return profileAnalysisDAO.getLatestAnalysisByUserId(userId);
    }

    public List<AIAnalysisResult> getAIAnalysisHistory(int userId) {
        if (userId <= 0) return new ArrayList<>();
        return profileAnalysisDAO.getAnalysisHistory(userId);
    }

    public boolean isGroqConfigured() {
        return groqAIService.isConfigured();
    }

    public String getMaskedGroqKey() {
        return groqAIService.getMaskedApiKey();
    }

    public boolean saveGroqApiKey(String apiKey) {
        return groqAIService.saveApiKey(apiKey);
    }

    /**
     * Evaluates whether required info is filled, gathers candidate profile facts,
     * invokes Groq LLM intelligence engine, and persists the analysis history.
     */
    public AIAnalysisResult runAIAnalysis(int userId, String customApiKey) throws Exception {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user session.");
        }

        // 1. Check readiness (whether user has filled required info)
        ProfileReadiness readiness = evaluateReadiness(userId);
        if (!readiness.isReadyForAnalysis()) {
            StringBuilder missingMsg = new StringBuilder("Cannot analyze profile yet. Please complete required information: ");
            if (!readiness.isHasCareerGoal()) missingMsg.append("Target Role (Career Goal), ");
            if (readiness.getSkillCount() == 0) missingMsg.append("Technical Skills, ");
            if (readiness.getEducationCount() == 0) missingMsg.append("Academic Education, ");
            String msg = missingMsg.toString();
            if (msg.endsWith(", ")) msg = msg.substring(0, msg.length() - 2);
            throw new IllegalArgumentException(msg + ".");
        }

        // 2. Load full user profile artifacts
        User user = userDAO.getUserById(userId);
        CareerGoal goal = careerGoalDAO.getCareerGoalByUserId(userId);
        List<Skill> skills = skillDAO.getSkillsByUserId(userId);
        List<Project> projects = projectDAO.getProjectsByUserId(userId);
        List<Experience> experiences = experienceDAO.getExperienceByUserId(userId);
        List<Certification> certifications = certificationDAO.getCertificationsByUserId(userId);
        List<Education> educations = educationDAO.getEducationByUserId(userId);

        // 3. Invoke Groq AI Service
        AIAnalysisResult result = groqAIService.analyzeProfile(
            user, goal, skills, projects, experiences, certifications, educations, customApiKey
        );

        // 4. Persist analysis report in MySQL profile_analysis_history
        if (result != null) {
            String role = (goal != null) ? goal.getEffectiveTargetRole() : "Software Developer";
            String rawJson = result.getRawJson();
            if (rawJson == null || rawJson.trim().isEmpty()) {
                rawJson = "{}";
            }
            int savedId = profileAnalysisDAO.saveAnalysis(userId, role, rawJson, result.getModelUsed());
            result.setAnalysisId(savedId);
            result.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        }

        return result;
    }

    private void syncProfileCompletion(int userId) {
        try {
            int score = profileDAO.calculateProfileCompletion(userId);
            profileDAO.updateProfileCompletion(userId, score);
        } catch (Exception ignored) {}
    }
}

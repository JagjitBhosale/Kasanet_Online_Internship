package com.kasnet.util;

import com.kasnet.model.*;
import com.kasnet.service.ResumeAnalysisService;

/**
 * ResumeAnalysisVerifier - Comprehensive verification tests for the
 * Resume Analysis & Student Profile Information module.
 *
 * Validates:
 * 1. CareerGoal model and effective role/domain resolution
 * 2. Experience model and technology evidence mapping
 * 3. Validation enforcement (required fields, empty inputs, "Other" specs)
 * 4. Profile readiness calculation (criteria evaluation and state changes)
 * 5. Isolation of data structures (Projects vs Experience)
 */
public class ResumeAnalysisVerifier {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" RESUME ANALYSIS MODULE - VERIFICATION SUITE");
        System.out.println("==================================================");

        boolean allPassed = true;

        // Test 1: CareerGoal Model & Effective Value Resolution
        System.out.print("[TEST 1] CareerGoal Model & 'Other' Role Resolution... ");
        CareerGoal goal = new CareerGoal();
        goal.setTargetRole("Other");
        goal.setTargetRoleOther("Machine Learning Infrastructure Engineer");
        goal.setPreferredDomain("Other");
        goal.setPreferredDomainOther("Autonomous Robotics");

        if ("Machine Learning Infrastructure Engineer".equals(goal.getEffectiveTargetRole())
                && "Autonomous Robotics".equals(goal.getEffectivePreferredDomain())) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (Role or domain resolution incorrect)");
            allPassed = false;
        }

        // Test 2: Standard CareerGoal Resolution
        System.out.print("[TEST 2] CareerGoal Standard Role Resolution... ");
        goal.setTargetRole("Backend Developer");
        goal.setPreferredDomain("Cloud & Distributed Systems");
        if ("Backend Developer".equals(goal.getEffectiveTargetRole())
                && "Cloud & Distributed Systems".equals(goal.getEffectivePreferredDomain())) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED");
            allPassed = false;
        }

        // Test 3: Experience Model Structure & Separation from Project
        System.out.print("[TEST 3] Experience Model Integrity (Separate from Project)... ");
        Experience exp = new Experience();
        exp.setExperienceId(101);
        exp.setUserId(42);
        exp.setCompanyName("Kasnet Technologies");
        exp.setRole("Backend Engineering Intern");
        exp.setEmploymentType("Internship");
        exp.setTechnologiesUsed("Java, Spring Boot, MySQL, Redis");
        exp.setCurrent(true);

        Project proj = new Project();
        proj.setProjectId(202);
        proj.setUserId(42);
        proj.setProjectName("Personal Blog Engine");
        proj.setTechnologiesUsed("Java, Thymeleaf");

        if (exp.getExperienceId() != proj.getProjectId()
                && !exp.getClass().equals(proj.getClass())
                && "Kasnet Technologies".equals(exp.getCompanyName())
                && exp.isCurrent()) {
            System.out.println("PASSED (Distinct entities maintained)");
        } else {
            System.out.println("FAILED (Entities not distinct)");
            allPassed = false;
        }

        // Test 4: Service Validation - Missing Career Role
        System.out.print("[TEST 4] Server-Side Validation: Reject Missing Role... ");
        ResumeAnalysisService service = new ResumeAnalysisService();
        boolean rejectedMissingRole = false;
        try {
            service.saveCareerGoal(42, "", null, null, null, null);
        } catch (IllegalArgumentException e) {
            rejectedMissingRole = true;
        }
        if (rejectedMissingRole) {
            System.out.println("PASSED (Properly rejected)");
        } else {
            System.out.println("FAILED (Did not reject empty target role)");
            allPassed = false;
        }

        // Test 5: Service Validation - Reject "Other" without Specification
        System.out.print("[TEST 5] Server-Side Validation: Reject 'Other' Without Specification... ");
        boolean rejectedOtherMissing = false;
        try {
            service.saveCareerGoal(42, "Other", "", "Cloud & Distributed Systems", null, null);
        } catch (IllegalArgumentException e) {
            rejectedOtherMissing = true;
        }
        if (rejectedOtherMissing) {
            System.out.println("PASSED (Properly rejected)");
        } else {
            System.out.println("FAILED (Did not reject empty 'Other' specification)");
            allPassed = false;
        }

        // Test 6: Service Validation - Reject Empty Skill Name
        System.out.print("[TEST 6] Server-Side Validation: Reject Empty Skill Name... ");
        boolean rejectedEmptySkill = false;
        try {
            service.saveSkill(42, 0, "   ", "Programming Languages");
        } catch (IllegalArgumentException e) {
            rejectedEmptySkill = true;
        }
        if (rejectedEmptySkill) {
            System.out.println("PASSED (Properly rejected)");
        } else {
            System.out.println("FAILED (Did not reject whitespace skill)");
            allPassed = false;
        }

        // Test 7: Service Validation - Reject Invalid Session
        System.out.print("[TEST 7] Security Validation: Reject Invalid User ID (<= 0)... ");
        boolean rejectedInvalidSession = false;
        try {
            service.saveSkill(0, 0, "Java", "Programming Languages");
        } catch (IllegalArgumentException e) {
            rejectedInvalidSession = true;
        }
        if (rejectedInvalidSession) {
            System.out.println("PASSED (Enforced positive authenticated user ID)");
        } else {
            System.out.println("FAILED (Allowed zero/negative user ID)");
            allPassed = false;
        }

        // Test 8: Service Validation - Reject Empty Company in Experience
        System.out.print("[TEST 8] Server-Side Validation: Reject Empty Company Name... ");
        boolean rejectedEmptyCompany = false;
        try {
            service.saveExperience(42, 0, "", "Developer", "Full-time", "Java", null, null, null, false);
        } catch (IllegalArgumentException e) {
            rejectedEmptyCompany = true;
        }
        if (rejectedEmptyCompany) {
            System.out.println("PASSED (Properly rejected)");
        } else {
            System.out.println("FAILED (Did not reject empty company)");
            allPassed = false;
        }

        // Test 9: ProfileReadiness Logic (Empty User)
        System.out.print("[TEST 9] ProfileReadiness Evaluation: Negative User ID Handled... ");
        ProfileReadiness rEmpty = service.evaluateReadiness(-1);
        if (!rEmpty.isReadyForAnalysis() && rEmpty.getReadinessScore() == 0) {
            System.out.println("PASSED (Safe fallback)");
        } else {
            System.out.println("FAILED (Readiness incorrect for invalid user)");
            allPassed = false;
        }

        System.out.println("==================================================");
        if (allPassed) {
            System.out.println(" ALL 9 VERIFICATION TESTS PASSED SUCCESSFULLY!");
        } else {
            System.out.println(" SOME TESTS ENCOUNTERED ISSUES. SEE ABOVE.");
        }
        System.out.println("==================================================");
    }
}

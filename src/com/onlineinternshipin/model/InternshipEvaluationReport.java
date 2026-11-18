package com.onlineinternshipin.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InternshipEvaluationReport {

    public static final String[] CRITERIA = {
        "Attendance",
        "Punctuality & Discipline",
        "Understanding of Project Requirements",
        "Project Assignment",
        "Project Task Completion",
        "Technical Skills & Practical Implementation",
        "Problem-Solving Ability",
        "Communication & Teamwork",
        "Internship Report Submission",
        "College Internship Presentation",
        "Global Certification Performance"
    };

    public final String studentName;
    public final String collegeName;
    public final String branchDepartment;
    public final String city;
    public final String internshipDuration;
    public final String certificationName;
    public final String certificationScore;
    public final LocalDate certificationDate;
    public final List<CriterionEvaluation> evaluations;
    public final int overallRating;
    public final String internshipStatus;
    public final String companyRecommendation;
    public final String keyStrengths;
    public final String improvementAreas;
    public final String evaluatorRemarks;
    public final String evaluatorName;
    public final String designation;
    public final String signature;
    public final LocalDate evaluationDate;

    public InternshipEvaluationReport(String studentName, String collegeName, String branchDepartment,
                                      String city, String internshipDuration, String certificationName,
                                      String certificationScore, LocalDate certificationDate,
                                      List<CriterionEvaluation> evaluations, int overallRating,
                                      String internshipStatus, String companyRecommendation,
                                      String keyStrengths, String improvementAreas, String evaluatorRemarks,
                                      String evaluatorName, String designation, String signature,
                                      LocalDate evaluationDate) {
        if (evaluations == null || evaluations.size() != CRITERIA.length) {
            throw new IllegalArgumentException("Exactly one evaluation is required for every criterion.");
        }
        this.studentName = studentName;
        this.collegeName = collegeName;
        this.branchDepartment = branchDepartment;
        this.city = city;
        this.internshipDuration = internshipDuration;
        this.certificationName = certificationName;
        this.certificationScore = certificationScore;
        this.certificationDate = certificationDate;
        this.evaluations = Collections.unmodifiableList(new ArrayList<>(evaluations));
        this.overallRating = overallRating;
        this.internshipStatus = internshipStatus;
        this.companyRecommendation = companyRecommendation;
        this.keyStrengths = keyStrengths;
        this.improvementAreas = improvementAreas;
        this.evaluatorRemarks = evaluatorRemarks;
        this.evaluatorName = evaluatorName;
        this.designation = designation;
        this.signature = signature;
        this.evaluationDate = evaluationDate;
    }

    public static final class CriterionEvaluation {
        public final String status;
        public final int rating;

        public CriterionEvaluation(String status, int rating) {
            this.status = status;
            this.rating = rating;
        }
    }
}

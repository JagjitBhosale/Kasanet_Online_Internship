package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Education;
import com.onlineinternshipin.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * EducationDAO - Maps academic history directly to the production 'student' table.
 */
public class EducationDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Education> getEducationByUserId(int userId) {
        List<Education> list = new ArrayList<>();
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return list;

        // SSC
        Education ssc = new Education();
        ssc.setEduId(1);
        ssc.setUserId(userId);
        ssc.setLevel("SSC");
        ssc.setSchoolCollegeName(s.getSchool10Name());
        ssc.setBoardUniversity(s.getBoard10());
        ssc.setPassingYear(parseIntSafely(s.getPassingYear10()));
        ssc.setPercentage(parseDoubleSafely(s.getPercentage10()));
        list.add(ssc);

        // HSC
        Education hsc = new Education();
        hsc.setEduId(2);
        hsc.setUserId(userId);
        hsc.setLevel("HSC");
        hsc.setSchoolCollegeName(s.getSchool12Name());
        hsc.setBoardUniversity(s.getBoard12());
        hsc.setStream(s.getStream12());
        hsc.setPassingYear(parseIntSafely(s.getPassingYear12()));
        hsc.setPercentage(parseDoubleSafely(s.getPercentage12()));
        list.add(hsc);

        // Diploma
        Education dip = new Education();
        dip.setEduId(3);
        dip.setUserId(userId);
        dip.setLevel("Diploma");
        dip.setSchoolCollegeName(s.getDiplomaSchoolName());
        dip.setBoardUniversity(s.getDiplomaBoard());
        dip.setStream(s.getDiplomaStream());
        dip.setPassingYear(parseIntSafely(s.getDiplomaPassingYear()));
        dip.setPercentage(parseDoubleSafely(s.getDiplomaPercentage()));
        list.add(dip);

        // Graduation
        Education grad = new Education();
        grad.setEduId(4);
        grad.setUserId(userId);
        grad.setLevel("Graduation");
        grad.setSchoolCollegeName(s.getGradCollegeName());
        grad.setBoardUniversity(s.getGradUniversity());
        grad.setStream(s.getGradStream());
        grad.setPassingYear(parseIntSafely(s.getGradPassingYear()));
        grad.setPercentage(parseDoubleSafely(s.getGradPercentage()));
        list.add(grad);

        // Master
        Education mas = new Education();
        mas.setEduId(5);
        mas.setUserId(userId);
        mas.setLevel("Master");
        mas.setSchoolCollegeName(s.getMasCollegeName());
        mas.setBoardUniversity(s.getMasUniversity());
        mas.setStream(s.getMasStream());
        mas.setPassingYear(parseIntSafely(s.getMasPassingYear()));
        mas.setPercentage(parseDoubleSafely(s.getMasPercentage()));
        list.add(mas);

        return list;
    }

    public Education getEducationByLevel(int userId, String level) {
        if (level == null) return null;
        for (Education e : getEducationByUserId(userId)) {
            if (level.equalsIgnoreCase(e.getLevel())) {
                return e;
            }
        }
        return null;
    }

    public Education getEducationById(int eduId, int userId) {
        for (Education e : getEducationByUserId(userId)) {
            if (e.getEduId() == eduId) {
                return e;
            }
        }
        return null;
    }

    public boolean saveOrUpdateEducation(Education edu) {
        if (edu == null || edu.getUserId() <= 0 || edu.getLevel() == null) return false;
        Student s = studentDAO.getStudentById(edu.getUserId());
        if (s == null) return false;

        String lvl = edu.getLevel().toUpperCase();
        if ("SSC".equals(lvl)) {
            s.setSchool10Name(edu.getSchoolCollegeName());
            s.setBoard10(edu.getBoardUniversity());
            s.setPassingYear10(edu.getPassingYear() > 0 ? String.valueOf(edu.getPassingYear()) : null);
            s.setPercentage10(edu.getPercentage() > 0 ? String.valueOf(edu.getPercentage()) : null);
        } else if ("HSC".equals(lvl)) {
            s.setSchool12Name(edu.getSchoolCollegeName());
            s.setBoard12(edu.getBoardUniversity());
            s.setStream12(edu.getStream());
            s.setPassingYear12(edu.getPassingYear() > 0 ? String.valueOf(edu.getPassingYear()) : null);
            s.setPercentage12(edu.getPercentage() > 0 ? String.valueOf(edu.getPercentage()) : null);
        } else if ("DIPLOMA".equals(lvl)) {
            s.setDiplomaSchoolName(edu.getSchoolCollegeName());
            s.setDiplomaBoard(edu.getBoardUniversity());
            s.setDiplomaStream(edu.getStream());
            s.setDiplomaPassingYear(edu.getPassingYear() > 0 ? String.valueOf(edu.getPassingYear()) : null);
            s.setDiplomaPercentage(edu.getPercentage() > 0 ? String.valueOf(edu.getPercentage()) : null);
        } else if ("GRADUATION".equals(lvl)) {
            s.setGradCollegeName(edu.getSchoolCollegeName());
            s.setGradUniversity(edu.getBoardUniversity());
            s.setGradStream(edu.getStream());
            s.setGradPassingYear(edu.getPassingYear() > 0 ? String.valueOf(edu.getPassingYear()) : null);
            s.setGradPercentage(edu.getPercentage() > 0 ? String.valueOf(edu.getPercentage()) : null);
        } else if ("MASTER".equals(lvl)) {
            s.setMasCollegeName(edu.getSchoolCollegeName());
            s.setMasUniversity(edu.getBoardUniversity());
            s.setMasStream(edu.getStream());
            s.setMasPassingYear(edu.getPassingYear() > 0 ? String.valueOf(edu.getPassingYear()) : null);
            s.setMasPercentage(edu.getPercentage() > 0 ? String.valueOf(edu.getPercentage()) : null);
        }

        return studentDAO.updateEducationDetails(s);
    }

    public boolean deleteEducation(int eduId, int userId) {
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return false;
        if (eduId == 1) {
            s.setSchool10Name(null); s.setBoard10(null); s.setPassingYear10(null); s.setPercentage10(null);
        } else if (eduId == 2) {
            s.setSchool12Name(null); s.setBoard12(null); s.setStream12(null); s.setPassingYear12(null); s.setPercentage12(null);
        } else if (eduId == 3) {
            s.setDiplomaSchoolName(null); s.setDiplomaBoard(null); s.setDiplomaStream(null); s.setDiplomaPassingYear(null); s.setDiplomaPercentage(null);
        } else if (eduId == 4) {
            s.setGradCollegeName(null); s.setGradUniversity(null); s.setGradStream(null); s.setGradPassingYear(null); s.setGradPercentage(null);
        } else if (eduId == 5) {
            s.setMasCollegeName(null); s.setMasUniversity(null); s.setMasStream(null); s.setMasPassingYear(null); s.setMasPercentage(null);
        }
        return studentDAO.updateEducationDetails(s);
    }

    private int parseIntSafely(String val) {
        if (val == null || val.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleSafely(String val) {
        if (val == null || val.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

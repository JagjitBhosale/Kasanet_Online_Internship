package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Skill;
import com.onlineinternshipin.model.Student;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * SkillDAO - Maps technical, soft, and domain skills directly to
 * 'technicalskill', 'softskill', and 'domain' columns in the production 'student' table.
 */
public class SkillDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Skill> getSkillsByUserId(int userId) {
        List<Skill> list = new ArrayList<>();
        list.addAll(getSkillsByType(userId, "Technical"));
        list.addAll(getSkillsByType(userId, "Soft"));
        list.addAll(getSkillsByType(userId, "Domain"));
        return list;
    }

    public List<Skill> getSkillsByType(int userId, String skillType) {
        List<Skill> list = new ArrayList<>();
        Student s = studentDAO.getStudentById(userId);
        if (s == null || skillType == null) return list;

        String raw = null;
        if ("Technical".equalsIgnoreCase(skillType)) {
            raw = s.getTechnicalSkill();
        } else if ("Soft".equalsIgnoreCase(skillType)) {
            raw = s.getSoftSkill();
        } else if ("Domain".equalsIgnoreCase(skillType)) {
            raw = s.getDomainSkill();
        }

        if (raw != null && !raw.trim().isEmpty()) {
            String[] tokens = raw.split(",");
            int id = 1;
            for (String t : tokens) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) {
                    Skill sk = new Skill();
                    sk.setSkillId(id++);
                    sk.setUserId(userId);
                    sk.setSkillName(trimmed);
                    sk.setSkillType(skillType);
                    sk.setProficiency("Intermediate");
                    sk.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    list.add(sk);
                }
            }
        }
        return list;
    }

    public Skill getSkillById(int skillId, int userId) {
        for (Skill sk : getSkillsByUserId(userId)) {
            if (sk.getSkillId() == skillId) {
                return sk;
            }
        }
        return null;
    }

    public boolean skillExists(int userId, String skillName, String skillType, int excludeSkillId) {
        if (skillName == null) return false;
        for (Skill sk : getSkillsByType(userId, skillType)) {
            if (sk.getSkillId() != excludeSkillId && skillName.trim().equalsIgnoreCase(sk.getSkillName())) {
                return true;
            }
        }
        return false;
    }

    public boolean updateSkill(Skill s) {
        return true;
    }

    public boolean saveSkillsOfType(int userId, String skillType, List<String> skillNames) {
        Student s = studentDAO.getStudentById(userId);
        if (s == null || skillType == null) return false;

        StringBuilder sb = new StringBuilder();
        if (skillNames != null) {
            for (int i = 0; i < skillNames.size(); i++) {
                String sn = skillNames.get(i).trim();
                if (!sn.isEmpty()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(sn);
                }
            }
        }
        String combined = sb.toString();

        String tech = s.getTechnicalSkill();
        String soft = s.getSoftSkill();
        String domain = s.getDomainSkill();

        if ("Technical".equalsIgnoreCase(skillType)) {
            tech = combined;
        } else if ("Soft".equalsIgnoreCase(skillType)) {
            soft = combined;
        } else if ("Domain".equalsIgnoreCase(skillType)) {
            domain = combined;
        }

        return studentDAO.updateSkills(userId, tech, soft, domain);
    }

    public boolean deleteAllSkillsOfType(int userId, String skillType) {
        return saveSkillsOfType(userId, skillType, new ArrayList<String>());
    }

    public boolean addSkill(Skill skill) {
        if (skill == null) return false;
        List<String> current = new ArrayList<>();
        for (Skill sk : getSkillsByType(skill.getUserId(), skill.getSkillType())) {
            current.add(sk.getSkillName());
        }
        current.add(skill.getSkillName());
        return saveSkillsOfType(skill.getUserId(), skill.getSkillType(), current);
    }

    public boolean deleteSkill(int skillId, int userId) {
        return true;
    }
}

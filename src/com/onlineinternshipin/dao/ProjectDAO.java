package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Project;
import com.onlineinternshipin.model.Student;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectDAO - Maps project details directly to 'Projectname' and 'projectdetails'
 * columns in the production 'student' table.
 */
public class ProjectDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Project> getProjectsByUserId(int userId) {
        List<Project> list = new ArrayList<>();
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return list;

        String pName = s.getProjectName();
        String pDetails = s.getProjectDetails();

        if (pName != null && !pName.trim().isEmpty()) {
            Project p = new Project();
            p.setProjectId(1);
            p.setUserId(userId);
            p.setProjectName(pName);
            p.setDescription(pDetails != null ? pDetails : "");
            p.setTechnologiesUsed(s.getTechnicalSkill() != null ? s.getTechnicalSkill() : "");
            p.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            list.add(p);
        }
        return list;
    }

    public Project getProjectById(int projectId, int userId) {
        List<Project> list = getProjectsByUserId(userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean addProject(Project p) {
        if (p == null || p.getUserId() <= 0) return false;
        String desc = p.getDescription() != null ? p.getDescription() : "";
        if (p.getTechnologiesUsed() != null && !p.getTechnologiesUsed().trim().isEmpty()) {
            desc = desc + " [Tech: " + p.getTechnologiesUsed() + "]";
        }
        if (p.getProjectUrl() != null && !p.getProjectUrl().trim().isEmpty()) {
            desc = desc + " [URL: " + p.getProjectUrl() + "]";
        }
        return studentDAO.updateProjects(p.getUserId(), p.getProjectName(), desc);
    }

    public boolean updateProject(Project p) {
        return addProject(p);
    }

    public boolean deleteProject(int projectId, int userId) {
        return studentDAO.updateProjects(userId, null, null);
    }
}

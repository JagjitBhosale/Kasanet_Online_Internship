package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Competition;
import com.onlineinternshipin.model.Student;

import java.util.ArrayList;
import java.util.List;

public class CompetitionDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Competition> getCompetitionsByUserId(int userId) {
        return new ArrayList<>();
    }

    public Competition getCompetitionById(int competitionId, int userId) {
        return null;
    }

    public boolean addCompetition(Competition c) {
        if (c == null || c.getUserId() <= 0) return false;
        Student s = studentDAO.getStudentById(c.getUserId());
        if (s == null) return false;
        String existing = s.getExtraActivities();
        String updated = (existing != null && !existing.trim().isEmpty())
                ? existing + "; Competition: " + c.getCompetitionName()
                : "Competition: " + c.getCompetitionName();
        return studentDAO.updateExtraActivities(c.getUserId(), updated);
    }

    public boolean updateCompetition(Competition c) {
        return addCompetition(c);
    }

    public boolean deleteCompetition(int competitionId, int userId) {
        return true;
    }
}

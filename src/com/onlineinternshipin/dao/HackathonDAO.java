package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Hackathon;
import com.onlineinternshipin.model.Student;

import java.util.ArrayList;
import java.util.List;

public class HackathonDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Hackathon> getHackathonsByUserId(int userId) {
        return new ArrayList<>();
    }

    public Hackathon getHackathonById(int hackathonId, int userId) {
        return null;
    }

    public boolean addHackathon(Hackathon h) {
        if (h == null || h.getUserId() <= 0) return false;
        Student s = studentDAO.getStudentById(h.getUserId());
        if (s == null) return false;
        String existing = s.getExtraActivities();
        String updated = (existing != null && !existing.trim().isEmpty())
                ? existing + "; Hackathon: " + h.getHackathonName()
                : "Hackathon: " + h.getHackathonName();
        return studentDAO.updateExtraActivities(h.getUserId(), updated);
    }

    public boolean updateHackathon(Hackathon h) {
        return addHackathon(h);
    }

    public boolean deleteHackathon(int hackathonId, int userId) {
        return true;
    }
}

package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Achievement;
import com.onlineinternshipin.model.Student;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * AchievementDAO - Maps extra activities directly to 'ExtraActivities' column in 'student' table.
 */
public class AchievementDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Achievement> getAchievementsByUserId(int userId) {
        List<Achievement> list = new ArrayList<>();
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return list;

        String act = s.getExtraActivities();
        if (act != null && !act.trim().isEmpty()) {
            Achievement a = new Achievement();
            a.setAchievementId(1);
            a.setUserId(userId);
            a.setAchievementType("General");
            a.setTitle("Activities & Achievements");
            a.setDescription(act);
            a.setExtraActivities(act);
            a.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            list.add(a);
        }
        return list;
    }

    public Achievement getAchievementById(int achievementId, int userId) {
        List<Achievement> list = getAchievementsByUserId(userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean addAchievement(Achievement a) {
        if (a == null || a.getUserId() <= 0) return false;
        String desc = a.getExtraActivities() != null && !a.getExtraActivities().trim().isEmpty()
                ? a.getExtraActivities() : a.getDescription();
        return studentDAO.updateExtraActivities(a.getUserId(), desc);
    }

    public boolean updateAchievement(Achievement a) {
        return addAchievement(a);
    }

    public boolean deleteAchievement(int achievementId, int userId) {
        return studentDAO.updateExtraActivities(userId, null);
    }
}

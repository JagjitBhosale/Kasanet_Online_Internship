package com.kasnet.service;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;

public class ProfileService {

    private final ProfileDAO profileDAO;
    private final UserDAO userDAO;

    public ProfileService() {
        this.profileDAO = new ProfileDAO();
        this.userDAO = new UserDAO();
    }

    public ProfileService(ProfileDAO profileDAO, UserDAO userDAO) {
        this.profileDAO = profileDAO;
        this.userDAO = userDAO;
    }

    public int getProfileCompletion(int userId) {
        if (userId <= 0) return 0;
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);
        return completion;
    }

    public boolean updatePersonalDetails(StudentProfile profile) {
        if (profile == null || profile.getUserId() <= 0) return false;
        boolean updated = profileDAO.updatePersonalDetails(profile);
        if (updated) {
            getProfileCompletion(profile.getUserId());
        }
        return updated;
    }

    public StudentProfile getFullProfile(int userId) {
        if (userId <= 0) return null;
        StudentProfile profile = profileDAO.getProfileByUserId(userId);
        if (profile == null) {
            profileDAO.createProfile(userId);
            profile = profileDAO.getProfileByUserId(userId);
        }
        return profile;
    }
}

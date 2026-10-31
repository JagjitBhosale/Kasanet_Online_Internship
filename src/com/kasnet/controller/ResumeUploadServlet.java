package com.kasnet.controller;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.util.FileUtil;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
@MultipartConfig(maxFileSize = 5242880, maxRequestSize = 5242880) // 5MB
public class ResumeUploadServlet extends HttpServlet {

    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Part filePart = req.getPart("resumeFile");
            if (filePart != null && filePart.getSize() > 0) {
                String submittedName = new com.kasnet.service.FileUploadService()
                        .getContentDispositionFilename(filePart.getHeader("content-disposition"));
                if (FileUtil.isAllowedResumeType(submittedName)) {
                    String safeName = FileUtil.generateSafeFileName(submittedName);
                    String uploadPath = req.getServletContext().getRealPath("/uploads/resumes");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    filePart.write(uploadPath + File.separator + safeName);

                    StudentProfile profile = profileDAO.getProfileByUserId(userId);
                    if (profile != null) {
                        profile.setResumePath("/uploads/resumes/" + safeName);
                        profile.setResumeFilename(submittedName);
                        // Recalculate
                        int completion = profileDAO.calculateProfileCompletion(userId);
                        profileDAO.updateProfileCompletion(userId, completion);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/profile?tab=personal&success=1");
    }
}

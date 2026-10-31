package com.kasnet.controller;

import com.kasnet.dao.CertificationDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.Certification;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
public class CertificationServlet extends HttpServlet {

    private CertificationDAO certDAO = new CertificationDAO();
    private ProfileDAO profileDAO = new ProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?tab=certifications");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "add";

        if ("delete".equalsIgnoreCase(action)) {
            String certIdStr = req.getParameter("certId");
            if (certIdStr != null) {
                try {
                    int certId = Integer.parseInt(certIdStr);
                    certDAO.deleteCertification(certId, userId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            String certName = req.getParameter("certName");
            String provider = req.getParameter("provider");
            String technology = req.getParameter("technology");
            String certIdNumber = req.getParameter("certIdNumber");
            String issueDateStr = req.getParameter("issueDate");
            String expiryDateStr = req.getParameter("expiryDate");
            String credentialUrl = req.getParameter("credentialUrl");

            if (certName != null && !certName.trim().isEmpty()) {
                Certification cert = new Certification();
                cert.setUserId(userId);
                cert.setCertName(certName.trim());
                cert.setProvider(provider != null ? provider.trim() : null);
                cert.setTechnology(technology != null ? technology.trim() : null);
                cert.setCertIdNumber(certIdNumber != null ? certIdNumber.trim() : null);
                cert.setCredentialUrl(credentialUrl != null ? credentialUrl.trim() : null);

                if (issueDateStr != null && !issueDateStr.trim().isEmpty()) {
                    try {
                        cert.setIssueDate(Date.valueOf(issueDateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
                    try {
                        cert.setExpiryDate(Date.valueOf(expiryDateStr.trim()));
                    } catch (IllegalArgumentException ignored) {}
                }

                String certIdParam = req.getParameter("certId");
                if ("update".equalsIgnoreCase(action) && certIdParam != null && !certIdParam.isEmpty()) {
                    try {
                        cert.setCertId(Integer.parseInt(certIdParam));
                        certDAO.updateCertification(cert);
                    } catch (NumberFormatException ignored) {}
                } else {
                    certDAO.addCertification(cert);
                }
            }
        }

        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        resp.sendRedirect(req.getContextPath() + "/profile?tab=certifications&success=1");
    }
}

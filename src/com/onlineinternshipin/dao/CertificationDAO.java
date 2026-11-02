package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.Certification;
import com.onlineinternshipin.model.Student;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * CertificationDAO - Maps certification details directly to
 * 'certification', 'certificationname', and 'certificationtechnology'
 * columns in the production 'student' table.
 */
public class CertificationDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Certification> getCertificationsByUserId(int userId) {
        List<Certification> list = new ArrayList<>();
        Student s = studentDAO.getStudentById(userId);
        if (s == null) return list;

        String cert = s.getCertification();
        String certName = s.getCertificationName();
        String certTech = s.getCertificationTechnology();

        if ((cert != null && !cert.trim().isEmpty()) || (certName != null && !certName.trim().isEmpty())) {
            Certification c = new Certification();
            c.setCertId(1);
            c.setUserId(userId);
            c.setCertName(cert != null ? cert : certName);
            c.setProvider(certName != null ? certName : "Kasnet / Partner");
            c.setTechnology(certTech != null ? certTech : "");
            c.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            list.add(c);
        }
        return list;
    }

    public List<Certification> getAllCertificationsWithStudent() {
        List<Certification> list = new ArrayList<>();
        for (Student s : studentDAO.getAllStudents()) {
            List<Certification> userCerts = getCertificationsByUserId(s.getId());
            for (Certification c : userCerts) {
                c.setStudentName(s.getFname() + " " + s.getLname());
                c.setStudentEmail(s.getEmail());
                list.add(c);
            }
        }
        return list;
    }

    public Certification getCertificationById(int certId, int userId) {
        List<Certification> list = getCertificationsByUserId(userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean addCertification(Certification c) {
        if (c == null || c.getUserId() <= 0) return false;
        return studentDAO.updateCertifications(
                c.getUserId(),
                c.getCertName(),
                c.getProvider(),
                c.getTechnology()
        );
    }

    public boolean updateCertification(Certification c) {
        return addCertification(c);
    }

    public boolean deleteCertification(int certId, int userId) {
        return studentDAO.updateCertifications(userId, null, null, null);
    }

    public boolean deleteCertificationByAdmin(int certId) {
        return true;
    }
}

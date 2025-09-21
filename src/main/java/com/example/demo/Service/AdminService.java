package com.example.demo.Service;

import com.example.demo.DAO.AdminDAO;
import com.example.demo.Entity.Admin;
import com.example.demo.Config.AdminID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminDAO adminDAO;
    private final AdminID adminIDGenerator;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(AdminDAO adminDAO, AdminID adminIDGenerator) {
        this.adminDAO = adminDAO;
        this.adminIDGenerator = adminIDGenerator;
    }

    /** ================== LOGIN (Admin Name + Password) ================== */
    public Optional<Admin> login(String adminName, String rawPassword) {
        Optional<Admin> adminOpt = adminDAO.findByName(adminName);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(rawPassword, admin.getAdminPassword())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    /** ================== CRUD ================== */
    public List<Admin> getAllAdmins() {
        return adminDAO.findAll();
    }

    public Optional<Admin> findAdminById(String adminId) {
        return adminDAO.findById(adminId);
    }

    public void saveAdmin(Admin admin) {
        // ✅ Generate Admin ID if not already set
        if (admin.getAdminID() == null || admin.getAdminID().isBlank()) {
            admin.setAdminID(adminIDGenerator.generateAdminId());
        }
        // ✅ Hash password before saving
        admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
        adminDAO.save(admin);
    }

    public void updateAdmin(Admin admin) {
        // ✅ Re-hash password if updated
        if (admin.getAdminPassword() != null && !admin.getAdminPassword().isBlank()) {
            admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
        }
        adminDAO.update(admin);
    }

    public void deleteAdmin(String adminId) {
        adminDAO.delete(adminId);
    }

    /** ================== EXTRA STATS ================== */
    public long countRoles() {
        return adminDAO.findAll().stream()
                .map(Admin::getRoleName)
                .distinct()
                .count();
    }

    public Optional<Admin> getLastAddedAdmin() {
        return adminDAO.findAll().stream()
                .reduce((first, second) -> second); // last element
    }
}

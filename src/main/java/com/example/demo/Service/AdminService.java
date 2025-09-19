package com.example.demo.Service;

import com.example.demo.DAO.AdminDAO;
import com.example.demo.Entity.Admin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public Optional<Admin> login(String adminName, String password) {
        return adminDAO.login(adminName, password);
    }

    public List<Admin> getAllAdmins() {
        return adminDAO.findAll();
    }

    public Optional<Admin> findAdminById(String adminId) {
        return adminDAO.findById(adminId);
    }

    public void saveAdmin(Admin admin) {
        adminDAO.save(admin);
    }

    public void updateAdmin(Admin admin) {
        adminDAO.update(admin);
    }

    public void deleteAdmin(String adminId) {
        adminDAO.delete(adminId);
    }
}

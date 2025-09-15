package com.example.demo.Service;

import com.example.demo.DAO.AdminDAO;
import com.example.demo.Entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminDAO adminDAO;

    // ✅ Get all admins
    public List<Admin> getAllAdmins() {
        return adminDAO.findAll();
    }

    // ✅ Get admin by ID
    public Optional<Admin> getAdminById(String id) {
        return adminDAO.findById(id);
    }

    // ✅ Search admins by name
    public List<Admin> searchAdmins(String name) {
        return adminDAO.findByName(name);
    }

    // ✅ Create new admin
    public String createAdmin(Admin admin) {
        int result = adminDAO.save(admin);
        return result > 0 ? "Admin created successfully" : "Failed to create admin";
    }

    // ✅ Update admin
    public String updateAdmin(String id, Admin admin) {
        admin.setAdminID(id);
        int result = adminDAO.update(admin);
        return result > 0 ? "Admin updated successfully" : "Failed to update admin";
    }

    // ✅ Delete admin
    public String deleteAdmin(String id) {
        int result = adminDAO.delete(id);
        return result > 0 ? "Admin deleted successfully" : "Failed to delete admin";
    }
}

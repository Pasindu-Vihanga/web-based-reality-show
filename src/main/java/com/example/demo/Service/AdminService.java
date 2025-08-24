package com.example.demo.Service;

import com.example.demo.DAO.AdminDAO;
import com.example.demo.Entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminDAO adminDAO;

    // Using constructor injection is a best practice
    @Autowired
    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public void createAdmin(Admin admin) {
        // You could add business logic here, e.g., validation, before saving
        adminDAO.saveAdmin(admin);
    }

    public List<Admin> getAllAdmins() {
        return adminDAO.getAllAdmins();
    }

    public Admin getAdminById(String adminID) {
        // You could add logic for handling cases where the admin is not found
        return adminDAO.getAdminByID(adminID);
    }

    public void updateAdmin(String adminID, Admin admin) {
        // Ensure the ID from the path is set on the object to be updated
        admin.setAdminID(adminID);
        adminDAO.updateAdmin(admin);
    }

    public void deleteAdmin(String adminID) {
        adminDAO.deleteAdmin(adminID);
    }
}
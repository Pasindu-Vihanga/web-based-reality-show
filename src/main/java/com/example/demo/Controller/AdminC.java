package com.example.demo.Controller;

import com.example.demo.DAO.AdminDAO;
import com.example.demo.Entity.Admin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminC {


    private final AdminDAO adminDAO;

    public AdminC(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // ✅ Home page mapping to Thymeleaf template
    @GetMapping("/admin")
    public String home(Model model) {
        List<Admin> admins = adminDAO.findAll();
        model.addAttribute("admins", admins); // Pass admins to Thymeleaf
        return "admin"; // admin.html in templates folder
    }

    // ✅ Get all admins (REST API)
    @GetMapping
    @ResponseBody
    public List<Admin> getAllAdmins() {
        return adminDAO.findAll();
    }

    // ✅ Get admin by ID
    @GetMapping("/{id}")
    @ResponseBody
    public Optional<Admin> getAdminById(@PathVariable String id) {
        return adminDAO.findById(id);
    }

    // ✅ Search admin by name
    @GetMapping("/search")
    @ResponseBody
    public List<Admin> searchAdmins(@RequestParam String name) {
        return adminDAO.findByName(name);
    }

    // ✅ Create a new admin
    @PostMapping
    @ResponseBody
    public String createAdmin(@RequestBody Admin admin) {
        int result = adminDAO.save(admin);
        return result > 0 ? "Admin created successfully" : "Failed to create admin";
    }

    // ✅ Update existing admin
    @PutMapping("/{id}")
    @ResponseBody
    public String updateAdmin(@PathVariable String id, @RequestBody Admin admin) {
        admin.setAdminID(id);
        int result = adminDAO.update(admin);
        return result > 0 ? "Admin updated successfully" : "Failed to update admin";
    }

    // ✅ Delete admin
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteAdmin(@PathVariable String id) {
        int result = adminDAO.delete(id);
        return result > 0 ? "Admin deleted successfully" : "Failed to delete admin";
    }
}

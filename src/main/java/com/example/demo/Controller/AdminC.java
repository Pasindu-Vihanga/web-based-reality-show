package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminC {

    private final AdminService adminService;

    @Autowired
    public AdminC(AdminService adminService) {
        this.adminService = adminService;
    }

    // Displays the form to create a new admin
    @GetMapping("/")
    public String showCreateForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin"; // Returns the name of the HTML file (e.g., admin-form.html)
    }

    // Processes form submissions for both creating and updating an admin
    @PostMapping("/save")
    public String saveAdmin(@ModelAttribute("admin") Admin admin) {
        adminService.createAdmin(admin); // The service should handle saving a new or updating an existing entity
        return "redirect:/admins";
    }

    // Deletes an admin and redirects back to the list
    @GetMapping("/delete/{adminId}")
    public String deleteAdmin(@PathVariable String adminId) {
        adminService.deleteAdmin(adminId);
        return "redirect:/admins";
    }
}
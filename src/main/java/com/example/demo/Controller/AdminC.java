package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminC {

    private final AdminService adminService;

    public AdminC(AdminService adminService) {
        this.adminService = adminService;
    }

    // 🏠 Home page
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 📋 View all admins
    @GetMapping("/viewA")
    public String getAllAdmins(Model model) {
        List<Admin> admins = adminService.getAllAdmins();
        model.addAttribute("adminList", admins);
        return "viewA";
    }
    // 🔍 Search admin by name
    @GetMapping("/admin/search")
    public String searchAdmin(@RequestParam String adminName, Model model) {
        List<Admin> found = adminService.searchAdmins(adminName);
        if (!found.isEmpty()) {
            model.addAttribute("adminList", found);
        } else {
            model.addAttribute("adminList", List.of());
            model.addAttribute("message", "No admin found with name: " + adminName);
        }
        return "viewA";
    }

    // 📝 Admin registration form
    @GetMapping("/registerA")
    public String adminRegister() {
        return "registerA";
    }

    // 💾 Save new admin
    @PostMapping("/admin/save")
    public String saveAdmin(@RequestParam String adminID,
                            @RequestParam String adminName,
                            @RequestParam String adminPassword,
                            @RequestParam String roleName,
                            @RequestParam String roleID,
                            @RequestParam String mobileNumber,
                            @RequestParam String email,
                            @RequestParam String address) {
        Admin admin = new Admin(adminID, adminName, adminPassword, roleName, roleID, mobileNumber, email, address);
        adminService.createAdmin(admin);
        return "redirect:/viewA";
    }
    // ✏️ Update admin form
    @GetMapping("/admin/edit")
    public String editAdminForm(@RequestParam String adminID, Model model) {
        Optional<Admin> admin = adminService.getAdminById(adminID);
        if (admin.isPresent()) {
            model.addAttribute("admin", admin.get());
            return "admin_edit";
        } else {
            return "redirect:/admin/view";
        }
    }

    // ✅ Submit admin update
    @PostMapping("/admin/update")
    public String updateAdmin(@RequestParam String adminID,
                              @RequestParam String adminName,
                              @RequestParam String adminPassword,
                              @RequestParam String roleName,
                              @RequestParam String roleID,
                              @RequestParam String mobileNumber,
                              @RequestParam String email,
                              @RequestParam String address) {
        Admin admin = new Admin(adminID, adminName, adminPassword, roleName, roleID, mobileNumber, email, address);
        adminService.updateAdmin(adminID, admin);
        return "redirect:/admin/view";
    }
    // 🗑️ Delete admin
    @GetMapping("/admin/delete")
    public String deleteAdmin(@RequestParam String adminID) {
        adminService.deleteAdmin(adminID);
        return "redirect:/viewA";
    }
}
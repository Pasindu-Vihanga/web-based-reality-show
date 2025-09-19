package com.example.demo.Controller;

import com.example.demo.Service.*;
import com.example.demo.Entity.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AdminC {

    private final AdminService adminService;
    private final ShowService showService;
    private final ContestantService contestantService;
    private final VoteService voteService;
    private final ResultService resultService;

    public AdminC(AdminService adminService,
                  ShowService showService,
                  ContestantService contestantService,
                  VoteService voteService,
                  ResultService resultService) {
        this.adminService = adminService;
        this.showService = showService;
        this.contestantService = contestantService;
        this.voteService = voteService;
        this.resultService = resultService;
    }

    /** ========== HOME PAGE ========== */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /** ========== ADMIN LOGIN PAGE ========== */
    @GetMapping("/loginA")
    public String adminLoginPage() {
        return "loginA";
    }

    /** ========== PROCESS LOGIN ========== */
    @PostMapping("/admin/login")
    public String processLogin(@RequestParam String adminName,
                               @RequestParam String adminPassword,
                               HttpSession session,
                               Model model) {
        Optional<Admin> adminOpt = adminService.login(adminName, adminPassword);

        if (adminOpt.isPresent()) {
            session.setAttribute("loggedInAdmin", adminOpt.get());
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid Admin credentials!");
            return "loginA";
        }
    }

    /** ========== ADMIN DASHBOARD ========== */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // force login
        }

        // Dashboard stats
        int episodeCount = showService.getAllShows().size();
        int contestantCount = contestantService.getAllContestants().size();
        int activeSessions = voteService.getActiveSessions().size();
        int totalVotes = resultService.getAllResults().stream()
                .mapToInt(r -> r.getVotesCount())
                .sum();

        model.addAttribute("episodeCount", episodeCount);
        model.addAttribute("contestantCount", contestantCount);
        model.addAttribute("activeSessions", activeSessions);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("admin", loggedInAdmin);

        return "dashboardA";
    }

    /** ========== MANAGE ADMINS ========== */
    @GetMapping("/admin/manage")
    public String manageAdmins(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        List<Admin> adminList = adminService.getAllAdmins();
        model.addAttribute("adminList", adminList);
        model.addAttribute("newAdmin", new Admin());
        return "adminManage"; // new Thymeleaf template
    }

    /** ========== ADD ADMIN ========== */
    @PostMapping("/admin/add")
    public String addAdmin(@ModelAttribute Admin admin, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (admin.getAdminID() == null || admin.getAdminID().isBlank()) {
            admin.setAdminID(UUID.randomUUID().toString().substring(0, 8));
        }
        adminService.saveAdmin(admin);
        return "redirect:/admin/manage";
    }

    /** ========== EDIT ADMIN FORM ========== */
    @GetMapping("/admin/edit/{id}")
    public String editAdminForm(@PathVariable("id") String adminId, HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        Optional<Admin> admin = adminService.findAdminById(adminId);
        if (admin.isPresent()) {
            model.addAttribute("admin", admin.get());
            return "editA"; // new Thymeleaf template
        }
        return "redirect:/admin/manage";
    }

    /** ========== UPDATE ADMIN ========== */
    @PostMapping("/admin/update")
    public String updateAdmin(@ModelAttribute Admin admin, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        adminService.updateAdmin(admin);
        return "redirect:/admin/manage";
    }

    /** ========== DELETE ADMIN ========== */
    @PostMapping("/admin/delete/{id}")
    public String deleteAdmin(@PathVariable("id") String adminId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        adminService.deleteAdmin(adminId);
        return "redirect:/admin/manage";
    }

    /** ========== LOGOUT ========== */
    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginA";
    }
}

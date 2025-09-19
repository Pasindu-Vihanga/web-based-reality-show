package com.example.demo.Controller;

import com.example.demo.Service.*;
import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public String home(){
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
            return "/loginA";
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

        return "dashboardA"; // your new UI
    }

    /** ========== LOGOUT ========== */
    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginA";
    }
}

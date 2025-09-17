package com.example.demo.Controller;

import com.example.demo.Entity.Vote;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.VoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class VoteC {

    private final VoteService voteService;

    public VoteC(VoteService voteService) {
        this.voteService = voteService;
    }


    /** ========== VIEW ALL SESSIONS (Admin) ========== */
    @GetMapping("/voteSessionA")
    public String viewAllSessionsAdmin(Model model) {
        List<Vote> sessions = voteService.getAllSessions();
        model.addAttribute("sessionList", sessions);
        return "voteSessionA"; // Thymeleaf template
    }

    /** ========== VIEW ALL ACTIVE SESSIONS (User) ========== */
    @GetMapping("/voteSessionU")
    public String viewAllSessionsForUser(Model model) {
        List<Vote> activeSessions = voteService.getActiveSessions();
        model.addAttribute("sessionList", activeSessions);
        return "voteSessionU"; // Thymeleaf template
    }

    /** ========== ADD NEW SESSION (Form Submission) ========== */
    @PostMapping("/session/add")
    public String addSession(@ModelAttribute Vote session) {
        // Generate unique ID if not provided
        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(UUID.randomUUID().toString());
        }

        if (voteService.validateSession(session)) {
            voteService.saveSession(session);
        }
        return "redirect:/voteSessionA";
    }

    /** ========== EDIT SESSION FORM ========== */
    @GetMapping("/session/edit/{id}")
    public String editSessionForm(@PathVariable("id") String sessionId, Model model) {
        Optional<Vote> session = voteService.findSessionById(sessionId);
        if (session.isPresent()) {
            model.addAttribute("session", session.get());
            return "editSession"; // new Thymeleaf template for editing
        } else {
            return "redirect:/voteSessionA";
        }
    }

    /** ========== UPDATE SESSION ========== */
    @PostMapping("/session/update")
    public String updateSession(@ModelAttribute Vote session) {
        if (voteService.validateSession(session)) {
            voteService.updateSession(session);
        }
        return "redirect:/voteSessionA";
    }

    /** ========== DELETE SESSION ========== */
    @PostMapping("/session/delete/{id}")
    public String deleteSession(@PathVariable("id") String sessionId) {
        voteService.deleteSession(sessionId);
        return "redirect:/voteSessionA";
    }
}

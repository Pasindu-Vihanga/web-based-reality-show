package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class VoteC {

    private final VoteService voteService;
    private final ShowService showService;

    public VoteC(VoteService voteService, ShowService showService) {
        this.voteService = voteService;
        this.showService = showService;
    }

    /** ========== VIEW ALL SESSIONS (Admin Only, Session Handling) ========== */
    @GetMapping("/voteSessionA")
    public String viewAllSessionsAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // Redirect if not logged in as admin
        }

        List<Vote> sessions = voteService.getAllSessions();
        model.addAttribute("sessionList", sessions);

        // For dropdown in Add modal
        model.addAttribute("episodeList", showService.getAllShows());
        model.addAttribute("newSession", new Vote());

        return "voteSessionA";
    }

    /** ========== VIEW ALL ACTIVE SESSIONS (User) ========== */
    @GetMapping("/voteSessionU")
    public String viewAllSessionsForUser(Model model) {
        List<Vote> activeSessions = voteService.getActiveSessions();
        model.addAttribute("sessionList", activeSessions);
        return "voteSessionU";
    }

    /** ========== ADD NEW SESSION (Admin Only) ========== */
    @PostMapping("/session/add")
    public String addSession(@ModelAttribute Vote session, HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(UUID.randomUUID().toString());
        }

        if (voteService.validateSession(session)) {
            voteService.saveSession(session);
        }
        return "redirect:/voteSessionA";
    }

    /** ========== EDIT SESSION FORM (Admin Only) ========== */
    @GetMapping("/session/edit/{id}")
    public String editSessionForm(@PathVariable("id") String sessionId, Model model, HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        Optional<Vote> session = voteService.findSessionById(sessionId);
        if (session.isPresent()) {
            model.addAttribute("session", session.get());
            model.addAttribute("episodeList", showService.getAllShows()); // for dropdown
            return "editSession";
        } else {
            return "redirect:/voteSessionA";
        }
    }

    /** ========== UPDATE SESSION (Admin Only) ========== */
    @PostMapping("/session/update")
    public String updateSession(@ModelAttribute Vote session, HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (voteService.validateSession(session)) {
            voteService.updateSession(session);
        }
        return "redirect:/voteSessionA";
    }

    /** ========== DELETE SESSION (Admin Only) ========== */
    @PostMapping("/session/delete/{id}")
    public String deleteSession(@PathVariable("id") String sessionId, HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        voteService.deleteSession(sessionId);
        return "redirect:/voteSessionA";
    }
}

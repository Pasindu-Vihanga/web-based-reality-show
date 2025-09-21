package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
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

    /** ========== VIEW ALL SESSIONS (Admin Only) ========== */
    @GetMapping("/voteSessionA")
    public String viewAllSessionsAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        List<Vote> sessions = voteService.getAllSessions();
        model.addAttribute("sessionList", sessions);
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
    public String addSession(@ModelAttribute Vote session,
                             @RequestParam("episodeId") String episodeId,
                             HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        // Assign sessionId if missing
        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(UUID.randomUUID().toString());
        }

        // Default values
        if (session.getStatus() == null || session.getStatus().isBlank()) {
            session.setStatus("Scheduled");
        }
        if (session.getCurrentVotes() < 0) {
            session.setCurrentVotes(0);
        }

        // Attach linked episode
        showService.findShowById(episodeId).ifPresent(session::setShow);

        if (voteService.validateSession(session)) {
            voteService.saveSession(session);
        }

        return "redirect:/episodeView"; // Back to episode management
    }

    /** ========== EDIT SESSION FORM (Admin Only) ========== */
    @GetMapping("/session/edit/{id}")
    public String editSessionForm(@PathVariable("id") String sessionId,
                                  Model model,
                                  HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        Optional<Vote> session = voteService.findSessionById(sessionId);
        if (session.isPresent()) {
            model.addAttribute("session", session.get());
            model.addAttribute("episodeList", showService.getAllShows());
            return "editSession";
        } else {
            return "redirect:/episodeView";
        }
    }

    /** ========== UPDATE SESSION (Admin Only) ========== */
    @PostMapping("/session/update")
    public String updateSession(@ModelAttribute Vote session,
                                @RequestParam("episodeId") String episodeId,
                                HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        // Attach linked episode
        showService.findShowById(episodeId).ifPresent(session::setShow);

        if (session.getCurrentVotes() < 0) {
            session.setCurrentVotes(0);
        }

        if (voteService.validateSession(session)) {
            voteService.updateSession(session);
        }

        return "redirect:/episodeView";
    }

    /** ========== DELETE SESSION (Admin Only) ========== */
    @PostMapping("/session/delete/{id}")
    public String deleteSession(@PathVariable("id") String sessionId, HttpSession sessionHttp) {
        Admin loggedInAdmin = (Admin) sessionHttp.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        voteService.deleteSession(sessionId);
        return "redirect:/episodeView";
    }

    /** ===================================================
     *  🔹 REST Endpoints (for AJAX in episodeView modal)
     *  =================================================== */

    /** Get sessions by episode (JSON API) */
    @GetMapping("/api/votes/by-episode/{episodeId}")
    @ResponseBody
    public ResponseEntity<List<Vote>> getSessionsByEpisode(@PathVariable String episodeId) {
        return ResponseEntity.ok(voteService.findSessionsByEpisode(episodeId));
    }

    /** Toggle session active/inactive */
    @PostMapping("/api/votes/toggle/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleSession(@PathVariable("id") String sessionId) {
        Optional<Vote> sessionOpt = voteService.findSessionById(sessionId);
        if (sessionOpt.isPresent()) {
            Vote session = sessionOpt.get();
            session.setActive(!session.isActive());

            // Auto-change status based on active flag
            session.setStatus(session.isActive() ? "Live" : "Paused");

            voteService.updateSession(session);
            return ResponseEntity.ok("Session status updated");
        }
        return ResponseEntity.badRequest().body("Session not found");
    }
}

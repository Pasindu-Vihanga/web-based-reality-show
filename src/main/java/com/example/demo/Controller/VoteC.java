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
import java.util.UUID;

@Controller
public class VoteC {

    private final VoteService voteService;
    private final ShowService showService;

    public VoteC(VoteService voteService, ShowService showService) {
        this.voteService = voteService;
        this.showService = showService;
    }

    @GetMapping("/voteSessionA")
    public String viewAllSessionsAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        model.addAttribute("sessionList", voteService.getAllSessions());
        model.addAttribute("episodeList", showService.getAllShows());
        model.addAttribute("newSession", new Vote());
        return "voteSessionA";
    }

    @PostMapping("/session/add")
    public String addSession(@ModelAttribute Vote session, @RequestParam("episodeId") String episodeId, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(UUID.randomUUID().toString());
        }
        showService.findShowById(episodeId).ifPresent(session::setShow);
        voteService.saveSession(session);
        return "redirect:/voteSessionA";
    }

    @PostMapping("/session/update")
    public String updateSession(@ModelAttribute Vote session, @RequestParam("episodeId") String episodeId, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        showService.findShowById(episodeId).ifPresent(session::setShow);
        voteService.updateSession(session);
        return "redirect:/voteSessionA";
    }

    @PostMapping("/session/delete/{id}")
    public String deleteSession(@PathVariable("id") String sessionId, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        voteService.deleteSession(sessionId);
        return "redirect:/voteSessionA";
    }
}

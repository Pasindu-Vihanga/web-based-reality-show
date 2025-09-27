package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class VoteC {

    private final VoteService voteService;
    private final ShowService showService;
    private final ResultService resultService;

    public VoteC(VoteService voteService, ShowService showService, ResultService resultService) {
        this.voteService = voteService;
        this.showService = showService;
        this.resultService = resultService;
    }


    /** ================= ADMIN: VIEW ALL VOTING SESSIONS ================= */
    @GetMapping("/voteSessionA")
    public String viewAllSessionsAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        model.addAttribute("sessionList", voteService.getAllSessions());
        model.addAttribute("episodeList", showService.getAllShows());
        model.addAttribute("newSession", new Vote());
        return "voteSessionA";
    }

    @GetMapping("/voteSessionU")
    public String viewUserVotingSessions(Model model) {
        // Fetch only ongoing sessions
        List<Vote> ongoingSessions = voteService.getAllSessions().stream()
                .filter(s -> "Ongoing".equalsIgnoreCase(s.getStatus()))
                .toList();

        model.addAttribute("sessionList", ongoingSessions);
        return "voteSessionU"; // your user template
    }

    /** ================= ADD VOTING SESSION ================= */
    @PostMapping("/session/add")
    public String addSession(@ModelAttribute Vote session, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        voteService.saveSession(session);
        return "redirect:/voteSessionA";
    }

    /** ================= UPDATE VOTING SESSION ================= */
    @PostMapping("/session/update")
    public String updateSession(@ModelAttribute Vote session, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        voteService.updateSession(session);
        return "redirect:/voteSessionA";
    }

    /** ================= DELETE VOTING SESSION ================= */
    @PostMapping("/session/delete/{id}")
    public String deleteSession(@PathVariable("id") String sessionId, HttpSession httpSession) {
        Admin loggedInAdmin = (Admin) httpSession.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        voteService.deleteSession(sessionId);
        return "redirect:/voteSessionA";
    }

    /** ================= USER: CAST VOTE ================= */
    @PostMapping("/vote/cast")
    public String castVote(@RequestParam("sessionId") String sessionId,
                           @RequestParam("contestantId") String contestantId,
                           HttpSession session,
                           Model model) {

        var voteSessionOpt = voteService.findSessionById(sessionId);
        if (voteSessionOpt.isEmpty() || !voteSessionOpt.get().isActive()) {
            model.addAttribute("message", "Voting is not available for this session.");
            model.addAttribute("episode", null);
            return "epiforUser";
        }

        var voteSession = voteSessionOpt.get();

        // Find contestant and cast vote
        var episodeOpt = showService.findShowById(voteSession.getShow().getEpisodeId());
        if (episodeOpt.isPresent()) {
            var episode = episodeOpt.get();
            episode.getContestants().stream()
                    .filter(c -> c.getContestantId().equals(contestantId))
                    .findFirst()
                    .ifPresent(contestant -> resultService.castVote(sessionId, contestant));

            // Reload sessions
            episode.setSessions(voteService.findSessionsByEpisode(episode.getEpisodeId()));

            // For each session, load rankings
            for (var s : episode.getSessions()) {
                s.setResults(resultService.getRankings(s.getSessionId())); // sorted results
            }

            model.addAttribute("episode", episode);
        }

        model.addAttribute("message", "✅ Your vote has been recorded!");
        return "epiforUser";
    }
}

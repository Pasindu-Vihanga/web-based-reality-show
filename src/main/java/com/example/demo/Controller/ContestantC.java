package com.example.demo.Controller;

import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Result;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.ContestantService;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ContestantC {

    private final ContestantService contestantService;
    private final VoteService voteService;
    private final ResultService resultService;

    public ContestantC(ContestantService contestantService,
                       VoteService voteService,
                       ResultService resultService) {
        this.contestantService = contestantService;
        this.voteService = voteService;
        this.resultService = resultService;
    }

    /* ================== ADMIN: VIEW ALL ================== */
    @GetMapping("/contestantView")
    public String viewAllContestantsAdmin(Model model, HttpSession session) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);

        // Stats
        model.addAttribute("activeCount", contestantService.findByStatus("active").size());
        model.addAttribute("eliminatedCount", contestantService.findByStatus("eliminated").size());

        return "contestantView";
    }

    /* ================== USER: VIEW BY EPISODE ================== */
    @GetMapping("/contestantForUser/{episodeId}")
    public String viewContestantsForUser(@PathVariable("episodeId") String episodeId,
                                         Model model,
                                         HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU";
        }

        List<Contestant> contestants = contestantService.findByEpisodeId(episodeId);
        model.addAttribute("contestantList", contestants);
        return "contestantForUser";
    }

    /* ================== ADD NEW ================== */
    @PostMapping("/contestant/add")
    public String addContestant(@ModelAttribute Contestant contestant) {
        if (contestant.getContestantId() == null || contestant.getContestantId().isBlank()) {
            contestant.setContestantId(UUID.randomUUID().toString());
        }
        if (contestantService.validateContestant(contestant)) {
            contestantService.saveContestant(contestant);
        }
        return "redirect:/contestantView";
    }

    /* ================== EDIT FORM ================== */
    @GetMapping("/contestant/edit/{id}")
    public String editContestantForm(@PathVariable("id") String contestantId, Model model) {
        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);
        if (contestant.isPresent()) {
            model.addAttribute("contestant", contestant.get());
            return "editContestant";
        } else {
            return "redirect:/contestantView";
        }
    }

    /* ================== UPDATE ================== */
    @PostMapping("/contestant/update")
    public String updateContestant(@ModelAttribute Contestant contestant) {
        if (contestantService.validateContestant(contestant)) {
            contestantService.updateContestant(contestant);
        }
        return "redirect:/contestantView";
    }

    /* ================== DELETE ================== */
    @PostMapping("/contestant/delete/{id}")
    public String deleteContestant(@PathVariable("id") String contestantId) {
        contestantService.deleteContestant(contestantId);
        return "redirect:/contestantView";
    }

    /* ================== USER: VOTE ================== */
    @PostMapping("/vote/{id}")
    public String voteForContestant(@PathVariable("id") String contestantId,
                                    HttpSession session,
                                    Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU"; // must login
        }

        Optional<Contestant> contestantOpt = contestantService.findContestantById(contestantId);
        if (contestantOpt.isEmpty()) {
            model.addAttribute("error", "Contestant not found.");
            return "redirect:/epiforUser";
        }

        Contestant contestant = contestantOpt.get();

        if (!"active".equalsIgnoreCase(contestant.getStatus())) {
            model.addAttribute("error", "Cannot vote for eliminated contestant.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // 1. Find active voting session for this episode
        List<Vote> activeSessions = voteService.getActiveSessions();
        Vote sessionForEpisode = activeSessions.stream()
                .filter(s -> s.getShow().getEpisodeId().equals(contestant.getShow().getEpisodeId()))
                .findFirst()
                .orElse(null);

        if (sessionForEpisode == null) {
            model.addAttribute("error", "No active voting session for this episode.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // 2. Update or create result
        List<Result> results = resultService.findBySessionId(sessionForEpisode.getSessionId());
        Optional<Result> existingResult = results.stream()
                .filter(r -> r.getContestant().getContestantId().equals(contestant.getContestantId()))
                .findFirst();

        if (existingResult.isPresent()) {
            Result r = existingResult.get();
            r.setVotesCount(r.getVotesCount() + 1);
            resultService.updateResult(r);
        } else {
            Result newResult = new Result();
            newResult.setVotingSession(sessionForEpisode);
            newResult.setContestant(contestant);
            newResult.setVotesCount(1);
            newResult.setStatus("safe"); // default
            resultService.saveResult(newResult);
        }

        model.addAttribute("message", "Your vote has been recorded!");

        // Redirect back to episode’s contestant list
        return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
    }
}

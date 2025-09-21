package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.ContestantService;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.ShowService;
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
    private final ShowService showService;
    private final VoteService voteService;
    private final ResultService resultService;

    public ContestantC(ContestantService contestantService,
                       ShowService showService,
                       VoteService voteService,
                       ResultService resultService) {
        this.contestantService = contestantService;
        this.showService = showService;
        this.voteService = voteService;
        this.resultService = resultService;
    }

    /* ================== ADMIN: VIEW ALL ================== */
    @GetMapping("/contestantView")
    public String viewAllContestants(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        model.addAttribute("contestantList", contestantService.getAllContestants());
        model.addAttribute("episodeList", showService.getAllShows());
        model.addAttribute("contestant", new Contestant());
        return "contestantView";
    }

    @PostMapping("/contestant/add")
    public String addContestant(@ModelAttribute Contestant contestant, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (contestant.getContestantId() == null || contestant.getContestantId().isBlank()) {
            contestant.setContestantId(UUID.randomUUID().toString());
        }
        contestantService.saveContestant(contestant);
        return "redirect:/contestantView";
    }

    @GetMapping("/contestant/edit/{id}")
    public String editContestant(@PathVariable("id") String contestantId, Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);
        if (contestant.isPresent()) {
            model.addAttribute("contestant", contestant.get());
            model.addAttribute("episodeList", showService.getAllShows());
            return "editContestant";
        }
        return "redirect:/contestantView";
    }

    @PostMapping("/contestant/update")
    public String updateContestant(@ModelAttribute Contestant contestant, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        contestantService.updateContestant(contestant);
        return "redirect:/contestantView";
    }

    @PostMapping("/contestant/delete/{id}")
    public String deleteContestant(@PathVariable("id") String contestantId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        contestantService.deleteContestant(contestantId);
        return "redirect:/contestantView";
    }

    /* ================== USER: VIEW CONTESTANTS BY EPISODE ================== */
    @GetMapping("/contestantForUser/{episodeId}")
    public String viewContestantsForUser(@PathVariable("episodeId") String episodeId,
                                         Model model,
                                         HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<Contestant> contestants = contestantService.findByEpisodeId(episodeId);
        model.addAttribute("contestantList", contestants);
        model.addAttribute("episodeId", episodeId);
        return "contestantForUser";
    }

    /* ================== USER: VOTE FOR CONTESTANT ================== */
    @PostMapping("/vote/{contestantId}")
    public String voteForContestant(@PathVariable("contestantId") String contestantId,
                                    HttpSession session,
                                    Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        Optional<Contestant> contestantOpt = contestantService.findContestantById(contestantId);
        if (contestantOpt.isEmpty()) {
            model.addAttribute("error", "Contestant not found.");
            return "redirect:/epiforUser";
        }

        Contestant contestant = contestantOpt.get();

        if (!"active".equalsIgnoreCase(contestant.getStatus())) {
            model.addAttribute("error", "You cannot vote for an eliminated contestant.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // Find active session for this episode
        List<Vote> activeSessions = voteService.getActiveSessions();
        Vote sessionForEpisode = activeSessions.stream()
                .filter(s -> s.getShow().getEpisodeId().equals(contestant.getShow().getEpisodeId()))
                .findFirst()
                .orElse(null);

        if (sessionForEpisode == null) {
            model.addAttribute("error", "No active voting session for this episode.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // ✅ Cast vote
        resultService.castVote(sessionForEpisode.getSessionId(), contestant);

        model.addAttribute("message", "Your vote has been recorded!");
        return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
    }
}

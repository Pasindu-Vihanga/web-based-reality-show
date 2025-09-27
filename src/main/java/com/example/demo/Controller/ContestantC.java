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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

        // ✅ find active session for this episode
        List<Vote> activeSessions = voteService.getActiveSessions();
        Vote sessionForEpisode = activeSessions.stream()
                .filter(s -> s.getShow().getEpisodeId().equals(episodeId))
                .findFirst()
                .orElse(null);

        if (sessionForEpisode != null) {
            model.addAttribute("currentSessionId", sessionForEpisode.getSessionId());
        }

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

        // ✅ find active session for this episode
        List<Vote> activeSessions = voteService.getActiveSessions();
        Vote sessionForEpisode = activeSessions.stream()
                .filter(s -> s.getShow().getEpisodeId().equals(contestant.getShow().getEpisodeId()))
                .findFirst()
                .orElse(null);

        if (sessionForEpisode == null) {
            model.addAttribute("error", "No active voting session for this episode.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // ✅ check if user already voted in this session
        String votedKey = "voted-" + sessionForEpisode.getSessionId();
        if (session.getAttribute(votedKey) != null) {
            model.addAttribute("error", "You have already voted in this session.");
            return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
        }

        // ✅ Cast vote
        resultService.castVote(sessionForEpisode.getSessionId(), contestant);

        // ✅ Mark as voted for this session
        session.setAttribute(votedKey, true);

        model.addAttribute("message", "Your vote has been recorded!");
        return "redirect:/contestantForUser/" + contestant.getShow().getEpisodeId();
    }

    /* ================== SERVE CONTESTANT IMAGE ================== */
    @GetMapping("/images/{contestantId}")
    public ResponseEntity<byte[]> getContestantImage(@PathVariable("contestantId") String contestantId) {
        Optional<Contestant> contestantOpt = contestantService.findContestantById(contestantId);
        if (contestantOpt.isPresent() && contestantOpt.get().getImage() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + contestantId + ".jpg\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(contestantOpt.get().getImage());
        }
        return ResponseEntity.notFound().build();
    }

    /* ================== PUBLIC: VIEW SUMMARY OF CONTESTANTS ================== */
    @GetMapping("/contestantSummary")
    public String viewContestantSummary(Model model) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);

        // ✅ Extract Winner & Runner-up if available
        Contestant winner = contestants.stream()
                .filter(c -> "winner".equalsIgnoreCase(c.getStatus()))
                .findFirst().orElse(null);

        Contestant runnerUp = contestants.stream()
                .filter(c -> "runnerup".equalsIgnoreCase(c.getStatus()))
                .findFirst().orElse(null);

        model.addAttribute("winner", winner);
        model.addAttribute("runnerUp", runnerUp);

        return "contestantSum";
    }

}

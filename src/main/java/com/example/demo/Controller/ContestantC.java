package com.example.demo.Controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    /* ================== ADMIN/USER: VIEW ALL CONTESTANTS ================== */
    @GetMapping("/contestantView")
    public String viewAllContestants(Model model) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);
        model.addAttribute("contestant", new Contestant()); // for Add modal
        model.addAttribute("episodeList", showService.getAllShows()); // dropdown episodes
        return "contestantView";
    }

    /* ================== SAFE: GET /contestant/add → redirect ================== */
    @GetMapping("/contestant/add")
    public String redirectAddGet() {
        return "redirect:/contestantView";
    }

    /* ================== ADMIN: ADD CONTESTANT ================== */
    @PostMapping("/contestant/add")
    public String addContestant(@ModelAttribute Contestant contestant,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            contestant.setContestantId(UUID.randomUUID().toString());

            if (imageFile != null && !imageFile.isEmpty()) {
                contestant.setImage(imageFile.getBytes());
            }

            contestantService.saveContestant(contestant);
            redirectAttributes.addFlashAttribute("message", "✅ Contestant added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to add contestant: " + e.getMessage());
        }
        return "redirect:/contestantView";
    }


    /* ================== ADMIN: UPDATE CONTESTANT ================== */
    @PostMapping("/contestant/update")
    public String updateContestant(@ModelAttribute Contestant contestant,
                                   @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                contestant.setImage(imageFile.getBytes());
            } else {
                contestant.setImage(contestantService.findContestantById(contestant.getContestantId())
                        .map(Contestant::getImage)
                        .orElse(null));
            }

            contestantService.updateContestant(contestant);
            redirectAttributes.addFlashAttribute("message", "✏️ Contestant updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to update contestant.");
        }
        return "redirect:/contestantView";
    }

    /* ================== ADMIN: DELETE CONTESTANT ================== */
    @PostMapping("/contestant/delete/{id}")
    public String deleteContestant(@PathVariable("id") String contestantId,
                                   RedirectAttributes redirectAttributes) {
        try {
            contestantService.deleteContestant(contestantId);
            redirectAttributes.addFlashAttribute("message", "🗑 Contestant deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to delete contestant.");
        }
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
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        Optional<Contestant> contestantOpt = contestantService.findContestantById(contestantId);
        if (contestantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Contestant not found.");
            return "redirect:/feedbackU";
        }

        Contestant contestant = contestantOpt.get();
        if (!"active".equalsIgnoreCase(contestant.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "You cannot vote for an eliminated contestant.");
            return "redirect:/feedbackU";
        }

        List<Vote> activeSessions = voteService.getActiveSessions();
        Vote sessionForEpisode = activeSessions.stream()
                .filter(s -> s.getShow().getEpisodeId().equals(contestant.getShow().getEpisodeId()))
                .findFirst()
                .orElse(null);

        if (sessionForEpisode == null) {
            redirectAttributes.addFlashAttribute("error", "No active voting session for this episode.");
            return "redirect:/feedbackU";
        }

        String votedKey = "voted-" + sessionForEpisode.getSessionId();
        if (session.getAttribute(votedKey) != null) {
            redirectAttributes.addFlashAttribute("message", "You have already voted in this session.");
            return "redirect:/feedbackU";
        }

        resultService.castVote(sessionForEpisode.getSessionId(), contestant);
        session.setAttribute(votedKey, true);

        redirectAttributes.addFlashAttribute("message", "✅ Thank you for voting! Please leave your feedback below.");
        return "redirect:/feedbackU";
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

package com.example.demo.Controller;

import com.example.demo.Entity.Contestant;
import com.example.demo.Service.ContestantService;
import com.example.demo.Service.ShowService;
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

    public ContestantC(ContestantService contestantService, ShowService showService) {
        this.contestantService = contestantService;
        this.showService = showService;
    }

    /* ================== ADMIN: VIEW ALL ================== */
    @GetMapping("/contestantView")
    public String viewAllContestantsAdmin(Model model) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);

        // Dropdown episodes for Add modal
        model.addAttribute("episodeList", showService.getAllShows());

        // For modal form binding
        model.addAttribute("newContestant", new Contestant());

        // Optional stats
        model.addAttribute("activeCount", contestantService.findByStatus("active").size());
        model.addAttribute("eliminatedCount", contestantService.findByStatus("eliminated").size());

        return "contestantView";
    }

    /* ================== USER: VIEW BY EPISODE ================== */
    @GetMapping("/contestantForUser/{episodeId}")
    public String viewContestantsForUser(@PathVariable("episodeId") String episodeId, Model model) {
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
            model.addAttribute("episodeList", showService.getAllShows()); // Dropdown
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
    public String voteForContestant(@PathVariable("id") String contestantId, Model model) {
        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);

        if (contestant.isPresent() && "active".equalsIgnoreCase(contestant.get().getStatus())) {
            // 🔹 Here you’d implement vote persistence (not shown in your DAO/service yet)
            model.addAttribute("message", "Vote cast successfully for " + contestant.get().getName());
        } else {
            model.addAttribute("error", "Cannot vote for this contestant.");
        }

        // redirect back to same episode’s contestant list
        return contestant.map(c -> "redirect:/contestantForUser/" + c.getShow().getEpisodeId())
                .orElse("redirect:/epiforUser");
    }
}

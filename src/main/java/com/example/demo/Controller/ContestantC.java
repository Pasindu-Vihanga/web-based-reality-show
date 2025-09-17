package com.example.demo.Controller;

import com.example.demo.Entity.Contestant;
import com.example.demo.Service.ContestantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ContestantC {

    private final ContestantService contestantService;

    public ContestantC(ContestantService contestantService) {
        this.contestantService = contestantService;
    }

    /** ========== VIEW ALL CONTESTANTS (Admin) ========== */
    @GetMapping("/contestantView")
    public String viewAllContestantsAdmin(Model model) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);
        return "contestantView"; // Thymeleaf template
    }

    /** ========== VIEW CONTESTANTS BY EPISODE (User) ========== */
    @GetMapping("/contestantForUser/{episodeId}")
    public String viewContestantsForUser(@PathVariable("episodeId") String episodeId, Model model) {
        List<Contestant> contestants = contestantService.findByEpisodeId(episodeId);
        model.addAttribute("contestantList", contestants);
        return "contestantForUser"; // Thymeleaf template
    }

    /** ========== ADD NEW CONTESTANT (Form Submission) ========== */
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

    /** ========== EDIT CONTESTANT FORM ========== */
    @GetMapping("/contestant/edit/{id}")
    public String editContestantForm(@PathVariable("id") String contestantId, Model model) {
        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);
        if (contestant.isPresent()) {
            model.addAttribute("contestant", contestant.get());
            return "editContestant"; // Thymeleaf template for editing
        } else {
            return "redirect:/contestantView";
        }
    }

    /** ========== UPDATE CONTESTANT ========== */
    @PostMapping("/contestant/update")
    public String updateContestant(@ModelAttribute Contestant contestant) {
        if (contestantService.validateContestant(contestant)) {
            contestantService.updateContestant(contestant);
        }
        return "redirect:/contestantView";
    }

    /** ========== DELETE CONTESTANT ========== */
    @PostMapping("/contestant/delete/{id}")
    public String deleteContestant(@PathVariable("id") String contestantId) {
        contestantService.deleteContestant(contestantId);
        return "redirect:/contestantView";
    }
}

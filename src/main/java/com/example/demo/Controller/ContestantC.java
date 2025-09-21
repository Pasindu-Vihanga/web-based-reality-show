package com.example.demo.Controller;

import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Show;
import com.example.demo.Service.ContestantService;
import com.example.demo.Service.ShowService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /** ================== ADMIN VIEW ================== */
    @GetMapping("/contestantView")
    public String viewAllContestants(Model model) {
        List<Contestant> contestants = contestantService.getAllContestants();
        model.addAttribute("contestantList", contestants);
        model.addAttribute("episodeList", showService.getAllShows());
        return "contestantView";
    }

    /** ================== ADD ================== */
    @PostMapping("/contestant/add")
    public String addContestant(@ModelAttribute Contestant contestant,
                                @RequestParam("imageFile") MultipartFile file) throws IOException {
        if (contestant.getContestantId() == null || contestant.getContestantId().isBlank()) {
            contestant.setContestantId(UUID.randomUUID().toString());
        }
        if (!file.isEmpty()) {
            contestant.setImage(file.getBytes()); // ✅ save image bytes
        }
        if (contestantService.validateContestant(contestant)) {
            contestantService.saveContestant(contestant);
        }
        return "redirect:/contestantView";
    }

    /** ================== EDIT FORM ================== */
    @GetMapping("/contestant/edit/{id}")
    public String editContestantForm(@PathVariable("id") String contestantId, Model model) {
        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);
        if (contestant.isPresent()) {
            model.addAttribute("contestant", contestant.get());
            model.addAttribute("episodeList", showService.getAllShows());
            return "editContestant";
        } else {
            return "redirect:/contestantView";
        }
    }

    /** ================== UPDATE ================== */
    @PostMapping("/contestant/update")
    public String updateContestant(@ModelAttribute Contestant contestant,
                                   @RequestParam("imageFile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            contestant.setImage(file.getBytes()); // replace with new image if provided
        }
        if (contestantService.validateContestant(contestant)) {
            contestantService.updateContestant(contestant);
        }
        return "redirect:/contestantView";
    }

    /** ================== DELETE ================== */
    @PostMapping("/contestant/delete/{id}")
    public String deleteContestant(@PathVariable("id") String contestantId) {
        contestantService.deleteContestant(contestantId);
        return "redirect:/contestantView";
    }

    /** ================== SERVE IMAGE ================== */
    @GetMapping("/contestant/image/{id}")
    public void getImage(@PathVariable("id") String contestantId, HttpServletResponse response) throws IOException {
        Optional<Contestant> contestant = contestantService.findContestantById(contestantId);
        if (contestant.isPresent() && contestant.get().getImage() != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(contestant.get().getImage());
            response.getOutputStream().close();
        }
    }
}

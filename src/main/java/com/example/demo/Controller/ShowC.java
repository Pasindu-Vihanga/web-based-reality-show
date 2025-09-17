package com.example.demo.Controller;

import com.example.demo.Entity.Show;
import com.example.demo.Service.ShowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ShowC {

    private final ShowService showService;

    public ShowC(ShowService showService) {
        this.showService = showService;
    }

    /** ========== VIEW ALL EPISODES (Admin) ========== */
    @GetMapping("/episodeView")
    public String viewAllAdmin(Model model) {
        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "episodeView"; // Thymeleaf template
    }

    /** ========== VIEW ALL EPISODES (User) ========== */
    @GetMapping("/epiforUser")
    public String viewAllEpisodesForUser(Model model) {
        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "epiforUser"; // Thymeleaf template
    }

    /** ========== SEARCH EPISODES (User) ========== */
    @GetMapping("/search")
    public String searchEpisodes(@RequestParam String keyword, Model model) {
        List<Show> found = showService.findShowsByTitle(keyword);
        model.addAttribute("episodeList", found);
        model.addAttribute("searchKeyword", keyword);
        return "epiforUser";
    }

    /** ========== ADD NEW EPISODE (Form Submission) ========== */
    @PostMapping("/add")
    public String addEpisode(@ModelAttribute Show show) {
        // Generate unique ID if not provided
        if (show.getEpisodeId() == null || show.getEpisodeId().isBlank()) {
            show.setEpisodeId(UUID.randomUUID().toString());
        }

        if (showService.validateShow(show)) {
            showService.saveShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ========== EDIT EPISODE FORM ========== */
    @GetMapping("/edit/{id}")
    public String editEpisodeForm(@PathVariable("id") String episodeId, Model model) {
        Optional<Show> episode = showService.findShowById(episodeId);
        if (episode.isPresent()) {
            model.addAttribute("show", episode.get());
            return "editEpisode"; // new Thymeleaf template for editing
        } else {
            return "redirect:/episodeView";
        }
    }

    /** ========== UPDATE EPISODE ========== */
    @PostMapping("/update")
    public String updateEpisode(@ModelAttribute Show show) {
        if (showService.validateShow(show)) {
            showService.updateShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ========== DELETE EPISODE ========== */
    @PostMapping("/delete/{id}")
    public String deleteEpisode(@PathVariable("id") String episodeId) {
        showService.deleteShow(episodeId);
        return "redirect:/episodeManage/view";
    }
}

package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Show;
import com.example.demo.Service.ShowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class ShowC {

    private final ShowService showService;

    public ShowC(ShowService showService) {
        this.showService = showService;
    }

    /** ================= ADMIN: VIEW ALL EPISODES ================= */
    @GetMapping("/episodeView")
    public String viewAllAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        model.addAttribute("show", new Show()); // for add modal
        return "episodeView";
    }

    /** ================= USER: VIEW ALL EPISODES ================= */
    @GetMapping("/epiforUser")
    public String viewAllEpisodesForUser(Model model) {
        // No login check → if required, add same logic for User
        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "epiforUser"; // must exist in templates/
    }

    /** ================= ADD EPISODE ================= */
    @PostMapping("/add")
    public String addEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (show.getEpisodeId() == null || show.getEpisodeId().isBlank()) {
            show.setEpisodeId(UUID.randomUUID().toString());
        }
        if (show.getStatus() == null || show.getStatus().isBlank()) {
            show.setStatus("Upcoming"); // default
        }

        if (showService.validateShow(show)) {
            showService.saveShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ================= UPDATE EPISODE ================= */
    @PostMapping("/update")
    public String updateEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (showService.validateShow(show)) {
            showService.updateShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ================= DELETE EPISODE ================= */
    @PostMapping("/delete/{id}")
    public String deleteEpisode(@PathVariable("id") String episodeId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        showService.deleteShow(episodeId);
        return "redirect:/episodeView";
    }
}

package com.example.demo.Controller;

import com.example.demo.Entity.Show;
import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
import com.example.demo.Service.ShowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
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
    public String viewAllAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // redirect if not logged in
        }

        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "episodeView";
    }

    /** ========== VIEW ALL EPISODES (User) ========== */
    @GetMapping("/epiforUser")
    public String viewAllEpisodesForUser(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU"; // redirect if not logged in
        }

        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "epiforUser";
    }

    /** ========== SEARCH EPISODES (User) ========== */
    @GetMapping("/search")
    public String searchEpisodes(@RequestParam String keyword, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU";
        }

        List<Show> found = showService.findShowsByTitle(keyword);
        model.addAttribute("episodeList", found);
        model.addAttribute("searchKeyword", keyword);
        return "epiforUser";
    }

    /** ========== ADD NEW EPISODE (Admin Only) ========== */
    @PostMapping("/add")
    public String addEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (show.getEpisodeId() == null || show.getEpisodeId().isBlank()) {
            show.setEpisodeId(UUID.randomUUID().toString());
        }

        if (showService.validateShow(show)) {
            showService.saveShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ========== EDIT EPISODE FORM (Admin Only) ========== */
    @GetMapping("/edit/{id}")
    public String editEpisodeForm(@PathVariable("id") String episodeId, Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        Optional<Show> episode = showService.findShowById(episodeId);
        if (episode.isPresent()) {
            model.addAttribute("show", episode.get());
            return "editEpisode";
        } else {
            return "redirect:/episodeView";
        }
    }

    /** ========== UPDATE EPISODE (Admin Only) ========== */
    @PostMapping("/update")
    public String updateEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (showService.validateShow(show)) {
            showService.updateShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ========== DELETE EPISODE (Admin Only) ========== */
    @PostMapping("/delete/{id}")
    public String deleteEpisode(@PathVariable("id") String episodeId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        showService.deleteShow(episodeId);
        return "redirect:/episodeView";
    }
}

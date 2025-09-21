package com.example.demo.Controller;

import com.example.demo.Entity.Show;
import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
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
public class ShowC {

    private final ShowService showService;
    private final VoteService voteService;

    public ShowC(ShowService showService, VoteService voteService) {
        this.showService = showService;
        this.voteService = voteService;
    }

    /** ========== VIEW ALL EPISODES (Admin) ========== */
    @GetMapping("/episodeView")
    public String viewAllAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        model.addAttribute("show", new Show()); // ✅ prevent thymeleaf binding error
        return "episodeView";
    }

    /** ========== VIEW ALL EPISODES (User) ========== */
    @GetMapping("/epiforUser")
    public String viewAllEpisodesForUser(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<Show> episodes = showService.getAllShows();
        model.addAttribute("episodeList", episodes);
        return "epiforUser";
    }

    /** ========== SEARCH EPISODES (User) ========== */
    @GetMapping("/search")
    public String searchEpisodes(@RequestParam String keyword, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<Show> found = showService.findShowsByTitle(keyword);
        model.addAttribute("episodeList", found);
        model.addAttribute("searchKeyword", keyword);
        return "epiforUser";
    }

    /** ========== ADD NEW EPISODE ========== */
    @PostMapping("/add")
    public String addEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (show.getEpisodeId() == null || show.getEpisodeId().isBlank()) {
            show.setEpisodeId(UUID.randomUUID().toString());
        }
        if (show.getStatus() == null || show.getStatus().isBlank()) {
            show.setStatus("UPCOMING"); // ✅ default status
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
        if (loggedInAdmin == null) return "redirect:/loginA";

        Optional<Show> episode = showService.findShowById(episodeId);
        if (episode.isPresent()) {
            model.addAttribute("show", episode.get());
            return "editEpisode"; // ✅ Thymeleaf form template
        }
        return "redirect:/episodeView";
    }

    /** ========== UPDATE EPISODE ========== */
    @PostMapping("/update")
    public String updateEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (showService.validateShow(show)) {
            showService.updateShow(show);
        }
        return "redirect:/episodeView";
    }

    /** ========== DELETE EPISODE ========== */
    @PostMapping("/delete/{id}")
    public String deleteEpisode(@PathVariable("id") String episodeId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        // ✅ Delete linked voting sessions first to prevent FK issues
        voteService.findSessionsByEpisode(episodeId)
                .forEach(sessionObj -> voteService.deleteSession(sessionObj.getSessionId()));

        showService.deleteShow(episodeId);
        return "redirect:/episodeView";
    }
}

package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Show;
import com.example.demo.Service.ShowService;
import com.example.demo.Service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShowC {

    private final ShowService showService;
    private final VoteService voteService;

    public ShowC(ShowService showService, VoteService voteService) {
        this.showService = showService;
        this.voteService = voteService;
    }

    /** ================= ADMIN: VIEW ALL EPISODES ================= */
    @GetMapping("/episodeView")
    public String viewAllAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        List<Show> episodes = showService.getAllShows();

        // Enrich each show with its sessions
        for (Show ep : episodes) {
            ep.setSessions(voteService.findSessionsByEpisode(ep.getEpisodeId()));
        }

        model.addAttribute("episodeList", episodes);
        model.addAttribute("show", new Show()); // for add modal
        return "episodeView";
    }

    /** ================= USER: VIEW ALL EPISODES ================= */
    @GetMapping("/epiforUser")
    public String viewAllEpisodesForUser(Model model) {
        List<Show> episodes = showService.getAllShows();

        // pick the first available episode (Upcoming or Ongoing)
        Show firstAvailable = episodes.stream()
                .filter(ep -> "Upcoming".equalsIgnoreCase(ep.getStatus())
                        || "Ongoing".equalsIgnoreCase(ep.getStatus()))
                .findFirst()
                .orElse(null);

        if (firstAvailable != null) {
            // attach voting sessions
            firstAvailable.setSessions(voteService.findSessionsByEpisode(firstAvailable.getEpisodeId()));

            // for each session, attach contestants
            for (var session : firstAvailable.getSessions()) {
                session.setResults(null); // ignore results
                session.setShow(firstAvailable);
                // attach contestants from the episode itself
                session.setResults(null); // cleanup if not needed
            }
        }

        model.addAttribute("episode", firstAvailable);
        return "epiforUser";
    }


    /** ================= ADD EPISODE ================= */
    @PostMapping("/add")
    public String addEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        showService.saveShow(show);
        return "redirect:/episodeView";
    }

    /** ================= UPDATE EPISODE ================= */
    @PostMapping("/update")
    public String updateEpisode(@ModelAttribute Show show, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        showService.updateShow(show);
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

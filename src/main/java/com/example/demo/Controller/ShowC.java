package com.example.demo.Controller;

import com.example.demo.DAO.ShowDAO;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.Entity.Show;
import com.example.demo.Service.ShowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

    @Controller
    public class ShowC {

        private final ShowService showService;
        private final ShowDAO showDAO;

        public ShowC(ShowService showService, ShowDAO showDAO) {
            this.showService = showService;
            this.showDAO = showDAO;
        }

        // 🏠 Home page
        @GetMapping("/")
        public String episodes() {
            return "episodes";
        }

        // 📋 View all episodes
        @GetMapping("/episodes")
        public String viewAllEpisodes(Model model) {
            List<Show> episodes = showService.getAllShows();
            model.addAttribute("episodeList", episodes);
            return "episodes";
        }

        // 🔍 Search episodes by title
        @GetMapping("/search")
        public String searchEpisodes(@RequestParam String keyword, Model model) {
            List<Show> found = showService.findShowsByTitle(keyword);
            model.addAttribute("episodeList", found);
            model.addAttribute("searchKeyword", keyword);
            return "viewEpisodes";
        }

        // 📝 Show episode creation form
        @GetMapping("/create")
        public String showCreateForm() {
            return "createEpisode"; // your form page
        }

        // 💾 Save new episode
        @PostMapping("/save")
        public String saveEpisode(@ModelAttribute Show episode, RedirectAttributes redirectAttributes) {
            showService.saveShow(episode);
            redirectAttributes.addFlashAttribute("message", "Episode saved successfully!");
            return "redirect:/episodes";
        }

        // 🗑️ Delete episode
        @GetMapping("/delete")
        public String deleteEpisode(@RequestParam String episodeId, RedirectAttributes redirectAttributes) {
            showService.deleteShow(episodeId);
            redirectAttributes.addFlashAttribute("message", "Episode deleted successfully!");
            return "redirect:/episodes/view";
        }
    }

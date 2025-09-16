package com.example.demo.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.Entity.Show;
import com.example.demo.Service.ShowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

    @Controller
    @RequestMapping("/episodes")
    public class ShowC {

        private final ShowService showService;

        public ShowC(ShowService showService) {
            this.showService = showService;
        }

        // 📋 View all episodes
        @GetMapping("/episodes")
        public String viewAllEpisodes(Model model) {
            List<Show> episodes = showService.getAllEpisodes();
            model.addAttribute("episodeList", episodes);
            return "episodes"; // your JSP/HTML page name
        }

        // 🔍 Search episodes by title
        @GetMapping("/search")
        public String searchEpisodes(@RequestParam String keyword, Model model) {
            List<Show> found = showService.searchByTitle(keyword);
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
            showService.saveEpisode(episode);
            redirectAttributes.addFlashAttribute("message", "Episode saved successfully!");
            return "redirect:/episodes/view";
        }

        // 🗑️ Delete episode
        @GetMapping("/delete")
        public String deleteEpisode(@RequestParam String episodeId, RedirectAttributes redirectAttributes) {
            showService.deleteEpisode(episodeId);
            redirectAttributes.addFlashAttribute("message", "Episode deleted successfully!");
            return "redirect:/episodes/view";
        }
    }

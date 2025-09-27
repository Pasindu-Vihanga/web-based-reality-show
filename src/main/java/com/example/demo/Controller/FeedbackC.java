package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Feedback;
import com.example.demo.Entity.User;
import com.example.demo.Service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FeedbackC {

    private final FeedbackService feedbackService;

    public FeedbackC(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/feedbackU")
    public String viewUserFeedback(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<Feedback> myFeedback = feedbackService.findByUserId(loggedInUser.getUserId());
        model.addAttribute("feedbackList", myFeedback);
        model.addAttribute("newFeedback", new Feedback());
        model.addAttribute("user", loggedInUser);

        return "feedbackU";
    }

    @PostMapping("/feedback/add")
    public String addFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        feedback.setUser(loggedInUser);
        feedback.setSubmittedAt(LocalDateTime.now());
        feedbackService.saveFeedback(feedback);

        return "redirect:/feedbackU";
    }

    @GetMapping("/admin/feedbackA")
    public String viewAllFeedbackAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        List<Feedback> allFeedback = feedbackService.getAllFeedback();
        model.addAttribute("feedbackList", allFeedback);

        return "feedbackA";
    }

    @PostMapping("/admin/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable("id") Long feedbackId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        feedbackService.deleteFeedback(feedbackId);
        return "redirect:/admin/feedbackA";
    }
}

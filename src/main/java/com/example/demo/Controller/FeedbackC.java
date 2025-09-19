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

    /* ================== USER: VIEW MY FEEDBACK ================== */
    @GetMapping("/feedbackU")
    public String viewUserFeedback(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU"; // ✅ redirect if not logged in
        }

        List<Feedback> myFeedback = feedbackService.findByUserId(loggedInUser.getUserId());
        model.addAttribute("feedbackList", myFeedback);
        model.addAttribute("newFeedback", new Feedback()); // for feedback form
        model.addAttribute("user", loggedInUser); // keep session info in UI

        return "feedbackU"; // ✅ user UI template
    }

    /* ================== USER: SUBMIT FEEDBACK ================== */
    @PostMapping("/feedback/add")
    public String addFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU"; // ✅ must be logged in
        }

        feedback.setUser(loggedInUser);
        feedback.setSubmittedAt(LocalDateTime.now());
        feedbackService.saveFeedback(feedback);

        return "redirect:/feedbackU"; // ✅ reload user feedback page
    }

    /* ================== ADMIN: VIEW ALL FEEDBACK ================== */
    @GetMapping("/admin/feedbackA")
    public String viewAllFeedbackAdmin(HttpSession session, Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // ✅ force login
        }

        List<Feedback> allFeedback = feedbackService.getAllFeedback();
        model.addAttribute("feedbackList", allFeedback);
        model.addAttribute("admin", loggedInAdmin);

        return "feedbackA"; // ✅ must match templates/feedbackA.html
    }

    /* ================== ADMIN: EDIT FEEDBACK ================== */
    @GetMapping("/admin/feedback/edit/{id}")
    public String editFeedbackForm(@PathVariable("id") Long feedbackId,
                                   HttpSession session,
                                   Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // ✅ secure
        }

        Optional<Feedback> feedbackOpt = feedbackService.findById(feedbackId);
        if (feedbackOpt.isPresent()) {
            model.addAttribute("feedback", feedbackOpt.get());
            model.addAttribute("admin", loggedInAdmin);
            return "editFeedback"; // ✅ thymeleaf template
        }

        return "redirect:/admin/feedbackA"; // ✅ fallback
    }

    /* ================== ADMIN: UPDATE FEEDBACK ================== */
    @PostMapping("/admin/feedback/update")
    public String updateFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        feedbackService.updateFeedback(feedback);
        return "redirect:/admin/feedbackA";
    }

    /* ================== ADMIN: DELETE FEEDBACK ================== */
    @PostMapping("/admin/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable("id") Long feedbackId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        feedbackService.deleteFeedback(feedbackId);
        return "redirect:/admin/feedbackA";
    }
}

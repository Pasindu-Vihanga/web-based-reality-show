package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Result;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ContestantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class ResultC {

    private final ResultService resultService;
    private final VoteService voteService;
    private final ContestantService contestantService;

    public ResultC(ResultService resultService, VoteService voteService, ContestantService contestantService) {
        this.resultService = resultService;
        this.voteService = voteService;
        this.contestantService = contestantService;
    }

    /** ========== VIEW ALL RESULTS (ADMIN) ========== */
    @GetMapping("/resultsA")
    public String viewAllResultsAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA"; // only admins allowed
        }

        List<Result> results = resultService.getAllResults();
        model.addAttribute("resultList", results);

        // For dropdowns in Add form
        model.addAttribute("sessionList", voteService.getAllSessions());
        model.addAttribute("contestantList", contestantService.getAllContestants());
        model.addAttribute("newResult", new Result());

        return "resultsA";
    }

    /** ========== VIEW ACTIVE SESSION RESULTS (USER) ========== */
    @GetMapping("/resultsU")
    public String viewActiveResults(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/loginU"; // only logged-in users
        }

        // Get active sessions
        List<Vote> activeSessions = voteService.getActiveSessions();
        if (activeSessions.isEmpty()) {
            model.addAttribute("message", "No active results available.");
            return "resultsU";
        }

        // Pick latest session
        Vote latestSession = activeSessions.stream()
                .max(Comparator.comparing(Vote::getStartTime))
                .orElse(null);

        if (latestSession != null) {
            model.addAttribute("resultList", resultService.findBySessionId(latestSession.getSessionId()));
        } else {
            model.addAttribute("message", "No results available for active sessions.");
        }

        return "resultsU";
    }

    /** ========== ADD NEW RESULT (ADMIN) ========== */
    @PostMapping("/result/add")
    public String addResult(@ModelAttribute Result result, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (resultService.validateResult(result)) {
            resultService.saveResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ========== EDIT RESULT FORM (ADMIN) ========== */
    @GetMapping("/result/edit/{id}")
    public String editResultForm(@PathVariable("id") Long resultId, Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        Optional<Result> result = resultService.findById(resultId);
        if (result.isPresent()) {
            model.addAttribute("result", result.get());
            model.addAttribute("sessionList", voteService.getAllSessions());
            model.addAttribute("contestantList", contestantService.getAllContestants());
            return "editResult";
        } else {
            return "redirect:/resultsA";
        }
    }

    /** ========== UPDATE RESULT (ADMIN) ========== */
    @PostMapping("/result/update")
    public String updateResult(@ModelAttribute Result result, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        if (resultService.validateResult(result)) {
            resultService.updateResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ========== DELETE RESULT (ADMIN) ========== */
    @PostMapping("/result/delete/{id}")
    public String deleteResult(@PathVariable("id") Long resultId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/loginA";
        }

        resultService.deleteResult(resultId);
        return "redirect:/resultsA";
    }
}

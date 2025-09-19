package com.example.demo.Controller;

import com.example.demo.Entity.Result;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ContestantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String viewAllResultsAdmin(Model model) {
        List<Result> results = resultService.getAllResults();
        model.addAttribute("resultList", results);

        // For dropdowns
        model.addAttribute("sessionList", voteService.getAllSessions());
        model.addAttribute("contestantList", contestantService.getAllContestants());
        model.addAttribute("newResult", new Result());

        return "resultsA"; // Admin template
    }

    /** ========== VIEW RESULTS (USER by Session) ========== */
    @GetMapping("/results/{sessionId}")
    public String viewResultsForSession(@PathVariable("sessionId") String sessionId, Model model) {
        List<Result> results = resultService.findBySessionId(sessionId);
        model.addAttribute("resultList", results);
        return "resultsU"; // User template
    }

    /** ========== ADD NEW RESULT (ADMIN) ========== */
    @PostMapping("/result/add")
    public String addResult(@ModelAttribute Result result) {
        if (resultService.validateResult(result)) {
            resultService.saveResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ========== EDIT RESULT FORM ========== */
    @GetMapping("/result/edit/{id}")
    public String editResultForm(@PathVariable("id") Long resultId, Model model) {
        Optional<Result> result = resultService.findById(resultId);
        if (result.isPresent()) {
            model.addAttribute("result", result.get());
            model.addAttribute("sessionList", voteService.getAllSessions());
            model.addAttribute("contestantList", contestantService.getAllContestants());
            return "editResult"; // edit form template
        } else {
            return "redirect:/resultsA";
        }
    }

    /** ========== UPDATE RESULT ========== */
    @PostMapping("/result/update")
    public String updateResult(@ModelAttribute Result result) {
        if (resultService.validateResult(result)) {
            resultService.updateResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ========== DELETE RESULT ========== */
    @PostMapping("/result/delete/{id}")
    public String deleteResult(@PathVariable("id") Long resultId) {
        resultService.deleteResult(resultId);
        return "redirect:/resultsA";
    }
}


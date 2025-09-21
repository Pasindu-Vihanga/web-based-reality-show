package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Result;
import com.example.demo.Entity.User;
import com.example.demo.Entity.Vote;
import com.example.demo.Service.ResultService;
import com.example.demo.Service.VoteService;
import com.example.demo.Service.ContestantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
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

    /** ================== ADMIN: VIEW ALL ================== */
    @GetMapping("/resultsA")
    public String viewAllResultsAdmin(Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        List<Result> results = resultService.getAllResults();
        model.addAttribute("resultList", results);

        model.addAttribute("sessionList", voteService.getAllSessions());
        model.addAttribute("contestantList", contestantService.getAllContestants());
        model.addAttribute("newResult", new Result());

        return "resultsA";
    }

    /** ================== USER: VIEW ACTIVE SESSION RESULTS ================== */
    @GetMapping("/resultsU")
    public String viewActiveResults(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<Vote> activeSessions = voteService.getActiveSessions();
        if (activeSessions.isEmpty()) {
            model.addAttribute("message", "No active results available.");
            return "resultsU";
        }

        Vote latestSession = activeSessions.stream()
                .max(Comparator.comparing(Vote::getStartTime))
                .orElse(null);

        if (latestSession != null) {
            model.addAttribute("resultList", resultService.findBySessionId(latestSession.getSessionId()));
            model.addAttribute("totalVotes", resultService.countVotesBySession(latestSession.getSessionId()));
        } else {
            model.addAttribute("message", "No results available.");
        }

        return "resultsU";
    }

    /** ================== ADD NEW RESULT ================== */
    @PostMapping("/result/add")
    public String addResult(@ModelAttribute Result result, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (resultService.validateResult(result)) {
            resultService.saveResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ================== EDIT RESULT FORM ================== */
    @GetMapping("/result/edit/{id}")
    public String editResultForm(@PathVariable("id") Long resultId, Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        Optional<Result> result = resultService.findById(resultId);
        if (result.isPresent()) {
            model.addAttribute("result", result.get());
            model.addAttribute("sessionList", voteService.getAllSessions());
            model.addAttribute("contestantList", contestantService.getAllContestants());
            return "editResult";
        }
        return "redirect:/resultsA";
    }

    /** ================== UPDATE RESULT ================== */
    @PostMapping("/result/update")
    public String updateResult(@ModelAttribute Result result, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        if (resultService.validateResult(result)) {
            resultService.updateResult(result);
        }
        return "redirect:/resultsA";
    }

    /** ================== DELETE RESULT ================== */
    @PostMapping("/result/delete/{id}")
    public String deleteResult(@PathVariable("id") Long resultId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) return "redirect:/loginA";

        resultService.deleteResult(resultId);
        return "redirect:/resultsA";
    }

    /** ================== REST ENDPOINTS FOR RANKINGS ================== */

    @GetMapping("/api/results/rankings/{sessionId}")
    @ResponseBody
    public ResponseEntity<List<Result>> getRankings(@PathVariable String sessionId) {
        return ResponseEntity.ok(resultService.getRankings(sessionId));
    }

    @GetMapping("/api/results/totalVotes/{sessionId}")
    @ResponseBody
    public ResponseEntity<Integer> getTotalVotes(@PathVariable String sessionId) {
        return ResponseEntity.ok(resultService.countVotesBySession(sessionId));
    }

    @PostMapping("/api/results/clean")
    @ResponseBody
    public ResponseEntity<String> cleanResults() {
        int removed = resultService.cleanInvalidResults();
        return ResponseEntity.ok(removed + " invalid results removed.");
    }
}

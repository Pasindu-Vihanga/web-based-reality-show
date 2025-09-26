package com.example.demo.Service;

import com.example.demo.DAO.ResultDAO;
import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Result;
import com.example.demo.Entity.Vote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {

    private final ResultDAO resultDAO;

    public ResultService(ResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }

    /** ================= CRUD ================= */
    public void saveResult(Result result) {
        if (validateResult(result)) {
            resultDAO.save(result);
        }
    }

    public int updateResult(Result result) {
        if (validateResult(result)) {
            return resultDAO.update(result);
        }
        return 0;
    }

    public int deleteResult(Long resultId) {
        return resultDAO.delete(resultId);
    }

    public List<Result> getAllResults() {
        return resultDAO.findAll();
    }

    public Optional<Result> findById(Long resultId) {
        return resultDAO.findById(resultId);
    }

    /** ================= QUERY HELPERS ================= */
    public List<Result> findBySessionId(String sessionId) {
        return resultDAO.findBySessionId(sessionId);
    }

    public int countVotesBySession(String sessionId) {
        return resultDAO.countVotesBySession(sessionId);
    }

    public List<Result> getRankings(String sessionId) {
        return resultDAO.getRankings(sessionId);
    }

    /** ================= MAINTENANCE ================= */
    public int removeInvalidResults() {
        return resultDAO.removeInvalidResults();
    }

    /** ================= VALIDATION ================= */
    public boolean validateResult(Result result) {
        return result != null &&
                result.getVotingSession() != null &&
                result.getVotingSession().getSessionId() != null &&
                result.getContestant() != null &&
                result.getContestant().getContestantId() != null &&
                result.getVotesCount() >= 0 &&
                (result.getPlace() == null || result.getPlace() > 0) &&
                result.getStatus() != null && !result.getStatus().isBlank();
    }

    /** ================= CAST VOTE ================= */
    public void castVote(String sessionId, Contestant contestant) {
        // Fetch all results for this session
        List<Result> results = resultDAO.findBySessionId(sessionId);

        Optional<Result> existing = results.stream()
                .filter(r -> r.getContestant().getContestantId().equals(contestant.getContestantId()))
                .findFirst();

        if (existing.isPresent()) {
            // Increment vote count
            Result result = existing.get();
            result.setVotesCount(result.getVotesCount() + 1);
            resultDAO.update(result);
        } else {
            // Create new result row for this contestant in this session
            Result newResult = new Result();

            // Create a lightweight Vote object (only ID + default values)
            Vote voteSession = new Vote();
            voteSession.setSessionId(sessionId);
            voteSession.setActive(true);
            voteSession.setMaxVotesPerUser(1);  // default fallback
            voteSession.setStatus("Ongoing");   // since casting vote happens during an active session

            newResult.setVotingSession(voteSession);
            newResult.setContestant(contestant);
            newResult.setVotesCount(1);
            newResult.setPlace(null);
            newResult.setStatus("safe"); // default until processed (e.g. eliminated/qualified later)

            resultDAO.save(newResult);
        }
    }
}

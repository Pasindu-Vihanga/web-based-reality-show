package com.example.demo.Service;

import com.example.demo.DAO.ResultDAO;
import com.example.demo.Entity.Result;
import com.example.demo.Entity.Contestant;
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

    public void saveResult(Result result) {
        resultDAO.save(result);
    }

    public int updateResult(Result result) {
        return resultDAO.update(result);
    }

    public int deleteResult(Long resultId) {
        return resultDAO.delete(resultId);
    }

    public List<Result> getAllResults() {
        return resultDAO.findAll();
    }

    public List<Result> findBySessionId(String sessionId) {
        return resultDAO.findBySessionId(sessionId);
    }

    public Optional<Result> findById(Long resultId) {
        return resultDAO.findById(resultId);
    }

    public boolean validateResult(Result result) {
        if (result.getVotingSession() == null || result.getVotingSession().getSessionId() == null) return false;
        if (result.getContestant() == null || result.getContestant().getContestantId() == null) return false;
        if (result.getVotesCount() < 0) return false;
        if (result.getPlace() != null && result.getPlace() <= 0) return false;
        if (result.getStatus() == null || result.getStatus().isBlank()) return false;
        return true;
    }

    /** ================== CAST VOTE ================== */
    public void castVote(String sessionId, String contestantId) {
        // Check if contestant already has a result row for this session
        Optional<Result> existing = resultDAO.findBySessionId(sessionId).stream()
                .filter(r -> r.getContestant().getContestantId().equals(contestantId))
                .findFirst();

        if (existing.isPresent()) {
            resultDAO.incrementVote(sessionId, contestantId);
        } else {
            // Create new result record
            Result newResult = new Result();
            newResult.setVotingSession(new Vote(sessionId, null, null, null, true, 1));
            newResult.setContestant(new Contestant(contestantId, null, null, null, "active", null));
            newResult.setVotesCount(1);
            newResult.setPlace(null);
            newResult.setStatus("safe");
            resultDAO.save(newResult);
        }
    }
}

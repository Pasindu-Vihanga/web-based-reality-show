package com.example.demo.Service;

import com.example.demo.DAO.ResultDAO;
import com.example.demo.Entity.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {

    private final ResultDAO resultDAO;

    public ResultService(ResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }

    /** ================== CRUD ================== */
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

    /** ================== VALIDATION ================== */
    public boolean validateResult(Result result) {
        if (result.getVotingSession() == null || result.getVotingSession().getSessionId() == null) return false;
        if (result.getContestant() == null || result.getContestant().getContestantId() == null) return false;
        if (result.getVotesCount() < 0) return false;
        if (result.getPlace() != null && result.getPlace() <= 0) return false;
        if (result.getStatus() == null || result.getStatus().isBlank()) return false;
        return true;
    }

    /** ================== EXTRA FEATURES ================== */
    public int countVotesBySession(String sessionId) {
        return resultDAO.countVotesBySession(sessionId);
    }

    public List<Result> getRankings(String sessionId) {
        return resultDAO.getRankings(sessionId);
    }

    public int cleanInvalidResults() {
        return resultDAO.removeInvalidResults();
    }
}

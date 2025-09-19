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

    /** ========== SAVE NEW RESULT ========== */
    public void saveResult(Result result) {
        resultDAO.save(result);
    }

    /** ========== UPDATE RESULT ========== */
    public int updateResult(Result result) {
        return resultDAO.update(result);
    }

    /** ========== DELETE RESULT ========== */
    public int deleteResult(Long resultId) {
        return resultDAO.delete(resultId);
    }

    /** ========== FIND ALL RESULTS ========== */
    public List<Result> getAllResults() {
        return resultDAO.findAll();
    }

    /** ========== FIND RESULTS BY SESSION ========== */
    public List<Result> findBySessionId(String sessionId) {
        return resultDAO.findBySessionId(sessionId);
    }

    /** ========== FIND RESULT BY ID ========== */
    public Optional<Result> findById(Long resultId) {
        return resultDAO.findById(resultId);
    }

    /** ========== VALIDATION LOGIC ========== */
    public boolean validateResult(Result result) {
        if (result.getVotingSession() == null || result.getVotingSession().getSessionId() == null) {
            return false; // must be linked to a session
        }
        if (result.getContestant() == null || result.getContestant().getContestantId() == null) {
            return false; // must be linked to a contestant
        }
        if (result.getVotesCount() < 0) {
            return false; // votes cannot be negative
        }
        if (result.getPlace() != null && result.getPlace() <= 0) {
            return false; // place must be positive if set
        }
        if (result.getStatus() == null || result.getStatus().isBlank()) {
            return false; // must have a status
        }
        return true;
    }
}

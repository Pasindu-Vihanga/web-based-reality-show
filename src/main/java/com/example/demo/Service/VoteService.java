package com.example.demo.Service;

import com.example.demo.DAO.VoteDAO;
import com.example.demo.Entity.Vote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteDAO voteDAO;

    public VoteService(VoteDAO voteDAO) {
        this.voteDAO = voteDAO;
    }

    /** ========== GET ALL SESSIONS ========== */
    public List<Vote> getAllSessions() {
        return voteDAO.findAll();
    }

    /** ========== GET ACTIVE SESSIONS ========== */
    public List<Vote> getActiveSessions() {
        return voteDAO.findAll().stream()
                .filter(Vote::isActive)
                .toList();
    }

    /** ========== FIND BY ID ========== */
    public Optional<Vote> findSessionById(String sessionId) {
        return voteDAO.findById(sessionId);
    }

    /** ========== FIND BY EPISODE ID ========== */
    public List<Vote> findSessionsByEpisode(String episodeId) {
        return voteDAO.findByEpisodeId(episodeId);
    }

    /** ========== SAVE ========== */
    public void saveSession(Vote session) {
        voteDAO.save(session);
    }

    /** ========== UPDATE ========== */
    public int updateSession(Vote session) {
        return voteDAO.update(session);
    }

    /** ========== DELETE ========== */
    public int deleteSession(String sessionId) {
        return voteDAO.delete(sessionId);
    }

    /** ========== VALIDATION LOGIC ========== */
    public boolean validateSession(Vote session) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            return false;
        }
        if (session.getEndTime().isBefore(session.getStartTime())) {
            return false;
        }
        return session.getMaxVotesPerUser() > 0;
    }
}

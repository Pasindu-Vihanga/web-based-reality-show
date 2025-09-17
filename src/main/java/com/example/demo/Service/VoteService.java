package com.example.demo.Service;

import com.example.demo.DAO.VoteDAO;
import com.example.demo.Entity.Vote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteDAO votingSessionDAO;
    private final VoteDAO voteDAO;

    public VoteService(VoteDAO votingSessionDAO, VoteDAO voteDAO) {
        this.votingSessionDAO = votingSessionDAO;
        this.voteDAO = voteDAO;
    }

    /** ========== GET ALL SESSIONS ========== */
    public List<Vote> getAllSessions() {
        return voteDAO.findAll();
    }

    /** ========== GET ACTIVE SESSIONS ========== */
    public List<Vote> getActiveSessions() {
        return votingSessionDAO.findAll().stream()
                .filter(Vote::isActive)
                .toList();
    }

    /** ========== FIND BY ID ========== */
    public Optional<Vote> findSessionById(String sessionId) {
        return votingSessionDAO.findById(sessionId);
    }

    /** ========== FIND BY EPISODE ID ========== */
    public List<Vote> findSessionsByEpisode(String episodeId) {
        return votingSessionDAO.findByEpisodeId(episodeId);
    }

    /** ========== SAVE NEW SESSION ========== */
    public void saveSession(Vote session) {
        voteDAO.save(session);
    }

    /** ========== UPDATE SESSION ========== */
    public int updateSession(Vote session) {
        return voteDAO.update(session);
    }

    /** ========== DELETE SESSION ========== */
    public int deleteSession(String sessionId) {
        return voteDAO.delete(sessionId);
    }

    /** ========== VALIDATION LOGIC ========== */
    public boolean validateSession(Vote session) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            return false;
        }
        if (session.getEndTime().isBefore(session.getStartTime())) {
            return false; // End must be after start
        }
        if (session.getMaxVotesPerUser() <= 0) {
            return false; // At least 1 vote must be allowed
        }
        return true;
    }
}

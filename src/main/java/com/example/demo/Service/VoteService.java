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

    public List<Vote> getAllSessions() {
        return voteDAO.findAll();
    }

    public List<Vote> getActiveSessions() {
        return voteDAO.findAll().stream().filter(Vote::isActive).toList();
    }

    public Optional<Vote> findSessionById(String sessionId) {
        return voteDAO.findById(sessionId);
    }

    public List<Vote> findSessionsByEpisode(String episodeId) {
        return voteDAO.findByEpisodeId(episodeId);
    }

    public void saveSession(Vote session) {
        voteDAO.save(session);
    }

    public int updateSession(Vote session) {
        return voteDAO.update(session);
    }

    public int deleteSession(String sessionId) {
        return voteDAO.delete(sessionId);
    }

    public boolean validateSession(Vote session) {
        return session != null &&
                session.getStartTime() != null &&
                session.getEndTime() != null &&
                !session.getEndTime().isBefore(session.getStartTime()) &&
                session.getMaxVotesPerUser() > 0;
    }
}

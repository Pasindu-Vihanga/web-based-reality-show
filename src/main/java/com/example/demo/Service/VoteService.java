package com.example.demo.Service;

import com.example.demo.DAO.VoteDAO;
import com.example.demo.Entity.Vote;
import com.example.demo.Entity.Show;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteDAO voteDAO;
    private final ShowService showService; // ✅ keep ShowService

    public VoteService(VoteDAO voteDAO, @Lazy ShowService showService) { // ✅ lazy injection
        this.voteDAO = voteDAO;
        this.showService = showService;
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
        // ✅ ensure episode is valid
        Optional<Show> showOpt = showService.findShowById(session.getShow().getEpisodeId());
        if (showOpt.isPresent() && validateSession(session)) {
            session.setShow(showOpt.get());
            voteDAO.save(session);
        }
    }

    public int updateSession(Vote session) {
        Optional<Show> showOpt = showService.findShowById(session.getShow().getEpisodeId());
        if (showOpt.isPresent() && validateSession(session)) {
            session.setShow(showOpt.get());
            return voteDAO.update(session);
        }
        return 0;
    }

    public int deleteSession(String sessionId) {
        return voteDAO.delete(sessionId);
    }

    public boolean validateSession(Vote session) {
        return session != null &&
                session.getShow() != null &&
                session.getShow().getEpisodeId() != null &&
                session.getStartTime() != null &&
                session.getEndTime() != null &&
                !session.getEndTime().isBefore(session.getStartTime()) &&
                session.getMaxVotesPerUser() >= 0; // allow 0 = no limit
    }
}

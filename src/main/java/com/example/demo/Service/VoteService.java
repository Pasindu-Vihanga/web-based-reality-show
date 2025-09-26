package com.example.demo.Service;

import com.example.demo.Config.VoteID;
import com.example.demo.DAO.VoteDAO;
import com.example.demo.Entity.Show;
import com.example.demo.Entity.Vote;
import com.example.demo.Config.VoteID;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteDAO voteDAO;
    private final ShowService showService;

    public VoteService(VoteDAO voteDAO, ShowService showService) {
        this.voteDAO = voteDAO;
        this.showService = showService;
    }

    /** Initialize session ID generator from DB */
    @PostConstruct
    public void initSessionIdGenerator() {
        int last = voteDAO.getLastSessionNumber();
        VoteID.initialize(last);
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

    /** Save new session */
    public void saveSession(Vote session) {
        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(VoteID.generateSessionId());
        }

        // Resolve Show & auto set times if missing
        if (session.getShow() != null && session.getShow().getEpisodeId() != null) {
            Optional<Show> ep = showService.findShowById(session.getShow().getEpisodeId());
            if (ep.isPresent()) {
                Show show = ep.get();
                session.setShow(show);

                // Default startTime = showDate + showTime
                if (session.getStartTime() == null && show.getShowDate() != null && show.getShowTime() != null) {
                    session.setStartTime(show.getShowDate().atTime(show.getShowTime()));
                }
            }
        }

        // Default endTime = +1 hour after startTime
        if (session.getEndTime() == null && session.getStartTime() != null) {
            session.setEndTime(session.getStartTime().plusHours(1));
        }

        // Default status
        if (session.getStatus() == null || session.getStatus().isBlank()) {
            session.setStatus("Upcoming");
        }

        if (validateSession(session)) {
            voteDAO.save(session);
        }
    }

    /** Update existing session */
    public int updateSession(Vote session) {
        if (session.getShow() != null && session.getShow().getEpisodeId() != null) {
            Optional<Show> ep = showService.findShowById(session.getShow().getEpisodeId());
            ep.ifPresent(session::setShow);
        }

        // Auto update status
        LocalDateTime now = LocalDateTime.now();
        if (session.getStartTime() != null && session.getEndTime() != null) {
            if (now.isBefore(session.getStartTime())) {
                session.setStatus("Upcoming");
            } else if (now.isAfter(session.getEndTime())) {
                session.setStatus("Completed");
            } else {
                session.setStatus("Ongoing");
            }
        }

        if (validateSession(session)) {
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
                session.getMaxVotesPerUser() > 0 &&
                session.getStatus() != null;
    }
}

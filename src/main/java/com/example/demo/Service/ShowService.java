package com.example.demo.Service;

import com.example.demo.DAO.ShowDAO;
import com.example.demo.Entity.Show;
import com.example.demo.Entity.Vote;
import com.example.demo.Entity.Contestant;
import com.example.demo.Config.EpisodeID;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowDAO showDAO;
    private final ContestantService contestantService;
    private final VoteService voteService; // ✅ keep VoteService

    public ShowService(ShowDAO showDAO,
                       ContestantService contestantService,
                       @Lazy VoteService voteService) { // ✅ lazy injection
        this.showDAO = showDAO;
        this.contestantService = contestantService;
        this.voteService = voteService;
    }

    /** Initialize episode ID generator from DB */
    @PostConstruct
    public void initEpisodeIdGenerator() {
        int lastNumber = showDAO.getLastEpisodeNumber();
        EpisodeID.initialize(lastNumber);
    }

    /** Save new episode */
    public void saveShow(Show show) {
        if (show.getEpisodeId() == null || show.getEpisodeId().isBlank()) {
            show.setEpisodeId(EpisodeID.generateEpisodeId());
        }
        if (show.getStatus() == null || show.getStatus().isBlank()) {
            show.setStatus("Upcoming");
        }
        if (validateShow(show)) {
            showDAO.save(show);
        }
    }

    /** Update existing episode */
    public int updateShow(Show show) {
        if (validateShow(show)) {
            return showDAO.update(show);
        }
        return 0;
    }

    public void deleteShow(String episodeId) {
        showDAO.delete(episodeId);
    }

    /** Get all episodes enriched with sessions + contestants */
    public List<Show> getAllShows() {
        List<Show> shows = showDAO.findAll();
        for (Show show : shows) {
            List<Vote> sessions = voteService.findSessionsByEpisode(show.getEpisodeId());
            List<Contestant> contestants = contestantService.findByEpisodeId(show.getEpisodeId());
            show.setSessions(sessions);
            show.setContestants(contestants);
        }
        return shows;
    }

    public Optional<Show> findShowById(String episodeId) {
        Optional<Show> showOpt = showDAO.findById(episodeId);
        showOpt.ifPresent(show -> {
            show.setSessions(voteService.findSessionsByEpisode(episodeId));
            show.setContestants(contestantService.findByEpisodeId(episodeId));
        });
        return showOpt;
    }

    public List<Show> findShowsByTitle(String title) {
        return showDAO.findByTitle(title);
    }

    public boolean validateShow(Show show) {
        return show != null &&
                show.getEpisodeId() != null && !show.getEpisodeId().isBlank() &&
                show.getShowTitle() != null && !show.getShowTitle().isBlank();
    }
}

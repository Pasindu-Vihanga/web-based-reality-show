package com.example.demo.Service;

import com.example.demo.DAO.ShowDAO;
import com.example.demo.Entity.Show;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowDAO showDAO;

    public ShowService(ShowDAO showDAO) {
        this.showDAO = showDAO;
    }

    /** ========== SAVE NEW SHOW ========== */
    public void saveShow(Show show) {
        if (validateShow(show)) {
            if (show.getStatus() == null || show.getStatus().isBlank()) {
                show.setStatus("UPCOMING"); // ✅ Default status
            }
            showDAO.save(show);
        } else {
            throw new IllegalArgumentException("Invalid Show details!");
        }
    }

    /** ========== UPDATE SHOW ========== */
    public int updateShow(Show show) {
        if (validateShow(show)) {
            if (show.getStatus() == null || show.getStatus().isBlank()) {
                show.setStatus("UPCOMING");
            }
            return showDAO.update(show);
        }
        return 0;
    }

    /** ========== DELETE SHOW ========== */
    public void deleteShow(String episodeId) {
        if (episodeId != null && !episodeId.isBlank()) {
            showDAO.delete(episodeId);
        }
    }

    /** ========== GET ALL SHOWS ========== */
    public List<Show> getAllShows() {
        return showDAO.findAll();
    }

    /** ========== SEARCH SHOWS BY TITLE ========== */
    public List<Show> findShowsByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }
        return showDAO.findByTitle(title);
    }

    /** ========== FIND SHOW BY ID ========== */
    public Optional<Show> findShowById(String episodeId) {
        if (episodeId == null || episodeId.isBlank()) {
            return Optional.empty();
        }
        return showDAO.findById(episodeId);
    }

    /** ========== VALIDATE SHOW ========== */
    public boolean validateShow(Show show) {
        return show != null
                && show.getEpisodeId() != null && !show.getEpisodeId().isBlank()
                && show.getShowTitle() != null && !show.getShowTitle().isBlank()
                && show.getShowDate() != null; // ✅ showDate is mandatory
    }
}

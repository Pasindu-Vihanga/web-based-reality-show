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

    /** ========== SAVE SHOW ========== */
    public void saveShow(Show show) {
        if (validateShow(show)) {
            showDAO.save(show);
        } else {
            throw new IllegalArgumentException("Invalid show details provided");
        }
    }

    /** ========== UPDATE SHOW ========== */
    public int updateShow(Show show) {
        if (validateShow(show)) {
            return showDAO.update(show);
        }
        return 0;
    }

    /** ========== DELETE SHOW ========== */
    public void deleteShow(String episodeId) {
        if (episodeId != null && !episodeId.trim().isEmpty()) {
            showDAO.delete(episodeId);
        }
    }

    /** ========== GET ALL SHOWS ========== */
    public List<Show> getAllShows() {
        return showDAO.findAll();
    }

    /** ========== SEARCH SHOWS BY TITLE ========== */
    public List<Show> findShowsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of(); // return empty list instead of querying with null
        }
        return showDAO.findByTitle(title);
    }

    /** ========== FIND BY ID ========== */
    public Optional<Show> findShowById(String episodeId) {
        if (episodeId == null || episodeId.trim().isEmpty()) {
            return Optional.empty();
        }
        return showDAO.findById(episodeId);
    }

    /** ========== CHECK IF SHOW EXISTS ========== */
    public boolean showExists(String episodeId) {
        return findShowById(episodeId).isPresent();
    }

    /** ========== VALIDATION ========== */
    public boolean validateShow(Show show) {
        if (show == null) return false;
        if (show.getEpisodeId() == null || show.getEpisodeId().trim().isEmpty()) return false;
        if (show.getShowTitle() == null || show.getShowTitle().trim().isEmpty()) return false;
        if (show.getShowDate() == null) return false; // show date is mandatory
        // time can be optional (live shows might not have fixed time yet)
        return true;
    }
}

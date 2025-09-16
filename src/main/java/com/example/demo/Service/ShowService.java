package com.example.demo.Service;

import com.example.demo.DAO.ShowDAO;
import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    @Autowired
    private ShowDAO showDAO;

    // Save a new show
    public void saveShow(Show show) {
        showDAO.save(show);
    }

    // Update an existing show
    public int updateShow(Show show) {
        return showDAO.update(show);
    }

    // Delete a show by episode ID
    public int deleteShow(String episodeId) {
        return showDAO.delete(episodeId);
    }

    // Get all shows
    public List<Show> getAllShows() {
        return showDAO.findAll();
    }

    // Find shows by title
    public List<Show> findShowsByTitle(String title) {
        return showDAO.findByTitle(title);
    }

    // Find show by episode ID
    public Optional<Show> findShowById(String episodeId) {
        // Since your DAO doesn't have a findById method, we need to implement it
        // by filtering from all shows (this is not efficient for large datasets)
        return showDAO.findAll().stream()
                .filter(show -> show.getEpisodeId().equals(episodeId))
                .findFirst();
    }

    // Check if a show exists by episode ID
    public boolean showExists(String episodeId) {
        return findShowById(episodeId).isPresent();
    }

    // Additional business logic methods can be added here

    // Example: Validate show before saving
    public boolean validateShow(Show show) {
        if (show.getEpisodeId() == null || show.getEpisodeId().trim().isEmpty()) {
            return false;
        }
        if (show.getShowTitle() == null || show.getShowTitle().trim().isEmpty()) {
            return false;
        }
        if (show.getShowDate() == null) {
            return false;
        }
        return true;
    }

}
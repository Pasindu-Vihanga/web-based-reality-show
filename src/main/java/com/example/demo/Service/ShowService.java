package com.example.demo.Service;

import com.example.demo.DAO.ShowDAO;
import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {
    @Autowired
    private ShowDAO showDAO;

    public void saveShow(Show show) {
        showDAO.save(show);
    }

    public int updateShow(Show show) {
        return showDAO.update(show);
    }

    public void deleteShow(String episodeId) {
        showDAO.delete(episodeId);
    }

    public List<Show> getAllShows() {
        return showDAO.findAll();
    }

    public List<Show> findShowsByTitle(String title) {
        return showDAO.findByTitle(title);
    }

    public Optional<Show> findShowById(String episodeId) {
        return showDAO.findById(episodeId);
    }

    public boolean showExists(String episodeId) {
        return showDAO.findById(episodeId).isPresent();
    }

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
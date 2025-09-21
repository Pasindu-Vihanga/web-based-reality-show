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

    public void saveShow(Show show) {
        if (validateShow(show)) {
            showDAO.save(show);
        }
    }

    public int updateShow(Show show) {
        if (validateShow(show)) {
            return showDAO.update(show);
        }
        return 0;
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

    public boolean validateShow(Show show) {
        return show != null &&
                show.getEpisodeId() != null && !show.getEpisodeId().isBlank() &&
                show.getShowTitle() != null && !show.getShowTitle().isBlank();
    }
}

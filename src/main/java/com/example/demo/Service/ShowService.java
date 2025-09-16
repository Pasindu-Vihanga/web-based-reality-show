package com.example.demo.Service;

import com.example.demo.DAO.ShowDAO;
import com.example.demo.Entity.Show;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowService {

    private final ShowDAO showDAO;

    public ShowService(ShowDAO showDAO) {
        this.showDAO = showDAO;
    }

    public List<Show> getAllEpisodes() {
        return showDAO.findAll();
    }

    public List<Show> searchByTitle(String keyword) {
        return showDAO.findByShowTitleContainingIgnoreCase(keyword);
    }

    public void saveEpisode(Show episode) {
        showDAO.save(episode);
    }

    public void deleteEpisode(String episodeId) {
        showDAO.deleteById(episodeId);
    }
}
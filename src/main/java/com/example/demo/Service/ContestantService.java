package com.example.demo.Service;

import com.example.demo.DAO.ContestantDAO;
import com.example.demo.Entity.Contestant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContestantService {

    private final ContestantDAO contestantDAO;

    public ContestantService(ContestantDAO contestantDAO) {
        this.contestantDAO = contestantDAO;
    }

    public List<Contestant> getAllContestants() {
        return contestantDAO.findAll();
    }

    public List<Contestant> findByEpisodeId(String episodeId) {
        return contestantDAO.findByEpisodeId(episodeId);
    }

    public List<Contestant> findByStatus(String status) {
        return contestantDAO.findByStatus(status);
    }

    /** ✅ Fix: method renamed to match controller */
    public Optional<Contestant> findContestantById(String contestantId) {
        return contestantDAO.findById(contestantId);
    }

    @Transactional
    public void saveContestant(Contestant contestant) {
        if (validateContestant(contestant)) {
            contestantDAO.save(contestant);
        } else {
            throw new IllegalArgumentException("Invalid contestant data");
        }
    }

    @Transactional
    public int updateContestant(Contestant contestant) {
        if (validateContestant(contestant)) {
            return contestantDAO.update(contestant);
        }
        throw new IllegalArgumentException("Invalid contestant data");
    }

    @Transactional
    public int deleteContestant(String contestantId) {
        return contestantDAO.delete(contestantId);
    }

    public boolean validateContestant(Contestant contestant) {
        return contestant != null &&
                contestant.getName() != null && !contestant.getName().isBlank() &&
                contestant.getStatus() != null && !contestant.getStatus().isBlank() &&
                contestant.getShow() != null &&
                contestant.getShow().getEpisodeId() != null &&
                !contestant.getShow().getEpisodeId().isBlank();
    }
}

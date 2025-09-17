package com.example.demo.Service;

import com.example.demo.DAO.ContestantDAO;
import com.example.demo.Entity.Contestant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContestantService {

    private final ContestantDAO contestantDAO;

    public ContestantService(ContestantDAO contestantDAO) {
        this.contestantDAO = contestantDAO;
    }

    /** ========== GET ALL CONTESTANTS ========== */
    public List<Contestant> getAllContestants() {
        return contestantDAO.findAll();
    }

    /** ========== GET CONTESTANTS BY EPISODE ========== */
    public List<Contestant> findByEpisodeId(String episodeId) {
        return contestantDAO.findByEpisodeId(episodeId);
    }

    /** ========== GET CONTESTANTS BY STATUS ========== */
    public List<Contestant> findByStatus(String status) {
        return contestantDAO.findByStatus(status);
    }

    /** ========== FIND BY ID ========== */
    public Optional<Contestant> findContestantById(String contestantId) {
        return contestantDAO.findById(contestantId);
    }

    /** ========== SAVE NEW CONTESTANT ========== */
    public void saveContestant(Contestant contestant) {
        contestantDAO.save(contestant);
    }

    /** ========== UPDATE CONTESTANT ========== */
    public int updateContestant(Contestant contestant) {
        return contestantDAO.update(contestant);
    }

    /** ========== DELETE CONTESTANT ========== */
    public int deleteContestant(String contestantId) {
        return contestantDAO.delete(contestantId);
    }

    /** ========== VALIDATION LOGIC ========== */
    public boolean validateContestant(Contestant contestant) {
        if (contestant.getName() == null || contestant.getName().isBlank()) {
            return false; // Must have a name
        }
        if (contestant.getStatus() == null || contestant.getStatus().isBlank()) {
            return false; // Must have status
        }
        if (contestant.getShow() == null || contestant.getShow().getEpisodeId() == null) {
            return false; // Must be linked to a show/episode
        }
        return true;
    }
}

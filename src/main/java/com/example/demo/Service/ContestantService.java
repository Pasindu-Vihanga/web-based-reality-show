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

    public List<Contestant> getAllContestants() {
        return contestantDAO.findAll();
    }

    public Optional<Contestant> findContestantById(String contestantId) {
        return contestantDAO.findById(contestantId);
    }

    public void saveContestant(Contestant contestant) {
        contestantDAO.save(contestant);
    }

    public int updateContestant(Contestant contestant) {
        return contestantDAO.update(contestant);
    }

    public int deleteContestant(String contestantId) {
        return contestantDAO.delete(contestantId);
    }

    /** ✅ Validation */
    public boolean validateContestant(Contestant contestant) {
        if (contestant.getName() == null || contestant.getName().isBlank()) return false;
        if (contestant.getStatus() == null || contestant.getStatus().isBlank()) return false;
        if (contestant.getShow() == null || contestant.getShow().getEpisodeId() == null) return false;
        return true;
    }
}

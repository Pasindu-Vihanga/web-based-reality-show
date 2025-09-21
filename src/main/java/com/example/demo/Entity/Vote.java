package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voting_session")
public class Vote {

    @Id
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    /** 🔗 Each voting session belongs to one Episode */
    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Show show;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "max_votes_per_user", nullable = false)
    private int maxVotesPerUser;

    /** 🔗 One session has multiple results (votes per contestant) */
    @OneToMany(mappedBy = "votingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results;
}

package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voting_session")
public class Vote {

    @Id
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    // 🔗 Linked Show Episode
    @ManyToOne(fetch = FetchType.LAZY)
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

    // 🔹 New fields for richer functionality
    @Column(name = "current_votes", nullable = false)
    private int currentVotes = 0; // live monitoring

    @Column(name = "status", nullable = false)
    private String status = "Scheduled";
    // Possible values: Scheduled, Ongoing, Ended, Paused

    /** 🔹 Utility Methods **/
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return active && now.isAfter(startTime) && now.isBefore(endTime);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }
}

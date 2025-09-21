package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    /** 🔗 Each result is tied to a voting session */
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Vote votingSession;

    /** 🔗 Each result is tied to a contestant */
    @ManyToOne
    @JoinColumn(name = "contestant_id", nullable = false)
    private Contestant contestant;

    @Column(name = "votes_count", nullable = false)
    private int votesCount;

    @Column(name = "place")
    private Integer place; // 1 = winner, 2 = runner-up, etc.

    @Column(name = "status", nullable = false)
    private String status; // "winner", "eliminated", "safe"
}

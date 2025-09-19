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

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Vote votingSession;   // Voting session linked

    @ManyToOne
    @JoinColumn(name = "contestant_id", nullable = false)
    private Contestant contestant;  // Contestant being voted for

    @Column(name = "votes_count", nullable = false)
    private int votesCount;  // Number of votes received

    @Column(name = "place")
    private Integer place;   // Placement (1 = winner, 2 = runner-up, etc.)

    @Column(name = "status", nullable = false)
    private String status;
    // Example values: "winner", "eliminated", "safe"
}

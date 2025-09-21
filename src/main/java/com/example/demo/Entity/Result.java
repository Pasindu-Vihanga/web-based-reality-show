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
    private Vote votingSession;

    @ManyToOne
    @JoinColumn(name = "contestant_id", nullable = false)
    private Contestant contestant;

    @Column(name = "votes_count", nullable = false)
    private int votesCount;

    @Column(name = "place")
    private Integer place;

    @Column(name = "status", nullable = false)
    private String status;
    // Values: draft, final, public
}

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
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    /** The user who gave the feedback */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Feedback message */
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    /** Rating out of 5 (optional) */
    @Column(name = "rating")
    private Integer rating;

    /** Timestamp of submission */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
}

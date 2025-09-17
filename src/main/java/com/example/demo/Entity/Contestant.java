package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contestant")
public class Contestant {

        @Id
        @Column(name = "contestant_id", nullable = false, unique = true)
        private String contestantId;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "bio", length = 1000)
        private String bio;

        @Column(name = "image_url")
        private String imageUrl;

        @Column(name = "status", nullable = false)
        private String status;
        // Example values: "active", "eliminated"

        @ManyToOne
        @JoinColumn(name = "episode_id", nullable = false)
        private Show show;
        // Each contestant is linked to a Show/Episode
}


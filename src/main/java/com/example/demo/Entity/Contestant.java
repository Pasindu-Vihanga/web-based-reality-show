package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
        private String name;  // first name

        @Column(name = "last_name", nullable = false)
        private String lastName;  // last name / surname

        @Column(name = "age", nullable = false)
        private int age;

        @Column(name = "dob", nullable = false)
        private LocalDate dob;

        @Column(name = "contact_number")
        private String contactNumber;

        @Column(name = "bio", length = 1000)
        private String bio;

        @Lob
        @Column(name = "image", columnDefinition = "LONGBLOB")
        private byte[] image;

        @Column(name = "status", nullable = false)
        private String status; // active, eliminated, winner, runnerup

        /** 🔗 Each contestant belongs to one Episode */
        @ManyToOne
        @JoinColumn(name = "episode_id", nullable = false)
        private Show show;

        /** 🔗 Contestant appears in multiple results (votes across sessions) */
        @OneToMany(mappedBy = "contestant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Result> results;
}

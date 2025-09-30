package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

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

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "age", nullable = false)
        private int age;

        @Column(name = "dob", nullable = false)
        private LocalDate dob;

        @Column(name = "contact_number", length = 15)
        private String contactNumber;

        @Column(name = "bio", length = 1000)
        private String bio;

        @Lob
        @Column(name = "image", columnDefinition = "LONGBLOB")
        private byte[] image;

        @Column(name = "status", nullable = false)
        private String status;

        @ManyToOne
        @JoinColumn(name = "episode_id", nullable = false)
        private Show show;

        @OneToMany(mappedBy = "contestant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Result> results;
}

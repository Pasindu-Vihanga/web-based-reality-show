package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "showepi")
public class Show {

    @Id
    @Column(name = "episode_id", nullable = false, unique = true)
    private String episodeId;

    @Column(name = "show_title", nullable = false)
    private String showTitle;

    @Column(name = "show_description")
    private String showDescription;

    @Column(name = "show_type")
    private String showType;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_time")
    private LocalTime showTime;

    @Column(name = "status")
    private String status;

    /** 🔗 One Episode has many Voting Sessions */
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> sessions;

    /** 🔗 One Episode has many Contestants */
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contestant> contestants;
}

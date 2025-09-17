package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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
}
package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "showepi") // ✅ Corrected annotation syntax

public class Show {

    @Id
    @Column(name = "episodeId", nullable = false, length = 20)
    private String episodeId;

    @Column(name = "showTitle", nullable = false, length = 50)
    private String showTitle;

    @Column(name = "showDescription", nullable = false, length = 255)
    private String showDescription;

    @Column(name = "showImage")
    private String showImage; // ✅ Consider using byte[] if storing binary image data

    @Column(name = "showType", nullable = false, length = 20)
    private String showType;

    @Column(name = "showDate")
    private Date showDate;

    @Column(name = "showTime")
    private Time showTime;
}
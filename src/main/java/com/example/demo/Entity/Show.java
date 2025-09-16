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
    private String episodeId;

    private String showTitle;
    private String showDescription;
    private String showImage;
    private String showType;
    private Date showDate;
    private Time showTime;
}
package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;


@Data
@Entity
public class Show {

    @Id
    private String showId;
    private String showtitle;
    private String showdescription;
    private String showimage;
    private String showtype;
    private Date showdate;
    private Time showtime;
}

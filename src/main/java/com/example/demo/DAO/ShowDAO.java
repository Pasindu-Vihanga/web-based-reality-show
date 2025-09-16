package com.example.demo.DAO;

import com.example.demo.Entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowDAO extends JpaRepository<Show, String> {

    // 🔍 Find episodes by show type
    List<Show> findByShowType(String showType);

    // 🔍 Find episodes by title containing keyword
    List<Show> findByShowTitleContainingIgnoreCase(String keyword);

    // 📅 Find episodes by air date
    List<Show> findByShowDate(java.sql.Date showDate);

    // 🕒 Find episodes by time range
    List<Show> findByShowTimeBetween(java.sql.Time start, java.sql.Time end);
}
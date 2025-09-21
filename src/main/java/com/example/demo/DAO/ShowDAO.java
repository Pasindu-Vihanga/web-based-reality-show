package com.example.demo.DAO;

import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ShowDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Show> showRowMapper = (rs, rowNum) -> {
        Show show = new Show();
        show.setEpisodeId(rs.getString("episode_id"));
        show.setShowTitle(rs.getString("show_title"));
        show.setShowDescription(rs.getString("show_description"));
        show.setShowType(rs.getString("show_type"));

        if (rs.getDate("show_date") != null) {
            show.setShowDate(rs.getDate("show_date").toLocalDate());
        }
        if (rs.getTime("show_time") != null) {
            show.setShowTime(rs.getTime("show_time").toLocalTime());
        }

        show.setStatus(rs.getString("status"));
        show.setSessions(new ArrayList<>());
        return show;
    };

    public void save(Show show) {
        String sql = "INSERT INTO showepi (episode_id, show_title, show_description, show_type, show_date, show_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                show.getEpisodeId(),
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowType(),
                show.getShowDate() != null ? Date.valueOf(show.getShowDate()) : null,
                show.getShowTime() != null ? Time.valueOf(show.getShowTime()) : null,
                show.getStatus()
        );
    }

    public int update(Show show) {
        String sql = "UPDATE showepi SET show_title=?, show_description=?, show_type=?, show_date=?, show_time=?, status=? WHERE episode_id=?";
        return jdbcTemplate.update(sql,
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowType(),
                show.getShowDate() != null ? Date.valueOf(show.getShowDate()) : null,
                show.getShowTime() != null ? Time.valueOf(show.getShowTime()) : null,
                show.getStatus(),
                show.getEpisodeId()
        );
    }

    public int delete(String episodeId) {
        String sql = "DELETE FROM showepi WHERE episode_id=?";
        return jdbcTemplate.update(sql, episodeId);
    }

    public List<Show> findAll() {
        String sql = "SELECT * FROM showepi ORDER BY show_date DESC, show_time DESC";
        return jdbcTemplate.query(sql, showRowMapper);
    }

    public List<Show> findByTitle(String title) {
        String sql = "SELECT * FROM showepi WHERE LOWER(show_title) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, showRowMapper, "%" + title + "%");
    }

    public Optional<Show> findById(String episodeId) {
        String sql = "SELECT * FROM showepi WHERE episode_id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, showRowMapper, episodeId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

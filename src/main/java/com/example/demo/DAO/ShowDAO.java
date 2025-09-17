package com.example.demo.DAO;

import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public class ShowDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Show> showRowMapper = new RowMapper<>() {
        @Override
        public Show mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Show(
                    rs.getString("episode_id"),
                    rs.getString("show_title"),
                    rs.getString("show_description"),
                    rs.getString("show_type"),
                    rs.getDate("show_date").toLocalDate(),
                    rs.getTime("show_time").toLocalTime()
            );
        }
    };

    public void save(Show show) {
        String sql = "INSERT INTO showepi (episode_id, show_title, show_description, show_type, show_date, show_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                show.getEpisodeId(),
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowType(),
                Date.valueOf(show.getShowDate()),
                Time.valueOf(show.getShowTime())
        );
    }

    public int update(Show show) {
        String sql = "UPDATE showepi SET show_title = ?, show_description = ?, show_type = ?, show_date = ?, show_time = ? " +
                "WHERE episode_id = ?";
        return jdbcTemplate.update(sql,
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowType(),
                Date.valueOf(show.getShowDate()),
                Time.valueOf(show.getShowTime()),
                show.getEpisodeId()
        );
    }

    public int delete(String episodeId) {
        String sql = "DELETE FROM showepi WHERE episode_id = ?";
        return jdbcTemplate.update(sql, episodeId);
    }

    public List<Show> findAll() {
        String sql = "SELECT * FROM showepi";
        return jdbcTemplate.query(sql, showRowMapper);
    }

    public List<Show> findByTitle(String title) {
        String sql = "SELECT * FROM showepi WHERE show_title LIKE ?";
        return jdbcTemplate.query(sql, showRowMapper, "%" + title + "%");
    }

    public Optional<Show> findById(String episodeId) {
        String sql = "SELECT * FROM showepi WHERE episode_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, showRowMapper, episodeId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
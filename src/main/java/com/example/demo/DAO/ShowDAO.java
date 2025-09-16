package com.example.demo.DAO;

import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ShowDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for Show entity
    private final RowMapper<Show> showRowMapper = new RowMapper<>() {
        @Override
        public Show mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Show(
                    rs.getString("episode_id"),
                    rs.getString("show_title"),
                    rs.getString("show_description"),
                    rs.getString("show_image"),
                    rs.getString("show_type"),
                    rs.getDate("show_date"),
                    rs.getTime("show_time")
            );
        }
    };

    // Insert Show
    public void save(Show show) {
        String sql = "INSERT INTO showepi (episodeid, showTitle, showDescription, showType, showDate, showTime) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                show.getEpisodeId(),
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowType(),
                show.getShowDate(),
                show.getShowTime());
    }

    // Update Show
    public int update(Show show) {
        String sql = "UPDATE showepi SET episodeid=?, showTitle=?, showDescription=?, showType=?, showDate=?, showTime=?, WHERE episodeId=?";
        return jdbcTemplate.update(sql,
                show.getShowTitle(),
                show.getShowDescription(),
                show.getShowImage(),
                show.getShowType(),
                show.getShowDate(),
                show.getShowTime(),
                show.getEpisodeId());
    }
    // Delete Show by ID
    public int delete(String episodeId) {
        String sql = "DELETE FROM showepi WHERE episodeId=?";
        return jdbcTemplate.update(sql, episodeId);
    }

    // Get all Shows
    public List<Show> findAll() {
        String sql = "SELECT * FROM showepi";
        return jdbcTemplate.query(sql, showRowMapper);
    }

    // Find Shows by Title
    public List<Show> findByTitle(String title) {
        String sql = "SELECT * FROM showepi WHERE showepi.showTitle LIKE ?";
        return jdbcTemplate.query(sql, showRowMapper, "%" + title + "%");
    }
}
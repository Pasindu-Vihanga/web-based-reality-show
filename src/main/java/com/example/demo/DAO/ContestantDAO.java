package com.example.demo.DAO;

import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ContestantDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Contestant> contestantRowMapper = new RowMapper<>() {
        @Override
        public Contestant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Show show = new Show();
            show.setEpisodeId(rs.getString("episode_id"));

            return new Contestant(
                    rs.getString("contestant_id"),
                    rs.getString("name"),
                    rs.getString("bio"),
                    rs.getString("image_url"),
                    rs.getString("status"),
                    show
            );
        }
    };

    /** ========== SAVE NEW CONTESTANT ========== */
    public void save(Contestant contestant) {
        String sql = "INSERT INTO contestant (contestant_id, name, bio, image_url, status, episode_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                contestant.getContestantId(),
                contestant.getName(),
                contestant.getBio(),
                contestant.getImageUrl(),
                contestant.getStatus(),
                contestant.getShow().getEpisodeId()
        );
    }

    /** ========== UPDATE CONTESTANT ========== */
    public int update(Contestant contestant) {
        String sql = "UPDATE contestant SET name = ?, bio = ?, image_url = ?, status = ?, episode_id = ? " +
                "WHERE contestant_id = ?";
        return jdbcTemplate.update(sql,
                contestant.getName(),
                contestant.getBio(),
                contestant.getImageUrl(),
                contestant.getStatus(),
                contestant.getShow().getEpisodeId(),
                contestant.getContestantId()
        );
    }

    /** ========== DELETE CONTESTANT ========== */
    public int delete(String contestantId) {
        String sql = "DELETE FROM contestant WHERE contestant_id = ?";
        return jdbcTemplate.update(sql, contestantId);
    }

    /** ========== FIND ALL CONTESTANTS ========== */
    public List<Contestant> findAll() {
        String sql = "SELECT * FROM contestant";
        return jdbcTemplate.query(sql, contestantRowMapper);
    }

    /** ========== FIND BY EPISODE ========== */
    public List<Contestant> findByEpisodeId(String episodeId) {
        String sql = "SELECT * FROM contestant WHERE episode_id = ?";
        return jdbcTemplate.query(sql, contestantRowMapper, episodeId);
    }

    /** ========== FIND BY STATUS ========== */
    public List<Contestant> findByStatus(String status) {
        String sql = "SELECT * FROM contestant WHERE status = ?";
        return jdbcTemplate.query(sql, contestantRowMapper, status);
    }

    /** ========== FIND BY ID ========== */
    public Optional<Contestant> findById(String contestantId) {
        String sql = "SELECT * FROM contestant WHERE contestant_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, contestantRowMapper, contestantId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

package com.example.demo.DAO;

import com.example.demo.Entity.Vote;
import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class VoteDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Vote> sessionRowMapper = (rs, rowNum) -> {
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

        return new Vote(
                rs.getString("session_id"),
                show,
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getBoolean("active"),
                rs.getInt("max_votes_per_user")
        );
    };

    /** ========== SAVE ========== */
    public void save(Vote session) {
        String sql = "INSERT INTO voting_session (session_id, episode_id, start_time, end_time, active, max_votes_per_user) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                session.getSessionId(),
                session.getShow().getEpisodeId(),
                Timestamp.valueOf(session.getStartTime()),
                Timestamp.valueOf(session.getEndTime()),
                session.isActive(),
                session.getMaxVotesPerUser()
        );
    }

    /** ========== UPDATE ========== */
    public int update(Vote session) {
        String sql = "UPDATE voting_session SET episode_id = ?, start_time = ?, end_time = ?, active = ?, max_votes_per_user = ? " +
                "WHERE session_id = ?";
        return jdbcTemplate.update(sql,
                session.getShow().getEpisodeId(),
                Timestamp.valueOf(session.getStartTime()),
                Timestamp.valueOf(session.getEndTime()),
                session.isActive(),
                session.getMaxVotesPerUser(),
                session.getSessionId()
        );
    }

    /** ========== DELETE ========== */
    public int delete(String sessionId) {
        String sql = "DELETE FROM voting_session WHERE session_id = ?";
        return jdbcTemplate.update(sql, sessionId);
    }

    /** ========== FIND ALL ========== */
    public List<Vote> findAll() {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            """;
        return jdbcTemplate.query(sql, sessionRowMapper);
    }

    /** ========== FIND BY EPISODE ========== */
    public List<Vote> findByEpisodeId(String episodeId) {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE v.episode_id = ?
            """;
        return jdbcTemplate.query(sql, sessionRowMapper, episodeId);
    }

    /** ========== FIND BY ID ========== */
    public Optional<Vote> findById(String sessionId) {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE v.session_id = ?
            """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sessionRowMapper, sessionId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

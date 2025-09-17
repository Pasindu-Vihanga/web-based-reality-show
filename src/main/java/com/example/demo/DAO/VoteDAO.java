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

    private final RowMapper<Vote> sessionRowMapper = new RowMapper<>() {
        @Override
        public Vote mapRow(ResultSet rs, int rowNum) throws SQLException {
            Show show = new Show();
            show.setEpisodeId(rs.getString("episode_id"));

            return new Vote(
                    rs.getString("session_id"),
                    show,
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getTimestamp("end_time").toLocalDateTime(),
                    rs.getBoolean("active"),
                    rs.getInt("max_votes_per_user")
            );
        }
    };

    // Save new voting session
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

    // Update voting session
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

    // Delete session
    public int delete(String sessionId) {
        String sql = "DELETE FROM voting_session WHERE session_id = ?";
        return jdbcTemplate.update(sql, sessionId);
    }

    // Find all sessions
    public List<Vote> findAll() {
        String sql = "SELECT * FROM voting_session";
        return jdbcTemplate.query(sql, sessionRowMapper);
    }

    // Find sessions by episodeId
    public List<Vote> findByEpisodeId(String episodeId) {
        String sql = "SELECT * FROM voting_session WHERE episode_id = ?";
        return jdbcTemplate.query(sql, sessionRowMapper, episodeId);
    }

    // Find by ID
    public Optional<Vote> findById(String sessionId) {
        String sql = "SELECT * FROM voting_session WHERE session_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sessionRowMapper, sessionId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}


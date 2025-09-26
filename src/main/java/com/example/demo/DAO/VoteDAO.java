package com.example.demo.DAO;

import com.example.demo.Entity.Show;
import com.example.demo.Entity.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VoteDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Vote> voteRowMapper = (rs, rowNum) -> {
        // Build Show object
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

        // Build Vote object
        Vote vote = new Vote();
        vote.setSessionId(rs.getString("session_id"));
        vote.setShow(show);
        if (rs.getTimestamp("start_time") != null) {
            vote.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        }
        if (rs.getTimestamp("end_time") != null) {
            vote.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        }
        vote.setActive(rs.getBoolean("active"));
        vote.setMaxVotesPerUser(rs.getInt("max_votes_per_user"));
        vote.setStatus(rs.getString("status"));
        vote.setResults(new ArrayList<>());

        return vote;
    };

    /** Save new voting session */
    public void save(Vote session) {
        String sql = """
            INSERT INTO voting_session 
            (session_id, episode_id, start_time, end_time, active, max_votes_per_user, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(sql,
                session.getSessionId(),
                session.getShow().getEpisodeId(),
                Timestamp.valueOf(session.getStartTime()),
                Timestamp.valueOf(session.getEndTime()),
                session.isActive(),
                session.getMaxVotesPerUser(),
                session.getStatus()
        );
    }

    /** Update existing session */
    public int update(Vote session) {
        String sql = """
            UPDATE voting_session 
            SET episode_id=?, start_time=?, end_time=?, active=?, max_votes_per_user=?, status=?
            WHERE session_id=?
            """;
        return jdbcTemplate.update(sql,
                session.getShow().getEpisodeId(),
                Timestamp.valueOf(session.getStartTime()),
                Timestamp.valueOf(session.getEndTime()),
                session.isActive(),
                session.getMaxVotesPerUser(),
                session.getStatus(),
                session.getSessionId()
        );
    }

    /** Delete session */
    public int delete(String sessionId) {
        String sql = "DELETE FROM voting_session WHERE session_id=?";
        return jdbcTemplate.update(sql, sessionId);
    }

    /** Get all sessions */
    public List<Vote> findAll() {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status AS show_status
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            ORDER BY v.start_time DESC
            """;
        return jdbcTemplate.query(sql, voteRowMapper);
    }

    /** Get sessions by episode */
    public List<Vote> findByEpisodeId(String episodeId) {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status AS show_status
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE v.episode_id=?
            ORDER BY v.start_time DESC
            """;
        return jdbcTemplate.query(sql, voteRowMapper, episodeId);
    }

    /** Find session by ID */
    public Optional<Vote> findById(String sessionId) {
        String sql = """
            SELECT v.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status AS show_status
            FROM voting_session v
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE v.session_id=?
            """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, voteRowMapper, sessionId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** Get last numeric session number (for generator) */
    public int getLastSessionNumber() {
        String sql = "SELECT MAX(session_id) FROM voting_session";
        try {
            String lastId = jdbcTemplate.queryForObject(sql, String.class);
            if (lastId != null && lastId.startsWith("VS")) {
                return Integer.parseInt(lastId.substring(2));
            }
        } catch (Exception e) {
            // ignore if empty
        }
        return 0;
    }
}

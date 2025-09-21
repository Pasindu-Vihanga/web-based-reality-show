package com.example.demo.DAO;

import com.example.demo.Entity.Result;
import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Show;
import com.example.demo.Entity.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ResultDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Result> resultRowMapper = (rs, rowNum) -> {
        Contestant contestant = new Contestant();
        contestant.setContestantId(rs.getString("contestant_id"));
        contestant.setName(rs.getString("contestant_name"));

        Show show = new Show();
        show.setEpisodeId(rs.getString("episode_id"));
        show.setShowTitle(rs.getString("show_title"));

        Vote voteSession = new Vote();
        voteSession.setSessionId(rs.getString("session_id"));
        voteSession.setShow(show);

        return new Result(
                rs.getLong("result_id"),
                voteSession,
                contestant,
                rs.getInt("votes_count"),
                rs.getObject("place") != null ? rs.getInt("place") : null,
                rs.getString("status")
        );
    };

    /** ================== SAVE ================== */
    public void save(Result result) {
        String sql = "INSERT INTO results (session_id, contestant_id, votes_count, place, status) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                result.getVotingSession().getSessionId(),
                result.getContestant().getContestantId(),
                result.getVotesCount(),
                result.getPlace(),
                result.getStatus()
        );
    }

    /** ================== UPDATE ================== */
    public int update(Result result) {
        String sql = "UPDATE results SET session_id=?, contestant_id=?, votes_count=?, place=?, status=? WHERE result_id=?";
        return jdbcTemplate.update(sql,
                result.getVotingSession().getSessionId(),
                result.getContestant().getContestantId(),
                result.getVotesCount(),
                result.getPlace(),
                result.getStatus(),
                result.getResultId()
        );
    }

    /** ================== DELETE ================== */
    public int delete(Long resultId) {
        String sql = "DELETE FROM results WHERE result_id=?";
        return jdbcTemplate.update(sql, resultId);
    }

    /** ================== FIND ALL ================== */
    public List<Result> findAll() {
        String sql = """
            SELECT r.*, c.name AS contestant_name, s.episode_id, s.show_title
            FROM results r
            JOIN contestant c ON r.contestant_id = c.contestant_id
            JOIN voting_session v ON r.session_id = v.session_id
            JOIN showepi s ON v.episode_id = s.episode_id
        """;
        return jdbcTemplate.query(sql, resultRowMapper);
    }

    /** ================== FIND BY SESSION ================== */
    public List<Result> findBySessionId(String sessionId) {
        String sql = """
            SELECT r.*, c.name AS contestant_name, s.episode_id, s.show_title
            FROM results r
            JOIN contestant c ON r.contestant_id = c.contestant_id
            JOIN voting_session v ON r.session_id = v.session_id
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE r.session_id=?
            ORDER BY r.votes_count DESC
        """;
        return jdbcTemplate.query(sql, resultRowMapper, sessionId);
    }

    /** ================== FIND BY ID ================== */
    public Optional<Result> findById(Long resultId) {
        String sql = """
            SELECT r.*, c.name AS contestant_name, s.episode_id, s.show_title
            FROM results r
            JOIN contestant c ON r.contestant_id = c.contestant_id
            JOIN voting_session v ON r.session_id = v.session_id
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE r.result_id=?
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, resultRowMapper, resultId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** ================== INCREMENT VOTE ================== */
    public void incrementVote(String sessionId, String contestantId) {
        String sql = "UPDATE results SET votes_count = votes_count + 1 WHERE session_id=? AND contestant_id=?";
        jdbcTemplate.update(sql, sessionId, contestantId);
    }

    /** ================== EXTRA FEATURES ================== */

    /** Count total votes per session */
    public int countVotesBySession(String sessionId) {
        String sql = "SELECT SUM(votes_count) FROM results WHERE session_id=?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
        return total != null ? total : 0;
    }

    /** Rank contestants by votes */
    public List<Result> getRankings(String sessionId) {
        String sql = """
            SELECT r.*, c.name AS contestant_name, s.episode_id, s.show_title
            FROM results r
            JOIN contestant c ON r.contestant_id = c.contestant_id
            JOIN voting_session v ON r.session_id = v.session_id
            JOIN showepi s ON v.episode_id = s.episode_id
            WHERE r.session_id=?
            ORDER BY r.votes_count DESC
        """;
        return jdbcTemplate.query(sql, resultRowMapper, sessionId);
    }

    /** Validate & clean results */
    public int removeInvalidResults() {
        String sql = "DELETE FROM results WHERE votes_count < 0 OR contestant_id IS NULL";
        return jdbcTemplate.update(sql);
    }
}

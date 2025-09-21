package com.example.demo.DAO;

import com.example.demo.Entity.Result;
import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Show;
import com.example.demo.Entity.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResultDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Result> resultRowMapper = (rs, rowNum) -> {
        // Build Show (episode reference)
        Show show = new Show();
        show.setEpisodeId(rs.getString("episode_id"));
        show.setShowTitle(rs.getString("show_title"));

        // Build Vote (session reference)
        Vote vote = new Vote();
        vote.setSessionId(rs.getString("session_id"));
        vote.setShow(show);

        // Build Contestant
        Contestant contestant = new Contestant();
        contestant.setContestantId(rs.getString("contestant_id"));
        contestant.setName(rs.getString("contestant_name"));

        // Build Result
        return new Result(
                rs.getLong("result_id"),
                vote,
                contestant,
                rs.getInt("votes_count"),
                rs.getInt("place"),
                rs.getString("status")
        );
    };

    /** ================= CRUD ================= */

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

    public int delete(Long resultId) {
        String sql = "DELETE FROM results WHERE result_id=?";
        return jdbcTemplate.update(sql, resultId);
    }

    /** ================= QUERIES ================= */

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

    /** ================= AGGREGATES & VALIDATION ================= */

    /** ✅ Count total votes in a session */
    public int countVotesBySession(String sessionId) {
        String sql = "SELECT COALESCE(SUM(votes_count), 0) FROM results WHERE session_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
    }

    /** ✅ Get contestant rankings by votes */
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

    /** ✅ Remove invalid/dirty results (auto-cleanup) */
    public int removeInvalidResults() {
        String sql = "DELETE FROM results WHERE votes_count < 0 OR contestant_id IS NULL OR session_id IS NULL";
        return jdbcTemplate.update(sql);
    }
}

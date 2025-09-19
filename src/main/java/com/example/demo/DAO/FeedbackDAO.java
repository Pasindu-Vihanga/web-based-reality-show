package com.example.demo.DAO;

import com.example.demo.Entity.Feedback;
import com.example.demo.Entity.User;
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
public class FeedbackDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Feedback> feedbackRowMapper = new RowMapper<>() {
        @Override
        public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));

            return new Feedback(
                    rs.getLong("feedback_id"),
                    user,
                    rs.getString("message"),
                    rs.getInt("rating"),
                    rs.getTimestamp("submitted_at").toLocalDateTime()
            );
        }
    };

    /** ================== SAVE ================== */
    public void save(Feedback feedback) {
        String sql = "INSERT INTO feedback (user_id, message, rating, submitted_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                feedback.getUser().getUserId(),
                feedback.getMessage(),
                feedback.getRating(),
                Timestamp.valueOf(feedback.getSubmittedAt())
        );
    }

    /** ================== UPDATE ================== */
    public int update(Feedback feedback) {
        String sql = "UPDATE feedback SET message=?, rating=?, submitted_at=? WHERE feedback_id=?";
        return jdbcTemplate.update(sql,
                feedback.getMessage(),
                feedback.getRating(),
                Timestamp.valueOf(feedback.getSubmittedAt()),
                feedback.getFeedbackId()
        );
    }

    /** ================== DELETE ================== */
    public int delete(Long feedbackId) {
        String sql = "DELETE FROM feedback WHERE feedback_id=?";
        return jdbcTemplate.update(sql, feedbackId);
    }

    /** ================== FIND ALL ================== */
    public List<Feedback> findAll() {
        String sql = """
            SELECT f.*, u.username, u.email
            FROM feedback f
            JOIN users u ON f.user_id = u.user_id
            ORDER BY f.submitted_at DESC
        """;
        return jdbcTemplate.query(sql, feedbackRowMapper);
    }

    /** ================== FIND BY USER ================== */
    public List<Feedback> findByUserId(String userId) {
        String sql = """
            SELECT f.*, u.username, u.email
            FROM feedback f
            JOIN users u ON f.user_id = u.user_id
            WHERE f.user_id=?
            ORDER BY f.submitted_at DESC
        """;
        return jdbcTemplate.query(sql, feedbackRowMapper, userId);
    }

    /** ================== FIND BY ID ================== */
    public Optional<Feedback> findById(Long feedbackId) {
        String sql = """
            SELECT f.*, u.username, u.email
            FROM feedback f
            JOIN users u ON f.user_id = u.user_id
            WHERE f.feedback_id=?
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, feedbackRowMapper, feedbackId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

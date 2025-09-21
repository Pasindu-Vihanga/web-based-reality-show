package com.example.demo.DAO;

import com.example.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getBytes("photo"), // ✅ photo as byte[]
                    rs.getString("address"),
                    rs.getString("phone_number"),
                    rs.getString("email")
            );
        }
    };

    // ✅ Generate new user_id like USR000001
    private String generateUserId() {
        String sql = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        try {
            String lastId = jdbcTemplate.queryForObject(sql, String.class);
            if (lastId != null && lastId.startsWith("USR")) {
                int num = Integer.parseInt(lastId.substring(3));
                return String.format("USR%06d", num + 1);
            }
        } catch (DataAccessException e) {
            // No users yet → start with USR000001
        }
        return "USR000001";
    }

    public int save(User user) {
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateUserId());
        }

        String sql = "INSERT INTO users (user_id, username, password, photo, address, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getPhoto(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }

    public int update(User user) {
        String sql = "UPDATE users SET username=?, password=?, photo=?, address=?, phone_number=?, email=? " +
                "WHERE user_id=?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getPhoto(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getUserId()
        );
    }

    public int delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        return jdbcTemplate.update(sql, userId);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, userId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, email));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username, password));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /* ✅ New method: Search users by keyword */
    public List<User> searchUsers(String keyword) {
        String sql = "SELECT * FROM users " +
                "WHERE username LIKE ? OR email LIKE ? OR phone_number LIKE ?";
        String like = "%" + keyword + "%";
        return jdbcTemplate.query(sql, userRowMapper, like, like, like);
    }
}

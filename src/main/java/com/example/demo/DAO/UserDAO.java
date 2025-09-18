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

    // RowMapper for User entity
    private final RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("image_path"),
                    rs.getString("address"),
                    rs.getString("phone_number"),
                    rs.getString("email")
            );
        }
    };

    /** ================== INSERT ================== */
    public int save(User user) {
        String sql = "INSERT INTO users (user_id, username, password, image_path, address, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getImagePath(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }

    /** ================== UPDATE ================== */
    public int update(User user) {
        String sql = "UPDATE users SET username=?, password=?, image_path=?, address=?, phone_number=?, email=? " +
                "WHERE user_id=?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getImagePath(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getUserId()
        );
    }

    /** ================== DELETE ================== */
    public int delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        return jdbcTemplate.update(sql, userId);
    }

    /** ================== FIND ALL ================== */
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /** ================== FIND BY ID ================== */
    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, userId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /** ================== FIND BY USERNAME ================== */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /** ================== FIND BY EMAIL ================== */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, email));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /** ================== LOGIN CHECK (USERNAME + PASSWORD) ================== */
    public Optional<User> login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username, password));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}

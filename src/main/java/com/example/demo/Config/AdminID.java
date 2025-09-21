package com.example.demo.Config;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class AdminID {

    private final JdbcTemplate jdbcTemplate;

    public AdminID(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String PREFIX = "ADM";
    private static final int LENGTH = 6;

    public String generateAdminId() {
        String sql = "SELECT adminid FROM admin ORDER BY adminid DESC LIMIT 1";
        try {
            String lastId = jdbcTemplate.queryForObject(sql, String.class);
            if (lastId != null && lastId.startsWith(PREFIX)) {
                int num = Integer.parseInt(lastId.substring(PREFIX.length()));
                return PREFIX + String.format("%0" + LENGTH + "d", num + 1);
            }
        } catch (DataAccessException e) {
            // No admins yet → start from ADM000001
        }
        return PREFIX + String.format("%0" + LENGTH + "d", 1);
    }
}


package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminID {

    private static final String PREFIX = "ADM";
    private static final int LENGTH = 4;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminID(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateAdminId() {
        String sql = "SELECT adminid FROM admin ORDER BY adminid DESC LIMIT 1";
        try {
            String lastId = jdbcTemplate.queryForObject(sql, String.class);
            if (lastId != null && lastId.startsWith(PREFIX)) {
                int num = Integer.parseInt(lastId.substring(PREFIX.length()));
                return PREFIX + String.format("%0" + LENGTH + "d", num + 1);
            }
        } catch (DataAccessException e) {
            // No rows → first admin
        }
        return PREFIX + String.format("%0" + LENGTH + "d", 1);
    }
}

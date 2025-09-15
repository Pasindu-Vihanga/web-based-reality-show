package com.example.demo.DAO;

import com.example.demo.Entity.Admin;
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
public class AdminDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for Admin entity
    private final RowMapper<Admin> adminRowMapper = new RowMapper<>() {
        @Override
        public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Admin(
                    rs.getString("admin_id"),
                    rs.getString("admin_name"),
                    rs.getString("admin_password"),
                    rs.getString("role_name"),
                    rs.getString("role_id"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("address")
            );
        }
    };

    // Insert Admin
    public int save(Admin admin) {
        String sql = "INSERT INTO admin (admin_id, admin_name, admin_password, role_name, role_id, mobile_number, email, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                admin.getAdminID(),
                admin.getAdminName(),
                admin.getAdminPassword(),
                admin.getRoleName(),
                admin.getRoleID(),
                admin.getMobileNumber(),
                admin.getEmail(),
                admin.getAddress());
    }

    // Update Admin
    public int update(Admin admin) {
        String sql = "UPDATE admin SET admin_name=?, admin_password=?, role_name=?, role_id=?, mobile_number=?, email=?, address=? WHERE admin_id=?";
        return jdbcTemplate.update(sql,
                admin.getAdminName(),
                admin.getAdminPassword(),
                admin.getRoleName(),
                admin.getRoleID(),
                admin.getMobileNumber(),
                admin.getEmail(),
                admin.getAddress(),
                admin.getAdminID());
    }

    // Delete Admin by ID
    public int delete(String adminID) {
        String sql = "DELETE FROM admin WHERE admin_id=?";
        return jdbcTemplate.update(sql, adminID);
    }

    // Get all Admins
    public List<Admin> findAll() {
        String sql = "SELECT * FROM admin";
        return jdbcTemplate.query(sql, adminRowMapper);
    }

    // Find Admin by ID
    public Optional<Admin> findById(String adminID) {
        String sql = "SELECT * FROM admin WHERE admin_id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, adminRowMapper, adminID));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    // Find Admin by Name
    public List<Admin> findByName(String name) {
        String sql = "SELECT * FROM admin WHERE admin.admin_name LIKE ?";
        return jdbcTemplate.query(sql, adminRowMapper, "%" + name + "%");
    }
}

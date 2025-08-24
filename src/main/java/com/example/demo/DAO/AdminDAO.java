package com.example.demo.DAO;

import com.example.demo.Entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
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

    public void saveAdmin(Admin admin) {
        String sql = "INSERT INTO admin (admin_id, admin_name, admin_password, role_name, role_id, mobile_number, email, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                admin.getAdminID(),
                admin.getAdminName(),
                admin.getAdminPassword(),
                admin.getRoleName(),
                admin.getRoleID(),
                admin.getMobileNumber(),
                admin.getEmail(),
                admin.getAddress()
        );
    }

    public List<Admin> getAllAdmins() {
        String sql = "SELECT * FROM admin";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Admin getAdminByID(String adminID) {
        String sql = "SELECT * FROM admin WHERE admin_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRow, adminID);
    }

    public void updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET admin_name = ?, admin_password = ?, role_name = ?, role_id = ?, mobile_number = ?, email = ?, address = ? WHERE admin_id = ?";
        jdbcTemplate.update(sql,
                admin.getAdminName(),
                admin.getAdminPassword(),
                admin.getRoleName(),
                admin.getRoleID(),
                admin.getMobileNumber(),
                admin.getEmail(),
                admin.getAddress(),
                admin.getAdminID()
        );
    }

    public void deleteAdmin(String adminID) {
        String sql = "DELETE FROM admin WHERE admin_id = ?";
        jdbcTemplate.update(sql, adminID);
    }
}

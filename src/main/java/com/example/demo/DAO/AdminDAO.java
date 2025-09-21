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

    // ✅ RowMapper to convert DB row → Admin object
    private final RowMapper<Admin> adminRowMapper = new RowMapper<>() {
        @Override
        public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
            Admin admin = new Admin();
            admin.setAdminID(rs.getString("adminid"));
            admin.setAdminName(rs.getString("admin_name"));
            admin.setAdminPassword(rs.getString("admin_password"));
            admin.setRoleName(rs.getString("role_name"));
            admin.setRoleID(rs.getString("roleid"));
            admin.setMobileNumber(rs.getString("mobile_number"));
            admin.setEmail(rs.getString("email"));
            admin.setAddress(rs.getString("address"));
            return admin;
        }
    };

    /** ================== INSERT ================== */
    public int save(Admin admin) {
        String sql = "INSERT INTO admin (adminid, admin_name, admin_password, role_name, roleid, mobile_number, email, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                admin.getAdminID(),   // ⚠️ must be set in Service before save
                admin.getAdminName(),
                admin.getAdminPassword(),
                admin.getRoleName(),
                admin.getRoleID(),
                admin.getMobileNumber(),
                admin.getEmail(),
                admin.getAddress()
        );
    }

    /** ================== UPDATE ================== */
    public int update(Admin admin) {
        String sql = "UPDATE admin SET admin_name=?, admin_password=?, role_name=?, roleid=?, mobile_number=?, email=?, address=? " +
                "WHERE adminid=?";
        return jdbcTemplate.update(sql,
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

    /** ================== DELETE ================== */
    public int delete(String adminId) {
        String sql = "DELETE FROM admin WHERE adminid=?";
        return jdbcTemplate.update(sql, adminId);
    }

    /** ================== FIND ALL ================== */
    public List<Admin> findAll() {
        String sql = "SELECT * FROM admin";
        return jdbcTemplate.query(sql, adminRowMapper);
    }

    /** ================== FIND BY ID ================== */
    public Optional<Admin> findById(String adminId) {
        String sql = "SELECT * FROM admin WHERE adminid=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, adminRowMapper, adminId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /** ================== FIND BY NAME ================== */
    public Optional<Admin> findByName(String adminName) {
        String sql = "SELECT * FROM admin WHERE admin_name=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, adminRowMapper, adminName));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}

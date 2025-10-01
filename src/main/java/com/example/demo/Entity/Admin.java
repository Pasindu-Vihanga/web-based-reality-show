package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "adminid", nullable = false, unique = true, length = 9)
    private String adminID;   // e.g., ADM000001 (auto-generated)

    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    @Column(name = "admin_password", nullable = false, length = 100)
    private String adminPassword;  // ✅ store hashed password (BCrypt ~60 chars)

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(name = "roleid", nullable = false, unique = true, length = 9)
    private String roleID;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "address", nullable = false, length = 100)
    private String address;
}

package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "adminid", nullable = false, unique = true, length = 9)
    private String adminID;

    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    @Column(name = "admin_password", nullable = false, unique = true, length = 12)
    private String adminPassword;

    @Column(name = "role_name", nullable = false, length = 10)
    private String roleName;

    @Column(name = "roleid", nullable = false, unique = true, length = 9)
    private String roleID;

    @Column(name = "mobile_number", nullable = false, length = 11)
    private String mobileNumber;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

}

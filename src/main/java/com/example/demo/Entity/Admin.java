package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    private String adminID;

    private String adminName;
    private String adminPassword;
    private String roleName;
    private String roleID;
    private String mobileNumber;
    private String email;
    private String address;

    public Admin(String adminID, String adminName, String adminPassword, String roleName, String roleID, String mobileNumber, String email, String address) {
        this.adminID = adminID;
        this.adminName = adminName;
        this.adminPassword = adminPassword;
        this.roleName = roleName;
        this.roleID = roleID;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
    }
}

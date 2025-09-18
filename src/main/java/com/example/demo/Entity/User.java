package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 9)
    private String userId;  // e.g. WBSU0001

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // ✅ Store image path or URL instead of raw image data
    @Column(name ="image_path", length = 255)
    private String imagePath;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name="phone_number", length = 11, unique = true)
    private String phoneNumber;

    @Column(name= "email", nullable = false, unique = true)
    private String email;
}

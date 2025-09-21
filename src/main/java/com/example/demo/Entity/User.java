package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 12)
    @GeneratedValue(generator = "user-id-generator")
    @GenericGenerator(
            name = "user-id-generator",
            strategy = "com.example.demo.Config.UserID"
    )
    private String userId;   // auto-generated like USR000001

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // ✅ Store image as BLOB
    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name="phone_number", length = 11, unique = true)
    private String phoneNumber;

    @Column(name= "email", nullable = false, unique = true)
    private String email;
}

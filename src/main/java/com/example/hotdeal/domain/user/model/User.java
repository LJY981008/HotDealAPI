package com.example.hotdeal.domain.user.model;

import com.example.hotdeal.global.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password;
    private UserRole role;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = UserRole.USER;
    }

    public User() {}
}

package com.example.hotdeal.domain.user.auth.model;

import com.example.hotdeal.global.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "auth")
public class Auth {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password;
    private UserRole role;

    public Auth(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = UserRole.USER;
    }

    public Auth() {}
}

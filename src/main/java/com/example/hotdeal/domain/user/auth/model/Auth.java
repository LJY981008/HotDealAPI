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
    private UserRole role;

    public Auth(String email) {
        this.email = email;
        this.role = UserRole.USER;
    }

    public Auth() {}
}

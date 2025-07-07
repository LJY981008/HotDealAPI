package com.example.hotdeal.domain.user.auth.domain;

import com.example.hotdeal.global.enums.UserRole;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "auth")
public class Auth extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auth_id;

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

package com.example.hotdeal.domain.user.profile.domain;

import com.example.hotdeal.global.enums.UserRole;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

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

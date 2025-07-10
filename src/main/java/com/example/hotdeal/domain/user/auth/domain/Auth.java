package com.example.hotdeal.domain.user.auth.domain;

import com.example.hotdeal.global.enums.UserRole;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auth")
public class Auth extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password;
    private UserRole role;

    private Auth(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = UserRole.USER;
    }

    public static Auth createAuth(String email, String name, String password){
        return new Auth(email, name, password);
    }

    //어드민 등록을 위한 메서드
    public void changeRoleByAdmin() {
        this.role = UserRole.ADMIN;
    }
}
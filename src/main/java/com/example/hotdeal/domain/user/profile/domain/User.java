package com.example.hotdeal.domain.user.profile.domain;

import java.time.LocalDateTime;

import com.example.hotdeal.global.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

//물리적 삭제 Auth 복구시 이벤트로 재생성
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    //authId를 저장
    @Id
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private UserRole role;
    private LocalDateTime createdAt;

    public User(String email, String name, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.role = userRole;
    }

}
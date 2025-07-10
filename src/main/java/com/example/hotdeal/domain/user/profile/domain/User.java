package com.example.hotdeal.domain.user.profile.domain;

import com.example.hotdeal.global.enums.UserRole;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    //authId를 저장할거임
    @Id
    private Long user_id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private UserRole role;

    public User(String email, String name, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.role = userRole;
    }

}
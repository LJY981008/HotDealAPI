package com.example.hotdeal.domain.user.auth.domain;

import lombok.Getter;

@Getter
public class UserResponse {
    private final String token;

    public UserResponse(String token) {
        this.token = token;
    }
}

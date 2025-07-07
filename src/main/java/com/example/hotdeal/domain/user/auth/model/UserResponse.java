package com.example.hotdeal.domain.user.auth.model;

import lombok.Getter;

@Getter
public class UserResponse {
    private String token;

    public UserResponse(String token) {
        this.token = token;
    }
}

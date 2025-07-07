package com.example.hotdeal.domain.user.dto;

import com.example.hotdeal.domain.user.model.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private String token;
    private String name;

    public UserResponse(String token, String name) {
        this.token = token;
        this.name = name;
    }
}

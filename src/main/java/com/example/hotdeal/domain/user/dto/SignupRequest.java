package com.example.hotdeal.domain.user.dto;

import com.example.hotdeal.domain.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email @NotBlank
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public User toUser() {
        return new User(this.email,this.username,this.password);
    }
}

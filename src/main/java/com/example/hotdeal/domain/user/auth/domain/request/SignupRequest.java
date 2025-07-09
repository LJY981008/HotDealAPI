package com.example.hotdeal.domain.user.auth.domain.request;

import com.example.hotdeal.domain.user.auth.domain.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;

    public Auth toAuth() {
        return new Auth(this.email, this.name, this.password);
    }
}

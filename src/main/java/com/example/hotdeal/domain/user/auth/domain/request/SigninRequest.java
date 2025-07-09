package com.example.hotdeal.domain.user.auth.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
}

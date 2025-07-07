package com.example.hotdeal.domain.user.auth.model;

import com.example.hotdeal.domain.user.profile.model.User;
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

    public Auth toAuth() {
        return new Auth(this.email);
    }
}

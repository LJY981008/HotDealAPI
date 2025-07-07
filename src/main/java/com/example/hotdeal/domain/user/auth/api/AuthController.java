package com.example.hotdeal.domain.user.auth.api;

import com.example.hotdeal.domain.user.auth.application.AuthService;
import com.example.hotdeal.domain.user.auth.model.SigninRequest;
import com.example.hotdeal.domain.user.auth.model.SignupRequest;
import com.example.hotdeal.domain.user.auth.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @RequestBody SignupRequest signupRequest
    ) {
        UserResponse signup = authService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(signup);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody SigninRequest signinRequest
    ) {
        UserResponse signin = authService.signin(signinRequest);
        return ResponseEntity.status(HttpStatus.OK).body(signin);
    }
}

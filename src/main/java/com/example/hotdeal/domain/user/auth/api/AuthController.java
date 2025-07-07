package com.example.hotdeal.domain.user.auth.api;

import com.example.hotdeal.domain.user.auth.application.AuthService;
import com.example.hotdeal.domain.user.auth.domain.SigninRequest;
import com.example.hotdeal.domain.user.auth.domain.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
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

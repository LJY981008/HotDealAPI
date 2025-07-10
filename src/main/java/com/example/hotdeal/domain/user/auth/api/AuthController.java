package com.example.hotdeal.domain.user.auth.api;

import com.example.hotdeal.domain.user.auth.application.AuthService;
import com.example.hotdeal.domain.user.auth.domain.request.LogoutRequest;
import com.example.hotdeal.domain.user.auth.domain.response.AccessTokenResponse;
import com.example.hotdeal.domain.user.auth.domain.request.ReissueRequest;
import com.example.hotdeal.domain.user.auth.domain.request.SigninRequest;
import com.example.hotdeal.domain.user.auth.domain.request.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.response.TokenResponse;
import com.example.hotdeal.domain.user.auth.domain.response.CreateUserResponse;
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
    public ResponseEntity<CreateUserResponse> signup(
            @RequestBody SignupRequest signupRequest
    ) {
        CreateUserResponse response = authService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody SigninRequest signinRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signin(signinRequest));
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
        @RequestBody ReissueRequest request
    ) {
        AccessTokenResponse accessToken = authService.reissueAccessToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestBody LogoutRequest request
    ) {
        authService.registerTokenBlacklist(request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

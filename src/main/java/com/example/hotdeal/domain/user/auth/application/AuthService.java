package com.example.hotdeal.domain.user.auth.application;

import com.example.hotdeal.domain.user.auth.domain.Auth;
import com.example.hotdeal.domain.user.auth.security.JwtUtil;
import com.example.hotdeal.domain.user.auth.infra.AuthRepository;
import com.example.hotdeal.domain.user.auth.domain.SigninRequest;
import com.example.hotdeal.domain.user.auth.domain.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.UserResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {
        Auth user = signupRequest.toAuth();
        Auth savedUser = authRepository.save(user);
        String token = jwtUtil.createToken(savedUser.getAuth_id(), savedUser.getEmail(), savedUser.getRole());
        return new UserResponse(token);
    }

    @Transactional
    public UserResponse signin(SigninRequest signinRequest) {
        Auth user = authRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
        String token = jwtUtil.createToken(user.getAuth_id(), user.getEmail(), user.getRole());
        return new UserResponse(token);
    }
}

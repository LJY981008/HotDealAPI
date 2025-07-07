package com.example.hotdeal.domain.user.service;

import com.example.hotdeal.domain.user.dto.SigninRequest;
import com.example.hotdeal.domain.user.dto.SignupRequest;
import com.example.hotdeal.domain.user.dto.UserResponse;
import com.example.hotdeal.domain.user.model.User;
import com.example.hotdeal.domain.user.repository.UserRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import com.example.hotdeal.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResponse signup(SignupRequest signupRequest) {
        User user = signupRequest.toUser();
        User savedUser = userRepository.save(user);
        String token = jwtUtil.createToken(savedUser.getUserId(), savedUser.getEmail(), savedUser.getRole());
        return new UserResponse(token, savedUser.getName());
    }

    public UserResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
        String token = jwtUtil.createToken(user.getUserId(), user.getEmail(), user.getRole());
        return new UserResponse(token, user.getName());
    }
}

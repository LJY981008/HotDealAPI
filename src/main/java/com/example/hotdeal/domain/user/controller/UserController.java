package com.example.hotdeal.domain.user.controller;

import com.example.hotdeal.domain.user.dto.SigninRequest;
import com.example.hotdeal.domain.user.dto.SignupRequest;
import com.example.hotdeal.domain.user.dto.UserResponse;
import com.example.hotdeal.domain.user.service.UserService;
import com.example.hotdeal.global.constant.Const;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @RequestBody SignupRequest signupRequest
    ) {
        UserResponse signup = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(signup);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody SigninRequest signinRequest
    ) {
        UserResponse signin = userService.signin(signinRequest);
        return ResponseEntity.status(HttpStatus.OK).body(signin);
    }

}

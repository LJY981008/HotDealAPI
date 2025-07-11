package com.example.hotdeal.domain.user.profile.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import com.example.hotdeal.domain.user.profile.application.UserService;
import com.example.hotdeal.domain.user.profile.domain.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<UserResponse> getProfile(
		@AuthenticationPrincipal AuthUserDto authUser
	) {
		Long userId = authUser.getId();
		UserResponse userInfo = userService.findUserInfo(userId);
		return ResponseEntity.status(HttpStatus.OK).body(userInfo);
	}

}

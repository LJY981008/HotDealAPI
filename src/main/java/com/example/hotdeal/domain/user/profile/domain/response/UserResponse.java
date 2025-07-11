package com.example.hotdeal.domain.user.profile.domain.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserResponse {
	private final String email;
	private final String name;
	private final LocalDateTime createdAt;
}

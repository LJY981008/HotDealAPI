package com.example.hotdeal.domain.user.auth.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {
	@NotBlank(message = "AccessToken 을 입력해주세요.")
	private String accessToken;
}

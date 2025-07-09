package com.example.hotdeal.domain.user.auth.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class TokenResponse {
	private String accessToken;
	private String refreshToken;
}

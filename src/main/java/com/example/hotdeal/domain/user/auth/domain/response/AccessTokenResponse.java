package com.example.hotdeal.domain.user.auth.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//엑세스 토큰 재발급용 DTO
@Getter
@AllArgsConstructor(staticName = "of")
public class AccessTokenResponse {

	private final String accessToken;

}
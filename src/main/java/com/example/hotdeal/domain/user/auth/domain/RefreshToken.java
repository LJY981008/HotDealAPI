package com.example.hotdeal.domain.user.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24) //1일 세팅
public class RefreshToken {

	//redis 에 저장되기 때문에 JPA 의존성 X, 유저당 하나의 리프레시 토큰을 가진다. -> 만료시 자동삭제
	@Id
	private Long userId;
	private String refreshToken;

}
package com.example.hotdeal.domain.user.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "blacklistToken", timeToLive = 60 * 30) //엑세스토큰의 최대 시간이 30분임
public class TokenBlacklist {

	@Id
	private String token;

}
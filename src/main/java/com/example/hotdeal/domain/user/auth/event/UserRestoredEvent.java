package com.example.hotdeal.domain.user.auth.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserRestoredEvent {
	//authId 와 같은 값 저장
	private final Long userId;
	private final String email;
	private final String name;
	private final LocalDateTime createdAt;
}

package com.example.hotdeal.domain.user.profile.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//물리적 삭제 Auth 복구시 이벤트로 재생성
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

	//authId를 저장
	@Id
	private Long userId;

	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private LocalDateTime createdAt;

	public static User fromUserEvent(Long userId, String email, String name, LocalDateTime createdAt) {
		return new User(userId, email, name, createdAt);
	}

}
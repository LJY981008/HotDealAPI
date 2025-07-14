package com.example.hotdeal.domain.user.profile.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotdeal.domain.user.profile.domain.User;
import com.example.hotdeal.domain.user.profile.domain.response.UserResponse;
import com.example.hotdeal.domain.user.profile.infra.UserRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기 전용 서비스
public class UserService {

	private final UserRepository userRepository;

	public UserResponse findUserInfo(Long userId) {
		User foundUser = userRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));

		return UserResponse.of(foundUser.getEmail(), foundUser.getName(), foundUser.getCreatedAt());
	}

}
package com.example.hotdeal.domain.user.profile.application;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.example.hotdeal.domain.user.auth.event.UserRegisteredEvent;
import com.example.hotdeal.domain.user.auth.event.UserRestoredEvent;
import com.example.hotdeal.domain.user.auth.event.UserWithdrawnEvent;
import com.example.hotdeal.domain.user.profile.domain.User;
import com.example.hotdeal.domain.user.profile.infra.UserRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventListener {

	private final UserRepository userRepository;

	@EventListener
	public void handlerUserRegisteredEvent(UserRegisteredEvent event) {
		User user = User.fromUserEvent(event.getUserId(), event.getEmail(), event.getName(), event.getCreatedAt());
		userRepository.save(user);
	}

	@EventListener
	public void handlerUserWithdrawnEvent(UserWithdrawnEvent event) {
		User foundUser = userRepository.findByUserId(event.getUserId())
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));

		userRepository.delete(foundUser);
	}

	@EventListener
	public void handlerUserRestoredEvent(UserRestoredEvent event) {
		User user = User.fromUserEvent(event.getUserId(), event.getEmail(), event.getName(), event.getCreatedAt());
		userRepository.save(user);
	}
}

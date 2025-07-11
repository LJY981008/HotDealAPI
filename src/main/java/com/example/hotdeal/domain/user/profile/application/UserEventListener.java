package com.example.hotdeal.domain.user.profile.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.hotdeal.domain.user.auth.event.UserRegisteredEvent;
import com.example.hotdeal.domain.user.auth.event.UserRestoredEvent;
import com.example.hotdeal.domain.user.auth.event.UserWithdrawnEvent;
import com.example.hotdeal.domain.user.profile.domain.User;
import com.example.hotdeal.domain.user.profile.event.UserDeleteFailedEvent;
import com.example.hotdeal.domain.user.profile.event.UserRestoreFailedEvent;
import com.example.hotdeal.domain.user.profile.event.UserSaveFailedEvent;
import com.example.hotdeal.domain.user.profile.infra.UserRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlerUserRegisteredEvent(UserRegisteredEvent event) {
		try {
			User user = User.fromUserEvent(event.getUserId(), event.getEmail(), event.getName(), event.getCreatedAt());
			userRepository.save(user);
		} catch (Exception e) {
			log.info("프로필 생성 실패: userId={}, error={}", event.getUserId(), e.getMessage());
			eventPublisher.publishEvent(UserSaveFailedEvent.of(event.getUserId()));
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlerUserWithdrawnEvent(UserWithdrawnEvent event) {
		try {
			userRepository.deleteByUserId(event.getUserId());
		} catch (Exception e) {
			log.info("프로필 삭제 실패: userId={}, error={}", event.getUserId(), e.getMessage());
			eventPublisher.publishEvent(UserDeleteFailedEvent.of(event.getUserId()));
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlerUserRestoredEvent(UserRestoredEvent event) {
		try {
			User user = User.fromUserEvent(event.getUserId(), event.getEmail(), event.getName(), event.getCreatedAt());
			userRepository.save(user);
		} catch (Exception e) {
			log.info("프로필 생성 실패: userId={}, error={}", event.getUserId(), e.getMessage());
			eventPublisher.publishEvent(UserRestoreFailedEvent.of(event.getUserId()));
		}
	}

}

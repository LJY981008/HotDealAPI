package com.example.hotdeal.domain.user.auth.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.example.hotdeal.domain.user.auth.domain.Auth;
import com.example.hotdeal.domain.user.auth.infra.AuthRepository;
import com.example.hotdeal.domain.user.profile.event.UserSaveFailedEvent;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandlingFailedEventListener {
	private final AuthRepository authRepository;

	@EventListener
	public void handlerUserSaveFailedEvent(UserSaveFailedEvent event) {
		try {
			authRepository.deleteByAuthId(event.getAuthId());
		} catch (Exception e) {
			log.error("Auth 롤백 실패 : authId={}", event.getAuthId());
		}
	}

	@EventListener
	public void handlerUserDeleteFailedEvent(UserSaveFailedEvent event) {
		try {
			//User 삭제에 실패했으니 Auth 복구
			authRepository.restoredByAuthId(event.getAuthId());
		} catch (Exception e) {
			log.error("Auth 롤백 실패 : authId={}", event.getAuthId());
		}
	}

	@EventListener
	public void handlerUserRestoreFailedEvent(UserSaveFailedEvent event) {
		try {
			Auth foundAuth = authRepository.findByAuthIdAndDeletedTrue(event.getAuthId())
				.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
			foundAuth.softDelete();
		} catch (Exception e) {
			log.error("Auth 롤백 실패 : authId={}", event.getAuthId());
		}
	}

}
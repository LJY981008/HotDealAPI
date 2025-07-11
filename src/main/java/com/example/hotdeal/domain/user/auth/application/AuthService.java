package com.example.hotdeal.domain.user.auth.application;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.hotdeal.domain.user.auth.domain.TokenBlacklist;
import com.example.hotdeal.domain.user.auth.domain.request.PasswordRequest;
import com.example.hotdeal.domain.user.auth.domain.response.AccessTokenResponse;
import com.example.hotdeal.domain.user.auth.domain.Auth;
import com.example.hotdeal.domain.user.auth.domain.request.ReissueRequest;
import com.example.hotdeal.domain.user.auth.domain.response.TokenResponse;
import com.example.hotdeal.domain.user.auth.event.UserRegisteredEvent;
import com.example.hotdeal.domain.user.auth.event.UserRestoredEvent;
import com.example.hotdeal.domain.user.auth.event.UserWithdrawnEvent;
import com.example.hotdeal.domain.user.auth.infra.RefreshTokenRepository;
import com.example.hotdeal.domain.user.auth.infra.TokenBlacklistRepository;
import com.example.hotdeal.domain.user.auth.security.JwtUtil;
import com.example.hotdeal.domain.user.auth.infra.AuthRepository;
import com.example.hotdeal.domain.user.auth.domain.request.LoginRequest;
import com.example.hotdeal.domain.user.auth.domain.request.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.response.CreateUserResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final AuthRepository authRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final TokenBlacklistRepository tokenBlacklistRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;

	public CreateUserResponse signup(SignupRequest request) {
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		Auth auth = Auth.createAuth(request.getEmail(), request.getName(), encodedPassword);
		Auth savedAuth = authRepository.save(auth);

		//회원가입 이벤트 발행
		eventPublisher.publishEvent(
			UserRegisteredEvent.of(savedAuth.getAuthId(), savedAuth.getEmail(),
			savedAuth.getName(), savedAuth.getCreatedAt())
		);

		return CreateUserResponse.of(savedAuth.getAuthId(), savedAuth.getEmail(), savedAuth.getName());
	}

	public TokenResponse loginAndIssueToken(LoginRequest request) {
		Auth auth = getAuthOrThrow(authRepository.findByEmailAndDeletedFalse(request.getEmail()));
		return jwtUtil.createTokens(auth.getAuthId(), auth.getEmail(), auth.getRole());
	}

	public AccessTokenResponse reissueAccessToken(ReissueRequest request) {
		String refreshToken = request.getRefreshToken();

		//토큰 유효성 검사
		jwtUtil.extractClaims(refreshToken);

		//토큰에서 ID값 추출
		Long authId = jwtUtil.getUserId(refreshToken);

		//authId 값을 통해 저장된 리프레시토큰 찾아옴
		String foundToken = refreshTokenRepository.findById(authId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_TOKEN))
			.getRefreshToken();

		//request 와 일치하지 않을경우 예외 throw
		if (!refreshToken.equals(foundToken)) {
			throw new CustomException(CustomErrorCode.TOKEN_MISMATCH);
		}

		//엑세스 토큰 재발급을 위한 준비
		Auth foundAuth = getAuthOrThrow(authRepository.findByAuthIdAndDeletedFalse(authId));

		return AccessTokenResponse.of(jwtUtil.createAccessToken(foundAuth.getAuthId(), foundAuth.getEmail(), foundAuth.getRole()));
	}

	public void registerTokenBlacklist(String accessToken, PasswordRequest request) {
		//엑세스토큰 에서 ID값 추출
		Long authId = jwtUtil.getUserId(accessToken);

		//해당 ID 값의 유저
		Auth foundAuth = getAuthOrThrow(authRepository.findByAuthIdAndDeletedFalse(authId));

		//비밀번호가 요청과 일치 하지않을경우 예외 throw
		if(!passwordEncoder.matches(request.getPassword(), foundAuth.getPassword())) {
			throw new CustomException(CustomErrorCode.PASSWORD_MISMATCH);
		}

		//리프레시 토큰 삭제 -> 삭제가 안될시 만료된(이미삭제된)거라 상관없음
		if (refreshTokenRepository.existsById(authId)) {
			refreshTokenRepository.deleteById(authId);
		}

		//엑세스 토큰의 만료시간
		LocalDateTime accessTokenExpiredAt = jwtUtil.getExpiredAt(accessToken);

		//엑세스 토큰의 만료시간이 남았을경우
		if (!accessTokenExpiredAt.isBefore(LocalDateTime.now())) {
			tokenBlacklistRepository.save(new TokenBlacklist(accessToken));
		}
	}

	//self-invocation 피하기 위해 중복코드라도 작성
	public void deactivateUser(String accessToken, PasswordRequest request) {
		Long authId = jwtUtil.getUserId(accessToken);

		Auth foundAuth = getAuthOrThrow(authRepository.findByAuthIdAndDeletedFalse(authId));

		if(!passwordEncoder.matches(request.getPassword(), foundAuth.getPassword())) {
			throw new CustomException(CustomErrorCode.PASSWORD_MISMATCH);
		}

		if (refreshTokenRepository.existsById(authId)) {
			refreshTokenRepository.deleteById(authId);
		}

		LocalDateTime accessTokenExpiredAt = jwtUtil.getExpiredAt(accessToken);

		if (!accessTokenExpiredAt.isBefore(LocalDateTime.now())) {
			tokenBlacklistRepository.save(new TokenBlacklist(accessToken));
		}

		foundAuth.softDelete();

		//탈퇴 이벤트 발행
		eventPublisher.publishEvent(UserWithdrawnEvent.of(foundAuth.getAuthId()));
	}

	public void restoreDeleteUser(Long authId) {
		//복구할 id 값의 유저 조회
		Auth foundAuth = authRepository.findByAuthIdAndDeletedTrue(authId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER, "데이터를 복구할 유저가 없습니다."));

		//update 문 실시 delete = true -> delete = false
		authRepository.restoredByAuthId(authId);

		//복구 이벤트 발행
		eventPublisher.publishEvent(
			UserRestoredEvent.of(foundAuth.getAuthId(), foundAuth.getEmail(),
			foundAuth.getName(), foundAuth.getCreatedAt())
		);
	}

	//이하 헬퍼 메서드
	public Auth getAuthOrThrow(Optional<Auth> auth) {
		return auth.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
	}

}
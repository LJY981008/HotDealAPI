package com.example.hotdeal.domain.user.auth.api;

import com.example.hotdeal.domain.user.auth.application.AuthService;
import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import com.example.hotdeal.domain.user.auth.domain.request.PasswordRequest;
import com.example.hotdeal.domain.user.auth.domain.response.AccessTokenResponse;
import com.example.hotdeal.domain.user.auth.domain.request.ReissueRequest;
import com.example.hotdeal.domain.user.auth.domain.request.LoginRequest;
import com.example.hotdeal.domain.user.auth.domain.request.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.response.AuthResponse;
import com.example.hotdeal.domain.user.auth.domain.response.TokenResponse;
import com.example.hotdeal.domain.user.auth.domain.response.CreateUserResponse;
import com.example.hotdeal.domain.user.auth.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse<CreateUserResponse>> signup(
		@Valid @RequestBody SignupRequest request
	) {
		CreateUserResponse response = authService.signup(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.of(response, "회원가입에 성공했습니다."));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse<TokenResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		TokenResponse tokens = authService.loginAndIssueToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.of(tokens, "로그인에 성공했습니다."));
	}

	@PostMapping("/reissue")
	public ResponseEntity<AuthResponse<AccessTokenResponse>> reissue(
		@Valid @RequestBody ReissueRequest request
	) {
		AccessTokenResponse accessToken = authService.reissueAccessToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.of(accessToken, "엑세스토큰이 재발급되었습니다."));
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse<Void>> logout(
		HttpServletRequest servletRequest,
		@Valid @RequestBody PasswordRequest request
	) {
		//header 에서 accessToken 추출
		String BearerToken = servletRequest.getHeader("Authorization");
		String accessToken = jwtUtil.substringToken(BearerToken);

		authService.registerTokenBlacklist(accessToken, request);
		return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.of(null, "정상적으로 로그아웃되었습니다."));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<AuthResponse<Void>> withdraw(
		HttpServletRequest servletRequest,
		@Valid @RequestBody PasswordRequest request
	) {
		//header 에서 accessToken 추출
		String BearerToken = servletRequest.getHeader("Authorization");
		String accessToken = jwtUtil.substringToken(BearerToken);

		authService.deactivateUser(accessToken, request);
		return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.of(null, "정상적으로 탈퇴되었습니다."));
	}

	//어드민 전용 유저 복구 API
	@PostMapping("/{authId}/restore")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AuthResponse<Void>> restore(
		@RequestParam Long authId
	) {
		authService.restoreDeleteUser(authId);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}

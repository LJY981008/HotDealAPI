package com.example.hotdeal.domain.user.auth.infra;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.hotdeal.domain.user.auth.domain.Auth;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

	private final AuthRepository authRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) {
		String adminEmail = "admin@hotdeal.com";
		String adminName = "관리자";
		String adminPassword = passwordEncoder.encode("1234");

		//이미 admin 계정이 있을 시 리턴
		if (authRepository.existsByEmail(adminEmail)) {
			return;
		}

		Auth adminAuth = Auth.createAuth(adminEmail, adminName, adminPassword);
		adminAuth.changeRoleByAdmin();

		authRepository.save(adminAuth);
	}

}
package com.example.hotdeal.domain.user.auth.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordRequest {

	@NotBlank(message = "비밀번호는 필수입력값입니다.")
	private String password;

}
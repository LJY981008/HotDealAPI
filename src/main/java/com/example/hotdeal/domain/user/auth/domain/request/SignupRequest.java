package com.example.hotdeal.domain.user.auth.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

	@Email(message = "email 형식으로 입력해주세요.")
	@NotBlank(message = "이메일은 필수입력값입니다.")
	private String email;
	@NotBlank(message = "이름은 필수입력값입니다.")
	private String name;
	@NotBlank(message = "비밀번호는 필수입력값입니다.")
	private String password;

}
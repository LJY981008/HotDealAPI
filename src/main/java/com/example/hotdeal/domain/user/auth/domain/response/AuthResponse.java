package com.example.hotdeal.domain.user.auth.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse<T> {
	private T data;
	private final String message;
}

package com.example.hotdeal.global.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CustomErrorCode {

	ROLE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"),
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "진행중인 이벤트가 아닙니다."),
	NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "주문목록이 없습니다."),
	NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "제품이 없습니다."),
	NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 만료된 토큰입니다."),
	TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "저장된 토큰과 일치하지 않습니다."),
	PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	FAILED_CALL_API(HttpStatus.BAD_GATEWAY, "외부 API 호출 실패"),
	PRODUCT_SHORTAGE(HttpStatus.UNPROCESSABLE_ENTITY, "제품이 부족합니다.");

	private final HttpStatus httpStatus;
	private final String message;

	CustomErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

}
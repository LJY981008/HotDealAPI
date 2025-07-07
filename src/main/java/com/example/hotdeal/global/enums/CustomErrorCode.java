package com.example.hotdeal.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomErrorCode {
    ROLE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "진행중인 이벤트가 아닙니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    CustomErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
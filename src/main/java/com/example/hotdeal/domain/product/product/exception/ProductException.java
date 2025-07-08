package com.example.hotdeal.domain.product.product.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public static ProductException productIdNotFound() {
        return new ProductException("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
    }

    public static ProductException invalidCategory() {
        return new ProductException("INVALID_CATEGORY", "유효하지 않은 상품 카테고리입니다", HttpStatus.BAD_REQUEST);
    }

    private ProductException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
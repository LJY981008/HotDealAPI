package com.example.hotdeal.domain.product.product.domain;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

/**
 * 조회 요청하는 프로덕트의 아이디 리스트 DTO
 */
@Getter
public class SearchProductListRequest {

    List<Long> productIds;

    @AssertTrue
    private boolean isValidList() {
        return !productIds.isEmpty();
    }
}

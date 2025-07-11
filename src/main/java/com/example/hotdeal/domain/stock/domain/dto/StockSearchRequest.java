package com.example.hotdeal.domain.stock.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchRequest {

    @NotEmpty(message = "상품 ID 목록은 비어있을 수 없습니다")
    private List<Long> productIds;
}
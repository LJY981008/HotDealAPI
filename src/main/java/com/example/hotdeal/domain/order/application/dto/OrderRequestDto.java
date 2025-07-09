package com.example.hotdeal.domain.order.application.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;



@Getter
public class OrderRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "상품 개수는 1개 이상이어야 합니다.")
    private Integer quantity;

}

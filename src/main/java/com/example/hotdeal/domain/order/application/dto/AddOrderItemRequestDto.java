package com.example.hotdeal.domain.order.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AddOrderItemRequestDto {
    private List<OrderRequestDto> orderItems;
}

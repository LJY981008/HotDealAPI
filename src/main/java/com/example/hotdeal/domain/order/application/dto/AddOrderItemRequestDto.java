package com.example.hotdeal.domain.order.application.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class AddOrderItemRequestDto {

	private List<OrderRequestDto> orderItems;

}
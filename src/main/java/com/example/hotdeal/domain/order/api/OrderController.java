package com.example.hotdeal.domain.order.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotdeal.domain.order.application.Service.OrderService;
import com.example.hotdeal.domain.order.application.dto.AddOrderItemRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderItemResponseDto;
import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/products")
	public ResponseEntity<OrderItemResponseDto> addOrderItems(
		@AuthenticationPrincipal AuthUserDto user,
		@Valid @RequestBody AddOrderItemRequestDto requestDto) {

		OrderItemResponseDto responseDto = orderService.addOrder(user.getId(), requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	// 주문 삭제
	@PutMapping("/{orderId}")
	public ResponseEntity<String> orderCancel(@PathVariable Long orderId) {

		String cancel = orderService.orderCancel(orderId);

		return ResponseEntity.status(HttpStatus.OK).body(cancel);
	}

	// 주문 조회
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderItemResponseDto> searchOrder(@PathVariable Long orderId) {

		OrderItemResponseDto responseDto = orderService.searchOrder(orderId);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

}
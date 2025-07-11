package com.example.hotdeal.domain.order.api;

import com.example.hotdeal.domain.order.application.Service.OrderService;
import com.example.hotdeal.domain.order.application.dto.*;
import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<String> orderCancel(@PathVariable Long orderId){

        String cancel = orderService.orderCancel(orderId);

        return ResponseEntity.status(HttpStatus.OK).body(cancel);
    }

    // 주문 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderItemResponseDto> searchOrder(@PathVariable Long orderId){

        OrderItemResponseDto responseDto = orderService.searchOrder(orderId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    // 기존 방식 제품 추가
//    @PostMapping("/orders/v1")
//    public ResponseEntity<OrderResponseDto> addOrderItems(@AuthenticationPrincipal AuthUserDto user,
//                                                          @Valid @RequestBody OrderRequestDto requestDto) {
//
//        OrderResponseDto responseDto = orderService.addOrder(user.getId(), requestDto);
//
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

//    @PostMapping("/orders/v0")
//    public ResponseEntity<OrderItemResponseDto> addOrderItemsV0(
//            @AuthenticationPrincipal AuthUserDto user,
//            @Valid @RequestBody AddOrderItemRequestDto requestDto
//    ) {
//        OrderItemResponseDto responseDto = orderService.addOrderV0(user.getId(), requestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
}

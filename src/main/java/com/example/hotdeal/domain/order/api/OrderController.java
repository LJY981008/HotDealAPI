package com.example.hotdeal.domain.order.api;

import com.example.hotdeal.domain.order.application.Service.OrderService;
import com.example.hotdeal.domain.order.application.dto.OrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    // TODO : 주문하기, 주문취소, 주문추가, 주문조회

    // 주문하기
    @PostMapping("/ordering")
    public ResponseEntity<OrderResponseDto> ordering(@Valid @RequestBody OrderRequestDto requestDto){

//        orderService.ordering(requestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

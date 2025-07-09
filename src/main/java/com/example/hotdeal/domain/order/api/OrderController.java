package com.example.hotdeal.domain.order.api;

import com.example.hotdeal.domain.order.application.Service.OrderService;
import com.example.hotdeal.domain.order.application.dto.AddOrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderResponseDto;
import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> addOrderItems(@AuthenticationPrincipal AuthUserDto user,
                                                          @Valid @RequestBody OrderRequestDto requestDto) {

        OrderResponseDto responseDto = orderService.addOrder(user.getId(), requestDto);

        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }
}

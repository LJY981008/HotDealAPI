package com.example.hotdeal.domain.order.application.Service;

import com.example.hotdeal.domain.order.application.dto.OrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderResponseDto;
import com.example.hotdeal.domain.order.infra.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class OrderService{

    private final OrderRepository orderRepository;

//    public OrderResponseDto ordering(OrderRequestDto requestDto) {
//
//
//
//        return new OrderResponseDto();
//    }

}

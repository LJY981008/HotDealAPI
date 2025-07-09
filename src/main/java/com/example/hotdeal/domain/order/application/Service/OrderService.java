package com.example.hotdeal.domain.order.application.Service;

import com.example.hotdeal.domain.order.application.dto.AddOrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderItemDto;
import com.example.hotdeal.domain.order.application.dto.OrderRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderResponseDto;
import com.example.hotdeal.domain.order.domain.Order;
import com.example.hotdeal.domain.order.domain.OrderItem;
import com.example.hotdeal.domain.order.enums.OrderStatus;
import com.example.hotdeal.domain.order.infra.OrderRepository;
import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.infra.ProductRepositoryImpl;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepositoryImpl productRepositoryImpl;

    @Transactional
    public OrderResponseDto addOrder(Long userId, OrderRequestDto requestDto) {

        Product product = productRepositoryImpl.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT));

        BigDecimal result = product.getProductPrice().multiply(BigDecimal.valueOf(requestDto.getQuantity()));

        OrderItem orderItem = new OrderItem(product.getProductId(),
                product.getProductName(),
                requestDto.getQuantity(),
                result);

        Order order = new Order(userId, result);
        order.addOrderItem(orderItem);

        Order saveOrder = orderRepository.save(order);

        OrderItemDto orderItemDto = new OrderItemDto(product.getProductId(),
                product.getProductName(),
                requestDto.getQuantity(),
                product.getProductPrice());


        return new OrderResponseDto(userId
                , saveOrder.getOrderId()
                , orderItemDto
                , requestDto.getQuantity()
                , result
                , saveOrder.getOrderTime()
                , OrderStatus.ORDER_SUCCESS);
    }
}

package com.example.hotdeal.domain.order.application.Service;

import com.example.hotdeal.domain.order.application.dto.*;
import com.example.hotdeal.domain.order.domain.Order;
import com.example.hotdeal.domain.order.domain.OrderItem;
import com.example.hotdeal.domain.order.enums.OrderStatus;
import com.example.hotdeal.domain.order.infra.OrderRepository;
import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import com.example.hotdeal.domain.product.product.infra.ProductRepositoryImpl;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;
    private final ProductRepositoryImpl productRepositoryImpl;

    /*
     * 기존에 하던 Product에 연관관계 맺어서 가져오는 방법
     */
    @Transactional
    public OrderResponseDto addOrder(Long userId, OrderRequestDto requestDto) {

        Product product = productRepositoryImpl.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT));

        BigDecimal result = product.getProductPrice().multiply(BigDecimal.valueOf(requestDto.getQuantity()));

        OrderItem orderItem = new OrderItem(product.getProductId(),
                product.getProductName(),
                requestDto.getQuantity(),
                result);

        Order order = new Order(userId, result, requestDto.getQuantity());
        order.assignOrderItems(List.of(orderItem));

        Order saveOrder = orderRepository.save(order);

        OrderItemDto orderItemDto = new OrderItemDto(
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice());

        orderItemDto.updateItemQuantities(requestDto.getQuantity());
        orderItemDto.updateItemTotalPrice(result);
        saveOrder.setOrderStatus(OrderStatus.ORDER_PENDING);

        return new OrderResponseDto(userId,
                saveOrder.getOrderId(),
                orderItemDto,
                saveOrder.getOrderTotalCount(),
                result,
                saveOrder.getOrderTime(),
                saveOrder.getOrderStatus()
        );
    }

    /*
     * RestTemplate 적용한 버전
     */
    @Transactional
    public OrderItemResponseDto addOrderV1(Long userId, AddOrderItemRequestDto requestDto) {

        BigDecimal orderTotalPrice = BigDecimal.ZERO;
        int totalcount = 0;
        List<Long> productIds = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (OrderRequestDto item : requestDto.getOrderItems()) {
            productIds.add(item.getProductId());
            counts.add(item.getQuantity());
        }

        List<SearchProductResponse> searchProductResponses = productSearch(productIds);
        List<OrderItemDto> product = searchProductResponses.stream()
                .map(response -> new OrderItemDto(response.getProductId(),
                        response.getProductName(),
                        response.getOriginalPrice()))
                .toList();

        List<OrderItem> orderItems = new ArrayList<>();

        // Todo : 추후 개선
        for (int i = 0; i < productIds.size(); i++) {
            OrderItemDto orderItemDto = product.get(i);
            orderItemDto.updateItemQuantities(counts.get(i));

            // 개별 물품 총합
            Integer quantity = orderItemDto.getQuantity();
            BigDecimal productPrice = orderItemDto.getProductPrice();
            orderItemDto.updateItemTotalPrice(productPrice.multiply(BigDecimal.valueOf(quantity)));

            // 전체 물품 총합
            BigDecimal itemTotalPrice = orderItemDto.getItemTotalPrice();
            orderTotalPrice = orderTotalPrice.add(itemTotalPrice);

            // 전체 물품 개수
            totalcount += counts.get(i);
            orderItems.add(new OrderItem(orderItemDto.getProductId(), orderItemDto.getProductName(), orderItemDto.getQuantity(), orderItemDto.getItemTotalPrice()));
        }

        //product --> orderItem
        Order order = new Order(userId, orderTotalPrice, totalcount);
        order.assignOrderItems(orderItems);

        Order saveOrder = orderRepository.save(order);
        saveOrder.setOrderStatus(OrderStatus.ORDER_BEFORE);

        return new OrderItemResponseDto(userId,
                saveOrder.getOrderId(),
                product,
                saveOrder.getOrderTotalCount(),
                saveOrder.getOrderTotalPrice(),
                saveOrder.getOrderTime(),
                saveOrder.getOrderStatus());
    }

    private List<SearchProductResponse> productSearch(List<Long> productIds) {
        OrderAddProductRequest orderAddProductRequest = new OrderAddProductRequest(productIds);
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/products/search-product")
                .encode()
                .build()
                .toUri();

        ResponseEntity<List<SearchProductResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(orderAddProductRequest),
                new ParameterizedTypeReference<List<SearchProductResponse>>() {
                }
        );

        return response.getBody();
    }

    // 유저 삭제
    @Transactional
    public void orderCancel(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_ORDER));

        orderRepository.delete(order);

        order.setOrderStatus(OrderStatus.ORDER_FAILURE);
    }

    //
    public OrderItemResponseDto searchOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_ORDER));

        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        for(OrderItem orderItem : order.getOrderItems()){
            orderItemDtos.add(new OrderItemDto(orderItem.getProductId(), orderItem.getProductName(), orderItem.getItemTotalPrice()));

        }

        return new OrderItemResponseDto(order.getUserId(),
                orderId,
                orderItemDtos,
                order.getOrderTotalCount(),order.getOrderTotalPrice(),
                order.getOrderTime(),
                order.getOrderStatus());
    }
}

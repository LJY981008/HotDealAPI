package com.example.hotdeal.domain.order.application.Service;

import com.example.hotdeal.domain.common.client.product.HotDealApiClient;
import com.example.hotdeal.domain.event.domain.dto.EventProductResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;
    private final ProductRepositoryImpl productRepositoryImpl;
    private final HotDealApiClient apiClient;

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

        Order order = new Order(userId, result);
        order.addOrderItem(orderItem);

        Order saveOrder = orderRepository.save(order);

        OrderItemDto orderItemDto = new OrderItemDto(
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice());

        orderItemDto.updateItemQuantities(requestDto.getQuantity());
        orderItemDto.updateItemTotalPrice(result);
        saveOrder.setOrderStatus(OrderStatus.ORDER_PENDING);

        return new OrderResponseDto(userId
                , saveOrder.getOrderId()
                , orderItemDto
                , requestDto.getQuantity()
                , result
                , saveOrder.getOrderTime()
                , saveOrder.getOrderStatus()
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

        List<SearchProductResponse> searchProductResponses = apiClient.getProducts(productIds);
        List<OrderItemDto> product = searchProductResponses.stream().map(response -> new OrderItemDto(response.getProductId(),
                        response.getProductName(),
                        response.getOriginalPrice()))
                .toList();

        // Todo : 추후 개선
        for (int i = 0; i < productIds.size(); i++) {
            product.get(i).updateItemQuantities(counts.get(i));

            // 개별 물품 총합
            Integer quantity = product.get(i).getQuantity();
            BigDecimal productPrice = product.get(i).getProductPrice();
            product.get(i).updateItemTotalPrice(productPrice.multiply(BigDecimal.valueOf(quantity)));

            // 전체 물품 총합
            BigDecimal itemTotalPrice = product.get(i).getItemTotalPrice();
            orderTotalPrice = orderTotalPrice.add(itemTotalPrice);

            // 전체 물품 개수
            totalcount += counts.get(i);
        }

        Order order = new Order(userId, orderTotalPrice);
        Order saveOrder = orderRepository.save(order);
        saveOrder.setOrderStatus(OrderStatus.ORDER_BEFORE);

        return new OrderItemResponseDto(userId,
                saveOrder.getOrderId(),
                product,
                totalcount,
                orderTotalPrice,
                saveOrder.getOrderTime(),
                saveOrder.getOrderStatus());
    }

    @Transactional
    public void orderCancel(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_ORDER));

        orderRepository.delete(order);

        order.setOrderStatus(OrderStatus.ORDER_FAILURE);
    }


    public OrderItemResponseDto addOrderV0(Long id, AddOrderItemRequestDto requestDto) {
        //TODO 프로덕트 정보(이름, 가격)*완료* , 프로덕트 재고(남은 개수), 이벤트 정보(할인율, 할인가격)*완료* 호출 필요
        List<OrderRequestDto> orders = requestDto.getOrderItems();
        List<Long> productIds = orders.stream().map(OrderRequestDto::getProductId).toList();

        // 프로덕트 정보
        List<OrderItemDto> products = apiClient.getProducts(productIds).stream()
                .map(searchProduct ->
                    new OrderItemDto(searchProduct.getProductId(), searchProduct.getProductName(), searchProduct.getOriginalPrice())
                ).toList();

        // 이벤트 정보
        List<EventProductResponse> events = apiClient.getEvents(productIds);


        // 프로덕트 재고
        return null;
    }


}

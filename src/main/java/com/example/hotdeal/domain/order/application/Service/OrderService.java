package com.example.hotdeal.domain.order.application.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotdeal.domain.common.client.event.HotDealApiClient;
import com.example.hotdeal.domain.common.client.event.dto.EventProductResponse;
import com.example.hotdeal.domain.common.client.product.ProductApiClient;
import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import com.example.hotdeal.domain.common.client.stock.StockRestClient;
import com.example.hotdeal.domain.common.springEvent.order.OrderCreatedEvent;
import com.example.hotdeal.domain.order.application.dto.AddOrderItemRequestDto;
import com.example.hotdeal.domain.order.application.dto.OrderItemDto;
import com.example.hotdeal.domain.order.application.dto.OrderItemResponseDto;
import com.example.hotdeal.domain.order.application.dto.OrderRequestDto;
import com.example.hotdeal.domain.order.domain.Order;
import com.example.hotdeal.domain.order.domain.OrderItem;
import com.example.hotdeal.domain.order.enums.OrderStatus;
import com.example.hotdeal.domain.order.infra.OrderRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

	private final ApplicationEventPublisher eventPublisher;
	private final OrderRepository orderRepository;
	private final HotDealApiClient apiClient;
	private final ProductApiClient productApiClient;
	private final StockRestClient stockRestClient;
	private final boolean orderSituation = false;

	@Transactional
	public OrderItemResponseDto addOrder(Long userId, AddOrderItemRequestDto requestDto) {

		// 1. 요청 데이터 추출
		List<OrderRequestDto> orderRequests = requestDto.getOrderItems();
		List<Long> productIds = orderRequests.stream()
			.map(OrderRequestDto::getProductId)
			.toList();

		// 2. 상품 정보 조회 (이름, 원가)
		List<SearchProductResponse> products = productApiClient.getProducts(productIds);
		if (products.isEmpty()) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT);
		}

		// 남은 재고 조회
		for (OrderRequestDto dto : requestDto.getOrderItems()) {
			if (stockRestClient.getStock(dto.getProductId()).getQuantity() - dto.getQuantity() < -1) {
				throw new CustomException(CustomErrorCode.PRODUCT_SHORTAGE);
			}
		}

		// 3. 이벤트 정보 조회 (할인가) - 주석 해제하고 실제 호출
		List<EventProductResponse> events = apiClient.getEvents(productIds);
		log.info("조회된 이벤트 수: {}", events.size());

		// 4. 주문 아이템 생성 및 총액 계산
		List<OrderItemDto> orderItemDtos = new ArrayList<>();
		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal totalPrice = BigDecimal.ZERO;
		int totalCount = 0;

		for (OrderRequestDto request : orderRequests) {
			// 상품 정보 찾기
			SearchProductResponse product = findProductById(products, request.getProductId());

			// 이벤트 할인가 찾기 (없으면 원가 사용)
			BigDecimal finalPrice = findEventPrice(events, request.getProductId())
				.orElse(product.getOriginalPrice());

			log.info("상품 ID: {}, 원가: {}, 최종가격: {}",
				request.getProductId(), product.getOriginalPrice(), finalPrice);

			// 아이템별 총액 계산
			BigDecimal itemTotalPrice = finalPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

			// DTO 생성 (할인된 가격으로 표시)
			OrderItemDto orderItemDto = new OrderItemDto(
				product.getProductId(),
				product.getProductName(),
				finalPrice
			);
			orderItemDto.updateItemQuantities(request.getQuantity());
			orderItemDto.updateItemTotalPrice(itemTotalPrice);
			orderItemDtos.add(orderItemDto);

			// Entity 생성
			OrderItem orderItem = new OrderItem(
				product.getProductId(),
				product.getProductName(),
				request.getQuantity(),
				itemTotalPrice
			);
			orderItems.add(orderItem);

			// 전체 총액 및 수량 누적
			totalPrice = totalPrice.add(itemTotalPrice);
			totalCount += request.getQuantity();
		}

		// 5. 주문 생성 및 저장
		Order order = new Order(userId, totalPrice, totalCount);
		order.assignOrderItems(orderItems);
		order.setOrderStatus(OrderStatus.ORDER_BEFORE);

		Order savedOrder = orderRepository.save(order);

		// 6. 주문 생성 이벤트 발행 (재고 차감용)
		List<OrderCreatedEvent.OrderItemInfo> orderItemInfos = orderItems.stream()
			.map(item -> new OrderCreatedEvent.OrderItemInfo(
				item.getProductId(),
				item.getOrderItemCount(),
				item.getProductName()
			))
			.toList();

		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
			savedOrder.getOrderId(),
			userId,
			orderItemInfos
		);
		eventPublisher.publishEvent(orderCreatedEvent);

		// 7. 응답 생성
		return new OrderItemResponseDto(
			userId,
			savedOrder.getOrderId(),
			orderItemDtos,
			savedOrder.getOrderTotalCount(),
			savedOrder.getOrderTotalPrice(),
			savedOrder.getOrderTime(),
			savedOrder.getOrderStatus()
		);
	}

	// 주문 물품 삭제
	@Transactional
	public String orderCancel(Long orderId) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_ORDER));

		orderRepository.delete(order);
		order.setOrderStatus(OrderStatus.ORDER_FAILURE);

		eventPublisher.publishEvent(orderSituation);

		return "제품 주문이 취소되었습니다.";
	}

	// 주문 조회
	public OrderItemResponseDto searchOrder(Long orderId) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_ORDER));

		List<OrderItemDto> orderItemDtos = new ArrayList<>();

		for (OrderItem orderItem : order.getOrderItems()) {
			orderItemDtos.add(
				new OrderItemDto(orderItem.getProductId(), orderItem.getProductName(), orderItem.getItemTotalPrice()));

		}

		return new OrderItemResponseDto(order.getUserId(),
			orderId,
			orderItemDtos,
			order.getOrderTotalCount(), order.getOrderTotalPrice(),
			order.getOrderTime(),
			order.getOrderStatus());
	}

	// 헬퍼 메서드들
	private SearchProductResponse findProductById(List<SearchProductResponse> products, Long productId) {
		return products.stream()
			.filter(p -> p.getProductId().equals(productId))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT));
	}

	private Optional<BigDecimal> findEventPrice(List<EventProductResponse> events, Long productId) {
		return events.stream()
			.filter(e -> e.getProductId().equals(productId))
			.map(EventProductResponse::getDiscountPrice)
			.findFirst();
	}

}
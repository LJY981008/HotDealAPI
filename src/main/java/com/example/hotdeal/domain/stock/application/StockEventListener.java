package com.example.hotdeal.domain.stock.application;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotdeal.domain.common.springEvent.order.OrderCreatedEvent;
import com.example.hotdeal.domain.stock.domain.Stock;
import com.example.hotdeal.domain.stock.infra.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

	private final StockService stockService;
	private final StockRepository stockRepository;

	@Async
	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleOrderCreatedEvent(OrderCreatedEvent event) {
		log.info("주문 생성 이벤트 수신 - 주문ID: {}, 상품 개수: {}",
			event.getOrderId(), event.getOrderItems().size());

		try {
			for (OrderCreatedEvent.OrderItemInfo item : event.getOrderItems()) {
				// 상품별 재고 조회
				Stock stock = stockRepository.findByProductId(item.getProductId())
					.orElseGet(() -> {
						log.warn("재고 정보가 없습니다. 상품ID: {} - 새로운 재고 생성", item.getProductId());
						return stockRepository.save(new Stock(item.getProductId(), 0));
					});

				// 락을 사용한 재고 차감
				try {
					stockService.decreaseWithLock(stock.getId(), item.getQuantity());
					log.info("재고 차감 성공 - 상품ID: {}, 차감 수량: {}",
						item.getProductId(), item.getQuantity());
				} catch (Exception e) {
					log.error("재고 차감 실패 - 상품ID: {}, 에러: {}",
						item.getProductId(), e.getMessage());
					// 재고 부족 시 주문 실패 이벤트 발행 고려
					throw e;
				}
			}

			log.info("주문ID: {}의 모든 상품 재고 차감 완료", event.getOrderId());

		} catch (Exception e) {
			log.error("재고 처리 중 오류 발생 - 주문ID: {}, 에러: {}",
				event.getOrderId(), e.getMessage(), e);
			// 재고 차감 실패 시 보상 트랜잭션 또는 주문 취소 로직 필요
			// TODO: OrderFailedEvent 발행 고려
		}
	}

}
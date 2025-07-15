package com.example.hotdeal.domain.notification.application;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;
import com.example.hotdeal.domain.notification.domain.ListenProductEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final NotificationService notificationService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void addProductDiscountEvent(WSEventProduct event) {
		try {
			log.info("addProductDiscountEvent 시작 - 단일 이벤트: {}", event.product_id());

			ListenProductEvent listenProductEvent = new ListenProductEvent(event);
			notificationService.notifyProductEventMessage(listenProductEvent);

			log.info("addProductDiscountEvent 종료");
		} catch (Exception e) {
			log.error("addProductDiscountEvent 처리 실패 message : {}", e.getMessage());
		}
	}

}
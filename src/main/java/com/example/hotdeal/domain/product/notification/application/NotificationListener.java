package com.example.hotdeal.domain.product.notification.application;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;
import com.example.hotdeal.domain.product.notification.domain.ListenProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void addProductDiscountEvent(List<WSEventProduct> events){
        try {
            log.info("addProductDiscountEvent 시작");

            List<ListenProductEvent> listenProductEvents =
                    events.stream().map(ListenProductEvent::new).toList();

            notificationService.notifyProductEventMessage(listenProductEvents);

            log.info("addProductDiscountEvent 종료");
        } catch (Exception e) {
            log.error("addProductDiscountEvent 처리 실패 message : {}", e.getMessage());
        }
    }
}

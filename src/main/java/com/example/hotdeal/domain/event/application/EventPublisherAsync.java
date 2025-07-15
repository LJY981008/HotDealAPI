package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPublisherAsync {

    private final ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(List<WSEventProduct> wsEventProducts) {
        wsEventProducts.forEach(eventPublisher::publishEvent);
    }
}

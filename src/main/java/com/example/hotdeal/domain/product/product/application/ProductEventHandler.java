package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.event.domain.ProductEventAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventHandler {

    private final ProductService productService;

    @EventListener
    @Async
    public void handleProductEventAdded(ProductEventAddedEvent event) {
        try {
            log.info("Processing ProductEventAddedEvent: eventId={}, productIds={}",
                    event.getEventId(), event.getProductIds());

            productService.addEvent(event.getProductIds(), event.getEventId());

            log.info("Successfully processed ProductEventAddedEvent");
        } catch (Exception e) {
            log.error("Failed to process ProductEventAddedEvent", e);
        }
    }
}

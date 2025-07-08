package com.example.hotdeal.global.event.model;

public interface EventPublisher {
    void publish(DomainEvent event);
}

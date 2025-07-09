package com.example.hotdeal.domain.product.notification.application;

import com.example.hotdeal.domain.product.notification.domain.ListenProductEvent;
import com.example.hotdeal.domain.product.notification.domain.Notification;
import com.example.hotdeal.domain.product.notification.infra.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public void notifyProductEventMessage(List<ListenProductEvent> listenProductEvents) {
        List<Notification> notifications = listenProductEvents.stream().map(ListenProductEvent::toNotification).toList();
        notificationRepository.saveAll(notifications);
    }
}

package com.example.hotdeal.domain.product.notification.application;

import com.example.hotdeal.domain.product.notification.domain.ListenProductEvent;
import com.example.hotdeal.domain.product.notification.domain.Notification;
import com.example.hotdeal.domain.product.notification.infra.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    //TODO 구독 경로에 productID를 추가해 해당 프로덕트를 구독한 유저를 분류해도 될듯?
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyProductEventMessage(List<ListenProductEvent> listenProductEvents) {
        try {
            List<Notification> notifications = listenProductEvents.stream().map(ListenProductEvent::toNotification).toList();
            notificationRepository.saveAll(notifications);
            List<String> notificationMessages = notifications.stream().map(Notification::getNotification_message).toList();
            messagingTemplate.convertAndSend("/topic/notifications", notificationMessages);
        } catch (Exception e) {
            log.error("notifyProductEventMessage 에러 message {}", e.getMessage());
        }

    }
}

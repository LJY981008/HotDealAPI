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

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyProductEventMessage(ListenProductEvent listenProductEvent) {
        log.info("NotificationService.notifyProductEventMessage 시작");

        Notification notification = listenProductEvent.toNotification();
        notificationRepository.save(notification);
        String notificationMessages = notification.getNotification_message();

        log.info("웹소켓 메시지 전송 시작: {}", notificationMessages);

        try {
            messagingTemplate.convertAndSend(
                    "/topic/notification",
                    notificationMessages
            );
            log.debug("웹소켓 메시지 전송 완료");
        } catch (Exception wsException) {
            log.error("웹소켓 메시지 전송 실패: {}", wsException.getMessage(), wsException);
            throw wsException;
        }
    }

}

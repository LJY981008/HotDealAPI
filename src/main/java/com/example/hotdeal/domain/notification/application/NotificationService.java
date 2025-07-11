package com.example.hotdeal.domain.notification.application;

import com.example.hotdeal.domain.common.client.product.NotificationApiClient;
import com.example.hotdeal.domain.notification.domain.ListenProductEvent;
import com.example.hotdeal.domain.notification.domain.Notification;
import com.example.hotdeal.domain.notification.infra.NotificationRepository;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationApiClient apiClient;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyProductEventMessage(ListenProductEvent listenProductEvent) {
        log.info("NotificationService.notifyProductEventMessage 시작");

        Notification notification = listenProductEvent.toNotification();
        notificationRepository.save(notification);
        String notificationMessages = notification.getNotification_message();

        Long productId = listenProductEvent.getProductId();
        List<SubscribeResponse> subscribeResponses = apiClient.searchUserFromSubscribeToProduct(productId);

        log.info("웹소켓 메시지 전송 시작: {}", notificationMessages);

        try {
            for (SubscribeResponse subscribeResponse : subscribeResponses) {
                messagingTemplate.convertAndSendToUser(
                    subscribeResponse.userId().toString(),
                    "/topic/notification",
                    notificationMessages
                );
                log.debug("사용자 {}에게 알림 메시지 전송 완료", subscribeResponse.userId());
            }

            log.info("웹소켓 메시지 전송 완료 - 총 {}명의 구독자에게 전송", subscribeResponses.size());
        } catch (Exception wsException) {
            log.error("웹소켓 메시지 전송 실패: {}", wsException.getMessage(), wsException);
            throw wsException;
        }
    }

}

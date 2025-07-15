package com.example.hotdeal.domain.notification.application;

import com.example.hotdeal.domain.notification.domain.ListenProductEvent;
import com.example.hotdeal.domain.notification.domain.Notification;
import com.example.hotdeal.domain.notification.infra.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final List<Notification> buffer = new ArrayList<>();
    private final Object lock = new Object();
    private long lastInsertTime = System.currentTimeMillis();
    private final ApplicationContext context;

    @PostConstruct
    public void init() {
        insertTimer();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyProductEventMessage(ListenProductEvent listenProductEvent) {
        log.info("NotificationService.notifyProductEventMessage 시작");

        Notification notification = listenProductEvent.toNotification();
        addNotification(notification);            // 벌크 insert
        //notificationRepository.save(notification); // 기존 insert
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

    @Async
    public void insertBatch() {
        List<Notification> notifications;
        synchronized (lock) {
            notifications = new ArrayList<>(buffer);
            buffer.clear();
        }
        log.info("알림 insert 실행");
        notificationRepository.insertNotifications(notifications);
    }

    public void addNotification(Notification notification) {
        log.info("알림 버퍼에 추가");
        synchronized (lock) {
            buffer.add(notification);
            if(buffer.size() >= 1000) {
                log.debug("알림 개수 1000개 이상 생성");
                ((NotificationService) context.getBean(NotificationService.class)).insertBatch();
            }
        }
        lastInsertTime = System.currentTimeMillis();
    }

    private void insertTimer() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            synchronized (lock) {
                if (!buffer.isEmpty() && System.currentTimeMillis() - lastInsertTime >= 5000) {
                    log.debug("알림 스케줄러 실행");
                    ((NotificationService) context.getBean(NotificationService.class)).insertBatch();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}

package com.example.hotdeal.domain.product.notification.infra;

import com.example.hotdeal.domain.product.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}

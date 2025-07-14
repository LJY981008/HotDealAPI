package com.example.hotdeal.domain.notification.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotdeal.domain.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}

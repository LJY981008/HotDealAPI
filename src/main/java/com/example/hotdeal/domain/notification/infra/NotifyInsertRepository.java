package com.example.hotdeal.domain.notification.infra;

import com.example.hotdeal.domain.notification.domain.Notification;

import java.util.List;

public interface NotifyInsertRepository {
    void insertNotifications(List<Notification> notification);
}

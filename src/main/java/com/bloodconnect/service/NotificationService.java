package com.bloodconnect.service;

import com.bloodconnect.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getUserNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);

    long getUnreadCount(Long userId);
}

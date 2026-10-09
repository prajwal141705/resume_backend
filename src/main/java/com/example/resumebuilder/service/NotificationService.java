package com.example.resumebuilder.service;

import com.example.resumebuilder.model.Notification;
import com.example.resumebuilder.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification sendNotification(String userId, String title, String message, String type, String link) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type != null ? type : "SYSTEM")
                .link(link)
                .read(false)
                .createdAt(Instant.now())
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String userId, boolean isAdmin) {
        if (isAdmin) {
            return notificationRepository.findByUserIdInOrderByCreatedAtDesc(List.of(userId, "ADMIN"));
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(String id, String userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(String userId, boolean isAdmin) {
        List<Notification> list = getNotificationsForUser(userId, isAdmin);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}

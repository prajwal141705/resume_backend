package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.Notification;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        boolean isAdmin = currentUser != null && currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId, isAdmin));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id, @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        return ResponseEntity.ok(notificationService.markAsRead(id, userId));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        boolean isAdmin = currentUser != null && currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        notificationService.markAllAsRead(userId, isAdmin);
        return ResponseEntity.ok().build();
    }
}

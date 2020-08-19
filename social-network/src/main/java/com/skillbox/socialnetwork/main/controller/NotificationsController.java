package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.security.jwt.JwtUser;
import com.skillbox.socialnetwork.main.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationsController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage
    ){
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId(), offset, itemPerPage));
    }

    @PutMapping
    public ResponseEntity<?> markNotificationAsRead(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam(required = false) Integer notificationId,
            @RequestParam(required = false) Boolean all
    ){
        return ResponseEntity.ok(notificationService.markNotificationsAsRead(user.getId(), null, all));
    }
}

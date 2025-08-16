package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.Notification;
import ru.yandex.practicum.service.FirstNotificationService;
import ru.yandex.practicum.service.SecondNotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    FirstNotificationService firstNotificationService;

    @Autowired
    SecondNotificationService secondNotificationService;

    @PostMapping("/notifyFirst")
    public ResponseEntity<Void> notifyFirst(@RequestBody Notification notification) {
        firstNotificationService.sendFirstNotification(notification);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifySecond")
    public ResponseEntity<Void> notifySecond(@RequestBody Notification notification) {
        secondNotificationService.sendSecondNotification(notification);
        return ResponseEntity.ok().build();
    }
}
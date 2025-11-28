package com.spring.unictive.module.notification.presenter;

import com.spring.unictive.module.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationPresenter {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/email")
    public Map<String, Object> sendEmailNotification(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        Map<String, Object> response = new HashMap<>();
        notificationService.sendEmailNotification(to, subject, body);

        response.put("status", "success");
        response.put("message", "Email sent successfully processed on background");

        return response;
    }
}

package com.spring.unictive.module.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NotificationService {
    private static final Logger log = Logger.getLogger(NotificationService.class.getName());

    @Async
    public void sendEmailNotification(String to, String subject, String body) {
        log.info("Sending email notification to: " + to);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Email sent to {} with subject '{}'");
    }
}

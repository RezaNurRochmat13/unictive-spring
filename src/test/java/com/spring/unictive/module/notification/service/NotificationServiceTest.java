package com.spring.unictive.module.notification.service;

import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ContextConfiguration(classes = {NotificationServiceTest.TestConfig.class, NotificationService.class})
public class NotificationServiceTest {

    @Configuration
    @EnableAsync
    static class TestConfig implements AsyncConfigurer {
        @Override
        public Executor getAsyncExecutor() {
            return Runnable::run;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> {
                throw new RuntimeException("Async error in " + method.getName(), ex);
            };
        }
    }

    private final NotificationService notificationService;

    public NotificationServiceTest(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Test
    void testSendEmailNotification_shouldRunWithoutException() {
        assertDoesNotThrow(() ->
                notificationService.sendEmailNotification("test@example.com", "Hello", "Body")
        );
    }
}

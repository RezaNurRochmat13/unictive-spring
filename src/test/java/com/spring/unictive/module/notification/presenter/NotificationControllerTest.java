package com.spring.unictive.module.notification.presenter;

import com.spring.unictive.module.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void testSendEmailNotification_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/notifications/email")
                        .param("to", "test@example.com")
                        .param("subject", "Hello")
                        .param("body", "This is a test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Email sent successfully processed on background"));


        Mockito.verify(notificationService, times(1))
                .sendEmailNotification("test@example.com", "Hello", "This is a test");
    }

    @Test
    void testSendEmailNotification_missingParams_shouldFail() throws Exception {
        mockMvc.perform(post("/api/v1/notifications/email")
                                .param("to", "test@example.com")
                )
                .andExpect(status().isBadRequest());
    }
}

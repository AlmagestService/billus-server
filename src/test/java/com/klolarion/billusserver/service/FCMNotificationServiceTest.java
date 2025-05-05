package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class FCMNotificationServiceTest {
    @InjectMocks
    private FCMNotificationService fcmNotificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("FCM 알림 전송 테스트")
    void testSendNotification() {
        // TODO: mock 및 테스트 구현
        // boolean result = fcmNotificationService.sendNotification(...);
        // assertThat(result).isTrue();
    }
} 
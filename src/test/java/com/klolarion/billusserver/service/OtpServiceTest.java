package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class OtpServiceTest {
    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("OTP 검증 테스트")
    void testCheckOtp() {
        // TODO: mock 및 테스트 구현
        // otpService.check(...);
        // assertThat(...).isTrue();
    }
} 
package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import com.klolarion.billusserver.domain.entity.Company;
import static org.mockito.Mockito.mock;

class EmailVerificationServiceTest {
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 인증 검증 테스트")
    void testCheckEmailVerification() {
        // TODO: mock 및 테스트 구현
        // emailVerificationService.check(...);
        // assertThat(...).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 성공 시 예외 없이 동작")
    void testCheckEmailVerificationSuccess() {
        // given
        String code = "123456";
        Company company = mock(Company.class);
        // when & then
        emailVerificationService.check(code, company, "COMPANY");
        // 예외가 발생하지 않으면 성공
    }
} 
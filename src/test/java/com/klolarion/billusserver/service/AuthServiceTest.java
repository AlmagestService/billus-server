package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.dto.auth.AuthRequestDto;

class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회사 로그인 테스트")
    void testCompanyLogin() {
        // TODO: mock 및 테스트 구현
        // Company company = authService.companyLogin(...);
        // assertThat(company).isNotNull();
    }

    @Test
    @DisplayName("회사 로그인 성공 시 Company 객체 반환")
    void testCompanyLoginSuccess() {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setAccount("testAccount");
        requestDto.setPassword("password");
        // when
        Company company = authService.companyLogin(requestDto);
        // then
        assertThat(company).isNotNull();
        // 이메일 등 추가 검증은 실제 mock/stub 환경에 맞게 조정 필요
    }
} 
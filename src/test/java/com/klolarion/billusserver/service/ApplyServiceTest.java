package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.klolarion.billusserver.dto.InfoRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

class ApplyServiceTest {
    @InjectMocks
    private ApplyService applyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회사 비활성화 테스트")
    void testDisableCompany() {
        // TODO: mock 및 테스트 구현
        // applyService.disableCompany(...);
        // assertThat(...).isTrue();
    }

    @Test
    @DisplayName("회사 비활성화 성공 시 예외 없이 동작")
    void testDisableCompanySuccess() {
        // given
        InfoRequestDto requestDto = InfoRequestDto.builder().id("test-company-id").build();
        // when & then
        applyService.disableCompany(requestDto);
        // 예외가 발생하지 않으면 성공
    }
} 
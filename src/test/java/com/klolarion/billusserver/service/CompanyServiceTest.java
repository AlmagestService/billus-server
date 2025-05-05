package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.klolarion.billusserver.dto.company.CompanyResponseDto;
import com.klolarion.billusserver.domain.entity.Company;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CompanyServiceTest {
    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회사 정보 조회 시 CompanyResponseDto 반환")
    void testGetCompanyInfo() {
        // given
        Company company = mock(Company.class);
        // when
        CompanyResponseDto dto = companyService.getCompanyInfo(company);
        // then
        assertThat(dto).isNotNull();
        // 실제 company 필드 등 추가 검증은 mock/stub 환경에 맞게 조정 필요
    }
} 
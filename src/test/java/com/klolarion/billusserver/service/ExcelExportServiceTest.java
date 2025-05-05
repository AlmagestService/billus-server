package com.klolarion.billusserver.service;

import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.klolarion.billusserver.domain.entity.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

class ExcelExportServiceTest {
    @InjectMocks
    private ExcelExportService excelExportService;

    public ExcelExportServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("매장 월간 회사별 합계 엑셀 생성 테스트")
    void testStoreMonthlyCompanyTotalExcel() {
        BillResponseDto dto = BillResponseDto.builder()
                .index(1)
                .companyName("테스트회사")
                .price("10000")
                .date("202406")
                .build();
        ResponseEntity<byte[]> response = excelExportService.storeMonthlyCompanyTotalExcel(Collections.singletonList(dto));
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("회사 월간 매장별 합계 엑셀 생성 테스트")
    void testCompanyMonthlyStoreTotalExcel() {
        BillResponseDto dto = BillResponseDto.builder()
                .index(1)
                .storeName("테스트매장")
                .price("20000")
                .date("202406")
                .build();
        ResponseEntity<byte[]> response = excelExportService.companyMonthlyStoreTotalExcel(Collections.singletonList(dto));
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("직원 목록 엑셀 생성 테스트")
    void testCompanyEmployeeList() {
        MemberResponseDto dto = MemberResponseDto.builder()
                .memberId("1")
                .memberName("홍길동")
                .email("test@test.com")
                .tel("010-0000-0000")
                .build();
        ResponseEntity<byte[]> response = excelExportService.companyEmployeeList(Collections.singletonList(dto));
        assertThat(response).isNotNull();
    }
} 
package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.entity.Bill;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.dto.bill.BillRequestDto;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.domain.repository.BillRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BillServiceTest {
    @Mock
    private BillRepository billRepository;
    @Mock
    private JPAQueryFactory query;
    @Mock
    private EntityManager em;
    @InjectMocks
    private BillService billService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("새로운 장부 생성 시 Bill 객체 반환")
    void testNewBill() {
        // given
        BillRequestDto dto = new BillRequestDto();
        Member member = new Member();
        // when
        Bill bill = billService.newBill(dto, member);
        // then
        assertThat(bill).isNotNull();
    }

    @Test
    @DisplayName("월간 매장별 상세 장부 목록 조회")
    void testMonthlyStoreBillDetailList() {
        List<BillResponseDto> result = billService.monthlyStoreBillDetailList("202406", "storeId");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("월간 매장별 합계 목록 조회")
    void testMonthlyStoreBillTotalList() {
        List<BillResponseDto> result = billService.monthlyStoreBillTotalList("202406");
        assertThat(result).isNotNull();
    }
} 
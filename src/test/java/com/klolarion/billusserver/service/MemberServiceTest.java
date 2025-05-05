package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 이메일 찾기 테스트")
    void testFindEmail() {
        // TODO: mock 및 테스트 구현
        // String email = memberService.findEmail(...);
        // assertThat(email).isNotNull();
    }
} 
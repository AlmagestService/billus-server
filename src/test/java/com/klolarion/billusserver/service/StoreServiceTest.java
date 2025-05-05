package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;

class StoreServiceTest {
    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("매장 이메일 찾기 테스트")
    void testFindEmail() {
        // TODO: mock 및 테스트 구현
        // String email = storeService.findEmail(...);
        // assertThat(email).isNotNull();
    }
} 
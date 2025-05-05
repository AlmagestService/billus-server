package com.klolarion.billusserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("메뉴 정보 조회 테스트")
    void testFindMenu() {
        // TODO: mock 및 테스트 구현
        // MenuDto menu = menuService.findMenu(...);
        // assertThat(menu).isNotNull();
    }
} 
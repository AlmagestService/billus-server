package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.dto.InfoRequestDto;
import com.klolarion.billusserver.security.CustomStoreDetails;
import com.klolarion.billusserver.service.MenuService;
import com.klolarion.billusserver.util.CommonResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a2/v1/store")
public class MenuControllerV1 {
    private final MenuService menuService;

    /**
     * 메뉴 정보 조회 API
     * @param requestDto 메뉴 조회 정보
     * @return 메뉴 상세 정보
     */
    @PostMapping("/menus")
    public ResponseEntity<?> getMenu(@RequestBody InfoRequestDto requestDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "메뉴 조회 성공", 
            menuService.getMenu(requestDto)
        ));
    }

    /**
     * 메뉴 등록 API
     * @param requestDto 등록할 메뉴 정보
     * @param customStoreDetails 인증된 매장 정보
     * @return 등록된 메뉴 정보
     */
    @PostMapping("/")
    public ResponseEntity<?> insertMenu(@RequestBody InfoRequestDto requestDto,
                                        @AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "메뉴 등록 성공", 
            menuService.newMenu(requestDto, customStoreDetails.getStore())
        ));
    }

    /**
     * 메뉴 수정 API
     * @param requestDto 수정할 메뉴 정보
     * @return 수정된 메뉴 정보
     */
    @PutMapping("/")
    public ResponseEntity<?> updateMenu(@RequestBody InfoRequestDto requestDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "메뉴 수정 성공", 
            menuService.updateMenu(requestDto)
        ));
    }
}

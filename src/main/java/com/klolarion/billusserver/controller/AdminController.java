package com.klolarion.billusserver.controller;

import com.klolarion.billusserver.dto.InfoRequestDto;
import com.klolarion.billusserver.service.ApplyService;
import com.klolarion.billusserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/a0")
public class AdminController {
    private final AuthService authService;
    private final ApplyService applyService;

    /**
     * Admin 계정 확인
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body("I'm admin");
    }

    /**
     * Admin 권한 추가
     */
    @PostMapping("/insert/role")
    public ResponseEntity<?> insertRole(String roleName) {
            authService.insertRole(roleName);
            return ResponseEntity.status(HttpStatus.OK).body("Role created");
    }

    /**
     * 매장 비활성화
     * */
    @PostMapping("/disable/store")
    public ResponseEntity<?> disableStore(@RequestBody InfoRequestDto requestDto) {

            applyService.disableStore(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body("Store approved");
    }

    /**
     * 회사 비활성화
     * */
    @PostMapping("/disable/company")
    public ResponseEntity<?> disableCompany(@RequestBody InfoRequestDto requestDto) {
            applyService.disableCompany(requestDto);

            return ResponseEntity.status(HttpStatus.OK).body("Company disabled");
    }

}

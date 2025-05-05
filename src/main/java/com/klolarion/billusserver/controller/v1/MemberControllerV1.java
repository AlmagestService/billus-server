package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.domain.entity.Bill;
import com.klolarion.billusserver.dto.bill.BillRequestDto;
import com.klolarion.billusserver.security.CustomUserDetails;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.service.*;
import com.klolarion.billusserver.util.CommonResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a2/v1/member")
public class MemberControllerV1 {
    private final ApplyService applyService;
    private final MemberService memberService;
    private final MenuService menuService;
    private final BillService billService;
    private final FCMNotificationService fcmNotificationService;


    /**
     * 사용자 정보 조회 API
     * @param customUserDetails 인증된 사용자 정보
     * @return 사용자 상세 정보
     */
    @GetMapping("/")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용자 정보 조회 성공", 
            memberService.memberInfo(customUserDetails.getMember())
        ));
    }

    /**
     * QR 코드 스캔 시 매장 가격 조회 API
     * @param requestDto 매장 정보
     * @return 매장 가격 정보
     */
    @GetMapping("/store/price")
    public ResponseEntity<?> getStorePrice(@RequestBody InfoRequestDto requestDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "가격 조회 성공", 
            memberService.getStorePrice(requestDto)
        ));
    }

    /**
     * 장부 등록 API
     * @param requestDto 장부 등록 정보
     * @param customUserDetails 인증된 사용자 정보
     * @return 장부 등록 결과
     */
    @PostMapping("/bill")
    public ResponseEntity<?> newBill(@RequestBody BillRequestDto requestDto,
                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Bill bill = billService.newBill(requestDto, customUserDetails.getMember());
        fcmNotificationService.sendNotificationByToken(bill, requestDto.getExtraCount());
        
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "장부 생성 성공", 
            null
        ));
    }

    /**
     * 회사 검색 API
     * @param requestDto 회사 검색 정보
     * @return 회사 검색 결과
     */
    @PostMapping("/company")
    public ResponseEntity<?> searchCompany(@RequestBody InfoRequestDto requestDto) {
        applyService.lookCompany(requestDto);
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 조회 성공", 
            null
        ));
    }

    /**
     * 회사 가입 신청 API
     * @param requestDto 회사 정보
     * @param customUserDetails 인증된 사용자 정보
     * @return 가입 신청 결과
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyToCompany(@RequestBody InfoRequestDto requestDto,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        applyService.applyToCompany(requestDto, customUserDetails.getMember());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "승인 요청 성공", 
            null
        ));
    }

    /**
     * 회사 탈퇴 API
     * @param customUserDetails 인증된 사용자 정보
     * @return 탈퇴 결과
     */
    @PostMapping("/quit")
    public ResponseEntity<?> quitCompany(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        applyService.quitCompany(customUserDetails.getMember());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 등록 해제 성공", 
            null
        ));
    }

    /**
     * 매장 검색 API
     * @param storeSearchDto 매장 검색 조건 (이름, 주소)
     * @return 검색된 매장 목록
     */
    @PostMapping("/stores")
    public ResponseEntity<?> searchStore(@RequestBody InfoRequestDto storeSearchDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "매장 검색 성공", 
            memberService.searchStore(storeSearchDto)
        ));
    }

    /**
     * 매장 메뉴 조회 API
     * @param requestDto 매장 정보
     * @return 매장 메뉴 정보
     */
    @PostMapping("/store/menu")
    public ResponseEntity<?> getMenu(InfoRequestDto requestDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "메뉴 조회 성공", 
            menuService.getMenu(requestDto)
        ));
    }

}

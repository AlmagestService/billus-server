package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.auth.AuthRequestDto;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.dto.store.StoreResponseDto;
import com.klolarion.billusserver.security.CustomStoreDetails;
import com.klolarion.billusserver.service.StoreService;
import com.klolarion.billusserver.service.*;
import com.klolarion.billusserver.util.CommonResponseHelper;
import com.klolarion.billusserver.util.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a2/v1/store")
public class StoreControllerV1 {
    private final ApplyService applyService;
    private final StoreService storeService;
    private final CloseSumService closeSumService;
    private final BillService billService;
    private final RedisService redisService;
    private final EmailVerificationService emailService;
    private final OtpService otpService;

    /**
     * 매장 정보 조회 API
     * @param customStoreDetails 인증된 매장 정보
     * @return 매장 상세 정보
     */
    @GetMapping("/")
    public ResponseEntity<?> getStoreInfo(@AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "매장 정보 조회 성공", 
            storeService.initStore()
        ));
    }

    /**
     * 전화번호 중복 확인 API
     * @param registerDto 전화번호 정보
     * @return 중복 확인 결과
     */
    @PostMapping("/tel")
    public ResponseEntity<?> lookTelUpdate(AuthRequestDto registerDto) {
        storeService.lookTelUpdate(registerDto.getTel());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 전화번호", 
            null
        ));
    }

    /**
     * 이메일 중복 확인 API
     * @param registerDto 이메일 정보
     * @return 중복 확인 결과
     */
    @PostMapping("/email")
    public ResponseEntity<?> lookEmailUpdate(AuthRequestDto registerDto) {
        storeService.lookEmailUpdate(registerDto.getEmail());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 이메일", 
            null
        ));
    }

    /**
     * 이메일 인증 코드 전송 API
     * @param customStoreDetails 인증된 매장 정보
     * @return 코드 전송 결과
     */
    @PostMapping("/code/send")
    public ResponseEntity<?> sendEmailCode(@AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        String code = otpService.generateStoreOtp(customStoreDetails.getStore().getId().toString());
        emailService.send(code, customStoreDetails.getStore().getEmail());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "코드전송 성공", 
            null
        ));
    }

    /**
     * 이메일 인증 코드 확인 API
     * @param otpDto 인증 코드 정보
     * @param customStoreDetails 인증된 매장 정보
     * @return 코드 확인 결과
     */
    @PostMapping("/code/check")
    public ResponseEntity<?> checkEmailCode(InfoRequestDto otpDto,
                                            @AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        emailService.check(otpDto.getCode(), customStoreDetails.getStore(), "STORE");
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "코드 일치", 
            null
        ));
    }

    /**
     * 매장 정보 수정 API
     * @param storeDto 수정할 매장 정보
     * @return 수정 결과
     */
    @PutMapping("/info")
    public ResponseEntity<?> changeInfo(@RequestBody StoreResponseDto storeDto) {
        storeService.changeInfo(storeDto);
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "변경 성공", 
            null
        ));
    }

    /**
     * 비밀번호 변경 API
     * @param passwordDto 비밀번호 변경 정보
     * @return 변경 결과
     */
    @PutMapping("/pw")
    public ResponseEntity<?> changePassword(@RequestBody InfoRequestDto passwordDto) {
        storeService.changePassword(passwordDto);
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "변경 성공", 
            null
        ));
    }

    /**
     * 가격 정보 수정 API
     * @param priceDto 수정할 가격 정보
     * @return 수정 결과
     */
    @PutMapping("/price")
    public ResponseEntity<?> updatePrice(@RequestBody InfoRequestDto priceDto) {
        storeService.updatePrice(priceDto.getPrice());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "변경 성공", 
            null
        ));
    }

    /**
     * 매장 로그아웃 API
     * @param response HTTP 응답 객체 (쿠키 삭제용)
     * @param customStoreDetails 인증된 매장 정보
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<?> storeLogout(HttpServletResponse response,
                                         @AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        ResponseCookie cookie = ResponseCookie.from("store_access_token", null)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        redisService.resetStatus(customStoreDetails.getStore().getStoreAccount());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "로그아웃 성공", 
            null
        ));
    }

    /**
     * 매장 탈퇴 API
     * @return 탈퇴 결과
     */
    @DeleteMapping("/")
    public ResponseEntity<?> leave() {
        storeService.leave();
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "탈퇴 성공", 
            null
        ));
    }

    /**
     * 메뉴 정보 조회 API
     * @param date 조회할 날짜
     * @param meal 조회할 식사 시간
     * @return 메뉴 정보
     */
    @GetMapping("/my/menus")
    public ResponseEntity<?> getMyMenu(@RequestParam String date, String meal) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "메뉴 조회 성공", 
            storeService.getMyMenu(date, meal)
        ));
    }

    /**
     * 일별 전체 매출 합계 조회 API
     * @param date 조회할 날짜
     * @return 일별 매출 합계
     */
    @GetMapping("/bill/day/all/sum")
    public ResponseEntity<?> storeDayAllSum(@RequestParam String date) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "일별 전체 매출 합계 조회 성공", 
            closeSumService.storeDayAllSum(date)
        ));
    }

    /**
     * 일별 전체 매출 합계 조회 API (모바일)
     * @param date 조회할 날짜
     * @return 일별 매출 합계
     */
    @GetMapping("/bill/day/all/sum/mobile")
    public ResponseEntity<?> storeDayAllSumMobile(String date) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "일별 전체 매출 합계 조회 성공", 
            closeSumService.storeDayAllSumMobile(date)
        ));
    }

    /**
     * 월별 전체 매출 합계 조회 API
     * @param month 조회할 월
     * @return 월별 매출 합계
     */
    @GetMapping("/bill/month/all/sum")
    public ResponseEntity<?> storeMonthAllSum(@RequestParam String month) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 전체 매출 합계 조회 성공", 
            closeSumService.storeMonthAllSum(month)
        ));
    }

    /**
     * 월별 전체 매출 합계 조회 API (모바일)
     * @param month 조회할 월
     * @return 월별 매출 합계
     */
    @GetMapping("/bill/month/all/sum/mobile")
    public ResponseEntity<?> storeMonthAllSumMobile(String month) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 전체 매출 합계 조회 성공", 
            closeSumService.storeMonthAllSumMobile(month)
        ));
    }

    /**
     * 연별 전체 매출 합계 조회 API
     * @param year 조회할 연도
     * @return 연별 매출 합계
     */
    @GetMapping("/bill/year/all/sum")
    public ResponseEntity<?> storeYearAllSum(@RequestParam String year) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "연별 전체 매출 합계 조회 성공", 
            closeSumService.storeYearAllSum(year)
        ));
    }

    /**
     * 연별 전체 매출 합계 조회 API (모바일)
     * @param year 조회할 연도
     * @return 연별 매출 합계
     */
    @GetMapping("/bill/year/all/sum/mobile")
    public ResponseEntity<?> storeYearAllSumMobile(String year) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "연별 전체 매출 합계 조회 성공", 
            closeSumService.storeYearAllSumMobile(year)
        ));
    }

    /**
     * 일별 회사별 매출 합계 조회 API
     * @param date 조회할 날짜
     * @return 회사별 일 매출 목록
     */
    @GetMapping("/bill/day/company/total")
    public ResponseEntity<?> storeDayCompanySum(@RequestParam String date) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "일별 회사별 매출 합계 조회 성공", 
            billService.dailyCompanyBillTotalList(date)
        ));
    }

    /**
     * 월별 회사별 매출 합계 조회 API
     * @param month 조회할 월
     * @return 회사별 월 매출 목록
     */
    @GetMapping("/bill/month/company/total")
    public ResponseEntity<?> storeMonthCompanyEachSum(@RequestParam String month) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 회사별 매출 합계 조회 성공", 
            billService.monthlyCompanyBillTotalList(month)
        ));
    }

    /**
     * 일별 회사별 매출 합계 조회 API (모바일)
     * @param date 조회할 날짜
     * @return 회사별 일 매출 목록
     */
    @GetMapping("/bill/day/company/total/mobile")
    public ResponseEntity<?> storeDayCompanySumMobile(String date) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "일별 회사별 매출 합계 조회 성공", 
            billService.dailyCompanyBillTotalList(date)
        ));
    }

    /**
     * 월별 회사별 매출 합계 조회 API (모바일)
     * @param month 조회할 월
     * @return 회사별 월 매출 목록
     */
    @GetMapping("/bill/month/company/total/mobile")
    public ResponseEntity<?> storeMonthCompanyEachSumMobile(String month) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 회사별 매출 합계 조회 성공", 
            billService.monthlyCompanyBillTotalList(month)
        ));
    }
}

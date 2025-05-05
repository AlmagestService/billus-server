package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.security.CustomCompanyDetails;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.company.CompanyResponseDto;
import com.klolarion.billusserver.service.*;
import com.klolarion.billusserver.util.RedisService;
import com.klolarion.billusserver.util.CommonResponseHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a2/v1/company")
public class CompanyControllerV1 {
    private final ApplyService applyService;
    private final CompanyService companyService;
    private final BillService billService;
    private final CloseSumService closeSumService;
    private final RedisService redisService;
    private final EmailVerificationService emailService;
    private final OtpService otpService;

    /**
     * 회사 정보 조회 API
     * @param customCompanyDetails 인증된 회사 정보
     * @return 회사 상세 정보
     */
    @GetMapping("/")
    public ResponseEntity<?> getCompanyInfo(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 정보 조회 성공", 
            companyService.getCompanyInfo(customCompanyDetails.getCompany())
        ));
    }

    /**
     * 회사 정보 수정 API
     * @param companyInitDto 수정할 회사 정보
     * @param customCompanyDetails 인증된 회사 정보
     * @return 수정 결과
     */
    @PutMapping("/")
    public ResponseEntity<?> changeInfo(@RequestBody CompanyResponseDto companyInitDto, @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        companyService.changeInfo(customCompanyDetails.getCompany(), companyInitDto);
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 정보 수정 성공", 
            null
        ));
    }

    /**
     * 회사 비밀번호 변경 API
     * @param passwordDto 비밀번호 변경 정보
     * @param customCompanyDetails 인증된 회사 정보
     * @return 변경 결과
     */
    @PostMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody InfoRequestDto passwordDto, @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        companyService.changePassword(customCompanyDetails.getCompany(), passwordDto);
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "비밀번호 변경 성공", 
            null
        ));
    }

    /**
     * 회사 로그아웃 API
     * @param response HTTP 응답 객체 (쿠키 삭제용)
     * @param customCompanyDetails 인증된 회사 정보
     * @return 로그아웃 결과
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        ResponseCookie cookie = ResponseCookie.from("company_token", null)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        redisService.resetStatus(customCompanyDetails.getCompany().getId() + "_billus_company");
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "로그아웃 성공", 
            null
        ));
    }

    /**
     * 소속 직원 목록 조회 API
     * @param customCompanyDetails 인증된 회사 정보
     * @return 직원 목록
     */
    @GetMapping("/my/employees")
    public ResponseEntity<?> getMyEmployees(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "직원 목록 조회 성공", 
            companyService.findMyEmp(customCompanyDetails.getCompany())
        ));
    }

    /**
     * 직원 가입 신청 목록 조회 API
     * @param customCompanyDetails 인증된 회사 정보
     * @return 가입 신청 직원 목록
     */
    @GetMapping("/employees")
    public ResponseEntity<?> getAppliedEmployees(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "가입 신청 직원 목록 조회 성공", 
            applyService.getAppliedMembers(customCompanyDetails.getCompany())
        ));
    }

    /**
     * 직원 가입 승인 API
     * @param requestDto 승인할 직원 정보
     * @param customCompanyDetails 인증된 회사 정보
     * @return 승인 결과
     */
    @PostMapping("/employee/approve")
    public ResponseEntity<?> approveEmployee(@RequestBody InfoRequestDto requestDto,
                                             @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        applyService.approveMemberFromCompany(requestDto, customCompanyDetails.getCompany());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "직원 가입 승인 성공", 
            null
        ));
    }

    /**
     * 직원 등록 해제 API
     * @param requestDto 해제할 직원 정보
     * @param customCompanyDetails 인증된 회사 정보
     * @return 해제 결과
     */
    @PostMapping("/employee/disable")
    public ResponseEntity<?> disableEmployee(@RequestBody InfoRequestDto requestDto,
                                             @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        applyService.disableMemberFromCompany(requestDto, customCompanyDetails.getCompany());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "직원 등록 해제 성공", 
            null
        ));
    }

    /**
     * 직원 가입 신청 거부 API
     * @param requestDto 거부할 직원 정보
     * @param customCompanyDetails 인증된 회사 정보
     * @return 거부 결과
     */
    @PostMapping("/apply/disable")
    public ResponseEntity<?> disableApply(@RequestBody InfoRequestDto requestDto,
                                          @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        applyService.disableEmployeeApply(requestDto, customCompanyDetails.getCompany());
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "직원 가입 신청 거부 성공", 
            null
        ));
    }

    /**
     * 일별 전체 매출 합계 조회 API
     * @param date 조회할 날짜
     * @param customCompanyDetails 인증된 회사 정보
     * @return 일별 매출 합계
     */
    @GetMapping("/day/all/sum")
    public ResponseEntity<?> companyDayAllSum(@RequestParam String date,
                                              @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "일별 전체 매출 합계 조회 성공", 
            closeSumService.companyDayAllSum(date)
        ));
    }

    /**
     * 월별 전체 매출 합계 조회 API
     * @param month 조회할 월
     * @param customCompanyDetails 인증된 회사 정보
     * @return 월별 매출 합계
     */
    @GetMapping("/month/all/sum")
    public ResponseEntity<?> companyMonthAllSum(@RequestParam String month,
                                                @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 전체 매출 합계 조회 성공", 
            closeSumService.companyMonthAllSum(month)
        ));
    }

    /**
     * 연별 전체 매출 합계 조회 API
     * @param year 조회할 연도
     * @param customCompanyDetails 인증된 회사 정보
     * @return 연별 매출 합계
     */
    @GetMapping("/year/all/sum")
    public ResponseEntity<?> companyYearAllSum(@RequestParam String year,
                                               @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "연별 전체 매출 합계 조회 성공", 
            closeSumService.companyYearAllSum(year)
        ));
    }

    /**
     * 월별 직원별 총 매출 조회 API
     * @param month 조회할 월
     * @param customCompanyDetails 인증된 회사 정보
     * @return 직원별 월 매출 목록
     */
    @GetMapping("/month/employee/total")
    public ResponseEntity<?> companyMonthMemberTotal(@RequestParam String month,
                                                     @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 직원별 총 매출 조회 성공", 
            billService.monthlyEmployeeBillTotal(month)
        ));
    }

    /**
     * 월별 직원 상세 매출 조회 API
     * @param month 조회할 월
     * @param id 직원 ID
     * @return 직원 상세 매출 목록
     */
    @GetMapping("/month/employee/detail")
    public ResponseEntity<?> companyMonthMemberDetail(@RequestParam String month, String id) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 직원 상세 매출 조회 성공", 
            billService.monthlyEmployeeBillList(month, id)
        ));
    }

    /**
     * 월별 매장 상세 매출 조회 API
     * @param month 조회할 월
     * @param id 매장 ID
     * @return 매장 상세 매출 목록
     */
    @GetMapping("/month/store/detail")
    public ResponseEntity<?> companyMonthStoreDetail(@RequestParam String month, String id) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 매장 상세 매출 조회 성공", 
            billService.monthlyStoreBillDetailList(month, id)
        ));
    }

    /**
     * 월별 매장별 총 매출 조회 API
     * @param month 조회할 월
     * @param customCompanyDetails 인증된 회사 정보
     * @return 매장별 월 매출 목록
     */
    @GetMapping("/month/store/total")
    public ResponseEntity<?> companyMonthStoreTotal(@RequestParam String month,
                                                    @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "월별 매장별 총 매출 조회 성공", 
            billService.monthlyStoreBillTotalList(month)
        ));
    }

    /**
     * 일별 매장별 매출 합계 조회 API
     * @param companyId 회사 ID
     * @param date 조회할 날짜
     * @return 매장별 일 매출 목록
     */
    @GetMapping("/company/day/store/sum")
    public ResponseEntity<?> companyDayStoreEachSum(@RequestParam String companyId, String date) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "일별 매장별 매출 합계 조회 성공", 
                closeSumService.companyDayStoreEachSum(date, companyId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "일별 매장별 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 월별 매장별 매출 합계 조회 API
     * @param companyId 회사 ID
     * @param month 조회할 월
     * @return 매장별 월 매출 목록
     */
    @GetMapping("/company/month/store/sum")
    public ResponseEntity<?> companyMonthStoreEachSum(@RequestParam String companyId, String month) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "월별 매장별 매출 합계 조회 성공", 
                closeSumService.companyMonthStoreEachSum(month, companyId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "월별 매장별 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 월별 직원별 매출 합계 조회 API
     * @param companyId 회사 ID
     * @param month 조회할 월
     * @return 직원별 월 매출 목록
     */
    @GetMapping("/company/month/member/sum")
    public ResponseEntity<?> companyMonthMemberEachSum(@RequestParam String companyId, String month) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "월별 직원별 매출 합계 조회 성공", 
                closeSumService.companyMonthMemberEachSum(month, companyId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "월별 직원별 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 연별 매장별 매출 합계 조회 API
     * @param companyId 회사 ID
     * @param year 조회할 연도
     * @return 매장별 연 매출 목록
     */
    @GetMapping("/company/year/store/sum")
    public ResponseEntity<?> companyYearStoreEachSum(@RequestParam String companyId, String year) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "연별 매장별 매출 합계 조회 성공", 
                closeSumService.companyYearStoreEachSum(year, companyId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "연별 매장별 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 연별 직원별 매출 합계 조회 API
     * @param companyId 회사 ID
     * @param year 조회할 연도
     * @return 직원별 연 매출 목록
     */
    @GetMapping("/company/year/member/sum")
    public ResponseEntity<?> companyYearMemberEachSum(@RequestParam String companyId, String year) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "연별 직원별 매출 합계 조회 성공", 
                closeSumService.companyYearMemberEachSum(year, companyId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "연별 직원별 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 직원 월별 전체 매출 합계 조회 API
     * @param memberId 직원 ID
     * @param month 조회할 월
     * @return 직원 월 매출 합계
     */
    @GetMapping("/member/month/all/sum")
    public ResponseEntity<?> memberMonthAllSum(@RequestParam String memberId, String month) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "직원 월별 전체 매출 합계 조회 성공", 
                closeSumService.memberMonthAllSum(month, memberId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "직원 월별 전체 매출 합계 조회 실패", 
                null
            ));
        }
    }

    /**
     * 직원 월별 상세 매출 조회 API
     * @param memberId 직원 ID
     * @param month 조회할 월
     * @return 직원 상세 매출 목록
     */
    @GetMapping("/member/month/detail")
    public ResponseEntity<?> memberMonthDetail(@RequestParam String memberId, String month) {
        try {
            return ResponseEntity.ok(CommonResponseHelper.createResponse(
                "200", 
                "OK", 
                "SUCCESS", 
                "직원 월별 상세 매출 조회 성공", 
                closeSumService.memberMonthDetail(month, memberId)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "직원 월별 상세 매출 조회 실패", 
                null
            ));
        }
    }

    /**
     * 이메일 인증 코드 전송 API
     * @param customCompanyDetails 인증된 회사 정보
     * @return 코드 전송 결과
     */
    @PostMapping("/code/send")
    public ResponseEntity<?> sendEmailCode(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        String code = otpService.generateCompanyOtp(customCompanyDetails.getCompany().getId().toString());
        emailService.send(code, customCompanyDetails.getCompany().getEmail());
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
     * @param customCompanyDetails 인증된 회사 정보
     * @return 코드 확인 결과
     */
    @PostMapping("/code/check")
    public ResponseEntity<?> checkEmailCode(InfoRequestDto otpDto,
                                          @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        emailService.check(otpDto.getCode(), customCompanyDetails.getCompany(), "COMPANY");
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "코드 일치", 
            null
        ));
    }
}

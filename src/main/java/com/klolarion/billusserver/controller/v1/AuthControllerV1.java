package com.klolarion.billusserver.controller.v1;


import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.auth.AuthRequestDto;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.klolarion.billusserver.security.token.TokenService;
import com.klolarion.billusserver.service.StoreService;
import com.klolarion.billusserver.service.*;
import com.klolarion.billusserver.util.CommonResponseHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a1/v1/auth")
public class AuthControllerV1 {

    private final TokenService tokenService;
    private final AuthService authService;
    private final CompanyService companyService;
    private final StoreService storeService;
    private final MemberService memberService;


    /**
     * 서비스 헬스체크
     * */
    @GetMapping("/health")
    public ResponseEntity<?> awsHealthCheck() {
        return ResponseEntity.ok("Health check success");
    }

    /**
     * 계정 중복 확인 (매장/회사)
     */
    @PostMapping("/look/account")
    public ResponseEntity<?> lookAccount(@RequestBody AuthRequestDto requestDto) {
        if (requestDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        if (requestDto.getUserRole().equals("STORE")) {
            authService.validateDuplicateAccount(requestDto);
        } else if (requestDto.getUserRole().equals("COMPANY")) {
            authService.validateDuplicateAccount(requestDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 계정입니다.", 
            null
        ));
    }

    /**
     * 전화번호 중복 확인 (매장/회사)
     */
    @PostMapping("/look/tel")
    public ResponseEntity<?> lookTel(@RequestBody AuthRequestDto requestDto) {
        if (requestDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        if (requestDto.getUserRole().equals("STORE")) {
            authService.validateDuplicateTel(requestDto);
        } else if (requestDto.getUserRole().equals("COMPANY")) {
            authService.validateDuplicateTel(requestDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 전화번호입니다.", 
            null
        ));
    }

    /**
     * 이메일 중복 확인 (매장/회사)
     */
    @PostMapping("/look/email")
    public ResponseEntity<?> lookEmail(@RequestBody AuthRequestDto requestDto) {
        if (requestDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        if (requestDto.getUserRole().equals("STORE")) {
            authService.validateDuplicateEmail(requestDto);
        } else if (requestDto.getUserRole().equals("COMPANY")) {
            authService.validateDuplicateEmail(requestDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 이메일입니다.", 
            null
        ));
    }

    /**
     * 사업자번호 중복 확인
     */
    @PostMapping("/look/biznum")
    public ResponseEntity<?> lookBizNum(@RequestBody AuthRequestDto requestDto,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if (requestDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        if (requestDto.getUserRole().equals("STORE") || requestDto.getUserRole().equals("COMPANY")) {
            authService.validateBizNum(requestDto, request, response);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "사용 가능한 사업자번호입니다.", 
            null
        ));
    }

    /**
     * 회원 등록 API
     * @param requestDto 회원 등록에 필요한 정보 (이름, 이메일, 전화번호 등)
     * @return 등록된 회원 정보
     */
    @PostMapping("/member/new")
    public ResponseEntity<?> almagestRegister(@RequestBody AuthRequestDto requestDto) {
        Member member = authService.memberRegister(requestDto);
        MemberResponseDto memberResponseDto = new MemberResponseDto();

        memberResponseDto.setMemberId(member.getId().toString());
        memberResponseDto.setAlmagestId(member.getAlmagestId());
        memberResponseDto.setEmail(member.getEmail());
        memberResponseDto.setTel(member.getTel());
        memberResponseDto.setMemberName(member.getMemberName());
        memberResponseDto.setLastUpdateDate(member.getLastUpdateDate().toString());
        memberResponseDto.setCompanyId(String.valueOf(member.getCompany().getId()));
        memberResponseDto.setCompanyName(member.getCompany().getCompanyName());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회원 등록 완료", 
            memberResponseDto
        ));
    }

    /**
     * 매장 등록 API
     * @param requestDto 매장 등록에 필요한 정보 (매장명, 사업자번호, 이메일 등)
     * @return 등록 결과 및 토큰
     */
    @PostMapping("/store/new")
    public ResponseEntity<?> storeRegister(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        Store store = authService.storeRegister(requestDto);


        String accessToken = tokenService.generateStoreAccessToken(store.getId().toString());
        String refreshToken = tokenService.generateStoreRefreshToken(store.getId().toString());

        ResponseCookie accessCookie = ResponseCookie.from("store_access_token", accessToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(600)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("store_refresh_token", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(2592000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "매장 등록 완료", 
            null
        ));
    }

    /**
     * 회사 등록 API
     * @param requestDto 회사 등록에 필요한 정보 (회사명, 사업자번호, 이메일 등)
     * @return 등록 결과 및 토큰
     */
    @PostMapping("/company/new")
    public ResponseEntity<?> companyRegister(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        Company company = authService.companyRegister(requestDto);

        String accessToken = tokenService.generateCompanyAccessToken(company.getId().toString());
        String refreshToken = tokenService.generateCompanyRefreshToken(company.getId().toString());
        
        ResponseCookie accessCookie = ResponseCookie.from("company_access_token", accessToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(600)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("company_refresh_token", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(2592000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 등록 완료", 
            null
        ));
    }


    /**
     * 관리자 로그인 API
     * @param requestDto 로그인에 필요한 정보 (아이디, 비밀번호)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 로그인 결과
     */
    @PostMapping("/login/admin")
    public ResponseEntity<?> adminLogin(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        Member member = authService.adminLogin(requestDto);
        String token = tokenService.generateAdminAccessToken(member.getId().toString());

        ResponseCookie cookie = ResponseCookie.from("admin_access_token", token)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(3600)
                .build();
                
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "관리자 로그인 성공", 
            null
        ));
    }

    /**
     * 매장 로그인 API
     * @param requestDto 로그인에 필요한 정보 (아이디, 비밀번호, FCM 토큰)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 로그인 결과
     */
    @PostMapping("/login/store")
    public ResponseEntity<?> storeLogin(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        Store store = authService.storeLogin(requestDto);

        if(requestDto.getFirebaseToken() != null && !requestDto.getFirebaseToken().isEmpty()) {
            storeService.initFCM(requestDto.getFirebaseToken(), store);
        }

        String accessToken = tokenService.generateStoreAccessToken(store.getId().toString());
        String refreshToken = tokenService.generateStoreRefreshToken(store.getId().toString());

        ResponseCookie accessCookie = ResponseCookie.from("store_access_token", accessToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(600)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("store_refresh_token", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(2592000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "매장 로그인 성공", 
            null
        ));
    }

    /**
     * 회사 로그인 API
     * @param requestDto 로그인에 필요한 정보 (아이디, 비밀번호)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 로그인 결과
     */
    @PostMapping("/login/company")
    public ResponseEntity<?> companyLogin(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        Company company = authService.companyLogin(requestDto);

        String accessToken = tokenService.generateCompanyAccessToken(company.getId().toString());
        String refreshToken = tokenService.generateCompanyRefreshToken(company.getId().toString());

        ResponseCookie accessCookie = ResponseCookie.from("company_access_token", accessToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(600)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("company_refresh_token", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(2592000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "회사 로그인 성공", 
            null
        ));
    }

    /**
     * 계정 찾기 (매장/회사)
     */
    @PostMapping("/find/account")
    public ResponseEntity<?> findAccount(@RequestBody InfoRequestDto findDto) {
        if (findDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        String result;
        if (findDto.getUserRole().equals("STORE")) {
            result = storeService.findAccount(findDto);
        } else if (findDto.getUserRole().equals("COMPANY")) {
            result = companyService.findAccount(findDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        InfoResponseDto infoResponseDto = new InfoResponseDto();
        infoResponseDto.setAccount(result);

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "계정이 조회되었습니다.", 
            infoResponseDto
        ));
    }

    /**
     * 이메일 찾기 (매장/회사)
     */
    @PostMapping("/find/email")
    public ResponseEntity<?> findEmail(@RequestBody InfoRequestDto findDto) {
        if (findDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        String result;
        if (findDto.getUserRole().equals("STORE")) {
            result = storeService.findEmail(findDto);
        } else if (findDto.getUserRole().equals("COMPANY")) {
            result = companyService.findEmail(findDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        InfoResponseDto infoResponseDto = new InfoResponseDto();
        infoResponseDto.setAccount(result);

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "이메일이 조회되었습니다.", 
            infoResponseDto
        ));
    }

    /**
     * 비밀번호 초기화 (매장/회사)
     */
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody InfoRequestDto findDto) {
        if (findDto.getUserRole() == null) {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "사용자 유형(매장/회사)을 선택해주세요.", 
                null
            ));
        }

        if (findDto.getUserRole().equals("STORE")) {
            storeService.resetPassword(findDto);
        } else if (findDto.getUserRole().equals("COMPANY")) {
            companyService.resetPassword(findDto);
        } else {
            return ResponseEntity.badRequest().body(CommonResponseHelper.createResponse(
                "400", 
                "Bad Request", 
                "ERROR", 
                "지원하지 않는 사용자 유형입니다. 매장 또는 회사를 선택해주세요.", 
                null
            ));
        }

        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "비밀번호가 초기화되었습니다. 이메일로 임시 비밀번호가 발송되었습니다.", 
            null
        ));
    }

    /**
     * 매장 검색 API
     * @param storeSearchDto 검색 조건 (매장명, 지역 등)
     * @return 검색된 매장 목록
     */
    @PostMapping("/stores")
    public ResponseEntity<?> searchStore(@RequestBody InfoRequestDto storeSearchDto) {
        return ResponseEntity.ok(CommonResponseHelper.createResponse(
            "200", 
            "OK", 
            "SUCCESS", 
            "매장 검색 성공.", 
            memberService.searchStore(storeSearchDto)
        ));
    }
}

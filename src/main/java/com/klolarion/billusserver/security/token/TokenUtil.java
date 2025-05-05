package com.klolarion.billusserver.security.token;

import com.klolarion.billusserver.exception.r401.InvalidTokenException;
import com.klolarion.billusserver.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.klolarion.billusserver.util.constants.ExternalURL.ALMAGEST_AUTH_SERVER_RENEW_URL;
import static com.klolarion.billusserver.util.constants.ExternalURL.ALMAGEST_LOGIN_PAGE_URL;


@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final RestTemplate restTemplate;
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;
    /**
     * ✅ 공통 토큰 추출 메서드 (쿠키에서 토큰 추출)
     */
    private Tokens extractTokensFromRequest(HttpServletRequest request, String accessTokenName, String refreshTokenName) {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (accessTokenName.equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if (refreshTokenName != null && refreshTokenName.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        return new Tokens(accessToken, refreshToken);
    }

    /**
     * ✅ 개별 사용자 유형별 메서드 (중복 제거)
     */
    public Tokens extractAdminTokens(HttpServletRequest request) {
        return extractTokensFromRequest(request, "admin_access_token", null);
    }

    public Tokens extractStoreTokens(HttpServletRequest request) {
        return extractTokensFromRequest(request, "store_access_token", "store_refresh_token");
    }

    public Tokens extractCompanyTokens(HttpServletRequest request) {
        return extractTokensFromRequest(request, "company_access_token", "company_refresh_token");
    }

    public Tokens extractMemberTokens(HttpServletRequest request) {
        return extractTokensFromRequest(request, "access_token", "refresh_token");
    }


    /**
     * 토큰 추출 : from Response ( Member의 인증 갱신 )
     */
    public Tokens extractCookiesFromResponse(ResponseEntity<String> tokenResponse) {
        // HttpHeaders에서 Set-Cookie 헤더 가져오기
        HttpHeaders headers = tokenResponse.getHeaders();
        List<String> setCookieHeaders = headers.get(HttpHeaders.SET_COOKIE);

        if (setCookieHeaders != null) {
            String accessToken = null;
            String refreshToken = null;

            // Set-Cookie 헤더에서 access_token과 refresh_token 추출
            for (String cookie : setCookieHeaders) {
                if (cookie.startsWith("access_token=")) {
                    accessToken = cookie.split(";")[0].split("=")[1]; // access_token 값 추출
                } else if (cookie.startsWith("refresh_token=")) {
                    refreshToken = cookie.split(";")[0].split("=")[1]; // refresh_token 값 추출
                }
            }

            return new Tokens(accessToken, refreshToken);
        } else {
            System.out.println("No cookies found in the response.");
        }
        return null;
    }

    /**
     * 인증토큰 갱신 요청
     */
    public void handleRefreshToken(String refreshToken, HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "refresh_token=" + refreshToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<String> tokenResponse = restTemplate.exchange(
                ALMAGEST_AUTH_SERVER_RENEW_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 토큰 추출
        Tokens tokens = extractCookiesFromResponse(tokenResponse);

        if(tokens == null){
            throw new InvalidTokenException("인증 실패");
        }

        String newAccessToken = tokens.getAccessToken();



        if (tokenResponse.getStatusCode().is2xxSuccessful()) {
            if (newAccessToken != null) {

                authenticateUser(newAccessToken, request);

                filterChain.doFilter(request, response);

            } else {
                redirectToLogin(response, request);
            }
        } else {
            redirectToLogin(response, request);
        }
    }

    /**
     * 사용자 인증 처리 ( Member )
     */
    public void authenticateUser(String accessToken, HttpServletRequest request) {
        String almagestId = tokenService.extractAlmagestSub(accessToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(almagestId);


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 사용자 인증 처리 ( Admin )
     */
    public void authenticateAdmin(String accessToken, HttpServletRequest request) {
        String adminId = tokenService.extractBillusSub(accessToken);

        UserDetails userDetails = customUserDetailsService.loadAdminById(adminId);


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 사용자 인증 처리 ( Store )
     */
    public void authenticateStore(String accessToken, HttpServletRequest request) {
        String storeId = tokenService.extractBillusSub(accessToken);

        UserDetails userDetails = customUserDetailsService.loadStoreById(storeId);


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 사용자 인증 처리 ( Company )
     */
    public void authenticateCompany(String accessToken, HttpServletRequest request) {
        String companyId = tokenService.extractBillusSub(accessToken);

        UserDetails userDetails = customUserDetailsService.loadCompanyById(companyId);


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 로그인 페이지로 Redirect
     */
    public void redirectToLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.sendRedirect(ALMAGEST_LOGIN_PAGE_URL + "?redirect_uri=" + URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8));
    }

}

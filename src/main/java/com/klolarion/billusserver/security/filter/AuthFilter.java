package com.klolarion.billusserver.security.filter;

import com.klolarion.billusserver.security.AuthUserDetails;
import com.klolarion.billusserver.exception.r401.InvalidTokenException;
import com.klolarion.billusserver.exception.r404.ResourceNotFoundException;
import com.klolarion.billusserver.security.CustomUserDetailsService;
import com.klolarion.billusserver.security.token.TokenService;
import com.klolarion.billusserver.security.token.TokenUtil;
import com.klolarion.billusserver.security.token.Tokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.http.server.PathContainer;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.PostConstruct;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final TokenUtil tokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    
    // 권한별 경로 패턴 정의
    private final Map<String, PathPattern> rolePatterns = new HashMap<>();
    
    @PostConstruct
    public void init() {
        PathPatternParser parser = new PathPatternParser();
        // 관리자 경로
        rolePatterns.put("Admin", parser.parse("/api/a0/**"));
        // 인증 불필요 경로
        rolePatterns.put("Public", parser.parse("/api/a1/**"));
        // 일반 사용자 경로
        rolePatterns.put("Member", parser.parse("/api/a2/member/**"));
        // 매장 경로
        rolePatterns.put("Store", parser.parse("/api/a2/store/**"));
        // 회사 경로
        rolePatterns.put("Company", parser.parse("/api/a2/company/**"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        PathContainer pathContainer = PathContainer.parsePath(path);

        // 인증 불필요 경로 체크
        if (rolePatterns.get("Public").matches(pathContainer)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 권한별 인증 처리
            if (rolePatterns.get("Admin").matches(pathContainer)) {
                authenticateAdmin(request);
            } else if (rolePatterns.get("Member").matches(pathContainer)) {
                authenticateMember(request, response, filterChain);
                return;
            } else if (rolePatterns.get("Store").matches(pathContainer)) {
                authenticateWithRefreshToken(request, response, "Store");
            } else if (rolePatterns.get("Company").matches(pathContainer)) {
                authenticateWithRefreshToken(request, response, "Company");
            } else {
                throw new ResourceNotFoundException("잘못된 경로입니다.");
            }

            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            log.error("❌ 인증 실패 - 경로: {}, 메시지: {}", path, e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    // ✅ Admin 인증 처리 (Access Token 검증, 만료 시 재로그인 필요)
    private void authenticateAdmin(HttpServletRequest request) {
        Tokens tokens = tokenUtil.extractAdminTokens(request);

        if (tokens.getAccessToken() == null || !tokenService.validateAdminAccessToken(tokens.getAccessToken())) {
            throw new InvalidTokenException("인증이 만료되었습니다. 다시 로그인하세요.");
        }

        authenticateUserByToken(request, tokens.getAccessToken(), "Admin");
    }

    // ✅ Member 인증 처리 (Access Token 검증 및 필요 시 인증 서버에 갱신 요청)
    private void authenticateMember(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Tokens tokens = tokenUtil.extractMemberTokens(request);

        if (tokens.getAccessToken() == null) {
            throw new InvalidTokenException("Member 인증 토큰이 없습니다.");
        }

        if (!tokenService.validateMemberAccessToken(tokens.getAccessToken())) {
            if (tokens.getRefreshToken() != null) {
                // 인증 서버에 요청하여 새 Access Token 받기
                tokenUtil.handleRefreshToken(tokens.getRefreshToken(), request, response, filterChain);
            } else {
                throw new InvalidTokenException("인증이 만료되었습니다. 다시 로그인하세요.");
            }
        } else {
            authenticateUserByToken(request, tokens.getAccessToken(), "Member");
        }
    }

    // ✅ Store & Company 인증 처리 (자체 Refresh Token으로 갱신)
    private void authenticateWithRefreshToken(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException, ServletException {
        Tokens tokens = role.equals("Store") ? tokenUtil.extractStoreTokens(request) : tokenUtil.extractCompanyTokens(request);

        if (tokens.getAccessToken() == null) {
            throw new InvalidTokenException(role + " 인증 토큰이 없습니다.");
        }

        if (!tokenService.validateBillusAccessToken(tokens.getAccessToken(), role)) {
            if (tokens.getRefreshToken() != null) {
                // 자체 인증 서버에서 Access Token 갱신
                String newAccessToken = tokenService.refreshAccessToken(tokens.getRefreshToken(), role);
                authenticateUserByToken(request, newAccessToken, role);
            } else {
                throw new InvalidTokenException("인증이 만료되었습니다. 다시 로그인하세요.");
            }
        } else {
            authenticateUserByToken(request, tokens.getAccessToken(), role);
        }
    }


    // ✅ 사용자 정보 조회 및 보안 컨텍스트 설정
    private void authenticateUserByToken(HttpServletRequest request, String token, String role) {
        String userId = tokenService.extractBillusSub(token);
        AuthUserDetails userDetails = loadUserByRole(userId, role);
        validateAndAuthenticateToken(request, userDetails);
    }

    // ✅ 역할별 사용자 정보 조회
    private AuthUserDetails loadUserByRole(String userId, String role) {
        return switch (role) {
            case "Admin" -> customUserDetailsService.loadAdminById(userId);
            case "Member" -> customUserDetailsService.loadUserByUsername(userId);
            case "Store" -> customUserDetailsService.loadStoreById(userId);
            case "Company" -> customUserDetailsService.loadCompanyById(userId);
            default -> throw new IllegalArgumentException("지원하지 않는 역할입니다: " + role);
        };
    }

    // ✅ 토큰 검증 및 보안 컨텍스트 설정
    private void validateAndAuthenticateToken(HttpServletRequest request, AuthUserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}

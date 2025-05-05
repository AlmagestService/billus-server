package com.klolarion.billusserver.security.token;

import com.klolarion.billusserver.exception.r401.InvalidTokenException;
import com.klolarion.billusserver.exception.r500.CodeGenerationException;
import com.klolarion.billusserver.util.GenerateCodeUtil;
import com.klolarion.billusserver.util.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final RedisService redisService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.public-key}")
    private String publicKey;

    @Value("${jwt.access-exp}")
    private long accessExpiration;

    @Value("${jwt.refresh-exp}")
    private long refreshExpiration;

    private final String ALMAGEST_ISSUER = "https://almagest.io";
    private final String BILL_US_ISSUER = "https://almagest.io/bill-us";

    /**
     * 🔹 **Almagest Member** 공개 키 복원
     */
    private Key toPublicKey() {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("🔴 공개 키 변환 중 오류 발생", e);
            throw new JwtException("토큰 검증 오류");
        }
    }

    /**
     * 🔹 **Secret Key (Bill-us) 변환**
     */
    private Key toSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey); // HMAC SHA256 키 생성
    }

    /**
     * ✅ **공개 키로 토큰 검증 (Almagest Member)**
     */
    private Claims getMemberClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(toPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 토큰에서 Almagest sub식별자 추출
     */
    public String extractAlmagestSub(String token) {
        return getMemberClaims(token).getSubject();
    }


    /**
     * JWT 토큰에서 Bill-us sub식별자 추출
     */
    public String extractBillusSub(String token) {
        return getBillusClaims(token).getSubject();
    }


    /**
     * ✅ **비밀 키로 토큰 검증 (Bill-us Admin, Store, Company)**
     */
    private Claims getBillusClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(toSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ **Member Access Token 검증 (Almagest)**
     * - 공개 키 사용
     */
    public boolean validateMemberAccessToken(String token) {
        try {
            Claims claims = getMemberClaims(token);
            return claims.getIssuer().equals(ALMAGEST_ISSUER) && claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            log.debug("🔴 Invalid Member Token");
            return false;
        }
    }

    /**
     * ✅ **Admin Access Token 생성 & 검증 (Bill-us)**
     * - 비밀 키 사용
     */
    public String generateAdminAccessToken(String adminId) {
        return generateAccessToken(adminId, "Admin", accessExpiration);
    }

    public boolean validateAdminAccessToken(String token) {
        try {
            Claims claims = getBillusClaims(token);
            return claims.getIssuer().equals(BILL_US_ISSUER) && claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            log.debug("🔴 Invalid Admin Token");
            throw new InvalidTokenException("Admin 인증 토큰이 유효하지 않습니다.");
        }
    }

    /**
     * ✅ **Store Access Token 생성 & 검증**
     */
    public String generateStoreAccessToken(String storeId) {
        return generateAccessToken(storeId, "Store", accessExpiration);
    }


    /**
     * ✅ **Company Access Token 생성 & 검증**
     */
    public String generateCompanyAccessToken(String companyId) {
        return generateAccessToken(companyId, "Company", accessExpiration);
    }


    /**
     * ✅ **Billus Access Token 검증 (Store, Company)**
     * - 역할(role)에 따라 Store 또는 Company의 Access Token을 검증
     */
    public boolean validateBillusAccessToken(String token, String role) {
        try {
            Claims claims = getBillusClaims(token);
            return claims.getIssuer().equals(BILL_US_ISSUER)
                    && claims.getExpiration().after(new Date())
                    && claims.getAudience().equals(role); // 역할도 검증
        } catch (JwtException e) {
            log.debug("🔴 Invalid {} Token", role);
            return false;
        }
    }


    /**
     * ✅ **Refresh Token을 사용해 Access Token 갱신**
     * @param refreshToken 사용자의 Refresh Token
     * @param role 사용자 역할 (Store 또는 Company)
     * @return 새로 생성된 Access Token
     */
    public String refreshAccessToken(String refreshToken, String role) {
        try {
            Claims claims = getBillusClaims(refreshToken);
            String userId = claims.getSubject();

            // 🔹 Redis에서 Refresh Token 검증 문자열 조회
            String storedVerifyString = redisService.getRefreshTokenVerification(userId);

            if (storedVerifyString == null || !storedVerifyString.equals(claims.get("vfs"))) {
                throw new InvalidTokenException("Refresh Token이 유효하지 않습니다.");
            }

            // 🔹 새로운 Access Token 발급
            return generateAccessToken(userId, role, accessExpiration);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
        } catch (JwtException e) {
            throw new InvalidTokenException("Refresh Token 검증 실패");
        }
    }

    /**
     * ✅ **공통 Access Token 생성**
     */
    private String generateAccessToken(String id, String audience, long expiration) {
        return Jwts.builder()
                .setIssuer(BILL_US_ISSUER)
                .setAudience(audience)
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * ✅ **Store Refresh Token 생성**
     */
    public String generateStoreRefreshToken(String storeId) {
        return generateRefreshToken(storeId, "Store");
    }

    /**
     * ✅ **Company Refresh Token 생성**
     */
    public String generateCompanyRefreshToken(String companyId) {
        return generateRefreshToken(companyId, "Company");
    }

    /**
     * ✅ Refresh 토큰 생성 (추가 Claims 포함)
     * Redis에 검증 문자열 저장 후 생성.
     * @param userId 사용자 식별자
     * @param audience 대상 (Admin, Store, Company 등)
     * @return 생성된 Refresh 토큰
     */
    public String generateRefreshToken(String userId, String audience) {
        try {
            // 토큰 검증용 문자열 생성
            String verifyString = GenerateCodeUtil.generateTokenVerifyString();

            // Redis에 검증용 문자열 저장
            redisService.setRefreshTokenVerification(
                    userId, verifyString, refreshExpiration
            );

            return buildRefreshToken(userId, audience, refreshExpiration, verifyString);
        } catch (Exception e) {
            log.error("🔴 Refresh 토큰 생성 실패: userId={}", userId, e);
            throw new CodeGenerationException("인증 과정에서 오류가 발생했습니다.");
        }
    }

    /**
     * ✅ Refresh 토큰 빌드 (HS256 서명)
     * 검증 문자열을 Claim에 추가.
     * @param userId 매장/회사 ID
     * @param audience 대상 (Admin, Store, Company)
     * @param refreshExpiration 만료 시간
     * @param verifyString 검증 문자열
     * @return 생성된 Refresh 토큰
     */
    private String buildRefreshToken(String userId, String audience, long refreshExpiration, String verifyString) {
        return Jwts.builder()
                .setIssuer(BILL_US_ISSUER) // 발행 주체
                .setAudience(audience) // 토큰 대상 (Store, Company)
                .setSubject(userId) // 매장/회사 ID
                .setIssuedAt(new Date(System.currentTimeMillis())) // 현재 시간
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration)) // 만료 시간
                .claim("vfs", verifyString) // 검증용 문자열 추가
                .signWith(toSecretKey(), SignatureAlgorithm.HS256) // HS256 서명
                .compact();
    }
}

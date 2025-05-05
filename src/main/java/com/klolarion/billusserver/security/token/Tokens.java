package com.klolarion.billusserver.security.token;


/**
 * 토큰 저장 클래스
 */
public class Tokens {
    private final String accessToken;
    private final String refreshToken;

    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}

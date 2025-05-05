package com.klolarion.billusserver.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Getter
@Configuration
public class SecretsConfig {
    
    // DB 관련
    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    // 메일 관련
    private final String mailHost;
    private final String mailPort;
    private final String mailUser;
    private final String mailPassword;

    // JPA 관련
    private final String ddlType;

    // Redis 관련
    private final String redisHost;
    private final String redisPort;

    // JWT 관련
    private final String jwtSecretKey;
    private final String jwtPublicKey;
    private final String jwtAccessExp;
    private final String jwtRefreshExp;

    // AES 관련
    private final String aesKey;

    // 서버 관련
    private final String serverPort;

    // AWS 관련
    private final String firebaseConfig;

    // 사업자번호 확인 공공 API
    private final String dataOrgApiKey;

    public SecretsConfig(Map<String, String> secrets) {
        // DB 관련
        this.dbHost = secrets.get("DB_HOST");
        this.dbPort = secrets.get("DB_PORT");
        this.dbName = secrets.get("BILLUS_DB_NAME");
        this.dbUser = secrets.get("DB_USER");
        this.dbPassword = secrets.get("DB_PASS");

        // 메일 관련
        this.mailHost = secrets.get("BILLUS_MAIL_HOST");
        this.mailPort = secrets.get("BILLUS_MAIL_PORT");
        this.mailUser = secrets.get("BILLUS_MAIL_USER");
        this.mailPassword = secrets.get("BILLUS_MAIL_KEY");

        // JPA 관련
        this.ddlType = secrets.get("DDL_TYPE");

        // Redis 관련
        this.redisHost = secrets.get("REDIS_HOST");
        this.redisPort = secrets.get("REDIS_PORT");

        // JWT 관련
        this.jwtSecretKey = secrets.get("BILLUS_JWT_KEY");
        this.jwtPublicKey = secrets.get("JWT_PUBLIC_KEY");
        this.jwtAccessExp = secrets.get("JWT_ACCESS_EXP");
        this.jwtRefreshExp = secrets.get("JWT_REFRESH_EXP");

        // AES 관련
        this.aesKey = secrets.get("AES_KEY");

        // 서버 관련
        this.serverPort = secrets.get("BILLUS_SERVER_PORT");

        // AWS 관련
        this.firebaseConfig = secrets.get("BILLUS_FCM_KEY");

        // 사업자번호 확인 공공 API
        this.dataOrgApiKey = secrets.get("DATA_ORG_API_KEY");
    }

    public void validateSecrets() {
        // DB 관련 검증
        if (dbHost == null) throw new IllegalStateException("DB host is missing");
        if (dbPort == null) throw new IllegalStateException("DB port is missing");
        if (dbName == null) throw new IllegalStateException("DB name is missing");
        if (dbUser == null) throw new IllegalStateException("DB user is missing");
        if (dbPassword == null) throw new IllegalStateException("DB password is missing");

        // 메일 관련 검증
        if (mailHost == null) throw new IllegalStateException("Mail host is missing");
        if (mailPort == null) throw new IllegalStateException("Mail port is missing");
        if (mailUser == null) throw new IllegalStateException("Mail user is missing");
        if (mailPassword == null) throw new IllegalStateException("Mail password is missing");

        // JPA 관련 검증
        if (ddlType == null) throw new IllegalStateException("DDL type is missing");

        // Redis 관련 검증
        if (redisHost == null) throw new IllegalStateException("Redis host is missing");
        if (redisPort == null) throw new IllegalStateException("Redis port is missing");

        // JWT 관련 검증
        if (jwtSecretKey == null) throw new IllegalStateException("JWT secret key is missing");
        if (jwtPublicKey == null) throw new IllegalStateException("JWT public key is missing");
        if (jwtAccessExp == null) throw new IllegalStateException("JWT access expiration is missing");
        if (jwtRefreshExp == null) throw new IllegalStateException("JWT refresh expiration is missing");

        // AES 관련 검증
        if (aesKey == null) throw new IllegalStateException("AES key is missing");

        // 서버 관련 검증
        if (serverPort == null) throw new IllegalStateException("Server port is missing");

        // AWS 관련 검증
        if (firebaseConfig == null) throw new IllegalStateException("Firebase config is missing");

        // 사업자번호 확인 공공 API
        if (dataOrgApiKey == null) throw new IllegalStateException("Data org api key is missing");
    }
} 
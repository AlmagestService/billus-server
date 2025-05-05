package com.klolarion.billusserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecretsManagerConfig {

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public Map<String, String> secrets(SecretsManagerClient client) {
        Map<String, String> secrets = new HashMap<>();
        
        // 시크릿 이름 목록
        String[] secretNames = {
            // DB 관련
            "DB_HOST",
            "DB_PORT",
            "BILLUS_DB_NAME",
            "DB_USER",
            "DB_PASS",
            
            // 메일 관련
            "BILLUS_MAIL_HOST",
            "BILLUS_MAIL_PORT",
            "BILLUS_MAIL_USER",
            "BILLUS_MAIL_KEY",
            
            // JPA 관련
            "DDL_TYPE",
            
            // Redis 관련
            "REDIS_HOST",
            "REDIS_PORT",
            
            // JWT 관련
            "BILLUS_JWT_KEY",
            "JWT_PUBLIC_KEY",
            "JWT_ACCESS_EXP",
            "JWT_REFRESH_EXP",
            
            // AES 관련
            "AES_KEY",
            
            // 서버 관련
            "BILLUS_SERVER_PORT",
            
            // AWS 관련
            "BILLUS_FCM_KEY",

            //사업자번호 확인 공공 API
            "DATA_ORG_API_KEY"
        };

        // 각 시크릿 가져오기
        for (String secretName : secretNames) {
            try {
                GetSecretValueRequest request = GetSecretValueRequest.builder()
                        .secretId(secretName)
                        .build();

                GetSecretValueResponse response = client.getSecretValue(request);
                secrets.put(secretName, response.secretString());
            } catch (Exception e) {
                System.err.println("Failed to get secret: " + secretName + ", Error: " + e.getMessage());
            }
        }

        return secrets;
    }

    @Bean
    public String getSecret(String secretName, SecretsManagerClient client) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            return response.secretString();
        } catch (Exception e) {
            System.err.println("Failed to get secret: " + secretName + ", Error: " + e.getMessage());
            return null;
        }
    }
} 
package com.klolarion.billusserver.config;//package org.almagestauth.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class S3Config {
//
//    @Value("${aws.s3.region}")
//    private String awsRegion;
//    @Value("${aws.s3.access-key}")
//    private String accessKey;
//    @Value("${aws.s3.secret-key}")
//    private String secretKey;
//
//    @Bean
//    public S3Client s3Client() {
//        return S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(accessKey, secretKey)
//                ))
//                .region(Region.of(awsRegion))
//                .build();
//    }
//}
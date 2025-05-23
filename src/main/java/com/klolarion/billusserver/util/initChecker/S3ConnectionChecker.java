//package org.almagestauth.utils.initchecker;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
//
//@Component
//@Slf4j
//public class S3ConnectionChecker implements ApplicationListener<ApplicationReadyEvent> {
//
//    private final S3Client s3Client;
//
//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;
//
//    public S3ConnectionChecker(S3Client s3Client) {
//        this.s3Client = s3Client;
//    }
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        try {
//            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
//            log.info("S3 connected successfully!: {}", bucketName);
//        } catch (Exception e) {
//            log.error("Failed to connect to S3: {}", bucketName, e);
//        }
//    }
//}
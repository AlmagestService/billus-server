package com.klolarion.billusserver.util;

import com.klolarion.billusserver.exception.r500.CodeGenerationException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

//Otp코드 생성 클래스
@Component
@NoArgsConstructor
public final class GenerateCodeUtil {


    //otp코드 생성
    public static String generateOtpCode() {
        String code;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();

            int c = random.nextInt(9000) + 1000;
            code = String.valueOf(c);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Problem when generating the random code");
        }
        return code;
    }

    //임의 비밀번호 생성
    public static String generateRandomPassword() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String randomPassword = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return randomPassword;
    }

    public static String generateTokenVerifyString() {
        try {
            int tokenLength = 32;
            byte[] randomBytes = new byte[tokenLength];
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(randomBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new CodeGenerationException("토큰 생성 중 오류가 발생했습니다");
        }
    }
}

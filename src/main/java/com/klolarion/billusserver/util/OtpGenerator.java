package com.klolarion.billusserver.util;

import com.klolarion.billusserver.domain.entity.Otp;
import com.klolarion.billusserver.domain.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OtpGenerator {
    private final OtpRepository otpRepository;

    public void generateOtp(String email){
        String code = GenerateCodeUtil.generateOtpCode();
        Otp otp = new Otp();
        otp.setCode(code);
        LocalDateTime now = LocalDateTime.now();
        otp.setCreatedTime(now);
        otp.setExpireTime(now.plusMinutes(10));
        otp.setUsed(false);
        otpRepository.save(otp); //otp 저장
    }
}

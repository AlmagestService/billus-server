package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.QOtp;
import com.klolarion.billusserver.domain.entity.*;
import com.klolarion.billusserver.util.GenerateCodeUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {
    private final JPAQueryFactory query;
    private final QOtp qOtp = QOtp.otp;
    private final QStore qStore = QStore.store;
    private final QCompany qCompany = QCompany.company;

    /**
     * 매장 이메일 인증용 OTP 생성
     */
    public String generateStoreOtp(String storeId) {
        UUID id = UUID.fromString(storeId);
        Store store = query.selectFrom(qStore)
                         .where(qStore.id.eq(id))
                         .fetchOne();
        
        if (store == null) {
            throw new UsernameNotFoundException("매장 정보를 찾을 수 없습니다.");
        }

        // OTP 생성 후 저장
        generateOtp("STORE", id.toString());

        // 저장된 OTP 조회 검증
        Otp otp = query.selectFrom(qOtp)
                      .where(qOtp.id.eq(id.toString())
                            .and(qOtp.targetType.eq("STORE"))
                            .and(qOtp.isUsed.eq("F"))
                            .and(qOtp.expireTime.gt(LocalDateTime.now())))
                      .fetchOne();
        
        if (otp == null) {
            throw new UsernameNotFoundException("OTP 조회 실패");
        }
        
        return otp.getCode();
    }

    /**
     * 회사 이메일 인증용 OTP 생성
     */
    public String generateCompanyOtp(String companyId) {
        UUID id = UUID.fromString(companyId);
        Company company = query.selectFrom(qCompany)
                            .where(qCompany.id.eq(id))
                            .fetchOne();
        
        if (company == null) {
            throw new UsernameNotFoundException("회사 정보를 찾을 수 없습니다.");
        }

        // OTP 생성 후 저장
        generateOtp("COMPANY", id.toString());

        // 저장된 OTP 조회 검증
        Otp otp = query.selectFrom(qOtp)
                      .where(qOtp.id.eq(id.toString())
                            .and(qOtp.targetType.eq("COMPANY"))
                            .and(qOtp.isUsed.eq("F"))
                            .and(qOtp.expireTime.gt(LocalDateTime.now())))
                      .fetchOne();
        
        if (otp == null) {
            throw new UsernameNotFoundException("OTP 조회 실패");
        }
        
        return otp.getCode();
    }

    public void generateOtp(String targetType, String id) {
        // 기존 OTP 삭제
        query.delete(qOtp)
             .where(qOtp.id.eq(id)
                   .and(qOtp.targetType.eq(targetType)))
             .execute();

        // 새 OTP 생성
        String code = GenerateCodeUtil.generateOtpCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);

        query.insert(qOtp)
             .columns(qOtp.id, qOtp.targetType, qOtp.code, qOtp.isUsed, qOtp.expireTime)
             .values(id, targetType, code, "F", expireTime)
             .execute();
    }
}

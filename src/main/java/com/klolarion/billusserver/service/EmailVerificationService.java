package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.QOtp;
import com.klolarion.billusserver.domain.entity.*;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.util.MailHandler;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailVerificationService {
    private final JavaMailSender javaMailSender;
    private final JPAQueryFactory query;
    private final QStore qStore = QStore.store;
    private final QCompany qCompany = QCompany.company;
    private final QOtp qOtp = QOtp.otp;

    /**
     * 이메일 인증 코드 전송
     */
    public void send(String code, String email) {
        try {
            MailHandler mailHandler = new MailHandler(javaMailSender);

            mailHandler.setTo(email);
            mailHandler.setSubject("Bill-us 이메일 인증");
            String htmlContent = "<p>" + "인증 코드 : " + code + "</p>";
            mailHandler.setText(htmlContent, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MailSendException("이메일 전송 실패.");
        }
    }

    /**
     * 이메일 인증 코드 확인
     */
    public <E> void check(String code, E entity, String target) {
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("OTP 코드 누락");
        }

        if (target == null || target.isEmpty()) {
            log.error("이메인 인증 대상 식별 불가.");
            throw new MailSendException("코드 검증 오류. 관리자에게 문의하세요.");
        }

        String id;

        if (target.equals("STORE")) {
            Store store = (Store) entity;
            id = store.getId().toString();
        } else if (target.equals("COMPANY")) {
            Company company = (Company) entity;
            id = company.getId().toString();
        } else {
            throw new BadRequestException("유효하지 않은 대상 타입");
        }

        // OTP 조회
        Otp otp = query.selectFrom(qOtp)
                      .where(qOtp.id.eq(id)
                            .and(qOtp.targetType.eq(target))
                            .and(qOtp.isUsed.eq("F"))
                            .and(qOtp.expireTime.gt(LocalDateTime.now())))
                      .fetchOne();

        if (otp == null) {
            throw new BadRequestException("OTP조회 실패");
        }

        // 입력한 코드와 OTP의 코드가 일치하는지 확인
        if (!code.equals(otp.getCode())) {
            throw new BadRequestException("OTP 검증 실패.");
        }

        // OTP 사용 처리
        query.update(qOtp)
             .set(qOtp.isUsed, "T")
             .where(qOtp.id.eq(id)
                   .and(qOtp.targetType.eq(target)))
             .execute();

        // target에 따라 이메일 인증 플래그 업데이트
        if (target.equals("STORE")) {
            query.update(qStore)
                 .set(qStore.isEmailVerified, "T")
                 .where(qStore.id.eq(UUID.fromString(id)))
                 .execute();
        } else {
            query.update(qCompany)
                 .set(qCompany.isEmailVerified, "T")
                 .where(qCompany.id.eq(UUID.fromString(id)))
                 .execute();
        }
    }
}

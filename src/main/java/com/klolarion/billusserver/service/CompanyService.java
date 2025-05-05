package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.company.CompanyResponseDto;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.exception.r401.AuthFailureException;
import com.klolarion.billusserver.util.GenerateCodeUtil;
import com.klolarion.billusserver.util.MailHandler;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompanyService {
    private final JPAQueryFactory query;
    private final JavaMailSender mailSender;
    private final EntityManager em;
    private final BCryptPasswordEncoder passwordEncoder;
    private final QCompany qCompany = QCompany.company;
    private final QMember qMember = QMember.member;

    /**
     * 회사 정보 조회
     * @param company 조회할 회사 객체
     * @return 회사 정보 DTO
     */
    public CompanyResponseDto getCompanyInfo(Company company) {
        QApply qApply = QApply.apply;

        boolean isApplied = query.selectFrom(qApply).where(
                qApply.company.id.eq(company.getId().toString())
                        .and(qApply.isApproved.eq("F"))
                        .and(qApply.offCd.eq("F"))
        ).fetchFirst() != null;

        return CompanyResponseDto.builder()
                .companyId(company.getId().toString())
                .companyName(company.getCompanyName())
                .email(company.getEmail())
                .tel(company.getTel())
                .zoneCode(company.getZoneCode())
                .address1(company.getAddress1())
                .address2(company.getAddress2())
                .isEnabled(company.getIsEnabled())
                .offCd(company.getOffCd())
                .isApplied(isApplied ? "T" : "F")
                .build();
    }

    /**
     * 소속 직원 목록 조회
     * @param company 조회할 회사 객체
     * @return 직원 정보 DTO 목록
     */
    public List<MemberResponseDto> findMyEmp(Company company) {
        List<MemberResponseDto> result = new ArrayList<>();
        List<Member> list = query.selectFrom(qMember)
                .where(qMember.company.id.eq(company.getId().toString()))
                .fetch();

        for (Member member : list) {
            MemberResponseDto tmp = MemberResponseDto.builder()
                    .memberId(member.getId().toString())
                    .memberName(member.getMemberName())
                    .email(member.getEmail())
                    .tel(member.getTel())
                    .companyId(member.getCompany().getId().toString())
                    .companyName(member.getCompany().getCompanyName())
                    .build();
            result.add(tmp);
        }

        return result;
    }

    /**
     * 회사 정보 수정
     * @param company 수정할 회사 객체
     * @param companyInitDto 수정할 회사 정보 DTO
     */
    public void changeInfo(Company company, CompanyResponseDto companyInitDto) {
        if (company == null) {
            throw new BadRequestException("회사 정보가 없습니다.");
        }

        long execute = query.update(qCompany)
                .set(qCompany.companyName, companyInitDto.getCompanyName())
                .set(qCompany.email, companyInitDto.getEmail())
                .set(qCompany.tel, companyInitDto.getTel())
                .set(qCompany.zoneCode, companyInitDto.getZoneCode())
                .set(qCompany.address1, companyInitDto.getAddress1())
                .set(qCompany.address2, companyInitDto.getAddress2())
                .where(qCompany.id.eq(company.getId().toString()))
                .execute();

        if (execute == 0) {
            throw new BadRequestException("회사 정보 수정에 실패했습니다.");
        }
    }

    /**
     * 비밀번호 변경
     * @param company 비밀번호를 변경할 회사 객체
     * @param requestDto 비밀번호 변경 정보 DTO
     */
    public void changePassword(Company company, InfoRequestDto requestDto) {
        if (company == null) {
            throw new BadRequestException("회사 정보가 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), company.getPassword())) {
            throw new AuthFailureException("현재 비밀번호가 일치하지 않습니다.");
        }

        long execute = query.update(qCompany)
                .set(qCompany.password, passwordEncoder.encode(requestDto.getNewPassword()))
                .where(qCompany.id.eq(company.getId().toString()))
                .execute();

        if (execute == 0) {
            throw new BadRequestException("비밀번호 변경에 실패했습니다.");
        }
    }

    /**
     * 비밀번호 초기화
     * @param requestDto 비밀번호 초기화 정보 DTO
     * @throws RuntimeException 계정이 존재하지 않는 경우
     */
    public void resetPassword(InfoRequestDto requestDto) {
        String randomPassword = GenerateCodeUtil.generateRandomPassword();

        Company company = query.selectFrom(qCompany)
                .where(qCompany.companyAccount.eq(requestDto.getAccount())
                        .and(qCompany.email.eq(requestDto.getEmail())))
                .fetchOne();

        if (company == null) {
            throw new BadRequestException("존재하지 않는 계정입니다.");
        }

        try {
            MailHandler mailHandler = new MailHandler(mailSender);
            String companyEmail = company.getEmail();

            mailHandler.setTo(companyEmail);
            mailHandler.setSubject("Bill-us 비밀번호 초기화");
            mailHandler.setText("초기화된 비밀번호 : " + randomPassword, false);
            mailHandler.send();

            long execute = query.update(qCompany)
                    .set(qCompany.password, passwordEncoder.encode(randomPassword))
                    .where(qCompany.id.eq(company.getId().toString()))
                    .execute();

            if (execute == 0) {
                throw new BadRequestException("비밀번호 초기화에 실패했습니다.");
            }

            em.clear();
            log.info("비밀번호 초기화 완료: account={}", company.getCompanyAccount());

        } catch (Exception e) {
            log.error("비밀번호 초기화 중 오류 발생: {}", e.getMessage());
            throw new BadRequestException("비밀번호 초기화 중 오류가 발생했습니다.");
        }
    }

    /**
     * 이메일 조회
     * @param findDto 계정 정보 DTO
     * @return 이메일 주소
     */
    public String findEmail(InfoRequestDto findDto) {
        String email = query.select(qCompany.email)
                .from(qCompany)
                .where(qCompany.companyAccount.eq(findDto.getAccount())
                        .and(qCompany.tel.eq(findDto.getTel())))
                .fetchOne();

        if (email == null) {
            throw new BadRequestException("이메일을 찾을 수 없습니다.");
        }

        return email;
    }

    /**
     * 계정 조회
     * @param findDto 계정 정보 DTO
     * @return 계정 ID
     */
    public String findAccount(InfoRequestDto findDto) {
        String account = query.select(qCompany.companyAccount)
                .from(qCompany)
                .where(qCompany.email.eq(findDto.getEmail())
                        .and(qCompany.tel.eq(findDto.getTel())))
                .fetchOne();

        if (account == null) {
            throw new BadRequestException("계정을 찾을 수 없습니다.");
        }

        return account;
    }
}

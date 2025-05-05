package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.Apply;
import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.InfoRequestDto;
import com.klolarion.billusserver.dto.InfoResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.exception.r404.CompanyNotFoundException;
import com.klolarion.billusserver.exception.r404.ApplyNotFoundException;
import com.klolarion.billusserver.domain.repository.ApplyRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplyService {

    private final JPAQueryFactory query;
    private final ApplyRepository applyRepository;
    private final EntityManager em;

    /**
     * 관리자 - 회사 비활성화
     */
    public void disableCompany(InfoRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("회사 식별자 누락");
        }

        QCompany qCompany = QCompany.company;
        QApply qApply = QApply.apply;
        String companyId = requestDto.getId();

        // 회사 존재 여부 확인
        Company company = query.selectFrom(qCompany)
                .where(qCompany.id.eq(companyId))
                .fetchOne();
        
        if (company == null) {
            throw new CompanyNotFoundException("존재하지 않는 회사입니다.");
        }

        // 회사 비활성화
        query.update(qCompany)
                .set(qCompany.isEnabled, "F")
                .where(qCompany.id.eq(companyId))
                .execute();

        // 관련 신청 비활성화
        query.update(qApply)
                .set(qApply.offCd, "T")
                .where(qApply.company.id.eq(companyId).and(qApply.member.isNull()))
                .execute();

        em.clear();
        log.info("회사 비활성화 완료: companyId={}", companyId);
    }

    /**
     * 관리자 - 매장 비활성화
     */
    public void disableStore(InfoRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("매장 식별자 누락");
        }

        QStore qStore = QStore.store;
        QApply qApply = QApply.apply;
        String storeId = requestDto.getId();

        // 매장 존재 여부 확인
        Store store = query.selectFrom(qStore)
                .where(qStore.id.eq(storeId))
                .fetchOne();
        
        if (store == null) {
            throw new CompanyNotFoundException("존재하지 않는 매장입니다.");
        }

        // 매장 비활성화
        query.update(qStore)
                .set(qStore.isEnabled, "F")
                .where(qStore.id.eq(storeId))
                .execute();

        // 관련 신청 비활성화
        query.update(qApply)
                .set(qApply.offCd, "T")
                .where(qApply.store.id.eq(storeId)
                        .and(qApply.member.isNull())
                        .and(qApply.company.isNull()))
                .execute();

        em.clear();
        log.info("매장 비활성화 완료: storeId={}", storeId);
    }

    /**
     * 회사 - 등록 신청한 사용자 목록 조회
     */
    public List<InfoResponseDto> getAppliedMembers(Company company) {
        QApply qApply = QApply.apply;
        QMember qMember = QMember.member;

        List<Apply> applyList = query.selectFrom(qApply)
                .leftJoin(qApply.member, qMember).fetchJoin()
                .where(qApply.member.isNotNull()
                        .and(qApply.isApproved.eq("F"))
                        .and(qApply.company.id.eq(company.getId().toString()))
                        .and(qApply.offCd.eq("F"))
                        .and(qApply.isRejected.eq("F")))
                .fetch();

        List<InfoResponseDto> result = new ArrayList<>();
        for (Apply apply : applyList) {
            Member member = apply.getMember();
            InfoResponseDto dto = InfoResponseDto.builder()
                    .applyId(apply.getId().toString())
                    .targetId(member.getId().toString())
                    .name(member.getMemberName())
                    .appliedAt(apply.getCreatedDate())
                    .build();
            result.add(dto);
        }

        log.debug("등록 신청 목록 조회: company={}, count={}", company.getId(), result.size());
        return result;
    }

    /**
     * 회사 - 등록 신청한 사용자 승인
     */
    public void approveMemberFromCompany(InfoRequestDto requestDto, Company company) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("사용자 식별자 누락");
        }

        String memberId = requestDto.getId();
        QApply qApply = QApply.apply;

        // 신청 존재 여부 확인
        Apply apply = query.selectFrom(qApply)
                .where(qApply.member.id.eq(memberId)
                        .and(qApply.company.id.eq(company.getId().toString()))
                        .and(qApply.isApproved.eq("F"))
                        .and(qApply.isRejected.eq("F"))
                        .and(qApply.offCd.eq("F")))
                .fetchOne();

        if (apply == null) {
            throw new ApplyNotFoundException("존재하지 않는 신청입니다.");
        }

        // 신청 승인 처리
        query.update(qApply)
                .set(qApply.isApproved, "T")
                .where(qApply.id.eq(apply.getId()))
                .execute();

        // 회원 소속 회사 설정
        QMember qMember = QMember.member;
        query.update(qMember)
                .set(qMember.company, company)
                .where(qMember.id.eq(memberId))
                .execute();

        log.info("사용자 등록 승인 완료: memberId={}, companyId={}", memberId, company.getId());
    }

    /**
     * 회사 - 사용자 등록 취소
     */
    public void disableMemberFromCompany(InfoRequestDto requestDto, Company company) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("사용자 식별자 누락");
        }

        String memberId = requestDto.getId();
        QApply qApply = QApply.apply;

        // 신청 존재 여부 확인
        Apply apply = query.selectFrom(qApply)
                .where(qApply.member.id.eq(memberId)
                        .and(qApply.company.id.eq(company.getId().toString()))
                        .and(qApply.isApproved.eq("T"))
                        .and(qApply.offCd.eq("F")))
                .fetchOne();

        if (apply == null) {
            throw new ApplyNotFoundException("존재하지 않는 등록입니다.");
        }

        // 신청 취소 처리
        query.update(qApply)
                .set(qApply.isApproved, "F")
                .set(qApply.isRejected, "T")
                .where(qApply.id.eq(apply.getId()))
                .execute();

        // 회원 소속 회사 제거
        QMember qMember = QMember.member;
        query.update(qMember)
                .setNull(qMember.company)
                .where(qMember.id.eq(memberId))
                .execute();

        em.clear();
        log.info("사용자 등록 취소 완료: memberId={}, companyId={}", memberId, company.getId());
    }

    /**
     * 회사 - 사용자의 등록 신청 거부
     */
    public void disableEmployeeApply(InfoRequestDto requestDto, Company company) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("사용자 식별자 누락");
        }

        String memberId = requestDto.getId();
        QApply qApply = QApply.apply;

        // 신청 존재 여부 확인
        Apply apply = query.selectFrom(qApply)
                .where(qApply.member.id.eq(memberId)
                        .and(qApply.company.id.eq(company.getId().toString()))
                        .and(qApply.isApproved.eq("F"))
                        .and(qApply.isRejected.eq("F"))
                        .and(qApply.offCd.eq("F")))
                .fetchOne();

        if (apply == null) {
            throw new ApplyNotFoundException("존재하지 않는 신청입니다.");
        }

        // 신청 거부 처리
        query.update(qApply)
                .set(qApply.offCd, "T")
                .where(qApply.id.eq(apply.getId()))
                .execute();

        em.clear();
        log.info("사용자 등록 신청 거부 완료: memberId={}, companyId={}", memberId, company.getId());
    }

    /**
     * 사용자 - 회사에 등록 신청
     */
    public void applyToCompany(InfoRequestDto requestDto, Member member) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("회사 식별자 누락");
        }

        QApply qApply = QApply.apply;

        // 기존 신청 여부 확인
        boolean exist = query.selectFrom(qApply)
                .where(qApply.member.eq(member)
                        .and(qApply.isRejected.eq("F"))
                        .and(qApply.offCd.eq("F")))
                .fetchFirst() != null;

        if (exist) {
            throw new BadRequestException("이미 신청한 내역이 있습니다.");
        }

        // 회사 존재 여부 확인
        String companyId = requestDto.getId();
        QCompany qCompany = QCompany.company;
        Company company = query.selectFrom(qCompany)
                .where(qCompany.id.eq(companyId))
                .fetchOne();

        if (company == null) {
            throw new CompanyNotFoundException("존재하지 않는 회사입니다.");
        }

        // 신청 생성
        Apply apply = Apply.builder()
                .company(company)
                .member(member)
                .isApproved("F")
                .isRejected("F")
                .offCd("F")
                .build();
        applyRepository.save(apply);

        log.info("회사 등록 신청 완료: memberId={}, companyId={}", member.getId(), companyId);
    }

    /**
     * 사용자 - ID로 회사 검색
     */
    public int lookCompany(InfoRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getId())) {
            throw new BadRequestException("회사 식별자 누락");
        }

        String companyId = requestDto.getId();
        QCompany qCompany = QCompany.company;
        Company company = query.selectFrom(qCompany)
                .where(qCompany.id.eq(companyId))
                .fetchOne();

        if (company == null) {
            return 3;  // 존재하지 않음
        } else if (company.isEnabled()) {
            return 1;  // 활성화 상태
        } else {
            return 2;  // 비활성화 상태
        }
    }

    /**
     * 사용자 - 회사 등록 취소
     */
    public void quitCompany(Member member) {
        if (member.getCompany() == null) {
            throw new BadRequestException("소속된 회사가 없습니다.");
        }

        QMember qMember = QMember.member;
        QApply qApply = QApply.apply;

        // 회원 소속 회사 제거
        query.update(qMember)
                .setNull(qMember.company)
                .where(qMember.id.eq(member.getId().toString()))
                .execute();

        // 신청 취소 처리
        query.update(qApply)
                .set(qApply.offCd, "T")
                .where(qApply.member.id.eq(member.getId().toString())
                        .and(qApply.company.id.eq(member.getCompany().getId().toString()))
                        .and(qApply.isRejected.eq("F")))
                .execute();

        em.clear();
        log.info("회사 등록 취소 완료: memberId={}, companyId={}", member.getId(), member.getCompany().getId());
    }
}

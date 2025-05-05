package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.*;
import com.klolarion.billusserver.dto.auth.AuthRequestDto;
import com.klolarion.billusserver.domain.repository.CompanyRepository;
import com.klolarion.billusserver.domain.repository.MemberRepository;
import com.klolarion.billusserver.domain.repository.RoleRepository;
import com.klolarion.billusserver.domain.repository.StoreRepository;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.exception.r401.AuthFailureException;
import com.klolarion.billusserver.exception.r409.DuplicateResourceException;
import com.klolarion.billusserver.util.BizNumValidator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final JPAQueryFactory query;
    private final BizNumValidator bizNumValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 등록
     */
    public Member memberRegister(AuthRequestDto requestDto) {
        validateMemberRegisterRequest(requestDto);

        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("사용자 권한 설정 실패. 관리자에게 문의하세요."));

        Member member = new Member();
        member.setAlmagestId(requestDto.getAlmagestId());
        member.setMemberName(requestDto.getName());
        member.setEmail(requestDto.getEmail());
        member.setTel(requestDto.getTel());
        member.setRole(role);
        member.setIsBanned("F");

        Member savedMember = memberRepository.save(member);
        log.info("사용자 등록 완료: id={}, name={}", savedMember.getId(), savedMember.getMemberName());
        return savedMember;
    }

    /**
     * 매장 로그인
     */
    public Store storeLogin(AuthRequestDto requestDto) {
        validateLoginRequest(requestDto);

        QStore qStore = QStore.store;
        Store store = query.selectFrom(qStore)
                .where(qStore.storeAccount.eq(requestDto.getAccount()))
                .fetchOne();

        if (store == null) {
            throw new AuthFailureException("매장 정보를 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), store.getPassword())) {
            throw new AuthFailureException("비밀번호가 일치하지 않습니다.");
        }

        validateStoreStatus(store);
        log.info("매장 로그인 성공: id={}, account={}", store.getId(), store.getStoreAccount());
        return store;
    }

    /**
     * 회사 로그인
     */
    public Company companyLogin(AuthRequestDto requestDto) {
        validateLoginRequest(requestDto);

        QCompany qCompany = QCompany.company;
        Company company = query.selectFrom(qCompany)
                .where(qCompany.companyAccount.eq(requestDto.getAccount()))
                .fetchOne();

        if (company == null) {
            throw new AuthFailureException("회사 정보를 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), company.getPassword())) {
            throw new AuthFailureException("비밀번호가 일치하지 않습니다.");
        }

        validateCompanyStatus(company);
        log.info("회사 로그인 성공: id={}, account={}", company.getId(), company.getCompanyAccount());
        return company;
    }

    /**
     * 관리자 로그인
     */
    public Member adminLogin(AuthRequestDto requestDto) {
        validateLoginRequest(requestDto);

        QMember qMember = QMember.member;
        Member member = query.selectFrom(qMember)
                .where(qMember.account.eq(requestDto.getAccount()))
                .fetchOne();

        if (member == null) {
            throw new AuthFailureException("계정을 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new AuthFailureException("비밀번호가 일치하지 않습니다.");
        }

        log.info("관리자 로그인 성공: id={}, account={}", member.getId(), member.getAccount());
        return member;
    }

    /**
     * 매장 등록
     */
    public Store storeRegister(AuthRequestDto requestDto) {
        validateStoreRegisterRequest(requestDto);

        Role storeRole = roleRepository.findById(3L)
                .orElseThrow(() -> new BadRequestException("사용자 권한 설정 실패. 관리자에게 문의하세요."));

        Store newStore = new Store();
        BizNum bizNum = new BizNum();
        bizNum.setBizNum(requestDto.getBizNum());

        newStore.setStoreAccount(requestDto.getAccount());
        newStore.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        newStore.setStoreName(requestDto.getName());
        newStore.setEmail(requestDto.getEmail());
        newStore.setTel(requestDto.getTel());
        newStore.setBizNum(bizNum);
        newStore.setZoneCode(requestDto.getZoneCode());
        newStore.setAddress1(requestDto.getAddress1());
        newStore.setAddress2(requestDto.getAddress2());
        newStore.setIsEmailVerified("F");
        newStore.setIsEnabled("F");
        newStore.setOffCd("F");
        newStore.setRole(storeRole);

        Store savedStore = storeRepository.save(newStore);
        log.info("매장 등록 완료: id={}, account={}", savedStore.getId(), savedStore.getStoreAccount());
        return savedStore;
    }

    /**
     * 회사 등록
     */
    public Company companyRegister(AuthRequestDto requestDto) {
        validateCompanyRegisterRequest(requestDto);

        Role companyRole = roleRepository.findById(4L)
                .orElseThrow(() -> new BadRequestException("사용자 권한 설정 실패. 관리자에게 문의하세요."));

        Company newCompany = new Company();
        BizNum bizNum = new BizNum();
        bizNum.setBizNum(requestDto.getBizNum());

        newCompany.setCompanyAccount(requestDto.getAccount());
        newCompany.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        newCompany.setCompanyName(requestDto.getName());
        newCompany.setEmail(requestDto.getEmail());
        newCompany.setTel(requestDto.getTel());
        newCompany.setBizNum(bizNum);
        newCompany.setZoneCode(requestDto.getZoneCode());
        newCompany.setAddress1(requestDto.getAddress1());
        newCompany.setAddress2(requestDto.getAddress2());
        newCompany.setIsEmailVerified("F");
        newCompany.setIsEnabled("F");
        newCompany.setOffCd("F");
        newCompany.setRole(companyRole);

        Company savedCompany = companyRepository.save(newCompany);
        log.info("회사 등록 완료: id={}, account={}", savedCompany.getId(), savedCompany.getCompanyAccount());
        return savedCompany;
    }

    /**
     * 중복 검사 - 계정
     */
    public void validateDuplicateAccount(AuthRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getAccount())) {
            throw new BadRequestException("계정 정보 누락");
        }

        // 매장 계정 검사
        QStore qStore = QStore.store;
        boolean storeExists = query.selectFrom(qStore)
                .where(qStore.storeAccount.eq(requestDto.getAccount()))
                .fetchFirst() != null;

        // 회사 계정 검사
        QCompany qCompany = QCompany.company;
        boolean companyExists = query.selectFrom(qCompany)
                .where(qCompany.companyAccount.eq(requestDto.getAccount()))
                .fetchFirst() != null;

        if (storeExists || companyExists) {
            throw new DuplicateResourceException("이미 사용중인 계정입니다.");
        }
    }

    /**
     * 중복 검사 - 전화번호
     */
    public void validateDuplicateTel(AuthRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getTel())) {
            throw new BadRequestException("전화번호 정보 누락");
        }

        // 매장 전화번호 검사
        QStore qStore = QStore.store;
        boolean storeExists = query.selectFrom(qStore)
                .where(qStore.tel.eq(requestDto.getTel()))
                .fetchFirst() != null;

        // 회사 전화번호 검사
        QCompany qCompany = QCompany.company;
        boolean companyExists = query.selectFrom(qCompany)
                .where(qCompany.tel.eq(requestDto.getTel()))
                .fetchFirst() != null;

        if (storeExists || companyExists) {
            throw new DuplicateResourceException("이미 사용중인 전화번호입니다.");
        }
    }

    /**
     * 중복 검사 - 이메일
     */
    public void validateDuplicateEmail(AuthRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getEmail())) {
            throw new BadRequestException("이메일 정보 누락");
        }

        // 매장 이메일 검사
        QStore qStore = QStore.store;
        boolean storeExists = query.selectFrom(qStore)
                .where(qStore.email.eq(requestDto.getEmail()))
                .fetchFirst() != null;

        // 회사 이메일 검사
        QCompany qCompany = QCompany.company;
        boolean companyExists = query.selectFrom(qCompany)
                .where(qCompany.email.eq(requestDto.getEmail()))
                .fetchFirst() != null;

        if (storeExists || companyExists) {
            throw new DuplicateResourceException("이미 사용중인 이메일입니다.");
        }
    }

    /**
     * 사업자번호 검증 및 중복 검사
     */
    public void validateBizNum(AuthRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(requestDto.getBizNum())) {
            throw new BadRequestException("사업자번호 정보 누락");
        }

        // 사업자번호 유효성 검증
        bizNumValidator.validateBizNum(request, response, requestDto.getBizNum());

        // 중복 검사
        QBizNum qBizNum = QBizNum.bizNum1;
        boolean exists = query.selectFrom(qBizNum)
                .where(qBizNum.bizNum.eq(requestDto.getBizNum()))
                .fetchFirst() != null;

        if (exists) {
            throw new DuplicateResourceException("이미 등록된 사업자번호입니다.");
        }
    }

    private void validateMemberRegisterRequest(AuthRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getAlmagestId())) {
            throw new BadRequestException("Almagest 식별자 누락");
        }
        if (StringUtils.isBlank(requestDto.getEmail())) {
            throw new BadRequestException("이메일 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getName())) {
            throw new BadRequestException("이름 정보 누락");
        }
    }

    private void validateLoginRequest(AuthRequestDto requestDto) {
        if (StringUtils.isBlank(requestDto.getAccount())) {
            throw new BadRequestException("계정 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getPassword())) {
            throw new BadRequestException("비밀번호 정보 누락");
        }
    }

    private void validateStoreRegisterRequest(AuthRequestDto requestDto) {
        validateLoginRequest(requestDto);
        if (StringUtils.isBlank(requestDto.getName())) {
            throw new BadRequestException("매장명 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getEmail())) {
            throw new BadRequestException("이메일 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getTel())) {
            throw new BadRequestException("전화번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getBizNum())) {
            throw new BadRequestException("사업자번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getZoneCode())) {
            throw new BadRequestException("우편번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getAddress1())) {
            throw new BadRequestException("주소 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getAddress2())) {
            throw new BadRequestException("상세주소 정보 누락");
        }
    }

    private void validateCompanyRegisterRequest(AuthRequestDto requestDto) {
        validateLoginRequest(requestDto);
        if (StringUtils.isBlank(requestDto.getName())) {
            throw new BadRequestException("회사명 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getEmail())) {
            throw new BadRequestException("이메일 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getTel())) {
            throw new BadRequestException("전화번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getBizNum())) {
            throw new BadRequestException("사업자번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getZoneCode())) {
            throw new BadRequestException("우편번호 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getAddress1())) {
            throw new BadRequestException("주소 정보 누락");
        }
        if (StringUtils.isBlank(requestDto.getAddress2())) {
            throw new BadRequestException("상세주소 정보 누락");
        }
    }

    private void validateStoreStatus(Store store) {
        if (!store.isEnabled()) {
            throw new AuthFailureException("현재 사용 불가. 관리자에게 문의하세요.");
        }
        if (!store.isEmailVerified()) {
            throw new AuthFailureException("이메일 인증이 필요합니다.");
        }
        if (store.isOff()) {
            throw new AuthFailureException("현재 사용 불가. 관리자에게 문의하세요.");
        }
    }

    private void validateCompanyStatus(Company company) {
        if (!company.isEnabled()) {
            throw new AuthFailureException("현재 사용 불가. 관리자에게 문의하세요.");
        }
        if (!company.isEmailVerified()) {
            throw new AuthFailureException("이메일 인증이 필요합니다.");
        }
        if (company.isOff()) {
            throw new AuthFailureException("현재 사용 불가. 관리자에게 문의하세요.");
        }
    }

    public void insertRole(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            throw new BadRequestException("권한명 정보 누락");
        }
        Role newRole = new Role(roleName);
        roleRepository.save(newRole);
        log.info("권한 등록 완료: name={}", roleName);
    }
}

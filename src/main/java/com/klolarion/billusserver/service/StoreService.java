package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.*;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.store.StoreResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.exception.r401.AuthFailureException;
import com.klolarion.billusserver.exception.r404.ResourceNotFoundException;
import com.klolarion.billusserver.util.GenerateCodeUtil;
import com.klolarion.billusserver.util.MailHandler;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final JPAQueryFactory query;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 전화번호 중복 확인
     * @param tel 확인할 전화번호
     * @param store 현재 매장 엔티티
     * @return 사용 가능 여부
     */
    public boolean lookTelUpdate(String tel, Store store) {
        if (tel == null || tel.isBlank()) {
            throw new BadRequestException("전화번호가 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        Store result = query.selectFrom(qStore)
                .where(qStore.tel.eq(tel))
                .fetchOne();
        return store.getTel().equals(tel) || result == null;
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일
     * @param store 현재 매장 엔티티
     * @return 사용 가능 여부
     */
    public boolean lookEmailUpdate(String email, Store store) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("이메일이 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        Store result = query.selectFrom(qStore)
                .where(qStore.email.eq(email))
                .fetchOne();
        return store.getEmail().equals(email) || result == null;
    }

    /**
     * 가격 정보 수정
     * @param price 변경할 가격(문자열)
     * @param store 매장 엔티티
     */
    public void updatePrice(String price, Store store) {
        if (price == null || price.isBlank()) {
            throw new BadRequestException("가격이 누락되었습니다.");
        }
        try {
            Integer priceTmp = Integer.parseInt(price);
            QStore qStore = QStore.store;
            long execute = query.update(qStore)
                    .set(qStore.price, priceTmp)
                    .where(qStore.id.eq(store.getId()))
                    .execute();
            if (execute == 0) {
                throw new BadRequestException("가격 수정에 실패했습니다.");
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("올바르지 않은 가격 형식입니다.");
        }
    }

    /**
     * 매장 정보(웹) 조회
     * @param store 매장 엔티티
     * @return 매장 정보 DTO
     */
    public StoreResponseDto initStoreInfo(Store store) {
        QApply qApply = QApply.apply;
        boolean isApplied = query.selectFrom(qApply)
                .where(qApply.store.id.eq(store.getId())
                        .and(qApply.offCd.eq("F"))
                        .and(qApply.isApproved.eq("F")))
                .fetchFirst() != null;
        return StoreResponseDto.builder()
                .storeId(store.getId().toString())
                .storeName(store.getStoreName())
                .email(store.getEmail())
                .tel(store.getTel())
                .bizNum(store.getBizNum() != null ? store.getBizNum().getBizNum() : null)
                .zoneCode(store.getZoneCode())
                .address1(store.getAddress1())
                .address2(store.getAddress2())
                .isEnabled(store.isEnabled() ? "T" : "F")
                .price(store.getPrice() != null ? String.valueOf(store.getPrice()) : null)
                .offCd(store.isOff() ? "T" : "F")
                .isApplied(isApplied ? "T" : "F")
                .isEmailVerified(store.isEmailVerified() ? "T" : "F")
                .build();
    }

    /**
     * 매장 정보(모바일) 조회
     * @param date 조회 날짜
     * @param store 매장 엔티티
     * @return 매장 정보 DTO
     */
    public StoreResponseDto initStoreMobileInfo(String date, Store store) {
        if (date == null || date.isBlank()) {
            throw new BadRequestException("날짜가 누락되었습니다.");
        }
        QApply qApply = QApply.apply;
        QBill qBill = QBill.bill;
        try {
            boolean isApplied = query.selectFrom(qApply)
                    .where(qApply.store.id.eq(store.getId())
                            .and(qApply.offCd.eq("F"))
                            .and(qApply.isApproved.eq("F")))
                    .fetchFirst() != null;
            Tuple tuple = query.select(qBill.store.price.sum(), qBill.count())
                    .from(qBill)
                    .where(qBill.date.eq(date)
                            .and(qBill.store.id.eq(store.getId())))
                    .fetchOne();
            Long count = tuple != null ? tuple.get(qBill.count()) : 0L;
            Long totalAmount = tuple != null ? Long.valueOf(tuple.get(qBill.store.price.sum())) : 0L;
            return StoreResponseDto.builder()
                    .storeId(store.getId().toString())
                    .storeName(store.getStoreName())
                    .email(store.getEmail())
                    .tel(store.getTel())
                    .bizNum(store.getBizNum() != null ? store.getBizNum().getBizNum() : null)
                    .zoneCode(store.getZoneCode())
                    .address1(store.getAddress1())
                    .address2(store.getAddress2())
                    .isEnabled(store.isEnabled() ? "T" : "F")
                    .price(store.getPrice() != null ? String.valueOf(store.getPrice()) : null)
                    .offCd(store.isOff() ? "T" : "F")
                    .isApplied(isApplied ? "T" : "F")
                    .todayCount(String.valueOf(count))
                    .todayTotal(String.valueOf(totalAmount))
                    .isEmailVerified(store.isEmailVerified() ? "T" : "F")
                    .build();
        } catch (Exception e) {
            throw new BadRequestException("매장 정보 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 매장 메뉴 정보 조회
     * @param date 날짜
     * @param meal 식사 구분
     * @param store 매장 엔티티
     * @return 메뉴 정보 DTO
     */
    public InfoResponseDto getMyMenu(String date, String meal, Store store) {
        if (date == null || date.isBlank()) {
            throw new BadRequestException("날짜가 누락되었습니다.");
        }
        if (meal == null || meal.isBlank()) {
            throw new BadRequestException("식사 구분이 누락되었습니다.");
        }
        QMenu qMenu = QMenu.menu;
        Menu menu = query.selectFrom(qMenu)
                .where(qMenu.store.id.eq(store.getId())
                        .and(qMenu.date.eq(date))
                        .and(qMenu.meal.eq(meal)))
                .fetchOne();
        if (menu == null) {
            return null;
        }
        return InfoResponseDto.builder()
            .storeId(menu.getStore().getId().toString())
            .menuId(menu.getId().toString())
            .date(menu.getDate())
            .storeName(menu.getStore().getStoreName())
            .meal(menu.getMeal())
            .menu1(menu.getMenu1())
            .menu2(menu.getMenu2())
            .menu3(menu.getMenu3())
            .menu4(menu.getMenu4())
            .menu5(menu.getMenu5())
            .menu6(menu.getMenu6())
            .menu7(menu.getMenu7())
            .menu8(menu.getMenu8())
            .menu9(menu.getMenu9())
            .menu10(menu.getMenu10())
            .menu11(menu.getMenu11())
            .menu12(menu.getMenu12())
            .build();
    }

    /**
     * 매장 정보 수정
     * @param storeDto 수정할 매장 정보 DTO
     * @param store 매장 엔티티
     */
    public void updateStoreInfo(StoreResponseDto storeDto, Store store) {
        if (storeDto == null) {
            throw new BadRequestException("매장 정보가 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        long execute = query.update(qStore)
                .set(qStore.storeName, storeDto.getStoreName())
                .set(qStore.email, storeDto.getEmail())
                .set(qStore.tel, storeDto.getTel())
                .set(qStore.zoneCode, storeDto.getZoneCode())
                .set(qStore.address1, storeDto.getAddress1())
                .set(qStore.address2, storeDto.getAddress2())
                .where(qStore.id.eq(store.getId()))
                .execute();
        if (execute == 0) {
            throw new BadRequestException("매장 정보 수정에 실패했습니다.");
        }
    }

    /**
     * 비밀번호 변경
     * @param passwordDto 비밀번호 변경 정보 DTO
     * @param store 매장 엔티티
     */
    public void changePassword(InfoRequestDto passwordDto, Store store) {
        if (passwordDto == null) {
            throw new BadRequestException("비밀번호 정보가 누락되었습니다.");
        }
        if (!passwordEncoder.matches(passwordDto.getPassword(), store.getPassword())) {
            throw new AuthFailureException("현재 비밀번호가 일치하지 않습니다.");
        }
        QStore qStore = QStore.store;
        long execute = query.update(qStore)
                .set(qStore.password, passwordEncoder.encode(passwordDto.getNewPassword()))
                .where(qStore.id.eq(store.getId()))
                .execute();
        if (execute == 0) {
            throw new BadRequestException("비밀번호 변경에 실패했습니다.");
        }
    }

    /**
     * 비밀번호 초기화
     * @param findDto 비밀번호 초기화 정보 DTO
     */
    public void resetPassword(InfoRequestDto findDto) {
        if (findDto == null) {
            throw new BadRequestException("계정 정보가 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        Store store = query.selectFrom(qStore)
                .where(qStore.storeAccount.eq(findDto.getAccount())
                        .and(qStore.tel.eq(findDto.getTel())))
                .fetchOne();
        if (store == null) {
            throw new ResourceNotFoundException("존재하지 않는 계정입니다.");
        }
        try {
            String randomPassword = GenerateCodeUtil.generateRandomPassword();
            MailHandler mailHandler = new MailHandler(mailSender);
            mailHandler.setTo(store.getEmail());
            mailHandler.setSubject("Bill-us 비밀번호 초기화");
            mailHandler.setText("초기화된 비밀번호 : " + randomPassword, false);
            mailHandler.send();
            long execute = query.update(qStore)
                    .set(qStore.password, passwordEncoder.encode(randomPassword))
                    .where(qStore.id.eq(store.getId()))
                    .execute();
            if (execute == 0) {
                throw new BadRequestException("비밀번호 초기화에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new BadRequestException("비밀번호 초기화 중 오류가 발생했습니다.");
        }
    }

    /**
     * 이메일 찾기
     * @param findDto 계정 정보 DTO
     * @return 이메일
     */
    public String findEmail(InfoRequestDto findDto) {
        if (findDto == null) {
            throw new BadRequestException("계정 정보가 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        String email = query.select(qStore.email)
                .from(qStore)
                .where(qStore.storeAccount.eq(findDto.getAccount())
                        .and(qStore.tel.eq(findDto.getTel())))
                .fetchOne();
        if (email == null) {
            throw new ResourceNotFoundException("이메일을 찾을 수 없습니다.");
        }
        return email;
    }

    /**
     * 계정 찾기
     * @param findDto 계정 정보 DTO
     * @return 계정
     */
    public String findAccount(InfoRequestDto findDto) {
        if (findDto == null) {
            throw new BadRequestException("계정 정보가 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        String account = query.select(qStore.storeAccount)
                .from(qStore)
                .where(qStore.email.eq(findDto.getEmail())
                        .and(qStore.tel.eq(findDto.getTel())))
                .fetchOne();
        if (account == null) {
            throw new ResourceNotFoundException("계정을 찾을 수 없습니다.");
        }
        return account;
    }

    /**
     * FCM 토큰 저장
     * @param firebaseToken FCM 토큰
     * @param store 매장 엔티티
     */
    public void initFCM(String firebaseToken, Store store) {
        if (firebaseToken == null || firebaseToken.isBlank()) {
            throw new BadRequestException("Firebase 토큰이 누락되었습니다.");
        }
        QStore qStore = QStore.store;
        long execute = query.update(qStore)
                .set(qStore.firebaseToken, firebaseToken)
                .where(qStore.id.eq(store.getId()))
                .execute();
        if (execute == 0) {
            throw new BadRequestException("Firebase 토큰 설정에 실패했습니다.");
        }
    }

    /**
     * 매장 탈퇴(비활성화)
     * @param store 매장 엔티티
     */
    public void leave(Store store) {
        QStore qStore = QStore.store;
        long execute = query.update(qStore)
                .set(qStore.offCd, "T")
                .where(qStore.id.eq(store.getId()))
                .execute();
        if (execute == 0) {
            throw new BadRequestException("회원 탈퇴에 실패했습니다.");
        }
    }
}

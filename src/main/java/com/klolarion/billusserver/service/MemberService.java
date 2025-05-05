package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.*;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.klolarion.billusserver.dto.store.StoreResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final JPAQueryFactory query;
    private static final QApply qApply = QApply.apply;
    private static final QStore qStore = QStore.store;

    /**
     * 사용자 정보 조회
     * @param member 조회할 회원 엔티티
     * @return 회원 정보 DTO
     */
    public MemberResponseDto memberInfo(Member member) {
        // 등록 신청한 회사가 있는지 조회
        boolean alreadyApplied = query.selectFrom(qApply)
                .where(qApply.member.id.eq(member.getId().toString())
                        .and(qApply.isRejected.eq("F"))
                        .and(qApply.offCd.eq("F")))
                .fetchFirst() != null;

        return MemberResponseDto.builder()
                .memberId(member.getId().toString())
                .memberName(member.getMemberName())
                .alreadyApplied(alreadyApplied ? "T" : "F")
                .companyId(member.getCompany() != null ? member.getCompany().getId().toString() : null)
                .companyName(member.getCompany() != null ? member.getCompany().getCompanyName() : null)
                .build();
    }

    /**
     * 매장 검색
     * @param requestDto 검색 조건 DTO (매장명, 주소 등)
     * @return 매장 정보 DTO 리스트
     */
    public List<StoreResponseDto> searchStore(InfoRequestDto requestDto) {
        BooleanBuilder searchBuilder = new BooleanBuilder();

        if (requestDto.getStoreName() != null) {
            searchBuilder.and(qStore.storeName.contains(requestDto.getStoreName()));
        }

        if (requestDto.getAddress() != null) {
            searchBuilder.and(qStore.address1.contains(requestDto.getAddress())
                    .or(qStore.address2.contains(requestDto.getAddress())));
        }

        List<Store> storeList = query.selectFrom(qStore)
                .where(
                        searchBuilder
                                .and(qStore.price.isNotNull())
                                .and(qStore.isEnabled.eq("T")))
                .limit(100)
                .fetch();

        return storeList.stream().map(store -> StoreResponseDto.builder()
                .storeId(store.getId().toString())
                .storeName(store.getStoreName())
                .email(store.getEmail())
                .price(String.valueOf(store.getPrice()))
                .tel(store.getTel())
                .zoneCode(store.getZoneCode())
                .address1(store.getAddress1())
                .address2(store.getAddress2())
                .build()).collect(Collectors.toList());
    }

    /**
     * QR코드 인식 후 매장 가격 조회
     * @param requestDto 매장 식별자 정보 DTO
     * @return 매장 가격 정보 DTO
     */
    public InfoResponseDto getStorePrice(InfoRequestDto requestDto) {

        if (requestDto.getId() == null || requestDto.getId().isEmpty()) {
            throw new BadRequestException("매장 식별자 누락. QR코드를 다시 인식하세요.");
        }

        Store store = query.selectFrom(qStore)
                .where(qStore.id.eq(requestDto.getId()))
                .fetchOne();

        if (store == null) {
            throw new BadRequestException("매장을 찾을 수 없습니다.");
        }

        return InfoResponseDto.builder()
                .price(String.valueOf(store.getPrice()))
                .build();
    }
}



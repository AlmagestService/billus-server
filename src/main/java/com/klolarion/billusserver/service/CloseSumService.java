package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.entity.Bill;
import com.klolarion.billusserver.domain.entity.QBill;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.dto.InfoResponseDto;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloseSumService {

    private final JPAQueryFactory query;

    // [특정 매장] 일별 전체 매출 합계
    public Integer storeDayAllSum(String date, Store store) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.eq(date).and(qBill.store.id.eq(store.getId())))
                .fetchOne();
    }

    // [특정 매장] 일별 전체 매출 합계 (매장용 모바일)
    public InfoResponseDto storeDayAllSumMobile(String date, Store store) {
        QBill qBill = QBill.bill;
        Tuple tuple = query.select(qBill.store.price.sum(), qBill.count())
                .from(qBill)
                .where(qBill.date.eq(date).and(qBill.store.id.eq(store.getId())))
                .fetchOne();

        try {
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum(String.valueOf(tuple.get(qBill.store.price.sum())))
                    .count(String.valueOf(tuple.get(qBill.count())))
                    .build();
        }catch (Exception e){
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum("0")
                    .count("0")
                    .build();
        }
    }

    // [특정 매장] 월별 전체 매출 합계
    public Integer storeMonthAllSum(String month, Store store) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.contains(month).and(qBill.store.id.eq(store.getId())))
                .fetchOne();
    }

    // [특정 매장] 월별 전체 매출 합계 (매장용 모바일)
    public InfoResponseDto storeMonthAllSumMobile(String month, Store store) {
        QBill qBill = QBill.bill;
        Tuple tuple = query.select(qBill.store.price.sum(), qBill.count())
                .from(qBill)
                .where(qBill.date.contains(month).and(qBill.store.id.eq(store.getId())))
                .fetchOne();
        try {
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum(String.valueOf(tuple.get(qBill.store.price.sum())))
                    .count(String.valueOf(tuple.get(qBill.count())))
                    .build();
        }catch (Exception e){
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum("0")
                    .count("0")
                    .build();
        }
    }

    // [특정 매장] 연별 전체 매출 합계
    public Integer storeYearAllSum(String year, Store store) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.contains(year).and(qBill.store.id.eq(store.getId())))
                .fetchOne();
    }

    // [특정 매장] 연별 전체 매출 합계 (매장용 모바일)
    public InfoResponseDto storeYearAllSumMobile(String year, Store store) {
        QBill qBill = QBill.bill;
        Tuple tuple = query.select(qBill.store.price.sum(), qBill.count())
                .from(qBill)
                .where(qBill.date.contains(year).and(qBill.store.id.eq(store.getId())))
                .fetchOne();

        try {
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum(String.valueOf(tuple.get(qBill.store.price.sum())))
                    .count(String.valueOf(tuple.get(qBill.count())))
                    .build();
        }catch (Exception e){
            return InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum("0")
                    .count("0")
                    .build();
        }
    }

    // [회사 전체] 일별 전체 매출 합계
    public Integer companyDayAllSum(String date, Company company) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.eq(date).and(qBill.company.id.eq(company.getId())))
                .fetchOne();
    }

    // [회사 전체] 월별 전체 매출 합계
    public Integer companyMonthAllSum(String date, Company company) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.contains(date).and(qBill.company.id.eq(company.getId())))
                .fetchOne();
    }

    // [회사 전체] 연별 전체 매출 합계
    public Integer companyYearAllSum(String year, Company company) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.contains(year).and(qBill.company.id.eq(company.getId())))
                .fetchOne();
    }

    // [회사 전체] 일별 매장별 매출 합계
    public List<InfoResponseDto> companyDayStoreEachSum(String date, Company company) {
        List<InfoResponseDto> result = new ArrayList<>();
        Long total = null;

        QBill qBill = QBill.bill;

        List<Tuple> list = query.select(qBill.store.storeName, qBill.store.price.sum())
                .from(qBill)
                .where(qBill.company.id.eq(company.getId()).and(qBill.date.contains(date)))
                .groupBy(qBill.store.storeName)
                .fetch();

        for (Tuple tuple : list) {
            String storeName = tuple.get(qBill.store.storeName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if(sum!=null) {
                total = Long.valueOf(sum);
            }
            InfoResponseDto tmp = InfoResponseDto.builder()
                    .storeName(storeName)
                    .totalSum(String.valueOf(total))
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    // [회사 전체] 월별 매장별 매출 합계
    public List<InfoResponseDto> companyMonthStoreEachSum(String date, Company company) {
        List<InfoResponseDto> result = new ArrayList<>();
        Long total = null;

        QBill qBill = QBill.bill;

        List<Tuple> list = query.select(qBill.store.storeName, qBill.store.price.sum())
                .from(qBill)
                .where(qBill.company.id.eq(company.getId()).and(qBill.date.contains(date)))
                .groupBy(qBill.store.storeName)
                .fetch();

        for (Tuple tuple : list) {
            String storeName = tuple.get(qBill.store.storeName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if(sum!=null) {
                total = Long.valueOf(sum);
            }
            InfoResponseDto tmp = InfoResponseDto.builder()
                    .storeName(storeName)
                    .totalSum(String.valueOf(total))
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    // [회사 전체] 월별 직원별 매출 합계
    public List<InfoResponseDto> companyMonthMemberEachSum(String date, Company company) {
        List<InfoResponseDto> result = new ArrayList<>();
        Long total = null;

        QBill qBill = QBill.bill;

        List<Tuple> list = query.select(qBill.member.memberName, qBill.store.price.sum())
                .from(qBill)
                .where(qBill.company.id.eq(company.getId()).and(qBill.date.contains(date)))
                .groupBy(qBill.member.memberName)
                .fetch();

        for (Tuple tuple : list) {
            String memberName = tuple.get(qBill.member.memberName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if(sum!=null) {
                total = Long.valueOf(sum);
            }
            InfoResponseDto tmp = InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum(String.valueOf(total))
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    // [회사 전체] 연별 매장별 매출 합계
    public List<InfoResponseDto> companyYearStoreEachSum(String date, Company company) {
        List<InfoResponseDto> result = new ArrayList<>();
        Long total = null;

        QBill qBill = QBill.bill;

        List<Tuple> list = query.select(qBill.store.storeName, qBill.store.price.sum())
                .from(qBill)
                .where(qBill.company.id.eq(company.getId()).and(qBill.date.contains(date)))
                .groupBy(qBill.store.storeName)
                .fetch();

        for (Tuple tuple : list) {
            String storeName = tuple.get(qBill.store.storeName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if(sum!=null) {
                total = Long.valueOf(sum);
            }
            InfoResponseDto tmp = InfoResponseDto.builder()
                    .storeName(storeName)
                    .totalSum(String.valueOf(total))
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    // [회사 전체] 연별 직원별 매출 합계
    public List<InfoResponseDto> companyYearMemberEachSum(String date, Company company) {
        List<InfoResponseDto> result = new ArrayList<>();
        Long total = null;

        QBill qBill = QBill.bill;

        List<Tuple> list = query.select(qBill.member.memberName, qBill.store.price.sum())
                .from(qBill)
                .where(qBill.company.id.eq(company.getId()).and(qBill.date.contains(date)))
                .groupBy(qBill.member.memberName)
                .fetch();

        for (Tuple tuple : list) {
            String memberName = tuple.get(qBill.member.memberName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if(sum!=null) {
                total = Long.valueOf(sum);
            }
            InfoResponseDto tmp = InfoResponseDto.builder()
                    .storeName(null)
                    .totalSum(String.valueOf(total))
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    // [특정 직원] 월별 전체 매출 합계
    public Integer memberMonthAllSum(String date, String memberId) {
        QBill qBill = QBill.bill;
        return query.select(qBill.store.price.sum())
                .from(qBill)
                .where(qBill.date.contains(date).and(qBill.member.id.eq(UUID.fromString(memberId))))
                .fetchOne();
    }

    // [특정 직원] 월별 상세 매출 내역
    public List<BillResponseDto> memberMonthDetail(String date, String memberId) {
        List<BillResponseDto> result = new ArrayList<>();
        QBill qBill = QBill.bill;

        List<Bill> list = query.selectFrom(qBill)
                .where(qBill.date.contains(date).and(qBill.member.id.eq(UUID.fromString(memberId))))
                .fetch();

        for (Bill bill : list) {
            BillResponseDto tmp = BillResponseDto.builder()
                    .storeName(bill.getStore().getStoreName())
                    .employeeName(bill.getMember().getMemberName())
                    .companyName(bill.getCompany().getCompanyName())
                    .price(String.valueOf(bill.getStore().getPrice()))
                    .date(bill.getDate())
                    .build();
            result.add(tmp);
        }

        return result;
    }
}
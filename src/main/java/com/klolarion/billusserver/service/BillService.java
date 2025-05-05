package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.*;
import com.klolarion.billusserver.domain.entity.Bill;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.bill.BillRequestDto;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.domain.repository.BillRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillService {

    private final BillRepository billRepository;
    private final JPAQueryFactory query;
    private final EntityManager em;
    private final QBill qBill = QBill.bill;
    private final QStore qStore = QStore.store;
    private final QMember qMember = QMember.member;
    private final QCompany qCompany = QCompany.company;

    private String companyId;  // 현재 회사 ID
    private String storeId;    // 현재 매장 ID

    /**
     * 직원이 새로운 장부를 생성합니다.
     * 매장 정보를 확인하고, 방문객 데이터가 있는 경우 visitorBill 메서드를 호출합니다.
     * 
     * @param requestDto 장부 생성 요청 정보 (매장 ID, 날짜, 방문객 수 등)
     * @param member 장부를 생성하는 직원 정보
     * @return 생성된 장부 객체
     * @throws BadRequestException 매장 정보를 찾을 수 없는 경우
     */
    public Bill newBill(BillRequestDto requestDto, Member member) {
        Store store = query.selectFrom(qStore)
                .where(qStore.id.eq(requestDto.getStoreId()))
                .fetchOne();
        if (store == null) {
            throw new BadRequestException("매장 정보를 찾을 수 없습니다.");
        }

        Bill newBill = Bill.builder()
                .store(store)
                .company(member.getCompany())
                .member(member)
                .date(requestDto.getDate())
                .build();

        // 방문 데이터가 존재하면 visitorBill 실행
        if (requestDto.getExtraCount() != null && !requestDto.getExtraCount().isEmpty()) {
            int extraCount = Integer.parseInt(requestDto.getExtraCount());
            if (extraCount > 10) {
                throw new BadRequestException("방문은 최대 10명까지 등록 가능합니다.");
            }
            if (extraCount > 0) {
                visitorBill(requestDto, extraCount, store, member);
            }
        }
        return newBill;
    }

    /**
     * 방문객 장부를 생성합니다.
     * 방문객은 최대 10명까지 등록 가능하며, 각 방문객마다 별도의 장부가 생성됩니다.
     * 
     * @param requestDto 장부 생성 요청 정보
     * @param count 방문객 수
     * @param store 방문한 매장 정보
     * @param member 방문객을 등록한 직원 정보
     */
    public void visitorBill(BillRequestDto requestDto, int count, Store store, Member member) {
        member.setMemberName("방문");

        for (int i = 0; i < count; i++) {
            Bill visitBill = Bill.builder()
                    .store(store)
                    .company(member.getCompany())
                    .member(null)  // Bill에서 Member가 Null이면 방문객 장부
                    .date(requestDto.getDate())
                    .build();

            billRepository.save(visitBill);
        }
    }

    /**
     * 특정 매장의 월간 상세 장부 목록을 조회합니다.
     * 매장별로 직원 이름과 매출 합계를 그룹화하여 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @param id 매장 ID
     * @return 매장별 월간 상세 장부 목록
     */
    public List<BillResponseDto> monthlyStoreBillDetailList(String month, String id) {
        Integer index = 0;
        List<BillResponseDto> result = new ArrayList<>();

        List<Tuple> list = query.select(
                        qBill.store.storeName,
                        qBill.member.memberName,
                        qBill.store.price.sum()
                ).from(qBill)
                .where(qBill.company.id.eq(companyId)
                        .and(qBill.date.contains(month))
                        .and(qBill.store.id.eq(id)))
                .groupBy(qBill.member.memberName)
                .fetch();

        for (Tuple tuple : list) {
            String memberName = tuple.get(qBill.member.memberName);
            String storeName = tuple.get(qBill.store.storeName);
            String total = tuple.get(qBill.store.price.sum()).toString();
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(index += 1)
                    .employeeName(memberName)
                    .storeName(storeName)
                    .companyName(null)
                    .price(total)
                    .date(month)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 모든 매장의 월간 매출 합계 목록을 조회합니다.
     * 매장별로 매출 합계를 계산하여 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @return 매장별 월간 매출 합계 목록
     */
    public List<BillResponseDto> monthlyStoreBillTotalList(String month) {
        List<BillResponseDto> result = new ArrayList<>();
        Integer index = 0;
        Integer total = null;

        List<Tuple> list = query.select(
                        qBill.store.storeName,
                        qBill.store.price.sum()
                ).from(qBill)
                .where(qBill.company.id.eq(companyId)
                        .and(qBill.date.contains(month)))
                .groupBy(qBill.store.storeName)
                .fetch();

        for (Tuple tuple : list) {
            String storeName = tuple.get(qBill.store.storeName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if (sum != null) {
                total = Integer.valueOf(sum);
            }
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(++index)
                    .employeeName(null)
                    .storeName(storeName)
                    .companyName(null)
                    .price(total != null ? total.toString() : "0")
                    .date(month)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 특정 직원의 월간 장부 상세 목록을 조회합니다.
     * 직원별로 매장과 날짜별 매출 정보를 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @param id 직원 ID
     * @return 직원별 월간 장부 상세 목록
     */
    public List<BillResponseDto> monthlyEmployeeBillList(String month, String id) {
        Integer index = 0;
        List<BillResponseDto> result = new ArrayList<>();

        List<Bill> list = query.selectFrom(qBill)
                .where(qBill.company.id.eq(companyId)
                        .and(qBill.date.contains(month))
                        .and(qBill.member.id.eq(id)))
                .fetch();

        for (Bill bill : list) {
            String memberName = bill.getMember().getMemberName();
            String storeName = bill.getStore().getStoreName();
            String price = bill.getStore().getPrice().toString();
            String date = bill.getDate();
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(index += 1)
                    .employeeName(memberName)
                    .storeName(storeName)
                    .companyName(null)
                    .price(price)
                    .date(date)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 모든 직원의 월간 매출 합계 목록을 조회합니다.
     * 직원별로 매출 합계를 계산하여 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @return 직원별 월간 매출 합계 목록
     */
    public List<BillResponseDto> monthlyEmployeeBillTotal(String month) {
        List<BillResponseDto> result = new ArrayList<>();
        Integer index = 0;
        Integer total = null;

        List<Tuple> list = query.select(
                        qBill.member.memberName,
                        qBill.store.price.sum()
                ).from(qBill)
                .where(qBill.company.id.eq(companyId)
                        .and(qBill.date.contains(month)))
                .groupBy(qBill.member.memberName)
                .fetch();

        for (Tuple tuple : list) {
            String memberName = tuple.get(qBill.member.memberName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if (sum != null) {
                total = Integer.valueOf(sum);
            }
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(++index)
                    .employeeName(memberName)
                    .storeName(null)
                    .companyName(null)
                    .price(total != null ? total.toString() : "0")
                    .date(month)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 특정 매장의 월간 직원별 장부 상세 목록을 조회합니다.
     * 날짜별로 직원의 방문 횟수를 피벗 테이블 형태로 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @param id 매장 ID
     * @return 매장별 월간 직원 방문 현황 목록
     */
    public List<Object[]> monthlyCompanyBillDetail(String month, String storeId) {
        //기준날짜 리스트
        List<String> dayList = new ArrayList<>();
        for (int i = 1; i < 32; i++) {
            String tmp = String.format("%02d", i);
            dayList.add(month + tmp);
        }

        //쿼리문 생성
        String sql = "select member_name, ";
        for (int i = 0; i < dayList.size(); i++) {
            if (i == 30) {
                String tmp = "MAX(CASE WHEN date = \'" + dayList.get(i) + "\' THEN row_count ELSE 0 END) AS \"01\" ";
                sql += tmp;
            } else {
                String tmp = "MAX(CASE WHEN date = \'" + dayList.get(i) + "\' THEN row_count ELSE 0 END) AS \"01\", ";
                sql += tmp;
            }
        }
        sql += "from ( SELECT member_name, date, COUNT(*) AS row_count FROM bill WHERE store_id = '" + storeId + "' AND company_id = '" + companyId + "' AND date LIKE '" + month + "%' GROUP BY member_name, date) as subquery group by member_name;";

        //쿼리 실행
        Query result = em.createNativeQuery(sql);
        List<Object[]> resultList = result.getResultList();

        return resultList;
    }

    /**
     * 매장별 회사의 월간 매출 합계 목록을 조회합니다.
     * 회사별로 매출 합계와 방문 횟수를 계산하여 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @return 회사별 월간 매출 합계 목록
     */
    public List<BillResponseDto> monthlyCompanyBillTotalList(String month) {
        List<BillResponseDto> result = new ArrayList<>();
        Integer index = 0;
        Integer total = null;

        List<Tuple> list = query.select(
                        qBill.company.id,
                        qBill.company.companyName,
                        qBill.store.price.sum()
                ).from(qBill)
                .where(qBill.store.id.eq(storeId)
                        .and(qBill.date.contains(month)))
                .groupBy(qBill.company.companyName)
                .fetch();

        for (Tuple tuple : list) {
            String companyId = tuple.get(qBill.company.id);
            String companyName = tuple.get(qBill.company.companyName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if (sum != null) {
                total = Integer.valueOf(sum);
            }
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(Integer.parseInt(companyId))
                    .employeeName(null)
                    .storeName(null)
                    .companyName(companyName)
                    .price(total != null ? total.toString() : "0")
                    .date(month)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 매장별 회사의 일일 매출 합계 목록을 조회합니다.
     * 회사별로 일일 매출 합계와 방문 횟수를 계산하여 반환합니다.
     * 
     * @param date 조회할 날짜 (YYYYMMDD 형식)
     * @return 회사별 일일 매출 합계 목록
     */
    public List<BillResponseDto> dailyCompanyBillTotalList(String date) {
        List<BillResponseDto> result = new ArrayList<>();
        Integer index = 0;
        Integer total = null;

        List<Tuple> list = query.select(
                        qBill.company.id,
                        qBill.company.companyName,
                        qBill.store.price.sum()
                ).from(qBill)
                .where(qBill.store.id.eq(storeId)
                        .and(qBill.date.contains(date)))
                .groupBy(qBill.company.companyName)
                .fetch();

        for (Tuple tuple : list) {
            String companyId = tuple.get(qBill.company.id);
            String companyName = tuple.get(qBill.company.companyName);
            Integer sum = tuple.get(qBill.store.price.sum());
            if (sum != null) {
                total = Integer.valueOf(sum);
            }
            BillResponseDto tmp = BillResponseDto.builder()
                    .index(Integer.parseInt(companyId))
                    .employeeName(null)
                    .storeName(null)
                    .companyName(companyName)
                    .price(total != null ? total.toString() : "0")
                    .date(date)
                    .count(null)
                    .build();
            result.add(tmp);
        }
        return result;
    }

    /**
     * 매장의 월간 회사별 장부 상세 목록을 조회합니다.
     * 날짜별로 회사의 방문 횟수를 피벗 테이블 형태로 반환합니다.
     * 
     * @param month 조회할 월 (YYYYMM 형식)
     * @return 매장별 월간 회사 방문 현황 목록
     */
    public List<Object[]> monthlyStoreBillDetail(String month) {
        //기준날짜 리스트
        List<String> dayList = new ArrayList<>();
        for (int i = 1; i < 32; i++) {
            String tmp = String.format("%02d", i);
            dayList.add(month + tmp);
        }

        //쿼리문 생성
        String sql = "select company_name, price,";
        for (int i = 0; i < dayList.size(); i++) {
            if (i == 30) {
                String tmp = "MAX(CASE WHEN date = \'" + dayList.get(i) + "\' THEN row_count ELSE 0 END) AS \"01\" ";
                sql += tmp;
            } else {
                String tmp = "MAX(CASE WHEN date = \'" + dayList.get(i) + "\' THEN row_count ELSE 0 END) AS \"01\", ";
                sql += tmp;
            }
        }
        sql += "from ( SELECT company_name, date, price, COUNT(*) AS row_count FROM bill WHERE store_id = '" + storeId + "' AND date LIKE '" + month + "%' GROUP BY company_name, date) as subquery group by company_name;";

        //쿼리 실행
        Query result = em.createNativeQuery(sql);
        List<Object[]> resultList = result.getResultList();

        return resultList;
    }
}

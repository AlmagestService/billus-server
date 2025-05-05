package com.klolarion.billusserver.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.klolarion.billusserver.domain.entity.Bill;
import com.klolarion.billusserver.domain.QBill;
import com.klolarion.billusserver.domain.QStore;
import com.klolarion.billusserver.domain.entity.Store;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final JPAQueryFactory query;

    /**
     * FCM 토큰을 이용해 매장에 장부 등록 알림을 전송합니다.
     * @param newBill   새로 등록된 장부 엔티티(Bill)
     * @param extraCount 추가 데이터(예: 추가 건수 등)
     * @return 알림 전송 성공 여부 (성공: true, 실패: false)
     */
    public boolean sendNotificationByToken(Bill newBill, String extraCount) {
        // FCM 알림 제목
        String title = "Bill-us 장부 등록";
        QStore qStore = QStore.store;
        QBill qBill = QBill.bill;
        Long count;
        Long total;

        try {
            // 장부등록에 사용된 매장 UUID로 매장 조회
            Store store = query.selectFrom(qStore)
                    .where(qStore.id.eq(newBill.getStore().getId().toString()))
                    .fetchOne();

            if (store != null) {
                // 오늘 날짜, 매장 기준으로 장부 건수 및 총액 집계
                Tuple tuple = query.select(qBill.store.price.sum(), qBill.count())
                        .from(qBill)
                        .where(qBill.date.eq(newBill.getDate())
                                .and(qBill.store.id.eq(store.getId().toString())))
                        .fetchOne();


                count = tuple.get(qBill.count());
                total = Long.valueOf(tuple.get(qBill.store.price.sum()));


                if (store.getFirebaseToken() != null) {
                    Notification notification = Notification.builder()
                            .setTitle(title)
//                        .setImage("")
                            .build();

                    // Bill, Store, Company, Member 엔티티에서 정보 추출
                    Message message = Message.builder()
                            .setToken(store.getFirebaseToken())
                            .setNotification(notification)
                            .putData("type", "bill")
                            .putData("companyId", newBill.getCompany().getId().toString())
                            .putData("companyName", newBill.getCompany().getCompanyName())
                            .putData("memberName", newBill.getMember().getMemberName())
                            .putData("extraCount", extraCount)
                            .putData("createdDate", newBill.getCreatedDate())
                            .putData("todayCount", String.valueOf(count))
                            .putData("todayTotal", String.valueOf(total))
                            .build();

                    try {
                        firebaseMessaging.send(message);
                        return true;
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}

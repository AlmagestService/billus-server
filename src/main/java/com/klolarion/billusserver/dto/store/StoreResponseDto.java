package com.klolarion.billusserver.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 매장 응답 Dto
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {
    private String storeId;
    private String storeName;
    private String email;
    private String price;
    private String tel;
    private String bizNum;
    private String zoneCode;
    private String address1;
    private String address2;
    private String offCd;
    private String isApplied;
    private String isEnabled;
    private String todayCount;
    private String todayTotal;
    private String isEmailVerified;
}

package com.klolarion.billusserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoResponseDto {
    private String applyId;
    private String targetId;
    private String name;
    private String appliedAt;
    private String totalSum;
    private String count;
    private String account;
    private String errCount;
    private String price;

    private String storeId;
    private String menuId;
    private String date;
    private String storeName;
    private String meal;
    private String menu1;
    private String menu2;
    private String menu3;
    private String menu4;
    private String menu5;
    private String menu6;
    private String menu7;
    private String menu8;
    private String menu9;
    private String menu10;
    private String menu11;
    private String menu12;
}

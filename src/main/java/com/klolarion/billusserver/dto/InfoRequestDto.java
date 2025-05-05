package com.klolarion.billusserver.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoRequestDto {
    private String id;
    @Size(max = 20)
    private String account;
    @Size(max = 50)
    private String email;
    @Size(max = 15)
    private String tel;
    @Size(max = 15)
    private String bizNum;
    @Size(max = 30)
    private String name;
    private String Address;
    private String price;

    private String password;
    private String newPassword;

    private String code;

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

    // 사용자 역할 구분 (STORE, COMPANY)
    private String userRole;
}

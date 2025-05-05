package com.klolarion.billusserver.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequestDto {
    @Size(max = 40)
    private String almagestId;
    @Size(max = 20)
    private String account;
    @Size(min = 8, max = 20)
    private String password;
    @Size(max = 30)
    private String name;
    @Size(max = 50)
    private String email;
    @Size(max = 15)
    private String tel;
    @Size(max = 15)
    private String bizNum;
    //store, company 가입시 주소입력
    @Size(max = 5)
    private String zoneCode;
    @Size(max = 50)
    private String address1;
    @Size(max = 50)
    private String address2;

    private String firebaseToken;

    // 사용자 역할 구분 (STORE, COMPANY, MEMBER, ADMIN)
    private String userRole;
}

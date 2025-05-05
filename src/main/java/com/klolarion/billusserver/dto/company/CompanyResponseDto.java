package com.klolarion.billusserver.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {
    private String companyId;
    private String companyName;
    private String email;
    private String tel;
    private String zoneCode;
    private String address1;
    private String address2;
    private String isEnabled;
    private String offCd;
    private String isApplied;
}

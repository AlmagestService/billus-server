package com.klolarion.billusserver.dto.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDto {
    private String memberId;
    private String memberName;
    private String companyId;
    private String companyName;
    private String storeId;
    private String storeName;
    private String price;
    private String date;
    private String extraCount;
}

package com.klolarion.billusserver.dto.bill;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDto {
    private Integer index;
    private String companyId;
    private String employeeName;
    private String storeName;
    private String companyName;
    private String price;
    private String date;
    private Long count;
}

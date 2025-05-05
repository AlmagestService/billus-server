package com.klolarion.billusserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 공통 응답 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDto<T> {

    // HTTP 응답 코드
    private String status;
    // HTTP 응답 메시지
    private String message;

    // API transaction ID
    private String apiTranId;
    // API transaction datetime
    private String apiTranDtm;

    // 서비스 응답 코드
    private String rspCode;
    private String rspMessage;

    // 요청별 Dto
    private T data;

    public CommonResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }
}


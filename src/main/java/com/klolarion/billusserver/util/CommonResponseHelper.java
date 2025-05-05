package com.klolarion.billusserver.util;

import com.klolarion.billusserver.dto.CommonResponseDto;

public class CommonResponseHelper {
    
    public static <T> CommonResponseDto<T> createResponse(String status, String message, String rspCode, String rspMessage, T data) {
        CommonResponseDto<T> responseDto = new CommonResponseDto<>();
        responseDto.setStatus(status);
        responseDto.setMessage(message);
        responseDto.setRspCode(rspCode);
        responseDto.setRspMessage(rspMessage);
        responseDto.setData(data);
        return responseDto;
    }
} 
package com.klolarion.billusserver.exception.r404;

import com.klolarion.billusserver.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class ApplyNotFoundException extends BaseException {
    public ApplyNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, BaseException.RESOURCE_NOT_FOUND, message);
    }

    public static ApplyNotFoundException notFound(String applyId) {
        return new ApplyNotFoundException(String.format("신청(%s)을 찾을 수 없습니다.", applyId));
    }
} 
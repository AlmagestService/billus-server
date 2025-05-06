package com.klolarion.billusserver.exception.r409;

import org.springframework.http.HttpStatus;

public class DuplicateApplyException extends RuntimeException {
    public DuplicateApplyException(String message) {
        super(message);
    }

    public static DuplicateApplyException duplicate(String applyType) {
        return new DuplicateApplyException(String.format("%s 신청이 이미 존재합니다.", applyType));
    }
} 
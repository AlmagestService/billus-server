package com.klolarion.billusserver.exception.r409;

import com.klolarion.billusserver.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateApplyException extends BaseException {
    public DuplicateApplyException(String message) {
        super(HttpStatus.CONFLICT, BaseException.DUPLICATE_RESOURCE, message);
    }

    public static DuplicateApplyException duplicate(String applyType) {
        return new DuplicateApplyException(String.format("%s 신청이 이미 존재합니다.", applyType));
    }
} 
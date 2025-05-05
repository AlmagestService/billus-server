package com.klolarion.billusserver.exception.r404;

import com.klolarion.billusserver.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class BillNotFoundException extends BaseException {
    public BillNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, BaseException.RESOURCE_NOT_FOUND, message);
    }

    public static BillNotFoundException notFound(String billId) {
        return new BillNotFoundException(String.format("청구서(%s)를 찾을 수 없습니다.", billId));
    }
} 
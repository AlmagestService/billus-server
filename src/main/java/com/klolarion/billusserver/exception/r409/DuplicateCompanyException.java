package com.klolarion.billusserver.exception.r409;

public class DuplicateCompanyException extends RuntimeException {
    public DuplicateCompanyException(String message) {
        super(message);
    }

    public static DuplicateCompanyException duplicate(String companyName) {
        return new DuplicateCompanyException(String.format("회사(%s)가 이미 존재합니다.", companyName));
    }
} 
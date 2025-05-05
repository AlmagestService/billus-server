package com.klolarion.billusserver.exception.r404;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String message) {
        super(message);
    }

    public static CompanyNotFoundException notFound(String companyId) {
        return new CompanyNotFoundException(String.format("회사(%s)를 찾을 수 없습니다.", companyId));
    }
} 
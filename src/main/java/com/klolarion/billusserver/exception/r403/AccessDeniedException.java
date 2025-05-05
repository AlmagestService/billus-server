package com.klolarion.billusserver.exception.r403;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public static AccessDeniedException accessDenied(String message) {
        return new AccessDeniedException(message);
    }
} 
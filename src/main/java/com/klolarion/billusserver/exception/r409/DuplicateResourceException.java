package com.klolarion.billusserver.exception.r409;


public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException duplicate(String resourceName) {
        return new DuplicateResourceException(String.format("%s가 이미 존재합니다.", resourceName));
    }
} 
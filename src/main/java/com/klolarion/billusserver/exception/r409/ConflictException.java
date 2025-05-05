package com.klolarion.billusserver.exception.r409;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public static ConflictException duplicate(String resourceType, String message) {
        return new ConflictException(String.format("%s 중복: %s", resourceType, message));
    }
} 
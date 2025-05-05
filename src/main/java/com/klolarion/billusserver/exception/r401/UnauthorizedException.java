package com.klolarion.billusserver.exception.r401;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException invalidCredentials(String message) {
        return new UnauthorizedException(message);
    }
} 
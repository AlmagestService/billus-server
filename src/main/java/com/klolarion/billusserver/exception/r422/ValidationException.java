package com.klolarion.billusserver.exception.r422;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException invalidInput(String message) {
        return new ValidationException(message);
    }
} 
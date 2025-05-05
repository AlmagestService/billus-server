package com.klolarion.billusserver.exception.r400;

public class InvalidBillDataException extends RuntimeException {
    public InvalidBillDataException(String message) {
        super(message);
    }

    public static InvalidBillDataException invalidData(String message) {
        return new InvalidBillDataException(message);
    }
} 
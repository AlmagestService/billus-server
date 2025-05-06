package com.klolarion.billusserver.exception.r404;

public class BillNotFoundException extends RuntimeException {
    public BillNotFoundException(String message) {
        super(message);
    }
} 
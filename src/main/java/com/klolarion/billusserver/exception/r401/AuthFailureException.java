package com.klolarion.billusserver.exception.r401;

public class AuthFailureException extends RuntimeException {
    private final String count;

    public AuthFailureException(String message) {
        super(message);
        this.count = "0";
    }

    public AuthFailureException(String message, int count) {
        super(message);
        this.count = String.valueOf(count);
    }

    public String getCount() {
        return count;
    }
}

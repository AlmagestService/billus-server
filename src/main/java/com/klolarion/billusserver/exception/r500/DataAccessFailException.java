package com.klolarion.billusserver.exception.r500;

public class DataAccessFailException extends RuntimeException {
    public DataAccessFailException(String message) {
        super(message);
    }

    public static DataAccessFailException databaseError(String message) {
        return new DataAccessFailException(message);
    }
} 
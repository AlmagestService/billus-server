package com.klolarion.billusserver.exception.r500;

public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }

    public static ServerException internal(String message) {
        return new ServerException(message);
    }
} 
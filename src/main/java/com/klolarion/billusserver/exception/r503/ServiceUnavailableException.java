package com.klolarion.billusserver.exception.r503;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public static ServiceUnavailableException serviceUnavailable(String message) {
        return new ServiceUnavailableException(message);
    }
}

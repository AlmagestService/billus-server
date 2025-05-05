package com.klolarion.billusserver.exception.r500;


public class RedisSessionException extends RuntimeException {
    public RedisSessionException(String message) {
        super(message);
    }
}

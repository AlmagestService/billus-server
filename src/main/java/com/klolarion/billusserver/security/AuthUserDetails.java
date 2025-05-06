package com.klolarion.billusserver.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * CustomUserDetails의 공통 인터페이스
 * */
public interface AuthUserDetails extends UserDetails {
    UUID getUserId();
}

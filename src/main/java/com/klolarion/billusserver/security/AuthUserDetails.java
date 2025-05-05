package com.klolarion.billusserver.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * CustomUserDetails의 공통 인터페이스
 * */
public interface AuthUserDetails extends UserDetails {
    String getUserId();
}

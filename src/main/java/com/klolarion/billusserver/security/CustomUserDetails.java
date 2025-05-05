package com.klolarion.billusserver.security;

import com.klolarion.billusserver.domain.entity.Member;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class CustomUserDetails implements AuthUserDetails {

    private Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Member member, Collection<? extends GrantedAuthority> authorities) {
        this.member = member;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Member getMember(){return member;}

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.member.getIsBanned() != null && !this.member.getIsBanned().equals('Y');
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled(){return true;}

    @Override
    public String getUserId() {
        return member.getId();
    }

    public String getAlmagestId(){
        return member.getAlmagestId();
    }
}

package com.klolarion.billusserver.security;


import com.klolarion.billusserver.domain.entity.Store;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;


public class CustomStoreDetails implements AuthUserDetails {

    private Store store;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomStoreDetails(Store store, Collection<? extends GrantedAuthority> authorities) {
        this.store = store;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Store getStore(){return store;}
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
        return this.store.getOffCd() != null && !this.store.getOffCd().equals('Y');
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return store.getIsEnabled() != null && !store.getIsEnabled().equals("Y");
    }

    @Override
    public UUID getUserId() {
        return store.getId();
    }
}

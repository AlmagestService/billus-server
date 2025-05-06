package com.klolarion.billusserver.security;

import com.klolarion.billusserver.domain.entity.Company;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;


public class CustomCompanyDetails implements AuthUserDetails {

    private Company company;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomCompanyDetails(Company company, Collection<? extends GrantedAuthority> authorities) {
        this.company = company;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Company getCompany(){return company;}
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
        return this.company.getOffCd() != null && !this.company.getOffCd().equals('Y');
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return company.getIsEnabled() != null && !company.getIsEnabled().equals("Y");
    }

    @Override
    public UUID getUserId() {
        return this.company.getId();
    }
}

package com.klolarion.billusserver.security;

import com.klolarion.billusserver.domain.entity.Company;
import com.klolarion.billusserver.domain.entity.Member;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.domain.repository.CompanyRepository;
import com.klolarion.billusserver.domain.repository.MemberRepository;
import com.klolarion.billusserver.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CompanyRepository companyRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String almagestId) throws UsernameNotFoundException {

        Optional<Member> savedMember = memberRepository.findByAlmagestId(almagestId);

        if(savedMember.isEmpty()){
            throw new BadRequestException("사용자를 찾을 수 없습니다.");
        }
        Member member = savedMember.get();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(member, authorities);
    }

    public CustomUserDetails loadAdminById(String adminId) throws UsernameNotFoundException {

        Optional<Member> savedMember = memberRepository.findById(adminId);

        if(savedMember.isEmpty()){
            throw new BadRequestException("사용자를 찾을 수 없습니다.");
        }
        Member member = savedMember.get();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new CustomUserDetails(member, authorities);
    }

    public CustomStoreDetails loadStoreById(String id) throws UsernameNotFoundException {
        Optional<Store> savedStore = storeRepository.findById(id);

        if(savedStore.isEmpty()){
            throw new BadRequestException("매장을 찾을 수 없습니다.");
        }
        Store store = savedStore.get();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STORE"));

        return new CustomStoreDetails(store, authorities);
    }

    public CustomCompanyDetails loadCompanyById(String id) throws UsernameNotFoundException {
        Optional<Company> savedCompany = companyRepository.findById(id);

        if(savedCompany.isEmpty()){
            throw new BadRequestException("회사를 찾을 수 없습니다.");
        }
        Company company = savedCompany.get();

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY"));

        return new CustomCompanyDetails(company, authorities);
    }
}

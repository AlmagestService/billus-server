package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findByCompanyAccount(String account);

}

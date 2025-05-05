package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByStoreAccount(String account);
}

package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}

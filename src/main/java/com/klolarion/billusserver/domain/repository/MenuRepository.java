package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}

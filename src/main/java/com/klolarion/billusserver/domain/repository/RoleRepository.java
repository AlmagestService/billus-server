package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

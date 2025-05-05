package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByAlmagestId(String almagestId);

    Optional<Member> findByAccount(String account);
}

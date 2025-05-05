package com.klolarion.billusserver.domain.repository;

import com.klolarion.billusserver.domain.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface OtpRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findOtpByIdAndTargetType(UUID id, String targetType);

}

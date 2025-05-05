package com.klolarion.billusserver.domain.entity;

import lombok.Getter;

/**
 * 직원 등록 신청 상태
 */
@Getter
public enum EmployeeRegistrationStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거부됨");

    private final String description;

    EmployeeRegistrationStatus(String description) {
        this.description = description;
    }
} 
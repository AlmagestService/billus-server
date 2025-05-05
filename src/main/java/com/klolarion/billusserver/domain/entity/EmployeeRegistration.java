package com.klolarion.billusserver.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 직원의 회사 등록 신청을 관리하는 엔티티
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRegistration extends BaseTime {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    @Comment("직원 등록 신청 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    @Comment("신청 대상 회사")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    @Comment("신청한 직원")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Comment("신청 상태")
    private EmployeeRegistrationStatus status;

    /**
     * 신청 상태 설정
     * @param status 새로운 상태
     */
    public void setStatus(EmployeeRegistrationStatus status) {
        this.status = status;
    }
} 
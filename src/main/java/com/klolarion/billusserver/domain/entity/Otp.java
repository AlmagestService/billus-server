package com.klolarion.billusserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OTP(One-Time Password) 인증을 관리하는 엔티티
 * 매장/회사당 하나의 OTP만 관리
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "otp")
public class Otp extends BaseTime {
    @Id
    @Column(name = "target_id", nullable = false, length = 36, columnDefinition = "VARCHAR(36)")
    @Comment("인증 대상 ID (매장 ID 또는 회사 ID)")
    private UUID id;

    @Column(name = "target_type", nullable = false, length = 10, columnDefinition = "VARCHAR(10)")
    @Comment("인증 대상 타입 (STORE/COMPANY)")
    private String targetType;

    @Column(name = "otp_code", nullable = false, length = 6, columnDefinition = "VARCHAR(6)")
    @Comment("OTP 인증 코드")
    private String code;

    @Column(name = "created_time", nullable = false, columnDefinition = "DATETIME")
    @Comment("OTP 생성 시간")
    private LocalDateTime createdTime;

    @Column(name = "expire_time", nullable = false, columnDefinition = "DATETIME")
    @Comment("OTP 만료 시간")
    private LocalDateTime expireTime;

    @Column(name = "is_used", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("사용 여부 (T: 사용됨, F: 미사용)")
    private String isUsed;

    /**
     * OTP가 만료되었는지 확인
     * @return 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * OTP가 사용되었는지 확인
     * @return 사용 여부
     */
    public boolean isUsed() {
        return "T".equals(this.isUsed);
    }

    /**
     * OTP 사용 상태 설정
     * @param used 사용 여부
     */
    public void setUsed(boolean used) {
        this.isUsed = used ? "T" : "F";
    }
}

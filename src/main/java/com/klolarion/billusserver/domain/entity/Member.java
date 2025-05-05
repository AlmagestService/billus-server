package com.klolarion.billusserver.domain.entity;

import com.klolarion.billusserver.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id", columnDefinition = "VARCHAR(36)")
    @Comment("회원 고유 식별자 (UUID)")
    private UUID id;

    @Column(name = "almagest_id", unique = true, nullable = false, length = 40, columnDefinition = "VARCHAR(40)")
    @Comment("Almagest 시스템에서 사용하는 회원 식별자")
    private String almagestId;

    @Column(name = "account", unique = true, length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("관리자 로그인에 사용되는 계정명")
    private String account;

    @Column(name = "password", nullable = false, length = 150, columnDefinition = "VARCHAR(150)")
    @Comment("암호화된 비밀번호")
    private String password;

    @Column(name = "member_name", unique = true, nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    @Comment("회원 실명")
    private String memberName;

    @Column(name = "email", unique = true, nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    @Comment("회원 이메일 주소")
    private String email;

    @Column(name = "tel", unique = true, nullable = false, length = 15, columnDefinition = "VARCHAR(15)")
    @Comment("회원 전화번호")
    private String tel;

    @Column(name = "last_update_date", columnDefinition = "DATETIME")
    @Comment("회원 정보 마지막 수정 일시")
    private LocalDateTime lastUpdateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @Comment("회원 권한 정보")
    private Role role;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @Comment("소속 회사 정보")
    private Company company;

    @Column(name = "is_banned", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("계정 차단 여부 (T: 차단, F: 정상)")
    private String isBanned;

    /**
     * 계정이 차단되었는지 확인
     * @return 차단 여부
     */
    public boolean isBanned() {
        return "T".equals(this.isBanned);
    }

    /**
     * 계정 차단 상태를 설정
     * @param banned 차단 여부
     */
    public void setBanned(boolean banned) {
        this.isBanned = banned ? "T" : "F";
    }

    /**
     * 회원 정보를 Spring Security의 UserDetails로 변환
     * @return CustomUserDetails 객체
     */
    public CustomUserDetails toCustomUserDetails() {
        return new CustomUserDetails(
                this,
                Collections.singletonList(new SimpleGrantedAuthority(this.role.getName()))
        );
    }
}

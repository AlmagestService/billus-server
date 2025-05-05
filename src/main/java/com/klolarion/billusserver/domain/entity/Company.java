package com.klolarion.billusserver.domain.entity;

import com.klolarion.billusserver.security.CustomCompanyDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Company extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column(name = "company_account", unique = true, nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("회사 계정")
    private String companyAccount;

    @Column(nullable = false, length = 150, columnDefinition = "VARCHAR(150)")
    @Comment("회사 비밀번호")
    private String password;

    @Column(name = "company_name", unique = true, nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    @Comment("회사 이름")
    private String companyName;

    @Column(unique = true, nullable = false, length = 8, columnDefinition = "VARCHAR(15)")
    @Comment("회사 전화번호")
    private String tel;

    @Column(unique = true, nullable = false, length = 8, columnDefinition = "VARCHAR(50)")
    @Comment("회사 이메일")
    private String email;

    @OneToOne
    @JoinColumn(name = "biz_num_id")
    @Comment("사업자번호")
    private BizNum bizNum;

    @Column(name = "zone_code", nullable = false, length = 5, columnDefinition = "VARCHAR(5)")
    @Comment("우편번호")
    private String zoneCode;

    @Column(name = "address_1", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    @Comment("주소")
    private String address1;

    @Column(name = "address_2", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    @Comment("상세 주소")
    private String address2;

    @Column(name = "is_email_verified", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("이메일 승인 여부 (T: 승인, F: 미승인)")
    private String isEmailVerified;

    @Column(name = "is_enabled", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("사용 승인 여부 (T: 승인, F: 미승인)")
    private String isEnabled;

    @Column(name = "off_cd", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("삭제 플래그 (T: 삭제, F: 정상)")
    private String offCd;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;


    /**
     * 정보를 UserDetails로 변환
     */
    public CustomCompanyDetails toCustomCompanyDetails() {
        return new CustomCompanyDetails(
                this,
                Collections.singletonList(new SimpleGrantedAuthority(this.role.getName()))
        );
    }

    /**
     * 이메일 인증 여부 확인
     * @return 이메일 인증 여부
     */
    public boolean isEmailVerified() {
        return "T".equals(this.isEmailVerified);
    }

    /**
     * 이메일 인증 상태 설정
     * @param verified 인증 여부
     */
    public void setEmailVerified(boolean verified) {
        this.isEmailVerified = verified ? "T" : "F";
    }

    /**
     * 사용 승인 여부 확인
     * @return 사용 승인 여부
     */
    public boolean isEnabled() {
        return "T".equals(this.isEnabled);
    }

    /**
     * 사용 승인 상태 설정
     * @param enabled 승인 여부
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled ? "T" : "F";
    }

    /**
     * 삭제 여부 확인
     * @return 삭제 여부
     */
    public boolean isOff() {
        return "T".equals(this.offCd);
    }

    /**
     * 삭제 상태 설정
     * @param off 삭제 여부
     */
    public void setOff(boolean off) {
        this.offCd = off ? "T" : "F";
    }
}

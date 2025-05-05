package com.klolarion.billusserver.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 사용 등록 통합 관리 Entity
 * */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Apply extends BaseTime {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    @Comment("등록 신청 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnore
    @Comment("신청한 매장")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    @Comment("신청한 회사")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    @Comment("신청한 회원")
    private Member member;

    @Column(name = "is_approved", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("승인 여부 (T: 승인, F: 미승인)")
    private String isApproved;

    @Column(name = "is_rejected", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("거부 여부 (T: 거부, F: 미거부)")
    private String isRejected;

    @Column(name = "off_cd", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'F'")
    @Comment("삭제 여부 (T: 삭제, F: 정상)")
    private String offCd;

    /**
     * 승인 여부 확인
     * @return 승인 여부
     */
    public boolean isApproved() {
        return "T".equals(this.isApproved);
    }

    /**
     * 승인 상태 설정
     * @param approved 승인 여부
     */
    public void setApproved(boolean approved) {
        this.isApproved = approved ? "T" : "F";
    }

    /**
     * 거부 여부 확인
     * @return 거부 여부
     */
    public boolean isRejected() {
        return "T".equals(this.isRejected);
    }

    /**
     * 거부 상태 설정
     * @param rejected 거부 여부
     */
    public void setRejected(boolean rejected) {
        this.isRejected = rejected ? "T" : "F";
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

package com.klolarion.billusserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 사업자번호 관리 엔티티
 * 매장과 회사의 사업자번호 관리
 * 국세청 API를 통해 유효성을 검증
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BizNum extends BaseTime {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "biz_num_id")
    @Comment("사업자번호 고유 식별자")
    private Long id;

    @Column(name = "biz_num", nullable = false, unique = true, length = 15, columnDefinition = "VARCHAR(10)")
    @Comment("사업자등록번호 (10자리 '-'제외)")
    private String bizNum;
}

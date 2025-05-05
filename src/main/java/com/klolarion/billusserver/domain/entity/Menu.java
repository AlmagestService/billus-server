package com.klolarion.billusserver.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseTime {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    @Comment("메뉴 고유 식별자")
    private Long id;

    @Column(nullable = false, length = 8, columnDefinition = "VARCHAR(8)")
    @Comment("메뉴 날짜 (YYYYMMDD 형식)")
    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnore
    @Comment("메뉴가 속한 매장")
    private Store store;

    @Column(nullable = false, length = 4, columnDefinition = "VARCHAR(4)")
    @Comment("식사 시간 (아침/점심/저녁)")
    private String meal;

    @Column(name = "menu_1", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("메인 메뉴 (필수)")
    private String menu1;

    @Column(name = "menu_2", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 1")
    private String menu2;

    @Column(name = "menu_3", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 2")
    private String menu3;

    @Column(name = "menu_4", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 3")
    private String menu4;

    @Column(name = "menu_5", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 4")
    private String menu5;

    @Column(name = "menu_6", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 5")
    private String menu6;

    @Column(name = "menu_7", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 6")
    private String menu7;

    @Column(name = "menu_8", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 7")
    private String menu8;

    @Column(name = "menu_9", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 8")
    private String menu9;

    @Column(name = "menu_10", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 9")
    private String menu10;

    @Column(name = "menu_11", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 10")
    private String menu11;

    @Column(name = "menu_12", length = 20, columnDefinition = "VARCHAR(20)")
    @Comment("서브 메뉴 11")
    private String menu12;

    /**
     * 메뉴 정보를 업데이트
     * @param meal 식사 시간
     * @param menu1 메인 메뉴
     * @param menu2 서브 메뉴 1
     * @param menu3 서브 메뉴 2
     * @param menu4 서브 메뉴 3
     * @param menu5 서브 메뉴 4
     * @param menu6 서브 메뉴 5
     * @param menu7 서브 메뉴 6
     * @param menu8 서브 메뉴 7
     * @param menu9 서브 메뉴 8
     * @param menu10 서브 메뉴 9
     * @param menu11 서브 메뉴 10
     * @param menu12 서브 메뉴 11
     */
    public void updateMenu(
            String meal, String menu1, String menu2,
            String menu3, String menu4, String menu5,
            String menu6, String menu7, String menu8,
            String menu9, String menu10, String menu11, String menu12) {
        this.meal = meal;
        this.menu1 = menu1;
        this.menu2 = menu2;
        this.menu3 = menu3;
        this.menu4 = menu4;
        this.menu5 = menu5;
        this.menu6 = menu6;
        this.menu7 = menu7;
        this.menu8 = menu8;
        this.menu9 = menu9;
        this.menu10 = menu10;
        this.menu11 = menu11;
        this.menu12 = menu12;
    }
}

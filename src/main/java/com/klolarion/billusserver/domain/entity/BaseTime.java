package com.klolarion.billusserver.domain.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTime {
    private static final String DATE_TIME_PATTERN = "yy.MM.dd HH:mm:ss";
    private static final String SEOUL_TIMEZONE = "Asia/Seoul";

    @CreatedDate
    @Column(updatable = false, columnDefinition = "VARCHAR(20)")
    @Comment("엔티티 생성 일시")
    private String createdDate;

    @LastModifiedDate
    @Column(columnDefinition = "VARCHAR(20)")
    @Comment("엔티티 마지막 수정 일시")
    private String lastModifiedDate;

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(SEOUL_TIMEZONE));
    }

    @PrePersist
    public void onPrePersist() {
        this.createdDate = getCurrentDateTime();
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.lastModifiedDate = getCurrentDateTime();
    }

    private String getCurrentDateTime() {
        return ZonedDateTime.now(ZoneId.of(SEOUL_TIMEZONE))
                .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }
}
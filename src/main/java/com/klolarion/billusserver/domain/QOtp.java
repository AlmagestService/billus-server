package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.klolarion.billusserver.domain.entity.Otp;

@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOtp extends EntityPathBase<Otp> {

    private static final long serialVersionUID = 1L;

    public static final QOtp otp = new QOtp("otp");

    public final StringPath id = createString("id");

    public final StringPath targetType = createString("targetType");

    public final StringPath code = createString("code");

    public final StringPath isUsed = createString("isUsed");

    public final DateTimePath<java.time.LocalDateTime> expireTime = createDateTime("expireTime", java.time.LocalDateTime.class);

    public QOtp(String variable) {
        super(Otp.class, forVariable(variable));
    }

    public QOtp(Path<? extends Otp> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtp(PathMetadata metadata) {
        super(Otp.class, metadata);
    }
} 
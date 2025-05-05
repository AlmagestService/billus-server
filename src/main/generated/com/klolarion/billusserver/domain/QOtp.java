package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Otp;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtp is a Querydsl query type for Otp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOtp extends EntityPathBase<Otp> {

    private static final long serialVersionUID = -1191072251L;

    public static final QOtp otp = new QOtp("otp");

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expireTime = createDateTime("expireTime", java.time.LocalDateTime.class);

    public final BooleanPath used = createBoolean("used");

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


package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.BizNum;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBizNum is a Querydsl query type for BizNum
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBizNum extends EntityPathBase<BizNum> {

    private static final long serialVersionUID = 1404408921L;

    public static final QBizNum bizNum1 = new QBizNum("bizNum1");

    public final StringPath bizNum = createString("bizNum");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath registered = createString("registered");

    public QBizNum(String variable) {
        super(BizNum.class, forVariable(variable));
    }

    public QBizNum(Path<? extends BizNum> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBizNum(PathMetadata metadata) {
        super(BizNum.class, metadata);
    }

}


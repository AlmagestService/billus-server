package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Closing;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClosing is a Querydsl query type for Closing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClosing extends EntityPathBase<Closing> {

    private static final long serialVersionUID = 1551326863L;

    public static final QClosing closing = new QClosing("closing");

    public final StringPath closingRange = createString("closingRange");

    public final NumberPath<Long> companyId = createNumber("companyId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final NumberPath<Long> storeId = createNumber("storeId", Long.class);

    public final StringPath totalSalesAmount = createString("totalSalesAmount");

    public final StringPath type = createString("type");

    public QClosing(String variable) {
        super(Closing.class, forVariable(variable));
    }

    public QClosing(Path<? extends Closing> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClosing(PathMetadata metadata) {
        super(Closing.class, metadata);
    }

}


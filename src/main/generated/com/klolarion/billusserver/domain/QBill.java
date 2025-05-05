package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Bill;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBill is a Querydsl query type for Bill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBill extends EntityPathBase<Bill> {

    private static final long serialVersionUID = 1731068013L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBill bill = new QBill("bill");

    public final QBaseTime _super = new QBaseTime(this);

    public final QCompany company;

    //inherited
    public final StringPath createdDate = _super.createdDate;

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedDate = _super.lastModifiedDate;

    public final QMember member;

    public final QStore store;

    public QBill(String variable) {
        this(Bill.class, forVariable(variable), INITS);
    }

    public QBill(Path<? extends Bill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBill(PathMetadata metadata, PathInits inits) {
        this(Bill.class, metadata, inits);
    }

    public QBill(Class<? extends Bill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompany(forProperty("company"), inits.get("company")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}


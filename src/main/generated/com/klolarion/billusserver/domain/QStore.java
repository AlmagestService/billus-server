package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Store;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = 2139531579L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStore store = new QStore("store");

    public final QBaseTime _super = new QBaseTime(this);

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final QBizNum bizNum;

    //inherited
    public final StringPath createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final StringPath firebaseToken = createString("firebaseToken");

    public final StringPath id = createString("id");

    public final StringPath isEmailVerified = createString("isEmailVerified");

    public final StringPath isEnabled = createString("isEnabled");

    //inherited
    public final StringPath lastModifiedDate = _super.lastModifiedDate;

    public final StringPath offCd = createString("offCd");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final QRole role;

    public final StringPath serviceId = createString("serviceId");

    public final StringPath storeAccount = createString("storeAccount");

    public final StringPath storeName = createString("storeName");

    public final StringPath tel = createString("tel");

    public final StringPath zoneCode = createString("zoneCode");

    public QStore(String variable) {
        this(Store.class, forVariable(variable), INITS);
    }

    public QStore(Path<? extends Store> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStore(PathMetadata metadata, PathInits inits) {
        this(Store.class, metadata, inits);
    }

    public QStore(Class<? extends Store> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bizNum = inits.isInitialized("bizNum") ? new QBizNum(forProperty("bizNum")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
    }

}


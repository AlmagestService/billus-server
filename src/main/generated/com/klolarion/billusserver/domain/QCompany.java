package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Company;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompany is a Querydsl query type for Company
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompany extends EntityPathBase<Company> {

    private static final long serialVersionUID = 1635270231L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompany company = new QCompany("company");

    public final QBaseTime _super = new QBaseTime(this);

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final QBizNum bizNum;

    public final StringPath companyAccount = createString("companyAccount");

    public final StringPath companyName = createString("companyName");

    //inherited
    public final StringPath createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    public final StringPath isEmailVerified = createString("isEmailVerified");

    public final StringPath isEnabled = createString("isEnabled");

    //inherited
    public final StringPath lastModifiedDate = _super.lastModifiedDate;

    public final StringPath offCd = createString("offCd");

    public final StringPath password = createString("password");

    public final QRole role;

    public final StringPath tel = createString("tel");

    public final StringPath zoneCode = createString("zoneCode");

    public QCompany(String variable) {
        this(Company.class, forVariable(variable), INITS);
    }

    public QCompany(Path<? extends Company> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompany(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompany(PathMetadata metadata, PathInits inits) {
        this(Company.class, metadata, inits);
    }

    public QCompany(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bizNum = inits.isInitialized("bizNum") ? new QBizNum(forProperty("bizNum")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
    }

}


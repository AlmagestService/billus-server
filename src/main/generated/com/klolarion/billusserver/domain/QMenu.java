package com.klolarion.billusserver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.klolarion.billusserver.domain.entity.Menu;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenu is a Querydsl query type for Menu
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenu extends EntityPathBase<Menu> {

    private static final long serialVersionUID = 1731391941L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenu menu = new QMenu("menu");

    public final QBaseTime _super = new QBaseTime(this);

    //inherited
    public final StringPath createdDate = _super.createdDate;

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedDate = _super.lastModifiedDate;

    public final StringPath meal = createString("meal");

    public final StringPath menu1 = createString("menu1");

    public final StringPath menu10 = createString("menu10");

    public final StringPath menu11 = createString("menu11");

    public final StringPath menu12 = createString("menu12");

    public final StringPath menu2 = createString("menu2");

    public final StringPath menu3 = createString("menu3");

    public final StringPath menu4 = createString("menu4");

    public final StringPath menu5 = createString("menu5");

    public final StringPath menu6 = createString("menu6");

    public final StringPath menu7 = createString("menu7");

    public final StringPath menu8 = createString("menu8");

    public final StringPath menu9 = createString("menu9");

    public final QStore store;

    public QMenu(String variable) {
        this(Menu.class, forVariable(variable), INITS);
    }

    public QMenu(Path<? extends Menu> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMenu(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMenu(PathMetadata metadata, PathInits inits) {
        this(Menu.class, metadata, inits);
    }

    public QMenu(Class<? extends Menu> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}


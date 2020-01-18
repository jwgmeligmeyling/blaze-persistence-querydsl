package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Operator;

public enum JPQLNextOps implements Operator {
    PAGE_POSITION(Long.class),
    ENTITY_FUNCTION(Object.class),
    SET_UNION(Object.class),
    SET_UNION_ALL(Object.class),
    SET_INTERSECT(Object.class),
    SET_INTERSECT_ALL(Object.class),
    SET_EXCEPT(Object.class),
    SET_EXCEPT_ALL(Object.class),
    GROUP_CONCAT(String.class),
    WINDOW_GROUP_CONCAT(String.class),
    GREATEST(Object.class),
    LEAST(Object.class),
    REPEAT(String.class),
    ROW_VALUES(Boolean.class),
    ROW_NUMBER(Object.class),
    RANK(Object.class),
    DENSE_RANK(Object.class),
    PERCENT_RANK(Object.class),
    CUME_DIST(Object.class),
    NTILE(Object.class),
    LEAD(Object.class),
    LAG(Object.class),
    FIRST_VALUE(Object.class),
    LAST_VALUE(Object.class),
    NTH_VALUE(Object.class),
    WITH_ALIAS(Object.class),
    WITH_COLUMNS(Object.class),
    WITH_RECURSIVE_COLUMNS(Object.class);

    private final Class<?> type;

    private JPQLNextOps(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

}

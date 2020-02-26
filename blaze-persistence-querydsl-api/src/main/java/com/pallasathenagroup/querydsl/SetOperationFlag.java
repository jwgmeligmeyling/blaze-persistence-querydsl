package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;

public class SetOperationFlag extends QueryFlag {

    public SetOperationFlag(Expression<?> flag) {
        super(Position.START_OVERRIDE, flag);
    }

    public static SetOperationFlag getSetOperationFlag(QueryMetadata queryMetadata) {
        return queryMetadata.getFlags().stream().filter(SetOperationFlag.class::isInstance).map(SetOperationFlag.class::cast).findAny().orElse(null);
    }

}

package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.BlazeJPAQuery;
import com.pallasathenagroup.querydsl.api.FullQueryBuilder;
import com.pallasathenagroup.querydsl.api.PaginatedCriteriaBuilder;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;

public class AbstractFullQueryBuilder<T, X extends FullQueryBuilder<T, X>> extends AbstractQueryBuilder<T, X> implements FullQueryBuilder<T, X> {

    public AbstractFullQueryBuilder(BlazeJPAQuery<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public QueryMetadata getMetadata() {
        return blazeJPAQuery.getMetadata();
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return blazeJPAQuery.accept(v, context);
    }

    @Override
    public Class<? extends T> getType() {
        return blazeJPAQuery.getType();
    }

}

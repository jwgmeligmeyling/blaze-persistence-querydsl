package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.BaseFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.CommonQueryBuilder;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import java.util.List;

public abstract class BaseFinalSetOperationCriteriaBuilderImpl<T, Q extends BaseFinalSetOperationBuilder<T, Q> & CommonQueryBuilder<Q>>
        extends BaseFinalSetOperationBuilderImpl<T, Q>
        implements BaseOngoingFinalSetOperationBuilder<T, Q> {

    protected final SetExpression<T> blazeJPAQuery;
    protected final Q self = (Q) this;

    public BaseFinalSetOperationCriteriaBuilderImpl(SetExpression<T> blazeJPAQuery) {
        this.blazeJPAQuery = blazeJPAQuery;
    }

    public Q limit(long l) {
        blazeJPAQuery.limit(l);
        return self;
    }

    public Q offset(long l) {
        blazeJPAQuery.offset(l);
        return self;
    }

    public Q restrict(QueryModifiers queryModifiers) {
//        blazeJPAQuery.restrict(queryModifiers);
        return self;
    }

    public Q orderBy(OrderSpecifier<?>... orderSpecifiers) {
        blazeJPAQuery.orderBy(orderSpecifiers);
        return self;
    }


    public <U> Q set(ParamExpression<U> paramExpression, U u) {
//        blazeJPAQuery.set(paramExpression, u);
        return self;
    }


    public String getQueryString() {
        return blazeJPAQuery.getQueryString();
    }


    public TypedQuery<T> getQuery() {
        throw new UnsupportedOperationException();
    }


    public List<T> getResultList() {
        return blazeJPAQuery.fetch();
    }


    public T getSingleResult() {
        return blazeJPAQuery.fetchOne();
    }

    public QueryMetadata getMetadata() {
        return blazeJPAQuery.getMetadata();
    }

    @Nullable
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return blazeJPAQuery.accept(v, context);
    }

    public Class<? extends T> getType() {
        return blazeJPAQuery.getType();
    }
}
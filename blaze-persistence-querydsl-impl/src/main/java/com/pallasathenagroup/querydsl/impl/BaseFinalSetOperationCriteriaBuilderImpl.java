package com.pallasathenagroup.querydsl.impl;

import com.blazebit.persistence.LimitBuilder;
import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.BaseOngoingFinalSetOperationBuilder;
import com.pallasathenagroup.querydsl.api.OrderByBuilder;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Visitor;

import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import java.util.List;

public abstract class BaseFinalSetOperationCriteriaBuilderImpl<X, Y extends LimitBuilder<Y> & OrderByBuilder<Y>, T>
        extends BaseFinalSetOperationBuilderImpl<X, Y, T>
        implements BaseOngoingFinalSetOperationBuilder<X, Y> {

    protected final SetExpression<T> blazeJPAQuery;
    protected final Y self = (Y) this;

    public BaseFinalSetOperationCriteriaBuilderImpl(SetExpression<T> blazeJPAQuery) {
        this.blazeJPAQuery = blazeJPAQuery;
    }

    @Override
    public Y setFirstResult(int i) {
        blazeJPAQuery.offset(i);
        return self;
    }

    @Override
    public Y setMaxResults(int i) {
        blazeJPAQuery.limit(i);
        return self;
    }

    @Override
    public int getFirstResult() {
        return blazeJPAQuery.getMetadata().getModifiers().getOffsetAsInteger();
    }

    @Override
    public int getMaxResults() {
        return blazeJPAQuery.getMetadata().getModifiers().getLimitAsInteger();
    }

    @Override
    public Y orderBy(OrderSpecifier<?>... orderSpecifiers) {
        blazeJPAQuery.orderBy(orderSpecifiers);
        return self;
    }

    public <U> Y set(ParamExpression<U> paramExpression, U u) {
//        blazeJPAQuery.set(paramExpression, u);
        return self;
    }


    public String getQueryString() {
        return blazeJPAQuery.getQueryString();
    }


    public TypedQuery<X> getQuery() {
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

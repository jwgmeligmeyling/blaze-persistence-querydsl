package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCTECriteriaBuilder;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;

import java.util.function.Function;

public class FinalSetOperationCTECriteriaBuilderImpl<X, T> implements FinalSetOperationCTECriteriaBuilder<X> {

    private final SetExpression<T> setExpression;
    private final Function<SubQueryExpression<T>, X> finalizer;

    public FinalSetOperationCTECriteriaBuilderImpl(SetExpression<T> setExpression, Function<SubQueryExpression<T>, X> finalizer) {
        this.setExpression = setExpression;
        this.finalizer = finalizer;
    }

    @Override
    public X end() {
        return finalizer.apply(setExpression);
    }

    @Override
    public FinalSetOperationCTECriteriaBuilder<X> setFirstResult(int i) {
        setExpression.offset(i);
        return this;
    }

    @Override
    public FinalSetOperationCTECriteriaBuilder<X> setMaxResults(int i) {
        setExpression.limit(i);
        return this;
    }

    @Override
    public int getFirstResult() {
        return setExpression.getMetadata().getModifiers().getOffsetAsInteger();
    }

    @Override
    public int getMaxResults() {
        return setExpression.getMetadata().getModifiers().getLimitAsInteger();
    }

    @Override
    public FinalSetOperationCTECriteriaBuilder<X> orderBy(OrderSpecifier<?>... orderSpecifiers) {
        setExpression.orderBy(orderSpecifiers);
        return this;
    }
}

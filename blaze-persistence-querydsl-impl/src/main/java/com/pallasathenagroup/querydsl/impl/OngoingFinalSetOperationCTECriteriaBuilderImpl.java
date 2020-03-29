package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCTECriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCTECriteriaBuilder;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;

public abstract class OngoingFinalSetOperationCTECriteriaBuilderImpl<X, T>
        extends BaseFinalSetOperationBuilderImpl<X, OngoingFinalSetOperationCTECriteriaBuilder<X>, T>
        implements OngoingFinalSetOperationCTECriteriaBuilder<X> {

    protected final SetExpression<T> setExpression;

    public OngoingFinalSetOperationCTECriteriaBuilderImpl(SetExpression<T> setExpression) {
        this.setExpression = setExpression;
    }

    @Override
    public OngoingFinalSetOperationCTECriteriaBuilder<X> setFirstResult(int i) {
        setExpression.offset(i);
        return this;
    }

    @Override
    public OngoingFinalSetOperationCTECriteriaBuilder<X> setMaxResults(int i) {
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
    public OngoingFinalSetOperationCTECriteriaBuilder<X> orderBy(OrderSpecifier<?>... orderSpecifiers) {
        setExpression.orderBy(orderSpecifiers);
        return this;
    }

}

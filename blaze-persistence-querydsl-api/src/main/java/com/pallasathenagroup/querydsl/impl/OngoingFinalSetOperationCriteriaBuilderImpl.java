package com.pallasathenagroup.querydsl.impl;

import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.OngoingFinalSetOperationCriteriaBuilder;

public class OngoingFinalSetOperationCriteriaBuilderImpl<X, T>
        extends BaseFinalSetOperationCriteriaBuilderImpl<X,
        OngoingFinalSetOperationCriteriaBuilder<X>, T> implements OngoingFinalSetOperationCriteriaBuilder<X>  {

    public OngoingFinalSetOperationCriteriaBuilderImpl(SetExpression<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public X endSet() {
        return null;
    }
}

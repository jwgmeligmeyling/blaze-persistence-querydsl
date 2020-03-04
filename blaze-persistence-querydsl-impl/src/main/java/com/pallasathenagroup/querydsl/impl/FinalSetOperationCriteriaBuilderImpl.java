package com.pallasathenagroup.querydsl.impl;


import com.pallasathenagroup.querydsl.SetExpression;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;

public class FinalSetOperationCriteriaBuilderImpl<T> extends BaseFinalSetOperationCriteriaBuilderImpl<T, FinalSetOperationCriteriaBuilder<T>, T> implements FinalSetOperationCriteriaBuilder<T> {

    public FinalSetOperationCriteriaBuilderImpl(SetExpression<T> blazeJPAQuery) {
        super(blazeJPAQuery);
    }

    @Override
    public T endSet() {
        throw new UnsupportedOperationException();
    }

}

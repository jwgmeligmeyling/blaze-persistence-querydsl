package com.pallasathenagroup.querydsl.api;

public interface OngoingSetOperationCriteriaBuilder<X, Y, T> extends MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, BaseCriteriaBuilder<T, OngoingSetOperationCriteriaBuilder<X, Y, T>> {

    @Override
    Y endSet();
}

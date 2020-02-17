package com.pallasathenagroup.querydsl.api;

public interface MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> extends OngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<X, Y, T>, Y, StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>> {

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y, T> endSetWith();
}
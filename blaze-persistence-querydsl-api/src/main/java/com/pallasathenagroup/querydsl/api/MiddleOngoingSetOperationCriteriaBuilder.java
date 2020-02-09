package com.pallasathenagroup.querydsl.api;

public interface MiddleOngoingSetOperationCriteriaBuilder<T, Y> extends OngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<T, Y>, Y, StartOngoingSetOperationCriteriaBuilder<T, MiddleOngoingSetOperationCriteriaBuilder<T, Y>>> {

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y> endSetWith();
}
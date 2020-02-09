package com.pallasathenagroup.querydsl.api;

public interface StartOngoingSetOperationCriteriaBuilder<X, Y, T> extends
        MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>,
        StartOngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<X, Y, T>, Y, StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>>,
        BaseCriteriaBuilder<T, StartOngoingSetOperationCriteriaBuilder<X, Y, T>> {

}

package com.pallasathenagroup.querydsl.api;

public interface StartOngoingSetOperationCriteriaBuilder<X, Y> extends MiddleOngoingSetOperationCriteriaBuilder<X, Y>, StartOngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<X, Y>, Y, StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y>>>, BaseCriteriaBuilder<X, StartOngoingSetOperationCriteriaBuilder<X, Y>> {

}

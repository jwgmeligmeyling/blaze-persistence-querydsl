package com.pallasathenagroup.querydsl.api;

public interface LeafOngoingSetOperationCriteriaBuilder<T>
        extends BaseOngoingSetOperationBuilder<LeafOngoingSetOperationCriteriaBuilder<T>,
        FinalSetOperationCriteriaBuilder<T>,
        StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>>,
        BaseCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T>> {

    @Override
    FinalSetOperationCriteriaBuilder<T> endSet();
}

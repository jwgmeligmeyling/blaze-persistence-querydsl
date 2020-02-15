package com.pallasathenagroup.querydsl.api;

public interface LeafOngoingSetOperationCriteriaBuilder<T /* , X */>
        extends BaseOngoingSetOperationBuilder<LeafOngoingSetOperationCriteriaBuilder<T /* , X */>,
        FinalSetOperationCriteriaBuilder<T>,
        StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T /* , X */>>>,
        BaseCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T /*, X */>> {
}

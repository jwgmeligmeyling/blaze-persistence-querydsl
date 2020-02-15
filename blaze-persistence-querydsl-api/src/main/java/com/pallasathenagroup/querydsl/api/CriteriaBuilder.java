package com.pallasathenagroup.querydsl.api;


public interface CriteriaBuilder<T, X extends CriteriaBuilder<T, X>> extends
        FullQueryBuilder<T, X>,
        BaseCriteriaBuilder<T, X>,
        CTEBuilder<X>,
        SetOperationBuilder<
                LeafOngoingSetOperationCriteriaBuilder<T>,
                StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>>> {
}

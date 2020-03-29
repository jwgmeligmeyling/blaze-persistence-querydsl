package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.BaseOngoingSetOperationBuilder;

/**
 * An interface for builders that support set operators.
 *
 * @param <T> The query result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface LeafOngoingSetOperationCriteriaBuilder<T>
        extends BaseOngoingSetOperationBuilder<LeafOngoingSetOperationCriteriaBuilder<T>,
                FinalSetOperationCriteriaBuilder<T>,
                StartOngoingSetOperationCriteriaBuilder<T, LeafOngoingFinalSetOperationCriteriaBuilder<T>, T>>,
        BaseCriteriaBuilder<T, LeafOngoingSetOperationCriteriaBuilder<T>> {

    @Override
    FinalSetOperationCriteriaBuilder<T> endSet();
}

package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.BaseOngoingSetOperationBuilder;

/**
 * An interface for builders that support set operators.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface LeafOngoingFinalSetOperationCriteriaBuilder<X> extends
        BaseOngoingSetOperationBuilder<
                        LeafOngoingSetOperationCriteriaBuilder<X>,
                        FinalSetOperationCriteriaBuilder<X>,
                        StartOngoingSetOperationCriteriaBuilder<X, LeafOngoingFinalSetOperationCriteriaBuilder<X>, X>> {
}

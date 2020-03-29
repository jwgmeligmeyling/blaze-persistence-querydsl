package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.StartOngoingSetOperationBuilder;

/**
 * An interface for builders that support set operators.
 *
 * @param <X> The concrete builder type
 * @param <Y> The set sub-operation result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface StartOngoingSetOperationCTECriteriaBuilder<X, Y> extends MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>, StartOngoingSetOperationBuilder<OngoingSetOperationCTECriteriaBuilder<X, Y>, Y, StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>>, SelectBaseCTECriteriaBuilder<StartOngoingSetOperationCTECriteriaBuilder<X, Y>> {
}

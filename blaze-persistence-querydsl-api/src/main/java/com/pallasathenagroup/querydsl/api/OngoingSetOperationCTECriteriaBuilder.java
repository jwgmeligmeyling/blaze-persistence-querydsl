package com.pallasathenagroup.querydsl.api;

/**
 * An interface for builders that support set operators.
 *
 * @param <X> The builder result type
 * @param <Y> The set sub-operation result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface OngoingSetOperationCTECriteriaBuilder<X, Y> extends MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>, SelectBaseCTECriteriaBuilder<OngoingSetOperationCTECriteriaBuilder<X, Y>> {
}

package com.pallasathenagroup.querydsl.api;

/**
 * An interface for builders that support set operators.
 *
 * @param <X> The builder result type
 * @param <Y> The set sub-operation result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface MiddleOngoingSetOperationCTECriteriaBuilder<X, Y> extends OngoingSetOperationBuilder<OngoingSetOperationCTECriteriaBuilder<X, Y>, Y, StartOngoingSetOperationCTECriteriaBuilder<X, MiddleOngoingSetOperationCTECriteriaBuilder<X, Y>>> {

    /**
     * Finishes the current set operation builder and returns a final builder for ordering and limiting.
     *
     * @return The final builder for ordering and limiting
     */
    public OngoingFinalSetOperationCTECriteriaBuilder<Y> endSetWith();
}

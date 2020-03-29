package com.pallasathenagroup.querydsl.api;

/**
 * An interface for builders that support set operators.
 *
 * @param <T> The builder result type
 * @param <Y> The set sub-operation result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface MiddleOngoingSetOperationCriteriaBuilder<X, Y, T> extends OngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<X, Y, T>, Y, StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>> {

    @Override
    public OngoingFinalSetOperationCriteriaBuilder<Y> endSetWith();
}
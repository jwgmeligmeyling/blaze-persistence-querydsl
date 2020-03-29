package com.pallasathenagroup.querydsl.api;

/**
 * An interface for builders that support set operators.
 *
 * @param <T> The builder result type
 * @param <Y> The set sub-operation result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface OngoingSetOperationCriteriaBuilder<X, Y, T> extends MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, BaseCriteriaBuilder<T, OngoingSetOperationCriteriaBuilder<X, Y, T>> {

    @Override
    Y endSet();
}

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
public interface StartOngoingSetOperationCriteriaBuilder<X, Y, T> extends
        MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>,
        StartOngoingSetOperationBuilder<OngoingSetOperationCriteriaBuilder<X, Y, T>, Y, StartOngoingSetOperationCriteriaBuilder<X, MiddleOngoingSetOperationCriteriaBuilder<X, Y, T>, T>>,
        BaseCriteriaBuilder<T, StartOngoingSetOperationCriteriaBuilder<X, Y, T>> {}

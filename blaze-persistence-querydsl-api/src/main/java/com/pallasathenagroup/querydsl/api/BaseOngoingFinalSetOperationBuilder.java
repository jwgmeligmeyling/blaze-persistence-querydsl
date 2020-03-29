package com.pallasathenagroup.querydsl.api;


import com.blazebit.persistence.LimitBuilder;

/**
 * An interface for builders that support set operators.
 *
 * @param <T> The builder result type
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface BaseOngoingFinalSetOperationBuilder<T, X extends LimitBuilder<X> & OrderByBuilder<X>> extends LimitBuilder<X>, OrderByBuilder<X> {

    /**
     * Ends the set operation and returns the parent builder.
     *
     * @return The parent builder
     */
    public T endSet();
}

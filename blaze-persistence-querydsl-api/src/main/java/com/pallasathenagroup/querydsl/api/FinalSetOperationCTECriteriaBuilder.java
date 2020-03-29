package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.LimitBuilder;

/**
 * An interface for builders that support set operators.
 *
 * @param <X> The builder result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FinalSetOperationCTECriteriaBuilder<X> extends OrderByBuilder<FinalSetOperationCTECriteriaBuilder<X>>, LimitBuilder< FinalSetOperationCTECriteriaBuilder<X>> {

    /**
     * Finishes the CTE builder.
     *
     * @return The parent query builder
     */
    public X end();

}
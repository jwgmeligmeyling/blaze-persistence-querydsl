package com.pallasathenagroup.querydsl.api;


import com.blazebit.persistence.SetOperationBuilder;

/**
 * A builder for cte criteria queries. This is the entry point for building cte queries.
 *
 * @param <X> The result type which is returned after the CTE builder
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FullSelectCTECriteriaBuilder<X, T>  extends
        SelectBaseCTECriteriaBuilder<FullSelectCTECriteriaBuilder<X, T>>,
        SetOperationBuilder<LeafOngoingSetOperationCTECriteriaBuilder<X>, StartOngoingSetOperationCTECriteriaBuilder<X, LeafOngoingFinalSetOperationCTECriteriaBuilder<X>>> {

    /**
     * Finishes the CTE builder.
     *
     * @return The parent query builder
     */
    public X end();

}

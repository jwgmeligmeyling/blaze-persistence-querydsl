package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Path;

/**
 * A builder for cte criteria queries that select. This is the entry point for building cte queries.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface SelectBaseCTECriteriaBuilder<X extends SelectBaseCTECriteriaBuilder<X>> extends BaseCTECriteriaBuilder<X> {

    /**
     * Starts a select builder for building an expression to bind to the CTE attribute.
     *
     * @param cteAttribute The CTE attribute to which the resulting expression should be bound
     * @return A select builder for building an expression
     */
    <T> SelectBuilder<X, T> bind(Path<T> cteAttribute);

}

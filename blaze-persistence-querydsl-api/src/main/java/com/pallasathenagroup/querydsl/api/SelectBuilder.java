package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Expression;

/**
 * An interface for builders that support selecting.
 * This is related to the fact, that a query builder supports select clauses.
 *
 * @param <X> The result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface SelectBuilder<X, T> {

    /**
     * Adds a select clause with the given expression to the query.
     *
     * @param expression The expression for the select clause
     * @return The query builder for chaining calls
     */
    X select(Expression<? extends T> expression);
}

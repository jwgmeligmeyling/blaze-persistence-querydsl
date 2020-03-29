package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.OrderSpecifier;

/**
 * An interface for builders that support sorting.
 * This is related to the fact, that a query builder supports order by clauses.
 *
 * @param <Q> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface OrderByBuilder<Q extends OrderByBuilder<Q>> {

    /**
     * Add order expressions
     *
     * @param orderSpecifiers order
     * @return the current object
     */
    Q orderBy(OrderSpecifier<?>... orderSpecifiers);

}

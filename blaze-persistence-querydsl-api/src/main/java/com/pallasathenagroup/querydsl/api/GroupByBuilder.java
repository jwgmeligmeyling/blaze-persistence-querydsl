package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

/**
 * An interface for builders that support group by.
 * This is related to the fact, that a query builder supports group by clauses.
 *
 * @param <Q> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface GroupByBuilder<Q extends GroupByBuilder<Q>> {

    /**
     * Add grouping/aggregation expressions
     *
     * @param expressions group by expressions
     * @return the current object
     */
    Q groupBy(Expression<?>... expressions);

    /**
     * Add filters for aggregation
     *
     * @param predicates having conditions
     * @return the current object
     */
    Q having(Predicate... predicates);
}

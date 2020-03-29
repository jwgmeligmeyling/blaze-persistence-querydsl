package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Predicate;

/**
 * An interface for builders that support filtering. This is related to the
 * fact, that a query builder supports where clauses.
 *
 * @param <Q> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface WhereBuilder<Q extends WhereBuilder<Q>> {

    /**
     * Adds the given filter conditions
     *
     * <p>Skips null arguments</p>
     *
     * @param predicates filter conditions to be added
     * @return the current object
     */
    Q where(Predicate... predicates);
}

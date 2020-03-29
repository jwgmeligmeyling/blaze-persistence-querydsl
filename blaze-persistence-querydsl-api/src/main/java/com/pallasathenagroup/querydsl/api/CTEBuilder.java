package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.EntityPath;

/**
 * An interface for builders that support CTEs.
 * This is related to the fact, that a query builder supports the with clause.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface CTEBuilder<X extends CTEBuilder<X>> {

    /**
     * Creates a builder for a CTE with the given CTE type.
     *
     * @param entityPath The type of the CTE
     * @return The CTE builder
     */
    <T> FullSelectCTECriteriaBuilder<X, T> with(EntityPath<T> entityPath);

    /**
     * Creates a builder for a recursive CTE with the given CTE type.
     *
     * @param entityPath The type of the CTE
     * @return The recursive CTE builder
     */
    <T> FullSelectCTECriteriaBuilder<X, T> withRecursive(EntityPath<T> entityPath);

}

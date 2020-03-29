package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.PagedList;

/**
 * A builder for paginated criteria queries.
 *
 * @param <T> The query result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface PaginatedCriteriaBuilder<T> extends FullQueryBuilder<T, PaginatedCriteriaBuilder<T>> {

    /**
     * Execute the query and return the result as a type PagedList.
     *
     * @return The paged list of the results
     */
    @Override
    public PagedList<T> getResultList();

}

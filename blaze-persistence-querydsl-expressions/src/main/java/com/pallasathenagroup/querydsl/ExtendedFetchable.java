package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.Queryable;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;

import java.util.Optional;

/**
 * Extension for {@code Fetchable}
 *
 * @param <T> query return type.
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface ExtendedFetchable<T> extends Fetchable<T>  {

    /**
     * Execute the query and return the result as a type PagedList.
     *
     * @param firstResult The position of the first result to retrieve, numbered from 0
     * @param maxResults The maximum number of results to retrieve
     * @return The paged list of the results
     * @since 1.0
     * @see com.blazebit.persistence.FullQueryBuilder#page(int, int)
     */
    PagedList<T> fetchPage(int firstResult, int maxResults);

    /**
     * Execute the query and return the result as a type PagedList.
     *
     * @param keysetPage The key set from a previous result, may be null
     * @param firstResult The position of the first result to retrieve, numbered from 0
     * @param maxResults The maximum number of results to retrieve
     * @return The paged list of the results
     * @since 1.0
     * @see com.blazebit.persistence.FullQueryBuilder#page(KeysetPage, int, int)
     */
    PagedList<T> fetchPage(KeysetPage keysetPage, int firstResult, int maxResults);

    /**
     * Wrap the result of {@link #fetchOne()} inside an {@code Optional}.
     *
     * @return Optional query result
     * @throws NonUniqueResultException If the result was not unique
     * @since 1.0
     */
    default Optional<T> fetchOneOptional() throws NonUniqueResultException {
        return Optional.ofNullable(fetchOne());
    }
    /**
     * Wrap the result of {@link #fetchFirst()} inside an {@code Optional}.
     *
     * @return Optional query result
     * @since 1.0
     */
    default Optional<T> fetchFirstOptional() {
        return Optional.ofNullable(fetchFirst());
    }

    /**
     * Get the query string.
     *
     * @return the query string
     * @see Queryable#getQueryString()
     * @since 1.0
     */
    String getQueryString();

}

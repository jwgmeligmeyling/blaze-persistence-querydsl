package com.pallasathenagroup.querydsl.api;

import com.querydsl.jpa.JPQLQuery;

/**
 * An interface for builders that support fetching.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FetchBuilder<X extends FetchBuilder<X>> {

    /**
     * Add the "fetchJoin" flag to the last defined join Mind that collection joins might result in duplicate rows and that "inner join fetchJoin" will restrict your result set.
     *
     * @return this query builder for chaining calls
     * @see JPQLQuery#fetchJoin()
     */
    X fetchJoin();

    /**
     * Add the "fetchJoin all properties" flag to the last defined join.
     *
     * @return this query builder for chaining calls
     * @see JPQLQuery#fetchAll()
     */
    X fetchAll();

}

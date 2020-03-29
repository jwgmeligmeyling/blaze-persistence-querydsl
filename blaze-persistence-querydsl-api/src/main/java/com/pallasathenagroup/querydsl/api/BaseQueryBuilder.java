package com.pallasathenagroup.querydsl.api;


/**
 * A base interface for builders that support basic query functionality.
 * This interface is shared between normal query builders and subquery builders.
 *
 * @param <T> The query result type
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface BaseQueryBuilder<T, X extends BaseQueryBuilder<T, X>> extends CommonQueryBuilder<X>, FromBuilder<X>, WhereBuilder<X>, OrderByBuilder<X>, SelectBuilder<X, Object>, CorrelationQueryBuilder<X>, WindowContainerBuilder<X> {

    /**
     * Returns the result type of this query.
     *
     * @return The result type of this query
     */
    public Class<T> getResultType();

}
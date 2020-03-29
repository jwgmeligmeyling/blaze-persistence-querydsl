package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.ResultTransformer;
import com.querydsl.core.types.SubQueryExpression;

/**
 * A base interface for builders that support normal query functionality.
 * This interface is shared between the criteria builder and paginated criteria builder.
 *
 * @param <T> The query result type
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FullQueryBuilder<T, X extends FullQueryBuilder<T, X>> extends QueryBuilder<T, X>, FetchBuilder<X>, SubQueryExpression<T> {

    /**
     * Invokes {@link com.blazebit.persistence.FullQueryBuilder#pageBy(int, int, String, String...)} with the identifiers of the query root entity.
     *
     * @param firstResult The position of the first result to retrieve, numbered from 0
     * @param maxResults The maximum number of results to retrieve
     * @return This query builder as paginated query builder
     */
    public PaginatedCriteriaBuilder<T> page(int firstResult, int maxResults);

    /**
     * Apply the given transformer to this {@code FetchableQuery} instance and return the results
     *
     * @param <S>
     * @param transformer result transformer
     * @return transformed result
     */
    <S> S transform(ResultTransformer<S> transformer);
}

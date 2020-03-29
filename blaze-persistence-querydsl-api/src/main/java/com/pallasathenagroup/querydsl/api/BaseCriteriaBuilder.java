package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.DistinctBuilder;
import com.blazebit.persistence.LimitBuilder;

/**
 * A builder for criteria queries. This is the entry point for building queries.
 *
 * @param <T> The query result type
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface BaseCriteriaBuilder<T, X extends BaseCriteriaBuilder<T, X>> extends BaseQueryBuilder<T, X>, GroupByBuilder<X>, DistinctBuilder<X>, LimitBuilder<X> {}

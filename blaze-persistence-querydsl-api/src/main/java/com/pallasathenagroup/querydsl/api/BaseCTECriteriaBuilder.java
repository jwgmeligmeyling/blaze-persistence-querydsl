package com.pallasathenagroup.querydsl.api;


import com.blazebit.persistence.DistinctBuilder;
import com.blazebit.persistence.LimitBuilder;

/**
 * A builder for cte criteria queries. This is the entry point for building cte queries.
 *
 * @param <X> The concrete builder type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface BaseCTECriteriaBuilder<X extends BaseCTECriteriaBuilder<X>> extends CommonQueryBuilder<X>, FromBuilder<X>, WhereBuilder<X>, OrderByBuilder<X>, GroupByBuilder<X>, DistinctBuilder<X>, LimitBuilder<X>, WindowContainerBuilder<X> {
}

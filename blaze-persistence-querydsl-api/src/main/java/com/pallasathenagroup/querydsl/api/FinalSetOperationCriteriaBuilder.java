package com.pallasathenagroup.querydsl.api;

import com.blazebit.persistence.LimitBuilder;
import com.blazebit.persistence.Queryable;
import com.querydsl.core.types.SubQueryExpression;

/**
 * An interface for builders that support set operators.
 *
 * @param <T> The builder result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface FinalSetOperationCriteriaBuilder<T> extends
        CommonQueryBuilder<FinalSetOperationCriteriaBuilder<T>>, Queryable<T, FinalSetOperationCriteriaBuilder<T>>, LimitBuilder<FinalSetOperationCriteriaBuilder<T>>, OrderByBuilder<FinalSetOperationCriteriaBuilder<T>>, SubQueryExpression<T> {
}

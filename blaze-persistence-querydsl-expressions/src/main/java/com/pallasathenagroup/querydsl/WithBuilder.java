package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPQLQuery;

/**
 * {@code WithBuilder} is a builder for common table expressions.
 * Analog to {@link com.querydsl.sql.WithBuilder}.
 *
 * @param <R> Expression result type
 * @author Jan-Willem Gmelig Meyling
 * @since 1.0
 */
public interface WithBuilder<R> {

    R as(Expression<?> expr);

}

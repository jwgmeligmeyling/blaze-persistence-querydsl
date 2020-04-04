package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;

/**
 * Utility methods for generating set operations.
 *
 * Analog to {@code com.querydsl.sql.UnionUtils}.
 */
public final class SetUtils {

    @SafeVarargs
    public static <T> Expression<T> setOperation(JPQLNextOps setOperation, Expression<T>... expressions) {
        Expression<T> rv = expressions[0];
        for (int i = 1; i < expressions.length; i++) {
            rv = ExpressionUtils.operation(rv.getType(), setOperation, rv, expressions[i]);
        }
        return rv;
    }

    @SafeVarargs
    public static <T> Expression<T> union(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_UNION, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> unionAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_UNION_ALL, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> intersect(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_INTERSECT, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> intersectAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_INTERSECT_ALL, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> except(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_EXCEPT, expressions);
    }

    @SafeVarargs
    public static <T> Expression<T> exceptAll(Expression<T>... expressions) {
        return setOperation(JPQLNextOps.SET_EXCEPT_ALL, expressions);
    }

}

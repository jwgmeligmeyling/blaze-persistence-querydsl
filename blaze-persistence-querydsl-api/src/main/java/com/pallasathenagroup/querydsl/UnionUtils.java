package com.pallasathenagroup.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.SubQueryExpression;

public class UnionUtils {

    @SafeVarargs
    public static <T> Expression<T> union(Expression<T>... expressions) {
        final Operator operator = JPQLNextOps.SET_UNION;
        Expression<T> rv = expressions[0];
        for (int i = 1; i < expressions.length; i++) {
            rv = ExpressionUtils.operation(rv.getType(), operator, rv, expressions[i]);
        }
        return rv;
    }

    @SafeVarargs
    public static <T> Expression<T> unionAll(Expression<T>... expressions) {
        final Operator operator = JPQLNextOps.SET_UNION_ALL;
        Expression<T> rv = expressions[0];
        for (int i = 1; i < expressions.length; i++) {
            rv = ExpressionUtils.operation(rv.getType(), operator, rv, expressions[i]);
        }
        return rv;
    }

    @SafeVarargs
    public static <T> Expression<T> intersect(Expression<T>... expressions) {
        final Operator operator = JPQLNextOps.SET_INTERSECT;
        Expression<T> rv = expressions[0];
        for (int i = 1; i < expressions.length; i++) {
            rv = ExpressionUtils.operation(rv.getType(), operator, rv, expressions[i]);
        }
        return rv;
    }

    @SafeVarargs
    public static <T> Expression<T> intersectAll(Expression<T>... expressions) {
        final Operator operator = JPQLNextOps.SET_INTERSECT_ALL;
        Expression<T> rv = expressions[0];
        for (int i = 1; i < expressions.length; i++) {
            rv = ExpressionUtils.operation(rv.getType(), operator, rv, expressions[i]);
        }
        return rv;
    }

}

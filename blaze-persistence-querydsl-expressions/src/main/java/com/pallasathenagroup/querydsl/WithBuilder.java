package com.pallasathenagroup.querydsl;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPQLQuery;

/**
 * {@code WithBuilder} is a builder for common table expressions
 *
 * @param <R>
 */
public class WithBuilder<R> {

    private final QueryMixin<R> queryMixin;

    private final Expression<?> alias;

    public WithBuilder(QueryMixin<R> queryMixin, Expression<?> alias) {
        this.queryMixin = queryMixin;
        this.alias = alias;
    }

    public R as(Expression<?> expr) {
        Expression<?> flag = ExpressionUtils.operation(alias.getType(), JPQLNextOps.WITH_ALIAS, alias, expr);
        return queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, flag));
    }

}

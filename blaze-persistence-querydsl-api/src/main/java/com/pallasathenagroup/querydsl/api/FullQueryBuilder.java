package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.SubQueryExpression;

public interface FullQueryBuilder<T, X extends FullQueryBuilder<T, X>> extends QueryBuilder<T, X>, FetchBuilder<X>, SubQueryExpression<T> {
}

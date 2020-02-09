package com.pallasathenagroup.querydsl.api;

public interface QueryBuilder<T, X extends QueryBuilder<T, X>> extends BaseQueryBuilder<T, X>, Queryable<T, X> {
}

package com.pallasathenagroup.querydsl.api;

public interface BaseCriteriaBuilder<T, X extends BaseCriteriaBuilder<T, X>> extends BaseQueryBuilder<T, X>, GroupByBuilder<X>, DistinctBuilder<X>, LimitBuilder<X> { }

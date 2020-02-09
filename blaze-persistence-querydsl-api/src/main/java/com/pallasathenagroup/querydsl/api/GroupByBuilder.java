package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

public interface GroupByBuilder<Q extends GroupByBuilder<Q>> {

    Q groupBy(Expression<?>... expressions);
    Q having(Predicate... predicates);
}

package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.Predicate;

public interface WhereBuilder<Q extends WhereBuilder<Q>> {
    Q where(Predicate... predicates);
}

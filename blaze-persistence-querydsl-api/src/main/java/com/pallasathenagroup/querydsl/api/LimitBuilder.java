package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.QueryModifiers;

public interface LimitBuilder<Q extends LimitBuilder<Q>> {
    Q limit(long l);
    Q offset(long l);
    Q restrict(QueryModifiers queryModifiers);
}

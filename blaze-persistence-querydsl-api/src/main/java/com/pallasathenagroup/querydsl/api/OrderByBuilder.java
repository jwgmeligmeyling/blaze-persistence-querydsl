package com.pallasathenagroup.querydsl.api;

import com.querydsl.core.types.OrderSpecifier;

public interface OrderByBuilder<Q extends OrderByBuilder<Q>> {

    Q orderBy(OrderSpecifier<?>... orderSpecifiers);

}
